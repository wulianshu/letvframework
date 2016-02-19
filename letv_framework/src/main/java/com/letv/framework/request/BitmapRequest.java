package com.letv.framework.request;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.letv.framework.util.LogUtil;

/**
 * Created by liuzhuo on 2016/2/16.
 */
public class BitmapRequest extends Request<Bitmap> {

    private Response.Listener<Bitmap> mListener;

    public BitmapRequest(String url, Response.Listener<Bitmap> listener,
                         Response.ErrorListener errorListener) {
        super(Method.GET, url, errorListener);
        mListener = listener;
        // 增加超时时间
        setRetryPolicy(new DefaultRetryPolicy());
    }

    @Override
    protected Response<Bitmap> parseNetworkResponse(NetworkResponse response) {
        try{
            Bitmap bmp = BitmapFactory.decodeByteArray(response.data, 0, response.data.length);
            return Response.success(bmp, HttpHeaderParser.parseCacheHeaders(response));
        }catch(Throwable e){
            LogUtil.e(e.toString());
            return Response.error(new VolleyError(e.toString()));
        }
    }

    @Override
    protected void deliverResponse(Bitmap response) {
        if(mListener != null)
            mListener.onResponse(response);
    }
}
