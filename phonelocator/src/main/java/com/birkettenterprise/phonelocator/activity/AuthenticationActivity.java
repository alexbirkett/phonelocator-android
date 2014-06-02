/**
 * 
 *  Copyright 2011-2014 Birkett Enterprise Ltd
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 * 
 */

package com.birkettenterprise.phonelocator.activity;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.astuetz.PagerSlidingTabStrip;
import com.birkett.controllers.ActivityThatSupportsControllers;
import com.birkettenterprise.phonelocator.R;
import com.birkettenterprise.phonelocator.application.PhonelocatorApplication;
import com.birkettenterprise.phonelocator.controller.HockeyAppController;
import com.birkettenterprise.phonelocator.model.request.AuthenticateRequest;
import com.birkettenterprise.phonelocator.model.request.SingUpRequest;
import com.birkettenterprise.phonelocator.model.response.AuthenticateResponse;
import com.birkettenterprise.phonelocator.model.response.SignUpResponse;
import com.birkettenterprise.phonelocator.settings.SettingsHelper;
import com.birkettenterprise.phonelocator.utility.Constants;
import com.birkettenterprise.phonelocator.utility.JacksonRequest;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

public class AuthenticationActivity extends ActivityThatSupportsControllers {

    private AuthenticationAdapter adapter;
    private ViewPager viewPager;
    private PagerSlidingTabStrip tabs;
    private TextView actionButton;
    private View progressBar;
    private RequestQueue queue;

    private static int SIGN_UP_INDEX = 0;

    private ViewPager.OnPageChangeListener pageChangeListener = new ViewPager.OnPageChangeListener() {

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            if (position == SIGN_UP_INDEX) {
                actionButton.setText(R.string.action_sign_up);
            } else {
                actionButton.setText(R.string.action_sign_in);
            }

        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };

