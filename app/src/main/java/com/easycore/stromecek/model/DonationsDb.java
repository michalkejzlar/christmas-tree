package com.easycore.stromecek.model;


import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;


public class DonationsDb extends SQLiteOpenHelper {

    private static final String TAG = DonationsDb.class.getSimpleName();

    private static final String DB_NAME = "stromecek.db";
    private static final int DB_VERSION = 1;

    public static final String TABLE_NAME = "donations";
    public static final String INSERT_FILE_NAME = "donations_insert.sql";

    private static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " (" +
            Columns._ID + " INT PRIMARY KEY, " +
            Columns._PROJECT_NAME + " TEXT, " +
            Columns._COMPANY_NAME + " TEXT, " +
            Columns._SMS_CODE + " TEXT, " +
            Columns._PROJECT_DESC + " TEXT, " +
            Columns._COMPANY_DESC + " TEXT, " +
            Columns._COMPANY_IMG_LINK + " TEXT);";

    public static final class Columns implements BaseColumns {
        public static final String _PROJECT_NAME = "_project_name";
        public static final String _COMPANY_NAME = "_company_name";
        public static final String _SMS_CODE = "_sms_code";
        public static final String _PROJECT_DESC = "_project_desc";
        public static final String _COMPANY_DESC = "_company_desc";
        public static final String _COMPANY_IMG_LINK = "_company_img_link";
    }

    private final Context context;

    public DonationsDb(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
        try {
            insertDefaultValues(db);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // no migrations. Start from scratch.
        db.execSQL("DROP TABLE IF EXISTS " + DB_NAME);
        db.execSQL(CREATE_TABLE);
    }

    public List<Donation> getDonations() {
        final SQLiteDatabase db = getReadableDatabase();
        final List<Donation> donations = new ArrayList<>();

        Cursor c = db.rawQuery("SELECT * FROM " + TABLE_NAME, null);
        c.moveToFirst();

        while (c.moveToNext()) {
            Donation.Builder builder = new Donation.Builder();
            builder.setProjectName(c.getString(c.getColumnIndex(Columns._PROJECT_NAME)));
            builder.setCompanyName(c.getString(c.getColumnIndex(Columns._COMPANY_NAME)));
            builder.appendProjectDesc(c.getString(c.getColumnIndex(Columns._PROJECT_DESC)));
            builder.appendCompanyDesc(c.getString(c.getColumnIndex(Columns._COMPANY_DESC)));
            builder.setPicture(c.getString(c.getColumnIndex(Columns._COMPANY_IMG_LINK)));
            builder.setSmsCode(c.getString(c.getColumnIndex(Columns._SMS_CODE)));
            donations.add(builder.build());
        }
        c.close();
        return donations;
    }

    private void insertDefaultValues(SQLiteDatabase db) throws IOException {
        InputStream is = context.getAssets().open(INSERT_FILE_NAME);

        BufferedReader reader;
        reader = new BufferedReader(new InputStreamReader(is));

        String insert;
        try {
            while ((insert = reader.readLine()) != null) {
                db.execSQL(insert);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            is.close();
        }
    }

}
