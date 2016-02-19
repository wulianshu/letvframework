package com.letv.framework.base;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.android.volley.Request;
import com.bumptech.glide.Glide;
import com.letv.framework.interfaces.ErrorView;
import com.letv.framework.interfaces.INetWorkObServe;
import com.letv.framework.receiver.NetWorkStateReceiver;
import com.letv.framework.util.CommonUtil;
import com.letv.framework.util.MyBitmapImageViewTarget;

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;

public abstract class BaseActivity extends FragmentActivity implements INetWorkObServe {
	
	private SparseArray<View> mErr = new SparseArray<>();
	protected Handler mHandler;
	protected NetWorkStateReceiver mReceiver;
	
	@Override
	public void onCreate(Bundle bundle){
		super.onCreate(bundle);
		mHandler = new MyHandler(this);
		mReceiver = new NetWorkStateReceiver(this, this);
		mReceiver.regist();
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {

	}

	public void sendRequest(Request<?> req){
		req.setTag(this);
		CommonUtil.getInstance(this).sendRequest(req);
	}

	public void loadImage(String url, ImageView image, int defaultId){
		Glide.with(this).
				load(url).
				asBitmap().centerCrop().
				placeholder(defaultId).into(new MyBitmapImageViewTarget(image));
	}

	public void showErrorView(RelativeLayout root, ErrorView view){
		if(root == null || view == null || view.getView() == null)
			return;
		View tmp = mErr.get(root.hashCode(), null);
		if(tmp != null){
			root.removeView(tmp);
		}
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.MATCH_PARENT,
				RelativeLayout.LayoutParams.MATCH_PARENT);
		root.addView(view.getView(), params);
		mErr.append(root.hashCode(), view.getView());
	}
	
	public void hideErrorView(RelativeLayout root){
		if(root == null)
			return;
		View tmp = mErr.get(root.hashCode(), null);
		if(tmp != null){
			root.removeView(tmp);
			mErr.remove(root.hashCode());
		}			
	}
	
	@Override
	public void onDestroy(){
		super.onDestroy();
		if(mReceiver != null)
			mReceiver.unRegist();
		fixInputMethodManagerLeak();
		CommonUtil.getInstance(this).getRequestQueue().cancelAll(this);
		View v = this.getWindow().getDecorView();
		if(v instanceof ViewGroup)
			((ViewGroup)v).removeAllViews();
	}
	
	private void fixInputMethodManagerLeak() {
        try {
            // 对 mCurRootView mServedView mNextServedView 进行置空...
            InputMethodManager imm = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm == null) {
                return;
            }// author:sodino mail:sodino@qq.com

            Object obj_get = null;
            Field f_mCurRootView = imm.getClass().getDeclaredField("mCurRootView");
            Field f_mServedView = imm.getClass().getDeclaredField("mServedView");
            Field f_mNextServedView = imm.getClass().getDeclaredField("mNextServedView");

            if (f_mCurRootView.isAccessible() == false) {
                f_mCurRootView.setAccessible(true);
            }
            obj_get = f_mCurRootView.get(imm);
            if (obj_get != null) { // 不为null则置为空
                f_mCurRootView.set(imm, null);
            }

            if (f_mServedView.isAccessible() == false) {
                f_mServedView.setAccessible(true);
            }
            obj_get = f_mServedView.get(imm);
            if (obj_get != null) { // 不为null则置为空
                f_mServedView.set(imm, null);
            }

            if (f_mNextServedView.isAccessible() == false) {
                f_mNextServedView.setAccessible(true);
            }
            obj_get = f_mNextServedView.get(imm);
            if (obj_get != null) { // 不为null则置为空
                f_mNextServedView.set(imm, null);
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }
	
	protected abstract void handleInfo(Message msg);
	
	protected static class MyHandler extends Handler {
        private final WeakReference<BaseActivity> mFragmentView;

        MyHandler(BaseActivity view) {
            this.mFragmentView = new WeakReference<BaseActivity>(view);
        }

        @Override
        public void handleMessage(Message msg) {
        	BaseActivity service = mFragmentView.get();
            if (service != null) {
                try {
                    super.handleMessage(msg);
                    service.handleInfo(msg);
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
