package com.texnoprom.mdam.services;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.texnoprom.mdam.models.FieldTask;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/** Задачи текущего пользователя на этом телефоне. */
public final class TaskStore {
    public static final String[] DEVICES = {"БКМ1", "БКМ4", "ММПР", "ОПЕ11", "ОПЕ14"};
    private static final String FILE_NAME = "field-tasks.json";
    private static final Gson GSON = new Gson();
    private static final Type LIST_TYPE = new TypeToken<ArrayList<FieldTask>>() {
    }.getType();
    private static final Object LOCK = new Object();

    private TaskStore() {
    }

    public static boolean knownDevice(String device) {
        if (device == null) {
            return false;
        }
        for (String item : DEVICES) {
            if (item.equals(device)) {
                return true;
            }
        }
        return false;
    }

    public static FieldTask add(Context context, String owner, String objectName, String deviceType, String note) {
        if (objectName == null || objectName.trim().length() == 0 || !knownDevice(deviceType)) {
            return null;
        }
        FieldTask task = new FieldTask(
                UUID.randomUUID().toString(),
                owner == null ? "" : owner,
                objectName.trim(),
                deviceType,
                note == null ? "" : note.trim(),
                System.currentTimeMillis());
        synchronized (LOCK) {
            List<FieldTask> all = read(context);
            all.add(0, task);
            if (!write(context, all)) {
                return null;
            }
        }
        return task;
    }

    public static List<FieldTask> load(Context context, String owner) {
        synchronized (LOCK) {
            return mine(read(context), owner);
        }
    }

    public static void complete(Context context, String id) {
        synchronized (LOCK) {
            List<FieldTask> all = read(context);
            if (markDone(all, id)) {
                write(context, all);
            }
        }
    }

    public static void reopen(Context context, String id) {
        synchronized (LOCK) {
            List<FieldTask> all = read(context);
            for (FieldTask task : all) {
                if (task != null && id != null && id.equals(task.getId())) {
                    task.setDone(false);
                }
            }
            write(context, all);
        }
    }

    public static void remove(Context context, String id) {
        synchronized (LOCK) {
            List<FieldTask> all = read(context);
            List<FieldTask> kept = new ArrayList<>();
            for (FieldTask task : all) {
                if (task == null || id == null || !id.equals(task.getId())) {
                    if (task != null) {
                        kept.add(task);
                    }
                }
            }
            write(context, kept);
        }
    }

    public static List<FieldTask> mine(List<FieldTask> all, String owner) {
        String who = owner == null ? "" : owner;
        List<FieldTask> mine = new ArrayList<>();
        if (all == null) {
            return mine;
        }
        for (FieldTask task : all) {
            if (task == null || task.getObjectName() == null || task.getId() == null) {
                continue;
            }
            String taskOwner = task.getOwner() == null ? "" : task.getOwner();
            if (who.equals(taskOwner)) {
                mine.add(task);
            }
        }
        return mine;
    }

    public static boolean markDone(List<FieldTask> tasks, String id) {
        if (tasks == null || id == null) {
            return false;
        }
        for (FieldTask task : tasks) {
            if (task != null && id.equals(task.getId()) && !task.isDone()) {
                task.setDone(true);
                return true;
            }
        }
        return false;
    }

    public static String encode(List<FieldTask> tasks) {
        return GSON.toJson(tasks == null ? new ArrayList<FieldTask>() : tasks);
    }

    public static List<FieldTask> decode(String json) {
        if (json == null || json.trim().length() == 0) {
            return new ArrayList<>();
        }
        try {
            List<FieldTask> parsed = GSON.fromJson(json, LIST_TYPE);
            return parsed == null ? new ArrayList<FieldTask>() : parsed;
        } catch (RuntimeException ignored) {
            return new ArrayList<>();
        }
    }

    private static List<FieldTask> read(Context context) {
        File file = file(context);
        if (!file.exists()) {
            return new ArrayList<>();
        }
        FileInputStream in = null;
        try {
            in = new FileInputStream(file);
            byte[] bytes = new byte[(int) file.length()];
            int offset = 0;
            while (offset < bytes.length) {
                int n = in.read(bytes, offset, bytes.length - offset);
                if (n < 0) {
                    break;
                }
                offset += n;
            }
            return decode(new String(bytes, 0, offset, "UTF-8"));
        } catch (Exception ignored) {
            return new ArrayList<>();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (Exception ignored) {
                }
            }
        }
    }

    private static boolean write(Context context, List<FieldTask> tasks) {
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(file(context));
            out.write(encode(tasks).getBytes("UTF-8"));
            return true;
        } catch (Exception ignored) {
            return false;
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (Exception ignored) {
                }
            }
        }
    }

    private static File file(Context context) {
        return new File(context.getApplicationContext().getFilesDir(), FILE_NAME);
    }
}
