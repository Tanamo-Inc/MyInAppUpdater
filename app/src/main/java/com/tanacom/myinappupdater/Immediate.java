
package com.tanacom.myinappupdater;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.tanacom.myinappupdater.lib.Constants;
import com.tanacom.myinappupdater.lib.InAppUpdateManager;


public class Immediate extends AppCompatActivity {

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
                .mode(Constants.UpdateMode.IMMEDIATE);

        inAppUpdateManager.checkForAppUpdate();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == REQ_CODE_VERSION_UPDATE) {
            if (resultCode == Activity.RESULT_CANCELED) {
                // If the update is cancelled by the user,
                // you can request to start the update again.
                inAppUpdateManager.checkForAppUpdate();

                Log.d(TAG, "Update flow failed! Result code: " + resultCode);
            }
        }

        super.onActivityResult(requestCode, resultCode, data);

    }

}
