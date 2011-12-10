/**
 * 
 *  Copyright 2011 Birkett Enterprise Ltd
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

import com.birkettenterprise.phonelocator.R;

import android.app.TabActivity;
import android.os.Bundle;
import android.widget.TabHost;
import android.content.Intent;

public class TabsAcitvity extends TabActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final TabHost tabHost = getTabHost();
        createStatusTabSpec(tabHost);
        createSettingsTabSpec(tabHost);
    }
    
    private void createStatusTabSpec(final TabHost tabHost) {
    	TabHost.TabSpec statusTabSpec = tabHost.newTabSpec("updates");
        statusTabSpec.setIndicator(getString(R.string.updates_tab));
        statusTabSpec.setContent(new Intent(this, UpdateLogActivity.class));
        tabHost.addTab(statusTabSpec);
    }
    
    private void createSettingsTabSpec(final TabHost tabHost) {
    	TabHost.TabSpec statusTabSpec = tabHost.newTabSpec("settings");
        statusTabSpec.setIndicator(getString(R.string.settings_tab), getResources().getDrawable(R.drawable.ic_tab_options));
        statusTabSpec.setContent(new Intent(this, SettingsActivity.class));
        tabHost.addTab(statusTabSpec);
    }
}
