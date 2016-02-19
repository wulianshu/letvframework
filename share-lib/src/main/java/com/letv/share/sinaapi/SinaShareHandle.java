package com.letv.share.sinaapi;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.letv.framework.request.BitmapRequest;
import com.letv.framework.util.CommonUtil;
import com.letv.framework.util.LogUtil;
import com.letv.share.Constants;
import com.letv.share.R;
import com.letv.share.ShareConfig;
import com.letv.share.ShareListener;
import com.letv.share.ShareListenerManager;
import com.letv.share.domain.ShareData;
import com.letv.share.util.PreferenceUtil;
import com.sina.weibo.sdk.api.ImageObject;
import com.sina.weibo.sdk.api.TextObject;
import com.sina.weibo.sdk.api.WebpageObject;
import com.sina.weibo.sdk.api.WeiboMultiMessage;
import com.sina.weibo.sdk.api.share.IWeiboShareAPI;
import com.sina.weibo.sdk.api.share.SendMultiMessageToWeiboRequest;
import com.sina.weibo.sdk.api.share.WeiboShareSDK;
import com.sina.weibo.sdk.auth.AuthInfo;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;
import com.sina.weibo.sdk.openapi.LogoutAPI;
import com.sina.weibo.sdk.openapi.UsersAPI;
import com.sina.weibo.sdk.openapi.models.User;
import com.sina.weibo.sdk.utils.Utility;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;

public class SinaShareHandle {

	/**
	 * 封装了 "access_token"，"expires_in"，"refresh_token"，并提供了他们的管理功能
	 */
	private Oauth2AccessToken mAccessToken;
	/**
	 * 微博分享的接口实例
	 */
	private IWeiboShareAPI mWeiboShareAPI;
	/**
	 * 注意：SsoHandler 仅当 SDK 支持 SSO 时有效
	 */
	private SsoHandler mSsoHandler;
	/**
	 * 用户信息接口
	 */
	private UsersAPI mUsersAPI;
	/**
	 * 注销操作回调
	 */
	private LogOutRequestListener mLogoutRequestListener;

	private AuthInfo mAuthInfo;

	private Activity mContext;
	private ShareData mShareData;
	private Dialog mDialog;
	private Handler mHandler;

	private boolean isInstalledWeibo;
	private int supportApiLevel;
	private boolean mIsLogin = false;

	public SinaShareHandle(Activity context) {
		mContext = context;
		mHandler = new MyHandler(this);
		if (mAuthInfo == null)
			mAuthInfo = new AuthInfo(mContext, ShareConfig.getSinaAppKey(),
					ShareConfig.getSinaRedirectUrl(),
					ShareConfig.getSinaScope());
		if (mSsoHandler == null)
			mSsoHandler = new SsoHandler(mContext, mAuthInfo);
	}

	/**
	 * 分享处理，如果已授权则直接分享，如果未授权则先授权，授权成功后再分享
	 * 
	 * @param shareData
	 *            需要分享的数据，不能为null
	 * @param shareListener
	 *            分享监听器，不需要时可以为null
	 * @param getInfo
	 *            分享时是否获取用户信息
	 */
	public boolean shareHandle(ShareData shareData, ShareListener shareListener,
			boolean getInfo , IWeiboShareAPI weiboShareAPI) {
		if (mContext == null || shareData == null) {
			return false;
		}
		mShareData = shareData;
		ShareListenerManager.getInstance().setDataAndListener(shareData,
				shareListener);
		ShareListenerManager.getInstance().onShareStart();
		if(weiboShareAPI!=null){
			mWeiboShareAPI = weiboShareAPI;
		}

		if (!isAuthorized()) {
			authorize(false, getInfo);
			return true;
		} else {
			share();
			return false;
		}
	}

	public boolean isAuthorized() {
		Oauth2AccessToken accessToken = AccessTokenKeeper
				.readAccessToken(mContext.getApplicationContext());
		if (accessToken != null && accessToken.isSessionValid()) {
			return true;
		}
		return false;
	}

	private void authorize(boolean login, boolean getInfo) {
		mSsoHandler.authorize(new AuthListener(login, getInfo));
	}

