package com.letv.framework.util;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class CommonUtil {

	private static CommonUtil mInstance;
	private RequestQueue mQueue;
	
	public synchronized static CommonUtil getInstance(Context context){
		if(mInstance == null)
			return new CommonUtil(context);
		return mInstance;
	}
	
	private CommonUtil(Context context){
		context = context.getApplicationContext();
		init(context);
	}
	
	private void init(Context context) {
		mQueue = Volley.newRequestQueue(context);
	}
	
	public RequestQueue getRequestQueue(){
		return mQueue;
	}

	public void sendRequest(Request req){
		if(mQueue == null)
			return;
		mQueue.add(req);
	}

	public void sendRequest(Request req, Object tag){
		if(mQueue == null)
			return;
		req.setTag(tag);
		sendRequest(req);
	}
}
