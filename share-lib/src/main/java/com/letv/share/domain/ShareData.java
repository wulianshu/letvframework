package com.letv.share.domain;

import android.graphics.Bitmap;
import android.text.TextUtils;

import java.io.Serializable;

public class ShareData implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 分享平台
	 */
	public static final String SHARE_PLATFORM_SINA_WEIBO = "sina_weibo"; // 新浪微博
	public static final String SHARE_PLATFORM_QQ_FRIEND = "qq_friend"; // qq好友
	public static final String SHARE_PLATFORM_QQ_ZONE = "qq_zone"; // qq空间
	public static final String SHARE_PLATFORM_WEIXIN_FRIEND = "weixin_friend"; // 微信好友
	public static final String SHARE_PLATFORM_WEIXIN_MOMENTS = "weixin_moments"; // 微信朋友圈
	public static final String SHARE_PLATFORM_SYSTEM = "system"; // 系统

	/**
	 * 微信分享内容type
	 */
	public static final int WX_TYPE_TEXT = 0;
	public static final int WX_TYPE_IMAGE = 1;
	public static final int WX_TYPE_WEBPAGE = 2;

	private String platform; // 分享平台
	private String title; // 分享标题
	private String content; // 分享内容
	private String targetUrl; // 分享的目标url，即点击跳转的目标连接，分享到QQ平台时不能为空
	private String picUrl; // 分享的图片url，可以使本地图片路径，也可以是网络图片路径
	private String lon; // 分享位置的经度
	private String lat; // 分享位置的纬度
	private int wxType ; // 微信分享类型,0:text;1:image;2:webpage
	private Bitmap bitmap; // 分享图
	private boolean isShortUrl; //是否是短链接

	public boolean isShortUrl() {
		return isShortUrl;
	}

	public void setIsNewUrl(boolean isNewUrl) {
		this.isShortUrl = isNewUrl;
	}

	public void setBitmap(Bitmap bm) {
		bitmap = bm;
	}

	public Bitmap getBitmap() {
		return bitmap;
	}

	public String getPlatform() {
		return platform;
	}

	public void setPlatform(String platform) {
		this.platform = platform;
	}

	public String getTitle() {
		return title;
	}

    public void setTitle(String title) {
		this.title = title;
	}

	public String getContent() {
		if (SHARE_PLATFORM_SINA_WEIBO.equals(getPlatform())
				|| SHARE_PLATFORM_QQ_FRIEND.equals(getPlatform())) {
			return subStringByLenWithEndFlag(content, 140, "...");
		}
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getTargetUrl() {
		return targetUrl;
	}

	public void setTargetUrl(String targetUrl) {
		this.targetUrl = targetUrl;
	}

	public String getPicUrl() {
		return picUrl;
	}

	public void setPicUrl(String picUrl) {
		this.picUrl = picUrl;
	}

	public String getLon() {
		return lon;
	}

	public void setLon(String lon) {
		this.lon = lon;
	}

	public String getLat() {
		return lat;
	}

	public void setLat(String lat) {
		this.lat = lat;
	}

	public int getWxType() {
		return wxType;
	}

	public void setWxType(int wxType) {
		this.wxType = wxType;
	}

	/**
	 * 通过长度限制截取字符串，截取完以后会在字符串末尾加上endFlag
	 * 
	 * @param targetStr
	 *            需要截取的字符串
	 * @param lenLimit
	 *            截取的长度
	 * @param endFlag
	 *            末尾字符串
	 * @return
	 */
	private String subStringByLenWithEndFlag(String targetStr, int lenLimit,
			String endFlag) {
		String newStr = "";

		if (!TextUtils.isEmpty(targetStr) && lenLimit > 0) {
			if (targetStr.length() > lenLimit) {
				newStr = targetStr.substring(0, lenLimit) + endFlag;
			} else {
				newStr = targetStr;
			}
		}

		return newStr;
	}

	@Override
	public String toString() {
		return "ShareData{" + "platform='" + platform + '\'' + ", title='"
				+ title + '\'' + ", content='" + content + '\''
				+ ", targetUrl='" + targetUrl + '\'' + ", picUrl='" + picUrl
				+ '\'' + ", lon='" + lon + '\'' + ", lat='" + lat + '\''
				+ ", wxType=" + wxType + ", bitmap=" + bitmap + '}';
	}
}
