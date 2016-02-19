package com.letv.share;

import com.letv.share.domain.ShareData;

public interface ShareListener {

	void onShareStart();
	
	void onShareComplete(Object result, ShareData data);
	
	void onShareCancel();
	
	void onShareError(Exception e);
}
