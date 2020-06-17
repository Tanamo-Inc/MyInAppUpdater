package com.tanacom.myinappupdater;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButtonToggleGroup;
import com.tanacom.myinappupdater.lib.Constants;
import com.tanacom.myinappupdater.lib.InAppUpdateManager;
import com.tanacom.myinappupdater.lib.InAppUpdateStatus;


public class MainActivity extends AppCompatActivity implements InAppUpdateManager.InAppUpdateHandler {

    private static final int REQ_CODE_VERSION_UPDATE = 530;

    private static final String TAG = "MainActivity";

    private InAppUpdateManager inAppUpdateManager;

    protected MaterialButtonToggleGroup toggleGroup;

    protected Button updateButton;

    protected ProgressBar progressBar;

    protected TextView tvVersionCode;

    protected TextView tvUpdateAvailable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        init();
    }

    /**
     * @param
     * @method init
     */
    private void init() {

        toggleGroup = findViewById(R.id.toggle_button_group);

        updateButton = findViewById(R.id.bt_update);

        progressBar = findViewById(R.id.progressBar);

        tvVersionCode = findViewById(R.id.tv_available_version);

        tvUpdateAvailable = findViewById(R.id.tv_update_available);


        inAppUpdateManager = InAppUpdateManager.Builder(this, REQ_CODE_VERSION_UPDATE)
                .resumeUpdates(true)
                .handler(this);

        toggleGroup.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if (checkedId == R.id.tb_immediate && isChecked) {
                inAppUpdateManager.mode(Constants.UpdateMode.IMMEDIATE);
            } else {
                inAppUpdateManager.mode(Constants.UpdateMode.FLEXIBLE).useCustomNotification(true);
            }
        });

        updateButton.setOnClickListener(view -> inAppUpdateManager.checkForAppUpdate());

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
         * Called when the update status change occurred.
         */
        progressBar.setVisibility(status.isDownloading() ? View.VISIBLE : View.GONE);

        tvVersionCode.setText(String.format("Available version code: %d", status.availableVersionCode()));
        tvUpdateAvailable.setText(String.format("Update available: %s", String.valueOf(status.isUpdateAvailable())));

        if (status.isDownloaded()) {
            updateButton.setText("Complete Update");
            updateButton.setOnClickListener(view -> inAppUpdateManager.completeUpdate());
        }
    }

}