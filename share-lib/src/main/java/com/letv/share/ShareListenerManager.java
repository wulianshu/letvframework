package com.letv.share;

import android.util.Log;

import com.letv.share.domain.ShareData;


/**
 * <p>分享回调工具类
 * <p>由于分享需要回调，而分享到微信平台时无法通过设置监听实现，故通过单例实现回调，需要回调时需调用
 * @author xiaruri
 *
 */
public class ShareListenerManager {
	
	private static ShareListenerManager mShareListenerManager = null;
	
	private ShareData mShareData;
	private ShareListener mShareListener;

	private ShareListenerManager(){
	}
	
	public static ShareListenerManager getInstance(){
		if(mShareListenerManager == null){
			mShareListenerManager = new ShareListenerManager();
		}
		return mShareListenerManager;
	}
	
	public void setDataAndListener(ShareData shareData, ShareListener shareListener){
		mShareData = shareData;
		mShareListener = shareListener;
	}
	
	public void onShareStart(){
		if(mShareListener == null){
			return;
		}
		mShareListener.onShareStart();
	}
	
	public void onShareCancel(){
		Log.e("xrr", "ShareListenerManager onShareCancel mShareListener = "+mShareListener);
		if(mShareListener == null){
			return;
		}
		mShareListener.onShareCancel();
		reset();
	}
	
	public void onShareComplete(Object result, Exception e){
		Log.e("xrr", "ShareListenerManager onShareComplete mShareListener = "+mShareListener);
		if(mShareListener == null){
			return;
		}
		if(result != null && e == null){
			mShareListener.onShareComplete(result, mShareData);
		} else{
			mShareListener.onShareError(e);
		}
		reset();
	}
	
	public void onShareError(Exception e){
		Log.e("xrr", "ShareListenerManager onShareError mShareListener = "+mShareListener);
		if(mShareListener == null){
			return;
		}
		mShareListener.onShareError(e);
		reset();
	}
	
	private void reset(){
		mShareListener = null;
		mShareData = null;
	}
}
