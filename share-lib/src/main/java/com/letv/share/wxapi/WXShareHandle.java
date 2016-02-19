package com.letv.share.wxapi;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.text.TextUtils;

import com.android.volley.toolbox.RequestFuture;
import com.letv.framework.request.BitmapRequest;
import com.letv.framework.util.BitmapUtil;
import com.letv.framework.util.CommonUtil;
import com.letv.framework.util.LogUtil;
import com.letv.framework.util.UiUtil;
import com.letv.share.R;
import com.letv.share.ShareConfig;
import com.letv.share.ShareListener;
import com.letv.share.ShareListenerManager;
import com.letv.share.domain.ShareData;
import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXImageObject;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.modelmsg.WXTextObject;
import com.tencent.mm.sdk.modelmsg.WXWebpageObject;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import org.apache.http.impl.client.DefaultHttpClient;

import java.io.ByteArrayOutputStream;
import java.io.File;

public class WXShareHandle {

	public static final int TIMELINE_SUPPORTED_VERSION = 0x21020001;
	private static final int THUMB_SIZE = 100;
	
	private Context mContext;
	private ShareData mShareData;
	private IWXAPI mApi;
	private AsynShareWX mAsynShareWX;
	
	/**
	 * @param context			上下文对象，不能为null
	 */
	public WXShareHandle(Context context){
		mContext = context;
		mApi = WXAPIFactory.createWXAPI(mContext, ShareConfig.getWxAppId(), true);
		mApi.registerApp(ShareConfig.getWxAppId());
	}
	
	/**
	 * 分享到微信好友
	 * @param shareData
	 * @param shareListener
	 */
	public void shareToWXFriendHandle(ShareData shareData, ShareListener shareListener) {
		if (mContext == null || shareData == null) {
			return;
		}
		
		int wxSdkVersion = mApi.getWXAppSupportAPI();
		if(!mApi.isWXAppInstalled()){
            UiUtil.showToast(mContext, "还没有安装微信拉，请先安装~");
			return;
		}
		if (wxSdkVersion < TIMELINE_SUPPORTED_VERSION) {
            UiUtil.showToast(mContext, "微信升级咯，亲要更新了啦~");
			return;
		} 
		
		mShareData = shareData;
		ShareListenerManager.getInstance().setDataAndListener(shareData, shareListener);
		ShareListenerManager.getInstance().onShareStart();
		
		shareToWXFriend();
	}
	
	/**
	 * 分享到微信朋友圈
	 * @param shareData
	 * @param shareListener
	 */
	public void shareToWXMomentsHandle(ShareData shareData, ShareListener shareListener){
		if (mContext == null || shareData == null) {
			return;
		}
		
		int wxSdkVersion = mApi.getWXAppSupportAPI();
		if(!mApi.isWXAppInstalled()){
            UiUtil.showToast(mContext, "还没有安装微信拉，请先安装~");
			return;
		}
		if (wxSdkVersion < TIMELINE_SUPPORTED_VERSION) {
            UiUtil.showToast(mContext, "微信升级咯，亲要更新了啦~");
			return;
		} 
		
		mShareData = shareData;
		ShareListenerManager.getInstance().setDataAndListener(shareData, shareListener);
		ShareListenerManager.getInstance().onShareStart();
		
		shareToWXMoments();
	}
	
	private void shareToWXFriend() {
		switch (mShareData.getWxType()) {
		case ShareData.WX_TYPE_TEXT:
			sendTextToWX(SendMessageToWX.Req.WXSceneSession);
			break;
		case ShareData.WX_TYPE_IMAGE:
			shareImageToWXHandle(SendMessageToWX.Req.WXSceneSession);
			break;
		case ShareData.WX_TYPE_WEBPAGE:
			shareWebpageToWXHandle(SendMessageToWX.Req.WXSceneSession);
			break;
		}
	}

	/**
	 * 分享文本到微信平台
	 */
	private void shareToWXMoments() {
		switch (mShareData.getWxType()) {
		case ShareData.WX_TYPE_TEXT:
			sendTextToWX(SendMessageToWX.Req.WXSceneTimeline);
			break;
		case ShareData.WX_TYPE_IMAGE:
			shareImageToWXHandle(SendMessageToWX.Req.WXSceneTimeline);
			break;
		case ShareData.WX_TYPE_WEBPAGE:
			shareWebpageToWXHandle(SendMessageToWX.Req.WXSceneTimeline);
			break;
		}
	}
	
	private void shareImageToWXHandle(int wxType){
		if(mAsynShareWX != null){
			mAsynShareWX = null;
		}
		mAsynShareWX = new AsynShareWX(wxType);
		mAsynShareWX.execute();
	}
	
	private void shareWebpageToWXHandle(int wxType){
		if(mShareData.getBitmap() != null){
			sendWebPageToWX(wxType, mShareData.getBitmap());
		}else{
			if(mAsynShareWX != null){
				mAsynShareWX = null;
			}
			mAsynShareWX = new AsynShareWX(wxType);
			mAsynShareWX.execute();
		}
	}
	
