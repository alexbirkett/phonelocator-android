package com.birkettenterprise.phonelocator.utility;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.birkettenterprise.phonelocator.settings.SettingsHelper;

import java.io.IOException;

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
        if (isAuthenticationException(volleyError.getCause()) || isAuthenticationStatusCode(volleyError)) {
            SettingsHelper.setAuthenticationToken(null);
        }
        listener.onErrorResponse(volleyError);
    }

    private boolean isAuthenticationException(Throwable error) {
        return (error != null && error instanceof IOException && error.getMessage() != null && error.getMessage().contains("authentication"));
    }

    private boolean isAuthenticationStatusCode(VolleyError error) {
        return (error.networkResponse != null && error.networkResponse.statusCode == 401);
    }
}
