package com.letv.framework.util;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * ui相关工具类
 */
public class UiUtil {

	/**
	 * 显示一个toast
	 * 
	 * @param msg
	 */
	public static void showToast(Context context, String msg) {
		showToast(context, msg, Toast.LENGTH_SHORT);
	}

	/**
	 * 显示一个toast
	 * 
	 * @param msg
	 */
	public static void showToast(Context context, String msg, int duration) {
		if(context == null)
			return;
		context = context.getApplicationContext();
		Toast mToast = new Toast(context);
		TextView mTextView = new TextView(context);
		mTextView.setGravity(Gravity.CENTER);
		mTextView.setTextColor(Color.WHITE);
		mTextView.setBackgroundColor(0xbb000000);
		int p = dip2px(context, 10);
		mTextView.setPadding(p, p, p, p);
		mToast.setView(mTextView);
		mTextView.setText(msg);
		mToast.setDuration(duration);
		mToast.show();
	}

	/**
	 * 显示一个toast
	 * 
	 * @param resId
	 */
	public static void showToast(Context context, int resId) {
		showToast(context, resId, Toast.LENGTH_SHORT);
	}

	/**
	 * 显示一个toast
	 * 
	 * @param resId
	 */
	public static void showToast(Context context, int resId, int duration) {
		if(context == null)
			return;
		context = context.getApplicationContext();
		String str = context.getString(resId);
		showToast(context, str, duration);
	}

	/**
	 * 根据view弹出软键盘
	 * 
	 * @param view
	 */
	public static void showSoftInput(final View view) {
		try {
			if (view != null) {
				view.requestFocus();
				view.post(new Runnable(){
					public void run(){
						InputMethodManager inputManager = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
						inputManager.showSoftInput(view, 0);
					}
				});
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 根据上下文对象隐藏软键盘
	 * 
	 * @param context
	 */
	public static void hideInput(Context context) {
		try {
			InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
			if (imm.isActive())
				imm.hideSoftInputFromWindow(((Activity) context).getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
		} catch (Exception e) {
//			e.printStackTrace();
		}
	}

	/**
	 * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
	 */
	public static int dip2px(Context context, float dpValue) {
		if (context != null) {
			final float scale = context.getResources().getDisplayMetrics().density;
			return (int) (dpValue * scale + 0.5f);
		}
		return (int) dpValue;
	}

	/**
	 * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
	 */
	public static int px2dip(Context context, float pxValue) {
		if (context != null) {
			final float scale = context.getResources().getDisplayMetrics().density;
			return (int) (pxValue / scale + 0.5f);
		}
		return (int) pxValue;
	}

	public static void destroyWebView(WebView wb){
		if(wb == null)
			return;
		if(wb.getParent() != null) {
			try {
				((ViewGroup) wb.getParent()).removeView(wb);
			}catch(Throwable e){
				e.printStackTrace();
			}
		}
		wb.removeAllViews();
		wb.destroy();
	}
}
