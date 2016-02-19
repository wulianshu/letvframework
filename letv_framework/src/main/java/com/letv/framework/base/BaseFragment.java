package com.letv.framework.base;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.android.volley.Request;
import com.letv.framework.interfaces.ErrorView;
import com.letv.framework.util.CommonUtil;

import java.lang.ref.WeakReference;

public abstract class BaseFragment extends Fragment {

	protected Handler mHandler;
	
	@Override
	public void onCreate(Bundle bundle){
		super.onCreate(bundle);
		mHandler = new MyHandler(this);
	}
	
	protected void showErrorView(RelativeLayout root, ErrorView view){
		if(getActivity() instanceof BaseActivity)
			((BaseActivity)getActivity()).showErrorView(root, view);
	}
	
	protected void hideErrorView(RelativeLayout root){
		if(getActivity() instanceof BaseActivity)
			((BaseActivity)getActivity()).hideErrorView(root);
	}

	public void sendRequest(Request<?> req){
		req.setTag(this);
		CommonUtil.getInstance(this.getActivity()).sendRequest(req);
	}

	public void loadImage(String url, ImageView image, int defaultId){
		if(getActivity() instanceof BaseActivity)
			((BaseActivity)getActivity()).loadImage(url, image, defaultId);
	}

	@Override
	public void onDestroy(){
		super.onDestroy();
		CommonUtil.getInstance(this.getActivity()).getRequestQueue().cancelAll(this);
	}

	protected abstract void handleInfo(Message msg);
	
	protected static class MyHandler extends Handler {
        private final WeakReference<BaseFragment> mFragmentView;

        MyHandler(BaseFragment view) {
            this.mFragmentView = new WeakReference<BaseFragment>(view);
        }

        @Override
        public void handleMessage(Message msg) {
        	BaseFragment service = mFragmentView.get();
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
