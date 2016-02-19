package com.letv.http;

import com.letv.http.impl.LetvHttpBaseParameter;

import java.io.IOException;

public interface LetvHttpBaseHandler {
	
	public String doGet(LetvHttpBaseParameter<?, ?, ?> params) throws IOException;
	
	public String doPost(LetvHttpBaseParameter<?, ?, ?> params) throws IOException;
	
}
