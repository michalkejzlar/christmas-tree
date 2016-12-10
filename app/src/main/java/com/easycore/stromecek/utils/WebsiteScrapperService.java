package com.easycore.stromecek.utils;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;
import com.easycore.stromecek.model.Donation;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public final class WebsiteScrapperService extends IntentService {

    private static final String WEBSITE_URL = "http://www.darcovskasms.cz";
    private static final String ENDPOINT = "/seznam-projektu/prehled-aktivnich-projektu.html";
    private static final String TAG = WebsiteScrapperService.class.getSimpleName();

    public WebsiteScrapperService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        try {
            parseDocument(WEBSITE_URL + ENDPOINT);
        } catch (IOException e) {
            // It's only one time thing.
            // TODO: 10/12/16 Log somewhere
            e.printStackTrace();
        }
    }

    // Connect to website and navigate to dms table rows
    private void parseDocument(final String url) throws IOException {
        final Document doc = Jsoup.connect(url).get();
        final Element body = doc.body();

        // get <table>
        final Elements table = body.getElementsByClass("dms");

        if (table.size() != 1) {
            // Only one table is supported.
            throw new RuntimeException("Unable to parse. " +
                    "None or multiple html tags with 'dms' className. Check website html document.");
        }

        // Get <tbody> inside of table
        final Elements tableBody = table.get(0).getElementsByTag("tbody");

        if (tableBody.size() != 1) {
            // Only one table body is supported.
            throw new RuntimeException("Unable to parse. " +
                    "None or more than one table body. Check website html document.");
        }

        // Get table rows
        final Elements rows = table.get(0).getElementsByTag("tr");

        final List<Donation> donations = parseTableRows(rows);

    }

    private List<Donation> parseTableRows(final Elements rows) throws RuntimeException {
        final List<Donation> donations = new ArrayList<>();

        for (Element row : rows) {
            final Elements columns = row.getElementsByTag("td"); // get columns

            if (columns.isEmpty()) {
                // skip <th> and other elements
                continue;
            }

            // quite dangerous, but #yolo
            final String href = columns.get(0).child(0).attr("href");

            try {
                Donation donation = parseDonation(href);
                donations.add(donation);
            } catch (IOException | RuntimeException e) {
                // skip corrupted donation
                Log.e(TAG, "Error parsing Donation: " + href, e);
                // TODO: 10/12/16 log somewhere else
            }
        }
        return donations;
    }

    private Donation parseDonation(final String href) throws IOException, RuntimeException {
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

}
