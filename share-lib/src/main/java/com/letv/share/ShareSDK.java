package com.letv.share;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ResolveInfo;
import android.os.Parcelable;
import android.util.Xml;

import com.letv.share.domain.ShareData;
import com.letv.share.qqapi.QQShareHandle;
import com.letv.share.sinaapi.SinaShareHandle;
import com.letv.share.sinaapi.WeiboShareActivity;
import com.letv.share.wxapi.WXShareHandle;

import org.xmlpull.v1.XmlPullParser;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class ShareSDK {

	private static final String SHARE_CONFIG_FILE_NAME = "share-lib.xml";
	private static final String TAG_NAME_APP_ID = "AppId";
	private static final String TAG_NAME_APP_KEY = "AppKey";
	private static final String TAG_NAME_APP_SECRET = "AppSecret";
	private static final String TAG_NAME_APP_REDIRECT_URL = "RedirectUrl";

	/**
	 * 分享初始化方法，在应用启动时必须调用该初始化方法
	 * 
	 * @param context
	 *            上下文对象，不能为null
	 */
	public static void init(Context context) {
		if (context == null) {
			return;
		}
		try {
			InputStream is = context.getResources().getAssets()
					.open(SHARE_CONFIG_FILE_NAME);
			XmlPullParser parser = Xml.newPullParser();
			parser.setInput(is, "utf-8");

			for (int type = parser.getEventType(); type != XmlPullParser.END_DOCUMENT; type = parser
					.next()) {
				if (type == XmlPullParser.START_TAG) {
					if ("WechatFriend".equals(parser.getName())) {
						String appId = parser.getAttributeValue("",
								TAG_NAME_APP_ID);
						String appSecret = parser.getAttributeValue("",
								TAG_NAME_APP_SECRET);

						ShareConfig.setWxAppId(appId);
						ShareConfig.setWxAppSecret(appSecret);
					} else if ("WechatMoments".equals(parser.getName())) {
						String appId = parser.getAttributeValue("",
								TAG_NAME_APP_ID);
						String appSecret = parser.getAttributeValue("",
								TAG_NAME_APP_SECRET);

						ShareConfig.setWxAppId(appId);
						ShareConfig.setWxAppSecret(appSecret);
					} else if ("QQFriend".equals(parser.getName())) {
						String appId = parser.getAttributeValue("",
								TAG_NAME_APP_ID);
						String appKey = parser.getAttributeValue("",
								TAG_NAME_APP_KEY);

						ShareConfig.setQqAppId(appId);
						ShareConfig.setQqAppKey(appKey);
					} else if ("QQZone".equals(parser.getName())) {
						String appId = parser.getAttributeValue("",
								TAG_NAME_APP_ID);
						String appKey = parser.getAttributeValue("",
								TAG_NAME_APP_KEY);

						ShareConfig.setQqAppId(appId);
						ShareConfig.setQqAppKey(appKey);
					} else if ("SinaWeibo".equals(parser.getName())) {
						String appKey = parser.getAttributeValue("",
								TAG_NAME_APP_KEY);
						String appSecret = parser.getAttributeValue("",
								TAG_NAME_APP_SECRET);
						String redirectUrl = parser.getAttributeValue("",
								TAG_NAME_APP_REDIRECT_URL);

						ShareConfig.setSinaAppKey(appKey);
						ShareConfig.setSinaAppSecret(appSecret);
						ShareConfig.setSinaRedirectUrl(redirectUrl);
					}
				}
			}
			// 初始化微信平台信息
			WXShareHandle wxShareHandle = new WXShareHandle(context);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 分享统一调用方法
	 * 
	 * @param context
	 *            上下文对象，不能为null
	 * @param shareData
	 *            需要分享的数据，不能为null
	 * @param shareListener
	 *            分享监听器，不需要时可以为null
	 */
	public static void share(Activity context, ShareData shareData,
			ShareListener shareListener, boolean getInfo) {
		if (context == null || shareData == null) {
			return;
		}
		if (ShareData.SHARE_PLATFORM_SINA_WEIBO.equals(shareData.getPlatform())) {
			WeiboShareActivity.shareListener = shareListener;
			Intent intent = new Intent(context, WeiboShareActivity.class);
			intent.putExtra("data", shareData);
			intent.putExtra("getInfo", getInfo);
			context.startActivity(intent);
		} else if (ShareData.SHARE_PLATFORM_QQ_FRIEND.equals(shareData
				.getPlatform())) {
			QQShareHandle qqShareHandle = new QQShareHandle(context);
			qqShareHandle.shareToQQFriendHandle(shareData, shareListener);
		} else if (ShareData.SHARE_PLATFORM_QQ_ZONE.equals(shareData
				.getPlatform())) {
			QQShareHandle qqShareHandle = new QQShareHandle(context);
			qqShareHandle.shareToQQZoneHandle(shareData, shareListener);
		} else if (ShareData.SHARE_PLATFORM_WEIXIN_FRIEND.equals(shareData
				.getPlatform())) {
			WXShareHandle wxShareHandle = new WXShareHandle(context);
			wxShareHandle.shareToWXFriendHandle(shareData, shareListener);
		} else if (ShareData.SHARE_PLATFORM_WEIXIN_MOMENTS.equals(shareData
				.getPlatform())) {
			WXShareHandle wxShareHandle = new WXShareHandle(context);
			wxShareHandle.shareToWXMomentsHandle(shareData, shareListener);
		} else if (ShareData.SHARE_PLATFORM_SYSTEM.equals(shareData
				.getPlatform())) {
			shareToSystem(context, shareData);
		}
	}

	/**
	 * 分享到系统
	 * 
	 * @param context
	 * @param shareData
	 */
	private static void shareToSystem(Context context, ShareData shareData) {
		if (context == null || shareData == null) {
			return;
		}
		try {
			String content = shareData.getContent() + shareData.getTargetUrl();
			Intent intent = new Intent(Intent.ACTION_SEND);
			intent.putExtra(Intent.EXTRA_SUBJECT, shareData.getTitle()); 
			intent.putExtra(Intent.EXTRA_TEXT, content);
			intent.putExtra("sms_body", content);
			intent.setType("text/plain");
			
			List<ResolveInfo> resInfo = context.getPackageManager()
					.queryIntentActivities(intent, 0);
			if (!resInfo.isEmpty()) {
				List<Intent> targetedShareIntents = new ArrayList<Intent>();
				for (ResolveInfo info : resInfo) {
					Intent targeted = new Intent(Intent.ACTION_SEND);
					targeted.putExtra(Intent.EXTRA_SUBJECT, shareData.getTitle()); 
					targeted.putExtra(Intent.EXTRA_TEXT, content);
					targeted.putExtra("sms_body", content);
					targeted.setType("text/plain");
					
					ActivityInfo activityInfo = info.activityInfo;
					if (activityInfo.packageName.contains("tencent.mm")
							|| activityInfo.name.contains("com.sina.weibo")
							|| activityInfo.packageName
									.contains("tencent.mobileqq")) {
						continue;
					}
					targeted.setPackage(activityInfo.packageName);
					targetedShareIntents.add(targeted);
				}

				Intent chooserIntent = Intent.createChooser(
						targetedShareIntents.remove(0), "分享到");
				if (chooserIntent == null) {
					return;
				}
				chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS,
						targetedShareIntents.toArray(new Parcelable[] {}));
				context.startActivity(chooserIntent);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 注销微博
	 * 
	 * @param context
	 * @param shareData
	 * @param shareListener
	 */
	public static void logoutWB(Activity context, ShareData shareData,
			ShareListener shareListener) {
		if (context == null || shareData == null) {
			return;
		}
		SinaShareHandle handle = new SinaShareHandle(context);
		handle.logoutWB(shareData, shareListener);
	}

	/**
	 * 重置微博授权记录
	 * @param context
	 */
	public static void resetWB(Activity context){
		if (context == null) {
			return;
		}
		SinaShareHandle handle = new SinaShareHandle(context);
		handle.resetAnthData();
	}
	
	/**
	 * 微博登录
	 * 
	 * @param context
	 * @param shareData
	 * @param shareListener
	 */
	public static void loginWB(Activity context, ShareData shareData,
			ShareListener shareListener, SinaShareHandle handle) {
		if (context == null || shareData == null) {
			return;
		}
		if(handle == null)
			handle = new SinaShareHandle(context);
		handle.loginWB(shareData, shareListener);
	}
	
	public static SinaShareHandle getSinaShareHandle(Activity context){
		return new SinaShareHandle(context);
	}
}
