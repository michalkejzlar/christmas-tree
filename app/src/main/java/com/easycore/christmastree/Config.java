package com.easycore.christmastree;


import android.text.TextUtils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

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

    /**
     * Picks random number every day
     * @param numberOfProjects Array size of objects to randomize in
     * @return random number every day.
     */
    public static int randomNumberEveryDay(int numberOfProjects) {
        final Calendar calendar = Calendar.getInstance();
        final int dayOfYear = calendar.get(Calendar.DAY_OF_YEAR);
        final Random rnd = new Random(dayOfYear);
        return rnd.nextInt(numberOfProjects);
    }

    /**
     * Picks random number every hour
     * @param numberOfProjects Array size of objects to randomize in
     * @return random number every hour.
     */
    public static int randomNumberEveryHour(int numberOfProjects) {
        final Calendar calendar = Calendar.getInstance();
        final int dayOfYear = calendar.get(Calendar.DAY_OF_YEAR);
        final int hourOfDay = calendar.get(Calendar.HOUR_OF_DAY);
        final Random rnd = new Random(dayOfYear * hourOfDay);
        return rnd.nextInt(numberOfProjects);
    }
}
