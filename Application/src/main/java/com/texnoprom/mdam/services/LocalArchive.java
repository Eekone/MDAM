package com.texnoprom.mdam.services;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.texnoprom.mdam.models.Register;
import com.texnoprom.mdam.models.RegisterBatch;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/** Показания, сохранённые на телефоне. Новая запись стоит первой. */
public final class LocalArchive {
    private static final String FILE_NAME = "local-archive.json";
    private static final Gson GSON = new Gson();
    private static final Type LIST_TYPE = new TypeToken<ArrayList<RegisterBatch>>() {
    }.getType();
    private static final Object LOCK = new Object();

    private LocalArchive() {
    }

    public static boolean add(Context context, RegisterBatch batch) {
        if (!keep(batch)) {
            return false;
        }
        synchronized (LOCK) {
            List<RegisterBatch> all = read(context);
            all.add(0, batch);
            return write(context, all);
        }
    }

    public static List<RegisterBatch> load(Context context) {
        synchronized (LOCK) {
            return read(context);
        }
    }

    public static void remove(Context context, int index) {
        synchronized (LOCK) {
            List<RegisterBatch> all = read(context);
            if (index < 0 || index >= all.size()) {
                return;
            }
            all.remove(index);
            write(context, all);
        }
    }

    public static boolean keep(RegisterBatch batch) {
        return batch != null && batch.getRegisters() != null && !batch.getRegisters().isEmpty();
    }

    /** Ручная запись может состоять из пользователя и времени, ещё без регистров. */
    public static boolean visible(RegisterBatch batch) {
        if (keep(batch)) {
            return true;
        }
        if (batch == null) {
            return false;
        }
        return text(batch.getUser()).length() > 0 || text(batch.getScreen()).length() > 0;
    }

    public static boolean addAny(Context context, RegisterBatch batch) {
        if (!visible(batch)) {
            return false;
        }
        if (batch.getRegisters() == null) {
            batch.setRegisters(new ArrayList<Register>());
        }
        synchronized (LOCK) {
            List<RegisterBatch> all = read(context);
            all.add(0, batch);
            return write(context, all);
        }
    }

    public static boolean replace(Context context, RegisterBatch batch) {
        if (batch == null || batch.getId() == null) {
            return false;
        }
        synchronized (LOCK) {
            List<RegisterBatch> all = read(context);
            for (int i = 0; i < all.size(); i++) {
                if (batch.getId().equals(all.get(i).getId())) {
                    all.set(i, batch);
                    return write(context, all);
                }
            }
            return false;
        }
    }

    private static String text(String value) {
        return value == null ? "" : value.trim();
    }

    public static String encode(List<RegisterBatch> batches) {
        return GSON.toJson(batches == null ? new ArrayList<RegisterBatch>() : batches);
    }

    public static List<RegisterBatch> decode(String json) {
        if (json == null || json.trim().length() == 0) {
            return new ArrayList<>();
        }
        try {
            List<RegisterBatch> parsed = GSON.fromJson(json, LIST_TYPE);
            if (parsed == null) {
                return new ArrayList<>();
            }
            List<RegisterBatch> kept = new ArrayList<>();
            for (RegisterBatch batch : parsed) {
                if (visible(batch)) {
                    kept.add(batch);
                }
            }
            return kept;
        } catch (RuntimeException ignored) {
            return new ArrayList<>();
        }
    }

    private static List<RegisterBatch> read(Context context) {
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

    private static boolean write(Context context, List<RegisterBatch> batches) {
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(file(context));
            out.write(encode(batches).getBytes("UTF-8"));
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
