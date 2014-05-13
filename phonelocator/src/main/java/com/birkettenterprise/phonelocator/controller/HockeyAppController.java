package com.birkettenterprise.phonelocator.controller;

import android.app.Activity;
import android.os.Bundle;

import com.birkett.controllers.Controller;

import net.hockeyapp.android.CrashManager;
import net.hockeyapp.android.UpdateManager;
import net.hockeyapp.android.tasks.CheckUpdateTask;

import java.lang.ref.WeakReference;

/**
 * Created by alex on 13/05/14.
 */
public class HockeyAppController extends Controller {

    private CheckUpdateTask checkUpdateTask;

    private static final String DOWNLOAD_URL = "https://rink.hockeyapp.net/";
    private static final String APP_ID = "3f7ef8dc87d197b81fb86ff41dcc1314";
    private Activity activity;

    public HockeyAppController(Activity activity) {
        this.activity = activity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        checkForUpdates();
    }

    @Override
    public Object onRetainNonConfigurationInstance() {
        checkUpdateTask.detach();
        return checkUpdateTask;
    }

    @Override
    public void onResume() {
        super.onResume();
        checkForCrashes();
    }

    private void checkForCrashes() {
        CrashManager.register(activity, APP_ID);
    }

    private void checkForUpdates() {
        // Remove this for store builds!
        UpdateManager.register(activity, APP_ID);
    }

}