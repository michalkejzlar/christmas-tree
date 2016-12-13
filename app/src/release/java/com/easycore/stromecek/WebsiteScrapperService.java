package com.easycore.christmastree;


import android.app.IntentService;
import android.content.Intent;

/**
 * Stub implementation of website scrapper service.
 */
public class WebsiteScrapperService extends IntentService {

    private static final String TAG = WebsiteScrapperService.class.getSimpleName();

    public WebsiteScrapperService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        throw new RuntimeException("No, you don't in production release!");
    }
}
