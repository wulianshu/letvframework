package com.letv.share.sinaapi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.letv.share.ShareConfig;
import com.letv.share.ShareListener;
import com.letv.share.domain.ShareData;
import com.sina.weibo.sdk.api.share.BaseResponse;
import com.sina.weibo.sdk.api.share.IWeiboHandler;
import com.sina.weibo.sdk.api.share.IWeiboShareAPI;
import com.sina.weibo.sdk.api.share.WeiboShareSDK;
import com.sina.weibo.sdk.constant.WBConstants;

import java.util.concurrent.atomic.AtomicBoolean;

public class WeiboShareActivity extends Activity implements IWeiboHandler.Response{

	private ShareListener mShareListener;
	private SinaShareHandle mHandle;
	private boolean isAuth;
	private IWeiboShareAPI mWeiboShareAPI;
	private AtomicBoolean isPaused = new AtomicBoolean(false);
	private ShareData mShareData;
	
	public static ShareListener shareListener;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		mShareData = (ShareData) getIntent().getSerializableExtra("data");
		if(mShareData == null){
			finish();
			return;
		}

		boolean getInfo = getIntent().getBooleanExtra("getInfo", false);
		if (mWeiboShareAPI == null)
			mWeiboShareAPI = WeiboShareSDK.createWeiboAPI(this,
					ShareConfig.getSinaAppKey());
		
		mShareListener = new ShareListener() {
            @Override
            public void onShareStart() {
            	if(shareListener != null)
            		shareListener.onShareStart();
            }

            @Override
            public void onShareComplete(Object result, ShareData data) {
            	if(shareListener != null)
            		shareListener.onShareComplete(result, mShareData);
				finish();
            }

			@Override
            public void onShareCancel() {
            	if(shareListener != null)
            		shareListener.onShareCancel();
				finish();
            }

            @Override
            public void onShareError(Exception e) {
				if(shareListener != null) {
					shareListener.onShareError(e);
				}
				finish();
            }

		};
        
        mHandle = new SinaShareHandle(this);
		isAuth = mHandle.shareHandle(mShareData, mShareListener, getInfo,mWeiboShareAPI);
	}

	@Override
	protected void onDestroy() {			
		shareListener = null;
		super.onDestroy();	
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		if(isPaused.get() && !isAuth)
			finish();
	}
	
	protected void onPause(){
		super.onPause();
		isPaused.set(true);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (mHandle != null && mHandle.getSsoHandler() != null) {
			mHandle.getSsoHandler().authorizeCallBack(requestCode, resultCode, data);
        }
	}

	/**
	 * 接收微客户端博请求的数据。
	 * 当微博客户端唤起当前应用并进行分享时，该方法被调用。
	 *
	 * @param baseResp 微博请求数据对象
	 * @see {@link IWeiboShareAPI#handleWeiboRequest}
	 */
	@Override
	public void onResponse(BaseResponse baseResp) {
		switch (baseResp.errCode) {
			case WBConstants.ErrorCode.ERR_OK:
				mShareListener.onShareComplete(baseResp,mShareData);
				break;
			case WBConstants.ErrorCode.ERR_CANCEL:
				mShareListener.onShareCancel();
				break;
			case WBConstants.ErrorCode.ERR_FAIL:
				Exception e = new Exception(baseResp.errMsg);
				mShareListener.onShareError(e);
				break;
		}
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);

		// 从当前应用唤起微博并进行分享后，返回到当前应用时，需要在此处调用该函数
		// 来接收微博客户端返回的数据；执行成功，返回 true，并调用
		// {@link IWeiboHandler.Response#onResponse}；失败返回 false，不调用上述回调
		mWeiboShareAPI.handleWeiboResponse(intent, this);
	}
}
