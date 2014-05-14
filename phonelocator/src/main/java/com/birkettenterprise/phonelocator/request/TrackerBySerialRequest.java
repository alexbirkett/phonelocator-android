package com.birkettenterprise.phonelocator.request;

import android.net.Uri;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.birkettenterprise.phonelocator.model.response.TrackerDO;
import com.birkettenterprise.phonelocator.model.response.TrackersResponseDO;
import com.birkettenterprise.phonelocator.utility.AuthenticationErrorErrorListenerWrapper;
import com.birkettenterprise.phonelocator.utility.AuthenticationHelper;
import com.birkettenterprise.phonelocator.utility.Constants;
import com.birkettenterprise.phonelocator.utility.JacksonRequest;
import com.birkettenterprise.phonelocator.utility.SerialUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by alex on 25/04/14.
 */
public class TrackerBySerialRequest extends JacksonRequest<TrackersResponseDO> {

    private static String getUrl(String serial) {
         Uri.Builder builder = Constants.BASE_URI.buildUpon();
         String url = builder.appendPath("trackers").appendQueryParameter("serial", serial).build().toString();
         return url;
    }

    public TrackerBySerialRequest(Response.Listener listener,
                                  Response.ErrorListener errorListener) {
        super(Method.GET, getUrl(SerialUtil.getSerial()), null, TrackersResponseDO.class, listener, new AuthenticationErrorErrorListenerWrapper(errorListener));
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        return AuthenticationHelper.getHeaders();
    }
}
