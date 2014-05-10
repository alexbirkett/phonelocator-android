package com.birkettenterprise.phonelocator.request;

import android.net.Uri;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.birkettenterprise.phonelocator.model.request.MessageRequestDO;
import com.birkettenterprise.phonelocator.model.response.AddTrackerResponse;
import com.birkettenterprise.phonelocator.model.response.MessageResponseDO;
import com.birkettenterprise.phonelocator.utility.AuthenticationErrorErrorListenerWrapper;
import com.birkettenterprise.phonelocator.utility.AuthenticationHelper;
import com.birkettenterprise.phonelocator.utility.Constants;
import com.birkettenterprise.phonelocator.utility.JacksonRequest;
import com.birkettenterprise.phonelocator.utility.SerialUtil;

import java.util.Map;

/**
 * Created by alex on 10/05/14.
 */
public class MessageRequest extends JacksonRequest<MessageResponseDO> {

    public MessageRequest(
                             MessageRequestDO messageRequestDO,
                             Response.Listener listener,
                             Response.ErrorListener errorListener) {
        super(Request.Method.POST, getAddTrackerRequestUrl(), messageRequestDO, MessageResponseDO.class, listener, new AuthenticationErrorErrorListenerWrapper(errorListener));
    }

    private static String getAddTrackerRequestUrl() {
        Uri.Builder builder = Constants.BASE_URI.buildUpon();
        String url = builder.appendPath("message").appendPath(SerialUtil.getSerial()).build().toString();
        return url;
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        return AuthenticationHelper.getHeaders();
    }
}
