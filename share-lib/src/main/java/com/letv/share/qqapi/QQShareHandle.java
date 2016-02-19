package com.letv.share.qqapi;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;

import com.letv.share.Constants;
import com.letv.share.ShareConfig;
import com.letv.share.ShareListener;
import com.letv.share.ShareListenerManager;
import com.letv.share.domain.ShareData;
import com.tencent.connect.auth.QQAuth;
import com.tencent.connect.share.QQShare;
import com.tencent.connect.share.QzoneShare;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import java.util.ArrayList;

/**
 * 分享到QQ好友或者QQ空间分发类
 * @author xiaruri
 *
 */
public class QQShareHandle {
	private Context mContext;
	private ShareData mShareData;
	private int mExtarFlag = 0x00;		
	private QQShare mQQShare = null;
	public static QQAuth mQQAuth;
	public Tencent mTencent;
	
	/**
	 * @param context			上下文对象，不能为null
	 */
	public QQShareHandle(Context context){
		mContext = context;
		mQQAuth = QQAuth.createInstance(ShareConfig.getQqAppId(), mContext);
    	mQQShare = new QQShare(mContext, mQQAuth.getQQToken());
	}
	
	public void shareToQQFriendHandle(ShareData shareData, ShareListener shareListener) {
		if (mContext == null || shareData == null) {
			return;
		}
		
		mShareData = shareData;
		ShareListenerManager.getInstance().setDataAndListener(shareData, shareListener);
		ShareListenerManager.getInstance().onShareStart();
		
		shareToQQFriend();
	}
	
	public void shareToQQZoneHandle(ShareData shareData, ShareListener shareListener){
		if (mContext == null || shareData == null) {
			return;
		}
		
		mShareData = shareData;
		ShareListenerManager.getInstance().setDataAndListener(shareData, shareListener);
		ShareListenerManager.getInstance().onShareStart();
		
		shareToQQZone();
	}
	
	private void shareToQQFriend(){
		final Bundle params = getShareToQQFriendParams();
		final Activity activity = (Activity) mContext;
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
                mQQShare.shareToQQ(activity, params, new IUiListener() {
                    @Override
                    public void onCancel() {
                    }
                    @Override
                    public void onComplete(Object response) {
                    	ShareListenerManager.getInstance().onShareComplete(response, null);
                    }
                    @Override
                    public void onError(UiError e) {
                    	ShareListenerManager.getInstance().onShareError(new Exception("share to qq friend fail"));
                    }
                });
//           }
//        }).start();
	}
	
	private void shareToQQZone() {
		final Bundle params = getShareToQQZoneParams();
		final Activity activity = (Activity) mContext;
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
            	mTencent.shareToQzone(activity, params, new IUiListener() {
                    @Override
                    public void onCancel() {
                    }
                    @Override
                    public void onComplete(Object response) {
                    	ShareListenerManager.getInstance().onShareComplete(response, null);
                    }
                    @Override
                    public void onError(UiError e) {
                    	ShareListenerManager.getInstance().onShareError(new Exception("share to qq zone fail"));
                    }
                });
//            }
//        }).start();
	}

	private Bundle getShareToQQFriendParams() {
		mExtarFlag &= (0xFFFFFFFF - QQShare.SHARE_TO_QQ_FLAG_QZONE_AUTO_OPEN);
		
		final Bundle params = new Bundle();
		
		params.putString(QQShare.SHARE_TO_QQ_TITLE, mShareData.getTitle());
		params.putString(QQShare.SHARE_TO_QQ_TARGET_URL, mShareData.getTargetUrl());
		params.putString(QQShare.SHARE_TO_QQ_SUMMARY, mShareData.getContent()); 
		if(!TextUtils.isEmpty(mShareData.getPicUrl())){
			if(mShareData.getPicUrl().contains("http")){
				params.putString(QQShare.SHARE_TO_QQ_IMAGE_URL, mShareData.getPicUrl());
			} else{
				params.putString(QQShare.SHARE_TO_QQ_IMAGE_LOCAL_URL, mShareData.getPicUrl());
			}
		}
		
		params.putString(QQShare.SHARE_TO_QQ_APP_NAME, "");
		params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_DEFAULT);
		params.putInt(QQShare.SHARE_TO_QQ_EXT_INT, mExtarFlag);
		
		return params;
	}
	
	private Bundle getShareToQQZoneParams() {
		mTencent = Tencent.createInstance(ShareConfig.getQqAppId(), mContext);
		final Bundle params = new Bundle();
		params.putInt(QzoneShare.SHARE_TO_QZONE_KEY_TYPE, QzoneShare.SHARE_TO_QZONE_TYPE_IMAGE_TEXT);
		params.putString(QzoneShare.SHARE_TO_QQ_TITLE, mShareData.getTitle());
		params.putString(QzoneShare.SHARE_TO_QQ_SUMMARY, mShareData.getContent());
		if(!TextUtils.isEmpty(mShareData.getTargetUrl())){
			params.putString(QzoneShare.SHARE_TO_QQ_TARGET_URL, mShareData.getTargetUrl());
		} else{
			params.putString(QzoneShare.SHARE_TO_QQ_TARGET_URL, Constants.DEFAULT_TARGET_URL);
		}
		ArrayList<String> imageUrls = new ArrayList<String>();
		if(!TextUtils.isEmpty(mShareData.getPicUrl())){
			if(mShareData.getPicUrl().contains("http")){
				imageUrls.add(mShareData.getPicUrl());
			}
		}
		params.putStringArrayList(QzoneShare.SHARE_TO_QQ_IMAGE_URL, imageUrls);
		return params;
	}
	
}
