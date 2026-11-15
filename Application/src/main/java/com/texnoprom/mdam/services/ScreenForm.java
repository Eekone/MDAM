package com.texnoprom.mdam.services;

import com.texnoprom.mdam.models.Register;
import com.texnoprom.mdam.models.RegisterInfo;
import com.texnoprom.mdam.utils.ParameterText;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/** Пустая экранная форма прибора: те же строки, что на вкладке модуля. */
public final class ScreenForm {
    private ScreenForm() {
    }

    public static int origin(String device, String screen) {
        if ("БКМ4".equals(device) && ArchiveWriter.VALUES.equals(screen)) {
            return 0;
        }
        if ("БКМ4".equals(device) && ArchiveWriter.CALIBRATION.equals(screen)) {
            return 0;
        }
        if (ArchiveWriter.CONFIG.equals(screen)) {
            return 10;
        }
        if (ArchiveWriter.CALIBRATION.equals(screen)) {
            return 15;
        }
        if (ArchiveWriter.MISC.equals(screen)) {
            return 30;
        }
        return 0;
    }

    public static int count(String device, String screen) {
        if ("БКМ4".equals(device) && ArchiveWriter.VALUES.equals(screen)) {
            return 12;
        }
        if ("БКМ4".equals(device) && ArchiveWriter.CALIBRATION.equals(screen)) {
            return 32;
        }
        if (ArchiveWriter.VALUES.equals(screen)) {
            return 10;
        }
        if (ArchiveWriter.CONFIG.equals(screen)) {
            return 5;
        }
        if (ArchiveWriter.CALIBRATION.equals(screen)) {
            return 15;
        }
        if (ArchiveWriter.MISC.equals(screen)) {
            return 11;
        }
        return 0;
    }

    public static com.texnoprom.mdam.models.RegisterBatch blank(String device, String screen, String user, String time) {
        int start = origin(device, screen);
        int total = count(device, screen);
        List<Register> rows = new ArrayList<>();
        for (int number = start; number < start + total; number++) {
            String raw = RegisterInfo.Name(device, 3, number);
            String title = ParameterText.name(raw);
            if (title.length() == 0 || title.startsWith("No ")) {
                title = "Регистр " + (number + 1);
            }
            Register row = new Register(3, number, title, 0f);
            row.setUnit(unit(device, row, raw));
            rows.add(row);
        }
        com.texnoprom.mdam.models.RegisterBatch batch =
                new com.texnoprom.mdam.models.RegisterBatch(time, device, rows);
        batch.setId(UUID.randomUUID().toString());
        batch.setUser(user);
        batch.setScreen(screen);
        return batch;
    }

    private static String unit(String device, Register row, String raw) {
        String fromTable = RegisterInfo.Unit(row, device);
        if (fromTable != null) {
            String trimmed = fromTable.trim();
            if (trimmed.length() > 0 && !"?".equals(trimmed) && !trimmed.startsWith("No ")) {
                return trimmed;
            }
        }
        return ParameterText.unit(raw);
    }
}
