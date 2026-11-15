package com.texnoprom.mdam.utils;

import android.content.Context;
import android.preference.PreferenceManager;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Locale;

/** Адрес backend. В настройках хранится начало адреса, путь /sotca/post добавляет запрос. */
public final class ServerAddress {
    public static final String PREF = "backend_base_url";
    public static final String DEFAULT = "http://zav.texnoprom.com:8080/";

    private ServerAddress() {
    }

    public static String get(Context context) {
        String saved = PreferenceManager.getDefaultSharedPreferences(context)
                .getString(PREF, DEFAULT);
        String normalized = normalize(saved);
        return normalized == null ? DEFAULT : normalized;
    }

    public static boolean save(Context context, String raw) {
        String normalized = normalize(raw);
        if (normalized == null) {
            return false;
        }
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putString(PREF, normalized)
                .apply();
        return true;
    }

    /**
     * Принимает адрес Mac в сети или полный адрес отправки.
     * http://192.168.1.20:8080 и http://192.168.1.20:8080/sotca/post дают одно начало.
     */
    public static String normalize(String raw) {
        if (raw == null) {
            return null;
        }
        String value = raw.trim();
        if (value.length() == 0) {
            return null;
        }
        if (!value.contains("://")) {
            value = "http://" + value;
        }
        String lower = value.toLowerCase(Locale.US);
        if (lower.endsWith("/sotca/post/")) {
            value = value.substring(0, value.length() - "/sotca/post/".length());
        } else if (lower.endsWith("/sotca/post")) {
            value = value.substring(0, value.length() - "/sotca/post".length());
        }
        try {
            URI uri = new URI(value);
            String scheme = uri.getScheme();
            String host = uri.getHost();
            if (scheme == null || host == null) {
                return null;
            }
            if (!"http".equalsIgnoreCase(scheme) && !"https".equalsIgnoreCase(scheme)) {
                return null;
            }
            StringBuilder origin = new StringBuilder();
            origin.append(scheme.toLowerCase(Locale.US)).append("://").append(host);
            if (uri.getPort() != -1) {
                origin.append(':').append(uri.getPort());
            }
            origin.append('/');
            return origin.toString();
        } catch (URISyntaxException e) {
            return null;
        }
    }
}
