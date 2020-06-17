package com.tanacom.myinappupdater;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.tanacom.myinappupdater.lib.Constants;
import com.tanacom.myinappupdater.lib.InAppUpdateManager;
import com.tanacom.myinappupdater.lib.InAppUpdateStatus;

public class FlexibleWithCustomNotification extends AppCompatActivity implements InAppUpdateManager.InAppUpdateHandler {
    private static final int REQ_CODE_VERSION_UPDATE = 530;
    private static final String TAG = "Sample";
    private InAppUpdateManager inAppUpdateManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();

    }


    private void init() {

        inAppUpdateManager = InAppUpdateManager.Builder(this, REQ_CODE_VERSION_UPDATE)
                .resumeUpdates(true) // Resume the update, if the update was stalled. Default is true
                .mode(Constants.UpdateMode.FLEXIBLE)
                // default is false. If is set to true you,
                // have to manage the user confirmation when
                // you detect the InstallStatus.DOWNLOADED status,
                .useCustomNotification(true)
                .handler(this);

        inAppUpdateManager.checkForAppUpdate();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == REQ_CODE_VERSION_UPDATE) {
            if (resultCode != RESULT_OK) {
                // If the update is cancelled or fails,
                // you can request to start the update again.
                inAppUpdateManager.checkForAppUpdate();

                Log.d(TAG, "Update flow failed! Result code: " + resultCode);
            }
        }

        super.onActivityResult(requestCode, resultCode, data);

    }

    // InAppUpdateHandler implementation

    @Override
    public void onInAppUpdateError(int code, Throwable error) {
        /*
         * Called when some error occurred. See Constants class for more details
         */
        Log.d(TAG, "code: " + code, error);
    }

    @Override
    public void onInAppUpdateStatus(InAppUpdateStatus status) {
        /*
         * If the update downloaded, ask user confirmation and complete the update
         */

        if (status.isDownloaded()) {

            new AlertDialog.Builder(this)
                    .setTitle("InAppUpdate")
                    .setMessage("An update has just been downloaded.")
                    .setPositiveButton("Complete", (dialog, which) -> {
                        // Triggers the completion of the update of the app for the flexible flow.
                        inAppUpdateManager.completeUpdate();
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        }
    }
}
