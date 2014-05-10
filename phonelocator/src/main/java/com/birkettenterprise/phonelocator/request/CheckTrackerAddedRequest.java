package com.birkettenterprise.phonelocator.request;

import android.net.Uri;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.birkettenterprise.phonelocator.utility.AuthenticationErrorErrorListenerWrapper;
import com.birkettenterprise.phonelocator.utility.AuthenticationHelper;
import com.birkettenterprise.phonelocator.utility.Constants;
import com.birkettenterprise.phonelocator.utility.SerialUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by alex on 25/04/14.
 */
public class CheckTrackerAddedRequest extends Request<Void> {

    private Callback callback;

    public interface Callback {
        public void onComplete(boolean added);
        public void onError(Exception error);
    }

    private static String getUrl(String serial) {
         Uri.Builder builder = Constants.BASE_URI.buildUpon();
         String url = builder.appendPath("trackers").appendQueryParameter("serial", serial).build().toString();
         return url;
    }

    public CheckTrackerAddedRequest(final Callback callback) {
        super(Method.GET, getUrl(SerialUtil.getSerial()), new AuthenticationErrorErrorListenerWrapper(new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError volleyError) {
                if (volleyError.networkResponse != null && volleyError.networkResponse.statusCode == 404) {
                    callback.onComplete(false);
                } else {
                    callback.onError(volleyError);
                }
            }
        }));
        this.callback = callback;
    }

    @Override
    protected Response<Void> parseNetworkResponse(NetworkResponse networkResponse) {
        return Response.success(null, HttpHeaderParser.parseCacheHeaders(networkResponse));
    }

    @Override
    protected void deliverResponse(Void aVoid) {
        callback.onComplete(true);
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        return AuthenticationHelper.getHeaders();
    }
}
