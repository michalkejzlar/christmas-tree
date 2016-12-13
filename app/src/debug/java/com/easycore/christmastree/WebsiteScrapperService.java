package com.easycore.christmastree;

import android.app.IntentService;
import android.content.Intent;
import android.os.Environment;
import android.util.Log;
import com.easycore.christmastree.model.Donation;
import com.easycore.christmastree.model.DonationsDb;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.easycore.christmastree.model.DonationsDb.TABLE_NAME;

/**
 * Background service for scraping www.darcovskasms.cz website.
 * DO NOT USE THIS IN PRODUCTION! Execute it only once and follow instructions bellow.
 * You can use this code as standalone Java application. This doesn't need to be in Android app.
 * Parser used for scraping - https://jsoup.org.
 *
 * INSTRUCTIONS:
 * 1. Set USE_SCRAPER in build.gradle file to true
 * 2. Make sure, you have READ & WRITE_EXTERNAL_STORAGE permissions.
 * 3. Make sure, you are on Wifi to prevent data charges
 * 4. Run this service by startService(new Intent(context, WebsiteScrapperService.class))
 * 5. It will automatically start scraping that website. See progress in Logcat under it's class name
 * 6. After scraping is finished, it will save all data as INSERT statements into .sql file on device
 * 7. Copy that file into assets folder inside IDE. See Logcat on how to copy from device to your machine.
 * 8. Double check all inserts are correct and escaped
 * 9. Insert all into local database and you are done. {@see DonationsDb}
 */
public final class WebsiteScrapperService extends IntentService {

    private static final String WEBSITE_URL = "http://www.darcovskasms.cz";
    private static final String ENDPOINT = "/seznam-projektu/prehled-aktivnich-projektu.html";
    private static final String TAG = WebsiteScrapperService.class.getSimpleName();

    public WebsiteScrapperService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        if (!BuildConfig.USE_SCRAPER || !BuildConfig.DEBUG) {
            return;
        }

        try {
            parseDocument(WEBSITE_URL + ENDPOINT);
            Log.d(TAG, "Scraping finished successfully!");
        } catch (IOException | RuntimeException e) {
            // It's only one time thing.
            Log.e(TAG, "Scraping failed miserably.", e);
        }
    }

    // Connect to website and navigate to dms table rows
    private void parseDocument(final String url) throws IOException, RuntimeException {
        final Document doc = Jsoup.connect(url).get();
        final Element body = doc.body();

        // get <table>
        final Elements table = body.getElementsByClass("dms");

        if (table.size() != 1) {
            // Only one table is supported.
            throw new IllegalArgumentException("Unable to parse. " +
                    "None or multiple html tags with 'dms' className. Check website html document.");
        }

        // Get <tbody> inside of table
        final Elements tableBody = table.get(0).getElementsByTag("tbody");

        if (tableBody.size() != 1) {
            // Only one table body is supported.
            throw new IllegalArgumentException("Unable to parse. " +
                    "None or more than one table body. Check website html document.");
        }

        // Get table rows
        final Elements rows = table.get(0).getElementsByTag("tr");

        final List<Donation> donations = parseTableRows(rows);

        if (donations.isEmpty()) {
            throw new IllegalStateException("No donation projects to save!");
        }

        saveDonationsToFile(donations);
    }

    /**
     * Parses each table row
     * @param rows Rows of table
     * @return List of donation projects
     */
    private List<Donation> parseTableRows(final Elements rows) {
        final List<Donation> donations = new ArrayList<>();

        for (Element row : rows) {
            final Elements columns = row.getElementsByTag("td"); // get columns

            if (columns.isEmpty()) {
                // skip <th> and other elements
                continue;
            }

            // quite dangerous, but #yolo
            final String href = columns.get(0).child(0).attr("href");
            final String SMSCode = columns.get(2).text();

            Log.d(TAG, "Processing " + href);

            try {
                Donation donation = parseDonation(SMSCode, href);
                donations.add(donation);
            } catch (IOException | RuntimeException e) {
                // skip corrupted donation
                Log.e(TAG, "Skipping. Error parsing Donation: " + href, e);
            }
        }
        return donations;
    }

    /**
     * Parses detail website of donation project for give href link
     * @param code DMS code of project.
     * @param href HREF link to website detail
     * @return Donation object
     * @throws IOException
     * @throws RuntimeException
     */
    private Donation parseDonation(final String code, final String href) throws IOException, RuntimeException {
        final Donation.Builder builder = new Donation.Builder();
        final Document doc = Jsoup.connect(WEBSITE_URL + href).get();
        final Element body = doc.body();

        final Element projectInfo = body.getElementById("projectleft");

        final Elements elements = projectInfo.getAllElements();

        int companyInfoStartPos = 0;

        // Get project info
        for (int i = 0; i < elements.size() ; i++) {
            final Element el = elements.get(i);

            // Project name
            if ("h2".equals(el.tagName())) {
                builder.setProjectName(el.text());
            }

            // Project description. Could be multiple times
            if ("p".equals(el.tagName())) {
                builder.appendProjectDesc(el.text());
            }

            // Company name.
            if ("h3".equals(el.tagName())) {
                // This is company info.
                companyInfoStartPos = i;
                break;
            }
        }

        builder.setSmsCode(code);

        if (companyInfoStartPos == 0) {
            // no company info
            return builder.build();
        }

        // Get company info
        for (int i = companyInfoStartPos; i < elements.size(); i++) {
            final Element el = elements.get(i);
            if ("h3".equals(el.tagName())) {
                builder.setCompanyName(el.text());
            }

            if ("p".equals(el.tagName())) {
                builder.appendCompanyDesc(el.text());
            }

        }

        return builder.build();
    }

    /**
     * Prints all donation projects data into one .sql file as INSERT commands.
     * Use adb to pull this file from device to your machine.
     * @param donations List of donation projects to save.
     * @throws IOException
     * @throws RuntimeException
     */
    private void saveDonationsToFile(final List<Donation> donations) throws IOException, RuntimeException {
        final File sql = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOWNLOADS),
                DonationsDb.INSERT_FILE_NAME);

        if (sql.exists()) {
            sql.delete();
        }
        sql.createNewFile();

        FileOutputStream outputStream;
        outputStream = new FileOutputStream(sql.getAbsolutePath());
        for (Donation d : donations) {
            final String insert = createInsert(d) + "\n";
            outputStream.write(insert.getBytes());
        }
        outputStream.close();

        Log.d(TAG, "File save finished successfully. " +
                "Use following command to copy file to your machine:");
        Log.d(TAG, "adb pull " + sql.getAbsolutePath() + " [/your/local/path]");
    }

    /**
     * Creates string for SQLite insert.
     * @param donation Donation to save
     * @return SQLite INSERT SYNTAX like INSERT INTO ... () VALUES ();
     */
    private static String createInsert(Donation donation) {
        return "INSERT INTO " + TABLE_NAME + " (" +
                DonationsDb.Columns._PROJECT_NAME + ", " +
                DonationsDb.Columns._COMPANY_NAME + ", " +
                DonationsDb.Columns._SMS_CODE + ", " +
                DonationsDb.Columns._PROJECT_DESC + ", " +
                DonationsDb.Columns._COMPANY_DESC + ", " +
                DonationsDb.Columns._COMPANY_IMG_LINK + ") VALUES (" +
                "'" + donation.getProjectName() + "', " +
                "'" + donation.getCompanyName() + "', " +
                "'" + donation.getSmsCode() + "', " +
                "'" + donation.getProjectDescription() + "', " +
                "'" + donation.getCompanyDescription() + "', " +
                "'" + donation.getPicture() + "');";
    }

}
