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

package com.commonsware.cwac.locpoll;

import android.content.Intent;
import android.os.Bundle;

public class LocationPollerParameter {

	static final String KEY = "com.commonsware.cwac.locpoll.";
	static final String INTENT_TO_BROADCAST_ON_COMPLETION_KEY = KEY + "EXTRA_INTENT";
	static final String PROVIDER_KEY = KEY + "EXTRA_PROVIDER";
	static final String PROVIDERS_KEY = KEY + "EXTRA_PROVIDERS";
	static final String TIMEOUT_KEY = KEY + "EXTRA_TIMEOUT";
	
	private static final int DEFAULT_TIMEOUT = 120000; // two minutes
	
	private Bundle bundle;
	
	public LocationPollerParameter(Bundle bundle) {
		this.bundle = bundle;
	}
	
	public long getTimeout() {
		return bundle.getLong(TIMEOUT_KEY, DEFAULT_TIMEOUT);
	}

	public void setTimeout(long timeout) {
		bundle.putLong(TIMEOUT_KEY, timeout);
	}

	public String[] getProviders() {
		String[] providers = bundle.getStringArray(PROVIDERS_KEY);
		if (providers == null) {
			String provider = bundle.getString(PROVIDER_KEY);
			if (provider != null) {
				providers = new String[] { provider };
			}
		}
		return providers;
	}

	public void setProviders(String[] providers) {
		bundle.putStringArray(PROVIDERS_KEY, providers);
	}
	
	public void addProvider(String provider) {
		String[] existingProviders = getProviders();
		if (existingProviders == null) {
			setProviders(new String[] { provider});
		} else {
			int providerArrayLength = getProviderArrayLength();
			String[] newProviders = new String[providerArrayLength + 1];
			System.arraycopy(existingProviders, 0, newProviders, 0, providerArrayLength);
			newProviders[providerArrayLength] = provider;
			setProviders(newProviders);
		}
	}
	
	public int getProviderArrayLength() {
		String[] providers = getProviders();
		if (providers == null) {
			return 0;
		} else {
			return providers.length;
		}
	}
	
    public Intent getIntentToBroadcastOnCompletion() {
    	return (Intent) bundle.get(INTENT_TO_BROADCAST_ON_COMPLETION_KEY);
    }
    
    public void setIntentToBroadcastOnCompletion(Intent intentToBroadcastOnCompletion) {
    	bundle.putParcelable(INTENT_TO_BROADCAST_ON_COMPLETION_KEY, intentToBroadcastOnCompletion);
    }

}
