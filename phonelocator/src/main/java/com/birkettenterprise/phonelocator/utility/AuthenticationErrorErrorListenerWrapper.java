package com.birkettenterprise.phonelocator.utility;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.birkettenterprise.phonelocator.settings.SettingsHelper;

/**
 * Created by alex on 06/05/14.
 */
public class AuthenticationErrorErrorListenerWrapper implements Response.ErrorListener {

    private Response.ErrorListener listener;

    public AuthenticationErrorErrorListenerWrapper(Response.ErrorListener listener) {
        this.listener = listener;
    }

    @Override
    public void onErrorResponse(VolleyError volleyError) {
        if (volleyError.networkResponse != null
            && volleyError.networkResponse.statusCode == 401) {
            SettingsHelper.setAuthenticationToken(null);
        }
        listener.onErrorResponse(volleyError);
    }
}
