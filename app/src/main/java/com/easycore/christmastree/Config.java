package com.easycore.christmastree;


import android.text.TextUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public final class Config {

    public static final String DONATION_PHONE_NUMBER = "87 777";

    public static final String TREE_STREAM_URL = "http://35.156.238.168:1935/live/stromecek/playlist.m3u8";

    private static final SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.getDefault());

    private Config() {

    }

    public static String dmsCode(String projectCode) {
        if (TextUtils.isEmpty(projectCode)) {
            throw new IllegalArgumentException("Donation project code could not be null.");
        }

        return "DMS " + projectCode.toUpperCase();
    }

    public static String formatDate(final Date date) {
        synchronized (Config.class) {
            return dateFormatter.format(date);
        }
    }
}