	private void sendTextToWX(int wxType){
		try{
			// 初始化一个WXTextObject对象
			WXTextObject textObj = new WXTextObject();
			textObj.text = mShareData.getContent();
	
			// 用WXTextObject对象初始化一个WXMediaMessage对象
			WXMediaMessage msg = new WXMediaMessage();
			msg.mediaObject = textObj;
			// 发送文本类型的消息时，title字段不起作用
			// msg.title = "Will be ignored";
			msg.description = mShareData.getContent();
	
			// 构造一个Req
			SendMessageToWX.Req req = new SendMessageToWX.Req();
			req.transaction = buildTransaction("text"); // transaction字段用于唯一标识一个请求
			req.message = msg;
			req.scene = wxType;
			
			// 调用api接口发送数据到微信
			mApi.sendReq(req);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	private void sendImageToWX(int wXType, Bitmap thumbBmp) {
		try{
    		WXImageObject imageObj = new WXImageObject();
    		if(mShareData.getPicUrl().startsWith("http")) {
    			imageObj.imageUrl = mShareData.getPicUrl();
    		} else{
    			imageObj.setImagePath(mShareData.getPicUrl());
    		}
			
			WXMediaMessage msg = new WXMediaMessage();
			msg.mediaObject = imageObj;
			msg.title = mShareData.getTitle();
			msg.description = mShareData.getContent();
			msg.thumbData = bmpToByteArray(thumbBmp, false);

			SendMessageToWX.Req req = new SendMessageToWX.Req();
			req.transaction = buildTransaction("img");
			req.message = msg;
			req.scene = wXType;
			mApi.sendReq(req);
    	} catch(Exception e) {
			e.printStackTrace();
		}
	}

	private void sendWebPageToWX(int wXType, Bitmap thumbBmp) {
		try{
	    	WXWebpageObject webpage = new WXWebpageObject();
	    	webpage.webpageUrl = mShareData.getTargetUrl();
			WXMediaMessage msg = new WXMediaMessage(webpage);
			msg.title = mShareData.getTitle();
			msg.description = mShareData.getContent();
			
			msg.thumbData = bmpToByteArray(thumbBmp, false);

			SendMessageToWX.Req req = new SendMessageToWX.Req();
			req.transaction = buildTransaction("webpage");
			req.message = msg;
			req.scene = wXType;
			mApi.sendReq(req);
    	} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	class AsynShareWX extends AsyncTask<Integer, Integer, Boolean> {
		private int mWXType ;
		private Bitmap mThumbBmp;
		private DefaultHttpClient httpclient = null;
		
		AsynShareWX(int WXType){
			mWXType = WXType;
		}
		
		@Override
		protected Boolean doInBackground(Integer... params) {
			try {
				String picUrl = mShareData.getPicUrl();
				if(!TextUtils.isEmpty(picUrl)){
					if(picUrl.startsWith("http")) {
						try{
							RequestFuture<Bitmap> future = RequestFuture.newFuture();
							BitmapRequest req = new BitmapRequest(picUrl, future, future);
							CommonUtil.getInstance(mContext).sendRequest(req);
							Bitmap bmp = future.get();
							mThumbBmp= Bitmap.createScaledBitmap(bmp, THUMB_SIZE, THUMB_SIZE, true);
							bmp.recycle();
							return true;
						}catch(Throwable e){
							LogUtil.e(e.toString());
							Bitmap bmp = BitmapUtil.LoadBackgroundResource(mContext, R.drawable.share_default).getBitmap();
							mThumbBmp= Bitmap.createScaledBitmap(bmp, THUMB_SIZE, THUMB_SIZE, true);
							bmp.recycle();
							return true;
						}
					}else{
						File file = new File(picUrl);
						if (file.exists()) {	
							Bitmap bmp = BitmapFactory.decodeFile(picUrl);
							mThumbBmp = Bitmap.createScaledBitmap(bmp, THUMB_SIZE, THUMB_SIZE, true);
							bmp.recycle();
							return true;
						} 
					}
				}else{
					Bitmap bmp = BitmapUtil.LoadBackgroundResource(mContext, R.drawable.share_default).getBitmap();
					mThumbBmp= Bitmap.createScaledBitmap(bmp, THUMB_SIZE, THUMB_SIZE, true);
					bmp.recycle();
					return true;
				}
				return false;
			} catch (Exception e) {
				Bitmap bmp = BitmapUtil.LoadBackgroundResource(mContext, R.drawable.share_default).getBitmap();
				mThumbBmp= Bitmap.createScaledBitmap(bmp, THUMB_SIZE, THUMB_SIZE, true);
				bmp.recycle();
				e.printStackTrace();
				return true;
			}
		}
		
		protected void onCancelled() {
			if(httpclient != null)
				httpclient.getConnectionManager().shutdown();
		}
 
		protected void onPostExecute(Boolean re) { 
			if(!re.booleanValue()){
                UiUtil.showToast(mContext, "没有找到对应图片!");
			}else{
				if(mShareData.getWxType() == ShareData.WX_TYPE_IMAGE){
					sendImageToWX(mWXType, mThumbBmp);
				} else if(mShareData.getWxType() == ShareData.WX_TYPE_WEBPAGE){
					sendWebPageToWX(mWXType, mThumbBmp);
				}
			}
		}
	}
	
	 private byte[] bmpToByteArray(final Bitmap bmp, final boolean needRecycle) {
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		bmp.compress(CompressFormat.PNG, 100, output);
		if (needRecycle) {
			bmp.recycle();
		}
		
		byte[] result = output.toByteArray();
		try {
			output.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return result;
	}
	
	private String buildTransaction(final String type) {
		return (type == null) ? String.valueOf(System.currentTimeMillis()) : type + System.currentTimeMillis();
	}
}
