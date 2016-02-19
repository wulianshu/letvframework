package com.letv.share;

import android.text.TextUtils;

/**
 * 分享配置类
 * @author xiaruri
 *
 */
public class ShareConfig {

	/**
	 * 新浪微博配置
	 */
	private static String sinaAppKey;
	private static String sinaAppSecret;
	private static String sinaRedirectUrl;
	private static String sinaScope = "email,direct_messages_read,direct_messages_write,"
			+ "friendships_groups_read,friendships_groups_write,statuses_to_me_read,"
			+ "follow_app_official_microblog";
	
	/**
	 * QQ平台配置
	 */
	private static String qqAppId;
	private static String qqAppKey;
	
	/**
	 * 微信平台配置
	 */
	private static String wxAppId;
	private static String wxAppSecret;
	
	public static String getSinaAppKey() {
		return sinaAppKey;
	}
	public static void setSinaAppKey(String sinaAppKey) {
		ShareConfig.sinaAppKey = sinaAppKey;
	}
	public static String getSinaAppSecret() {
		return sinaAppSecret;
	}
	public static void setSinaAppSecret(String sinaAppSecret) {
		ShareConfig.sinaAppSecret = sinaAppSecret;
	}
	public static String getSinaRedirectUrl() {
		if(TextUtils.isEmpty(sinaRedirectUrl)){
			return "https://api.weibo.com/oauth2/default.html";
		}
		return sinaRedirectUrl;
	}
	public static void setSinaRedirectUrl(String sinaRedirectUrl) {
		ShareConfig.sinaRedirectUrl = sinaRedirectUrl;
	}
	public static String getSinaScope() {
		return sinaScope;
	}
	public static void setSinaScope(String sinaScope) {
		ShareConfig.sinaScope = sinaScope;
	}
	public static String getQqAppId() {
		return qqAppId;
	}
	public static void setQqAppId(String qqAppId) {
		ShareConfig.qqAppId = qqAppId;
	}
	public static String getQqAppKey() {
		return qqAppKey;
	}
	public static void setQqAppKey(String qqAppKey) {
		ShareConfig.qqAppKey = qqAppKey;
	}
	public static String getWxAppId() {
		return wxAppId;
	}
	public static void setWxAppId(String wxAppId) {
		ShareConfig.wxAppId = wxAppId;
	}
	public static String getWxAppSecret() {
		return wxAppSecret;
	}
	public static void setWxAppSecret(String wxAppSecret) {
		ShareConfig.wxAppSecret = wxAppSecret;
	}
	
}
