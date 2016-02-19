package com.letv.framework.request;

import com.android.volley.VolleyError;

public class RequestParseException extends VolleyError {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private int mCode;
	
	public RequestParseException(int code){
		super();
		mCode = code;
	}

	public int getErrorCode(){
		return mCode;
	}
}