	public SsoHandler getSsoHandler() {
		return mSsoHandler;
	}

	/**
	 * 微博认证授权回调类。 1. SSO 授权时，需要在 {@link # onActivityResult} 中调用
	 * {@link SsoHandler#authorizeCallBack} 后， 该回调才会被执行。 2. 非 SSO
	 * 授权时，当授权结束后，该回调就会被执行。 当授权成功后，请保存该 access_token、expires_in、uid 等信息到
	 * SharedPreferences 中。
	 */
	class AuthListener implements WeiboAuthListener {

		private boolean getInfo = false;


		public AuthListener(boolean isLogin, boolean isGetInfo) {
			mIsLogin = isLogin;
			getInfo = isGetInfo;
		}

		@Override
		public void onCancel() {
			ShareListenerManager.getInstance().onShareCancel();
		}

		@Override
		public void onComplete(Bundle values) {
			// 从 Bundle 中解析 Token
			mAccessToken = Oauth2AccessToken.parseAccessToken(values);
			LogUtil.i("tag", "get token " + mAccessToken);
			if (mAccessToken.isSessionValid()) {
				// 保存 Token 到 SharedPreferences
				AccessTokenKeeper.writeAccessToken(mContext, mAccessToken);
				if (mIsLogin) {
					getUsrInfo();
				} else {
					if (getInfo)
						getUsrInfo();
					share();
				}
			} else {
				// 以下几种情况，您会收到 Code：
				// 1. 当您未在平台上注册的应用程序的包名与签名时；
				// 2. 当您注册的应用程序包名与签名不正确时；
				// 3. 当您在平台上注册的包名和签名与您当前测试的应用的包名和签名不匹配时。
				String code = values.getString("code");
				String message = "";
				if (!TextUtils.isEmpty(code)) {
					message = "\nObtained the code: " + code;
				}
				Log.e("tag", message);
				ShareListenerManager.getInstance().onShareError(
						new WeiboException("get sina token fail"));
			}
		}

		@Override
		public void onWeiboException(WeiboException e) {
			ShareListenerManager.getInstance().onShareError(e);
		}
	}

	/**
	 * 微博 OpenAPI 回调接口。
	 */
	private RequestListener mListener = new RequestListener() {
		@Override
		public void onComplete(String response) {
			if (!TextUtils.isEmpty(response)) {
				// 调用 User#parse 将JSON串解析成User对象
				User user = User.parse(response);
				if (user != null) {
					saveNickName(user.screen_name);
					if(!mIsLogin)
						return;
					ShareListenerManager.getInstance().onShareComplete(
							response, null);
				} else {
					RuntimeException e = new RuntimeException(response);
					ShareListenerManager.getInstance().onShareError(e);
				}
			}
		}

		@Override
		public void onWeiboException(WeiboException e) {
			ShareListenerManager.getInstance().onShareError(e);
		}
	};

