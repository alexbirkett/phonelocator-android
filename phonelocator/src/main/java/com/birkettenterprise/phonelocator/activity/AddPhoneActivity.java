package com.birkettenterprise.phonelocator.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.birkettenterprise.phonelocator.R;
import com.birkettenterprise.phonelocator.application.PhonelocatorApplication;
import com.birkettenterprise.phonelocator.request.AddTrackerRequest;
import com.birkettenterprise.phonelocator.request.CheckTrackerAddedRequest;
import com.birkettenterprise.phonelocator.settings.SettingsHelper;
import com.birkettenterprise.phonelocator.utility.SerialUtil;

/**
 * Created by alex on 05/05/14.
 */
public class AddPhoneActivity extends Activity {

    private View progressBar;
    private Button addTrackerButton;
    private Button retryCheckTrackerButton;
    private EditText trackerNameView;
    private TextView statusMessage;
    private CheckTrackerAddedRequest checkTrackerAddedRequest;
    private AddTrackerRequest addTrackerRequest;

    private CheckTrackerAddedRequest.Callback checkTrackerAddedCallback = new CheckTrackerAddedRequest.Callback() {

        @Override
        public void onComplete(boolean added) {
            checkTrackerAddedRequest = null;
            if (added) {
                handleSuccess();
            } else {
                configureViewsForTrackerNameInput();
            }
        }

        @Override
        public void onError(Exception error) {
            checkTrackerAddedRequest = null;
            if (SettingsHelper.getAuthenticationToken() == null) {
                finish();
            } else {
                statusMessage.setText(R.string.add_phone_error);
                progressBar.setVisibility(View.INVISIBLE);
                addTrackerButton.setVisibility(View.GONE);
                retryCheckTrackerButton.setVisibility(View.VISIBLE);
                retryCheckTrackerButton.setEnabled(true);
            }
        }
    };

    private View.OnClickListener addTrackerClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            handleAddTrackerClicked();
        }
    };

    private View.OnClickListener retryClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            checkTrackerAdded();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_phone);
        progressBar = findViewById(R.id.add_phone_progress_bar);
        addTrackerButton = (Button)findViewById(R.id.add_phone_button);
        retryCheckTrackerButton = (Button)findViewById(R.id.retry_check_tracker_button);
        trackerNameView = (EditText)findViewById(R.id.add_phone_name);
        addTrackerButton.setOnClickListener(addTrackerClickListener);
        retryCheckTrackerButton.setOnClickListener(retryClickListener);
        statusMessage = (TextView) findViewById(R.id.add_phone_status_message);
    }

    @Override
    public void onResume() {
        super.onResume();
        checkTrackerAdded();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (checkTrackerAddedRequest != null) {
            checkTrackerAddedRequest.cancel();
        }
        if (addTrackerRequest != null) {
            addTrackerRequest.cancel();
        }
    }

    private void checkTrackerAdded() {
        checkTrackerAddedRequest = new CheckTrackerAddedRequest(checkTrackerAddedCallback);
        PhonelocatorApplication.getInstance().getQueue().add(checkTrackerAddedRequest);

        progressBar.setVisibility(View.VISIBLE);
        addTrackerButton.setVisibility(View.GONE);
        trackerNameView.setVisibility(View.GONE);
        retryCheckTrackerButton.setEnabled(false);
        statusMessage.setText(R.string.add_phone_checking_tracker_added);
    }

    private void addTracker(String name, String serial) {

        Response.Listener listener = new  Response.Listener() {
            @Override
            public void onResponse(Object response) {
                handleAddTrackerResponse(null);
            }
        };

        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                NetworkResponse networkResponse = error.networkResponse;
                handleAddTrackerResponse(error);
            }
        };

        addTrackerRequest = new AddTrackerRequest(name, serial, listener, errorListener);

        PhonelocatorApplication.getInstance().getQueue().add(addTrackerRequest);
    }

    private void handleAddTrackerResponse(VolleyError error) {
        addTrackerRequest = null;
        if (error == null) {
            finish();
        } else {
            configureViewsForTrackerNameInput();
            statusMessage.setText(R.string.add_phone_error);
        }
    }

    private void toast(int resourceId) {
        Toast toast = Toast.makeText(this, resourceId, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.TOP, 0, 0);
        toast.show();
    }

    private void handleSuccess() {
        setResult(RESULT_OK, getIntent());
        finish();
    }

    private void configureViewsForTrackerNameInput() {
        progressBar.setVisibility(View.INVISIBLE);
        addTrackerButton.setEnabled(true);
        addTrackerButton.setVisibility(View.VISIBLE);
        statusMessage.setText(R.string.add_phone_description);
        trackerNameView.setVisibility(View.VISIBLE);
        retryCheckTrackerButton.setVisibility(View.GONE);
        trackerNameView.requestFocus();
    }

    private void handleAddTrackerClicked() {
        String trackerName = trackerNameView.getText().toString();
        if (trackerName.length() == 0) {
            toast(R.string.add_phone_enter_name);
        }
        addTracker(trackerName, SerialUtil.getSerial());
        progressBar.setVisibility(View.VISIBLE);
        addTrackerButton.setEnabled(false);
    }

}
