package com.texnoprom.mdam.utils;

/**
 * Отделяет наименование параметра от единицы измерения и убирает шаблон вроде "00.00".
 */
public final class ParameterText {
    private static final String[] UNIT_SUFFIXES = {
            "мм/год", "мкм", "сек.", "сек", "минут", "минуты", "мин", "дБ", "Ом", "ом", "В", "А", "С"
    };

    private ParameterText() {
    }

    public static String name(String raw) {
        return split(raw)[0];
    }

    public static String unit(String raw) {
        return split(raw)[1];
    }

    public static String[] split(String raw) {
        if (raw == null) {
            return new String[]{"", ""};
        }
        String text = raw.replace('\t', ' ');
        text = text.replaceAll("\"0+(?:\\.0+)?\"", " ");
        text = text.replaceAll("(?<![\\p{L}\\d])0{2,}(?:\\.0+)?(?![\\p{L}\\d])", " ");
        text = text.replaceAll("\\s+", " ").trim();

        String unit = "";
        for (String suffix : UNIT_SUFFIXES) {
            if (text.endsWith(" " + suffix)) {
                unit = canonicalUnit(suffix);
                text = text.substring(0, text.length() - suffix.length()).trim();
                break;
            }
        }
        return new String[]{text, unit};
    }

    private static String canonicalUnit(String suffix) {
        if ("ом".equals(suffix)) {
            return "Ом";
        }
        if ("минут".equals(suffix) || "минуты".equals(suffix) || "мин".equals(suffix)) {
            return "мин";
        }
        if ("сек.".equals(suffix) || "сек".equals(suffix)) {
            return "с";
        }
        if ("С".equals(suffix)) {
            return "°C";
        }
        return suffix;
    }
}
