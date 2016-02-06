/*
 *    Copyright 2015 David Newell
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package ca.lightseed.winston;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import java.util.concurrent.ExecutionException;

/**
 * Winston is a GPS data logger and asynchronous message passer for Android.
 * @author David Newell
 */

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        checkForUpdates();

        startService(new Intent(this, LocationSendService.class));

/**
 * TODO:  reverted back to the tutorial onCreate due to a bug. This is a priority fix.
 */
        try {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("www.davidnewell.ca")));
        } catch (android.content.ActivityNotFoundException anfe) {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("www.davidnewell.ca")));
        }

        finish();

    }

    /**
     * This method checks current app version against the latest available
     * on the server.  If a newer version exists, it updates the app.
     * Uses CWAC.
     * TODO: evaluate whether CWAK is the best solution for updates
     */
    private void checkForUpdates() {
        int currentVersionCode = 0, updatedVersionCode = 0;

        try {
            currentVersionCode = getPackageManager().getPackageInfo(
                    getPackageName(), 0).versionCode;

        } catch (Exception e) {
            Log.e("Updater:",
                    "An exception occurred while updating app", e);
        }

        try {
            updatedVersionCode = new GetUpdateCode().execute().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        if (updatedVersionCode > currentVersionCode) {
            updateAppFromServer();
        }
    }

    /**
     * This method handles app updates.
     * Uses CWAC.
     * TODO: evaluate whether CWAC is the best solution for updates
     */
    private void updateAppFromServer() {

        // Do update

        UpdateRequest.Builder builder = new UpdateRequest.Builder(this);

        builder.setVersionCheckStrategy(buildVersionCheckStrategy())
                .setPreDownloadConfirmationStrategy(
                        buildPreDownloadConfirmationStrategy())
                .setDownloadStrategy(buildDownloadStrategy())
                .setPreInstallConfirmationStrategy(
                        buildPreInstallConfirmationStrategy()).execute();

    }

    /**
     * The following CWAC methods (will) handle checking the server for a version number,
     * and presenting the user with the option to update.
     */
    DownloadStrategy buildDownloadStrategy() {
        if (Build.VERSION.SDK_INT >= 11) {
            return (new InternalHttpDownloadStrategy());
        }

        return (new SimpleHttpDownloadStrategy());
    }

    ConfirmationStrategy buildPreDownloadConfirmationStrategy() {
        return (new ImmediateConfirmationStrategy());
    }

    ConfirmationStrategy buildPreInstallConfirmationStrategy() {
        return (new ImmediateConfirmationStrategy());
    }

    VersionCheckStrategy buildVersionCheckStrategy() {
        return (new SimpleHttpVersionCheckStrategy(Constants.UPDATE_URL));
    }

}
