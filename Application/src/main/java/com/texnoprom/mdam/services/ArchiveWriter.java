package com.texnoprom.mdam.services;

import android.content.Context;
import android.preference.PreferenceManager;

import com.texnoprom.mdam.models.RegisterBatch;

import java.util.UUID;

/** Запись считанных регистров в локальный архив с пользователем и временем. */
public final class ArchiveWriter {
    public static final String VALUES = "Значения";
    public static final String CONFIG = "Конфигурация";
    public static final String CALIBRATION = "Калибровки";
    public static final String MISC = "Прочее";
    public static final String SAVED = "Сохранение";
    public static final String[] SCREENS = {VALUES, CONFIG, CALIBRATION, MISC};

    private ArchiveWriter() {
    }

    public static String currentUser(Context context) {
        String name = PreferenceManager.getDefaultSharedPreferences(context).getString("userName", "");
        if (name == null || "Not Available".equals(name)) {
            return "";
        }
        return name;
    }

    public static boolean saveRead(Context context, RegisterBatch batch, String screen) {
        if (context == null || !LocalArchive.keep(batch)) {
            return false;
        }
        batch.setId(UUID.randomUUID().toString());
        batch.setUser(currentUser(context));
        batch.setScreen(screen);
        return LocalArchive.add(context, batch);
    }
}