	/**
	 * 保存新浪微博用户信息
	 * 
	 * @param nick
	 */
	private void saveNickName(String nick) {
		try {
			PreferenceUtil pUtil = PreferenceUtil.getInstance(mContext);
			pUtil.saveString(Constants.SINA_NAME, nick);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 保存新浪微博用户信息
	 * 
	 */
	public String getNickName() {
		String name = "";
		try {
			PreferenceUtil pUtil = PreferenceUtil.getInstance(mContext);
			name = pUtil.getString(Constants.SINA_NAME, "");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return name;
	}
	
    private void getUsrInfo(){
    	
    	// 获取当前已保存过的 Token
        mAccessToken = AccessTokenKeeper.readAccessToken(mContext);
        // 获取用户信息接口
        mUsersAPI = new UsersAPI(mContext, ShareConfig.getSinaAppKey(), mAccessToken);
        String id = mAccessToken.getUid();
        if (TextUtils.isEmpty(id)) {
			return;
		}
        long uid = Long.parseLong(id);
        mUsersAPI.show(uid, mListener);
    }

	private void share() {
		if (mShareData == null) {
			return;
		}
		if (mWeiboShareAPI == null)
			mWeiboShareAPI = WeiboShareSDK.createWeiboAPI(mContext,
					ShareConfig.getSinaAppKey());
		mWeiboShareAPI.registerApp();

		isInstalledWeibo = mWeiboShareAPI.isWeiboAppInstalled();
		supportApiLevel = mWeiboShareAPI.getWeiboAppSupportAPI();

		if (mShareData.getBitmap() == null
				&& TextUtils.isEmpty(mShareData.getPicUrl())) {
			sendMultiMessage(true, false, true, false, false, false, null);
		} else {
			getImageObj();
		}
	}

	/**
	 * 
	 * @return 文本消息对象。
	 */
	private TextObject getTextObj() {
		TextObject textObject = new TextObject();
		StringBuilder sb = new StringBuilder();
		String str = mShareData.getContent();
		if (str == null)
			str = "";
		sb.append(str).append(mShareData.getTargetUrl());
		textObject.text = sb.toString();
		return textObject;
	}
	
	/**
	 * 创建网页消息对象
	 * @return
	 *//*
	private  WebpageObject getWebpageObj(){
		WebpageObject mediaObject = new WebpageObject();
		mediaObject.actionUrl=mShareData.getTargetUrl();
		return mediaObject;
	}*/

	/**
	 * 创建多媒体（网页）消息对象。
	 *
	 * @return 多媒体（网页）消息对象。
	 */
	private WebpageObject getWebpageObj(Bitmap bmp) {
		WebpageObject mediaObject = new WebpageObject();
		mediaObject.identify = Utility.generateGUID();
		mediaObject.title = mShareData.getTitle();
		mediaObject.description = mShareData.getContent();

		// 设置 Bitmap 类型的图片到视频对象里
		mediaObject.setThumbImage(bmp);
		mediaObject.actionUrl = mShareData.getTargetUrl();
		mediaObject.defaultText = mShareData.getContent();
		return mediaObject;
	}

	/**
	 * 创建图片消息对象。
	 * 
	 * @return 图片消息对象。
	 */
	@SuppressWarnings("deprecation")
	private void getImageObj() {
		final Message msg = mHandler.obtainMessage(0);
		if (mShareData.getBitmap() != null) {
			msg.obj = new BitmapDrawable(mShareData.getBitmap());
			mHandler.sendMessage(msg);
		} else if (!TextUtils.isEmpty(mShareData.getPicUrl())) {
			String url = mShareData.getPicUrl();
			if (url.startsWith("http")) {
				this.showLoadingDialog();
				Response.Listener<Bitmap> l = new Response.Listener<Bitmap>() {
					@Override
					public void onResponse(Bitmap response) {
						msg.obj = new BitmapDrawable(response);
						mHandler.sendMessage(msg);
					}
				};
				Response.ErrorListener e = new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						Resources res = mContext.getResources();
						Bitmap bmp = BitmapFactory.decodeResource(res, R.drawable.share_default);
						msg.obj = new BitmapDrawable(bmp);
						mHandler.sendMessage(msg);
					}
				};
				BitmapRequest req = new BitmapRequest(url, l, e);
				CommonUtil.getInstance(mContext)
						.sendRequest(req, mContext.getClass().getName());
			} else {
				if (url.startsWith("file://"))
					url = url.substring("file://".length());
				try {
					BitmapFactory.Options op = new BitmapFactory.Options();
					op.inPreferredConfig = Bitmap.Config.RGB_565;
					Bitmap bit = BitmapFactory.decodeFile(url, op);
					msg.obj = new BitmapDrawable(bit);
				} catch (Exception e) {
					e.printStackTrace();
					msg.obj = null;
				} catch (OutOfMemoryError e) {
					e.printStackTrace();
					msg.obj = null;
				}
				mHandler.sendMessage(msg);
			}
		} else {
			msg.obj = null;
			mHandler.sendMessage(msg);
		}
	}

	/**
	 * 第三方应用发送请求消息到微博，唤起微博分享界面。 注意：当
	 * {@link IWeiboShareAPI#getWeiboAppSupportAPI()} >= 10351 时，支持同时分享多条消息，
	 * 同时可以分享文本、图片以及其它媒体资源（网页、音乐、视频、声音中的一种）。
	 * 
	 * @param hasText
	 *            分享的内容是否有文本
	 * @param hasImage
	 *            分享的内容是否有图片
	 * @param hasWebpage
	 *            分享的内容是否有网页
	 * @param hasMusic
	 *            分享的内容是否有音乐
	 * @param hasVideo
	 *            分享的内容是否有视频
	 * @param hasVoice
	 *            分享的内容是否有声音
	 */
	private void sendMultiMessage(boolean hasText, boolean hasImage,
			boolean hasWebpage, boolean hasMusic, boolean hasVideo,
			boolean hasVoice, ImageObject imageObj) {

		// 1. 初始化微博的分享消息
		WeiboMultiMessage weiboMessage = new WeiboMultiMessage();
		if (hasText) {
			weiboMessage.textObject = getTextObj();
		}
		if (hasImage) {
			weiboMessage.imageObject = imageObj;
		}

		// 用户可以分享其它媒体资源（网页、音乐、视频、声音中的一种）
//		if (hasWebpage) {
//			Bitmap bmp = BitmapFactory.decodeByteArray(imageObj.imageData, 0, imageObj.imageData.length);
//			weiboMessage.mediaObject = getWebpageObj(bmp);
//		}
		// if (hasMusic) {
		// weiboMessage.mediaObject = getMusicObj();
		// }
		// if (hasVideo) {
		// weiboMessage.mediaObject = getVideoObj();
		// }
		// if (hasVoice) {
		// weiboMessage.mediaObject = getVoiceObj();
		// }

		// 2. 初始化从第三方到微博的消息请求
		SendMultiMessageToWeiboRequest request = new SendMultiMessageToWeiboRequest();
		// 用transaction唯一标识一个请求
		request.transaction = String.valueOf(System.currentTimeMillis());
		request.multiMessage = weiboMessage;

		if (isInstalledWeibo && supportApiLevel >= 10351) {
			mWeiboShareAPI.sendRequest(mContext, request);
		} else {
			// 3. 发送请求消息到微博，唤起微博分享界面
			AuthInfo authInfo = new AuthInfo(mContext,
					ShareConfig.getSinaAppKey(),
					ShareConfig.getSinaRedirectUrl(),
					ShareConfig.getSinaScope());
			Oauth2AccessToken accessToken = AccessTokenKeeper
					.readAccessToken(mContext.getApplicationContext());
			String token = "";
			if (accessToken != null) {
				token = accessToken.getToken();
 			}
			mWeiboShareAPI.sendRequest(mContext, request, authInfo, token,
					new WeiboAuthListener() {

						@Override
						public void onWeiboException(WeiboException arg0) {
						}

						@Override
						public void onComplete(Bundle bundle) {
							// TODO Auto-generated method stub
							Oauth2AccessToken newToken = Oauth2AccessToken
									.parseAccessToken(bundle);
							AccessTokenKeeper.writeAccessToken(
									mContext.getApplicationContext(), newToken);
						}

						@Override
						public void onCancel() {
						}
					});
		}

	}

	@SuppressWarnings("deprecation")
	private void showLoadingDialog() {
		if (mDialog == null) {
			mDialog = new Dialog(mContext, R.style.ContentOverlay);
			View v = View.inflate(mContext, R.layout.dialog_share, null);

			WindowManager wm = (WindowManager) mContext
					.getSystemService(Context.WINDOW_SERVICE);
			LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(wm
					.getDefaultDisplay().getWidth(), wm.getDefaultDisplay()
					.getHeight());

			mDialog.setContentView(v, lp);
		}
		if (!mDialog.isShowing())
			mDialog.show();
	}

	private void dismissLoadingDialog() {
		if (mDialog != null) {
			mDialog.dismiss();
		}
	}

	/**
	 * 注销新浪微博
	 * 
	 * @param shareData
	 * @param shareListener
	 */
	public void logoutWB(ShareData shareData, ShareListener shareListener) {
		if (mContext == null || shareData == null || shareListener == null) {
			return;
		}
		mShareData = shareData;
		ShareListenerManager.getInstance().setDataAndListener(shareData,
				shareListener);
		ShareListenerManager.getInstance().onShareStart();

		if (!isAuthorized()) {
			shareListener.onShareComplete("", null);
			return;
		} else {
			logout();
		}
	}

	private void logout() {
		mAccessToken = AccessTokenKeeper.readAccessToken(mContext);
		if (mAccessToken != null && mAccessToken.isSessionValid()) {
			if (mLogoutRequestListener == null)
				mLogoutRequestListener = new LogOutRequestListener();
			new LogoutAPI(mContext, ShareConfig.getSinaAppKey(), mAccessToken)
					.logout(mLogoutRequestListener);
		}
	}

	private void getBitmapDrawable(BitmapDrawable bitmapDrawable) {
		this.dismissLoadingDialog();
		if (bitmapDrawable != null) {
			sendMultiMessage(true, true, true, false, false, false,
					getImageObj(bitmapDrawable));
		} else {
			sendMultiMessage(true, false, true, false, false, false, null);
		}
	}

	/**
	 * 创建图片消息对象。
	 *
	 * @return 图片消息对象。
	 */
	private ImageObject getImageObj(BitmapDrawable drawable) {
		if(drawable == null)
			return null;
		ImageObject imageObject = new ImageObject();
		imageObject.setImageObject(drawable.getBitmap());
		return imageObject;
	}

	private static class MyHandler extends Handler {
		private final WeakReference<SinaShareHandle> mFragmentView;

		MyHandler(SinaShareHandle view) {
			this.mFragmentView = new WeakReference<SinaShareHandle>(view);
		}

		@Override
		public void handleMessage(Message msg) {
			SinaShareHandle service = mFragmentView.get();
			if (service != null) {
				super.handleMessage(msg);
				try {
					switch (msg.what) {
					case 0:
						service.getBitmapDrawable((BitmapDrawable) msg.obj);
						break;
					}
				} catch (Throwable e) {
					LogUtil.e(e.toString());
				}
			}
		}
	}

	/**
	 * 注销按钮的监听器，接收注销处理结果。（API请求结果的监听器）
	 */
	private class LogOutRequestListener implements RequestListener {
		@Override
		public void onComplete(String response) {
			if (TextUtils.isEmpty(response)) {
				ShareListenerManager.getInstance().onShareError(
						new WeiboException("get sina token fail"));
			} else {
				JSONObject object;
				try {
					object = new JSONObject(response);
					boolean result = object.optBoolean("result", true);
					if (result) {
						AccessTokenKeeper.clear(mContext);
						mAccessToken = null;
						resetAnthData();
						ShareListenerManager.getInstance().onShareComplete(
								response, null);
					}
				} catch (JSONException e) {
					e.printStackTrace();
					ShareListenerManager.getInstance().onShareError(
							new WeiboException("get sina token fail"));
				}
			}
		}

		@Override
		public void onWeiboException(WeiboException e) {
			ShareListenerManager.getInstance().onShareError(e);
		}
	}

	public void resetAnthData() {
		try {
			PreferenceUtil pUtil = PreferenceUtil.getInstance(mContext);
			pUtil.saveString(Constants.SINA_ACCESS_TOKEN, "");
			pUtil.saveLong(Constants.SINA_EXPIRES_IN, 0);
			pUtil.saveString(Constants.SINA_UID, "");
			pUtil.saveString(Constants.SINA_NAME, "");
			pUtil.saveLong(Constants.SINA_REMIND_IN, 0);
			AccessTokenKeeper.clear(mContext);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 微博登录
	 * 
	 * @param shareData
	 * @param shareListener
	 */
	public void loginWB(ShareData shareData, ShareListener shareListener) {
		if (mContext == null || shareData == null || shareListener == null) {
			return;
		}
		mShareData = shareData;
		ShareListenerManager.getInstance().setDataAndListener(shareData,
				shareListener);
		ShareListenerManager.getInstance().onShareStart();
		if (!isAuthorized()) {
			authorize(true, false);
		} else {
			mIsLogin = true;
			getUsrInfo();
		}
	}
}
