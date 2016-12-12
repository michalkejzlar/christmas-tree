package com.easycore.stromecek;


import android.text.TextUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public final class Config {

    public static final String DONATION_PHONE_NUMBER = "87 777";

    public static final long LIGHT_SIGNAL_DELAY_IN_MILLIS = TimeUnit.SECONDS.toMillis(5);

    public static final SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.getDefault());

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
