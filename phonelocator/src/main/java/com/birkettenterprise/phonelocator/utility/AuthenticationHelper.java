package com.birkettenterprise.phonelocator.utility;

import com.android.volley.AuthFailureError;
import com.birkettenterprise.phonelocator.settings.SettingsHelper;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by alex on 06/05/14.
 */
public class AuthenticationHelper {

    public static Map<String, String> getHeaders() {
        HashMap<String, String> headers = new HashMap<String, String>();
        String token = SettingsHelper.getAuthenticationToken();
        headers.put("Authorization", "Bearer " + token);
        return headers;
    }
}