    private View.OnClickListener actionButtonClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            if (viewPager.getCurrentItem() == SIGN_UP_INDEX) {
                signUp();
            } else {
                signIn();
            }

        }
    };

    public class AuthenticationAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return 2;
        }


        @Override
        public CharSequence getPageTitle(int position) {
            String title;
            if (position == SIGN_UP_INDEX) {
                title = getResources().getString(R.string.tab_sign_up);
            } else {
                title = getResources().getString(R.string.tab_sign_in);
            }
            return title;
        }

        public Object instantiateItem(ViewGroup collection, int position) {

            int resId = 0;
            if (position == SIGN_UP_INDEX) {
                resId = R.id.layout_sign_up;
            } else {
                resId = R.id.layout_sign_in;
            }
            return findViewById(resId);
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == ((View) arg1);
        }

    }

    // Bind the tabs to the ViewPager

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        addController(new HockeyAppController(this));
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authentication);
        adapter = new AuthenticationAdapter();
        viewPager = (ViewPager) findViewById(R.id.pager);
        viewPager.setAdapter(adapter);
        tabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        tabs.setViewPager(viewPager);
        tabs.setOnPageChangeListener(pageChangeListener);
        queue = PhonelocatorApplication.getInstance().getQueue();

        actionButton = (TextView) findViewById(R.id.authentication_action_button);
        actionButton.setOnClickListener(actionButtonClickListener);
        progressBar = findViewById(R.id.authentication_progress_bar);

    }

	private void toast(int resourceId) {
		Toast toast = Toast.makeText(this, resourceId, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.TOP, 0, 0);
        toast.show();
	}

    private void signUp() {
        if (validateSignUp()) {
            signUpRequest(getValueFromTextView(R.id.sign_up_user_name),
                          getValueFromTextView(R.id.sign_up_email),
                          getValueFromTextView(R.id.sign_up_password));
        }
    }

    private void signIn() {
        if (validateSignIn()) {
            signInRequest(getValueFromTextView(R.id.sign_in_email_or_user_name), getValueFromTextView(R.id.sign_in_password));
        }
    }

    private void signInRequest(String usernameOrEmail, String password) {
        setRequesting(true);
        AuthenticateRequest request = new AuthenticateRequest();
        request.email = usernameOrEmail;
        request.password = password;

        String url = Constants.BASE_URL + "/authenticate";
        JacksonRequest<AuthenticateResponse> jacksonRequest = new JacksonRequest(Request.Method.POST, url, request,AuthenticateResponse.class,
                new Response.Listener<AuthenticateResponse>() {

                    @Override
                    public void onResponse(AuthenticateResponse response) {
                        setRequesting(false);
                         SettingsHelper.setAuthenticationToken(response.token);
                        startDashboardActivity();
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        NetworkResponse networkResponse = error.networkResponse;
                        if ((networkResponse != null && networkResponse.statusCode == 401)
                         || (error.getMessage() != null && error.getMessage().contains("authentication"))) {
                            toast(R.string.sign_in_error_invalid_email_user_name_or_password);
                        } else {
                            toast(R.string.sign_in_could_not_connect_to_server);
                        }
                        setRequesting(false);
                    }
        });
        queue.add(jacksonRequest);
    }

    private void signUpRequest(final String username, final String email, final String password) {
        setRequesting(true);
        SingUpRequest request = new SingUpRequest();
        request.email = email;
        request.password = password;
        request.username = username;

        JacksonRequest<SignUpResponse> jacksonRequest = new JacksonRequest(Request.Method.POST, Constants.BASE_URL + "/signup", request, SignUpResponse.class,
                new Response.Listener<SignUpResponse>() {
                    @Override
                    public void onResponse(SignUpResponse response) {
                        signInRequest(email, password);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                NetworkResponse networkResponse = error.networkResponse;
                if (networkResponse != null && networkResponse.statusCode == 403) {
                    toast(R.string.sign_up_user_already_registered);
                } else {
                    toast(R.string.sign_up_could_not_connect_to_server);
                }
                setRequesting(false);
            }
        });
        queue.add(jacksonRequest);
    }

    private boolean validateSignUp() {
        String email = getValueFromTextView(R.id.sign_up_email);
        String userName = getValueFromTextView(R.id.sign_up_user_name);
        String password = getValueFromTextView(R.id.sign_up_password);
        boolean valid = true;
        if (email.length() == 0 || !email.contains("@")) {
            findViewById(R.id.sign_up_email).requestFocus();
            toast(R.string.sign_up_error_email);
            valid = false;
        } else if (userName.length() == 0) {
            findViewById(R.id.sign_up_user_name).requestFocus();
            toast(R.string.sign_up_error_username);
            valid = false;
        } else if (password.length() < 8) {
            findViewById(R.id.sign_up_password).requestFocus();
            toast(R.string.sign_up_error_password);
            valid = false;
        }
        return valid;
    }

    private boolean validateSignIn() {
        String emailOrUserName = getValueFromTextView(R.id.sign_in_email_or_user_name);
        String password = getValueFromTextView(R.id.sign_in_password);
        boolean valid = true;
        if (emailOrUserName.length() == 0) {
            findViewById(R.id.sign_in_email_or_user_name).requestFocus();
            toast(R.string.sign_in_error_username_or_email);
            valid = false;
        } else if (password.length() == 0) {
            findViewById(R.id.sign_in_password).requestFocus();
            toast(R.string.sign_in_error_password);
            valid = false;
        }
        return valid;
    }

    private String getValueFromTextView(int id) {
       TextView textView = (TextView) findViewById(id);
       return textView.getText().toString();
    }

    private void setRequesting(boolean requesting) {
        progressBar.setVisibility(requesting ? View.VISIBLE : View.INVISIBLE);
        actionButton.setEnabled(!requesting);
    }

    private void startDashboardActivity() {
        Intent intent = new Intent(this, DashboardActivity.class);
        // Use startActivityForResult to prevent flicker
        startActivity(intent);
        finish();
    }
}
