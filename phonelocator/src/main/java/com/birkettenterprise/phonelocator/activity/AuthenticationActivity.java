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

import com.astuetz.PagerSlidingTabStrip;
import com.birkettenterprise.phonelocator.R;
import com.birkettenterprise.phonelocator.fragment.SigninFragment;
import com.birkettenterprise.phonelocator.fragment.SignupFragment;
import com.birkettenterprise.phonelocator.settings.SettingsHelper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.widget.Toast;

public class AuthenticationActivity extends FragmentActivity {

    private AuthenticationAdapter adapter;
    private ViewPager viewPager;

    public static class AuthenticationAdapter extends FragmentPagerAdapter {
        public AuthenticationAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment = null;
            if (position == 0) {
                fragment = SignupFragment.newInstance(position);
            } else {
                fragment = SigninFragment.newInstance(position);
            }
            return fragment;
        }

        private final String[] TITLES = { "Sign up", "Sign in" };

        @Override
        public CharSequence getPageTitle(int position) {
            return TITLES[position];
        }

    }

    // Bind the tabs to the ViewPager

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authentication);
        adapter = new AuthenticationAdapter(getSupportFragmentManager());
        viewPager = (ViewPager)findViewById(R.id.pager);
        viewPager.setAdapter(adapter);
        PagerSlidingTabStrip tabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        tabs.setViewPager(viewPager);
    }

    @Override
	public void onPause() {
		super.onPause();
	}
	
	@Override
	public void onResume() {
		super.onResume();
        //if (isRegistered()) {
        //    startDashboardActivity();
        //}
    }
	
	@Override
	protected void onDestroy() {
	    super.onDestroy();
	}

	private void startDashboardActivity() {
		Intent intent = new Intent(this, DashboardActivity.class);
        startActivity(intent);
		finish();
	}
		
	private boolean isRegistered() {
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
		return SettingsHelper.isRegistered(sharedPreferences);
	}

	private void toast(int resourceId) {
		Toast.makeText(this, resourceId, Toast.LENGTH_LONG).show();
	}
}
