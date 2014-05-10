package com.birkettenterprise.phonelocator.request;

import android.net.Uri;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.birkettenterprise.phonelocator.utility.AuthenticationErrorErrorListenerWrapper;
import com.birkettenterprise.phonelocator.utility.AuthenticationHelper;
import com.birkettenterprise.phonelocator.utility.Constants;
import com.birkettenterprise.phonelocator.utility.JacksonRequest;

import java.util.Map;

/**
 * Created by alex on 06/05/14.
 */
public class AddTrackerRequest extends JacksonRequest<com.birkettenterprise.phonelocator.model.response.AddTrackerResponse> {

    public AddTrackerRequest(String name,
                             String serial,
                             Response.Listener listener,
                             Response.ErrorListener errorListener) {
        super(Request.Method.POST, getAddTrackerRequestUrl(), buildRequest(name, serial), com.birkettenterprise.phonelocator.model.response.AddTrackerResponse.class, listener, new AuthenticationErrorErrorListenerWrapper(errorListener));
    }

    private static String getAddTrackerRequestUrl() {
        Uri.Builder builder = Constants.BASE_URI.buildUpon();
        String url = builder.appendPath("trackers").build().toString();
        return url;
    }

    private static com.birkettenterprise.phonelocator.model.request.AddTrackerRequest buildRequest(String name, String serial) {
        com.birkettenterprise.phonelocator.model.request.AddTrackerRequest request = new com.birkettenterprise.phonelocator.model.request.AddTrackerRequest();
        request.name = name;
        request.serial = serial;
        return request;
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        return AuthenticationHelper.getHeaders();
    }
}
