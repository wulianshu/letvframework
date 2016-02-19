package com.letv.framework.ui.pulltorefresh;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RelativeLayout;

import com.letv.framework.R;

import java.util.concurrent.atomic.AtomicBoolean;

import in.srain.cube.views.ptr.PtrClassicFrameLayout;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;

public class PullToRefreshWebView extends RelativeLayout implements RefreshBase<WebView> {

    private PtrClassicFrameLayout mPtrFrame;
    private WebView mWebView;
    private PullToRefreshListener mListener;
    private AtomicBoolean isLoading = new AtomicBoolean(false);

    public PullToRefreshWebView(Context context) {
        super(context);
    }

    public PullToRefreshWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PullToRefreshWebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private void init(){
        LayoutInflater.from(getContext()).inflate(R.layout.header_with_webview, this);
        mWebView = (WebView) this.findViewById(R.id.rotate_header_web_view);
        mWebView.setWebViewClient(new WebViewClient() {

            @Override
            public void onPageStarted(WebView view, String url, Bitmap bitmap){
                isLoading.set(true);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                isLoading.set(false);
                mPtrFrame.refreshComplete();
            }
        });
        mPtrFrame = (PtrClassicFrameLayout) this.findViewById(R.id.rotate_header_web_view_frame);
        mPtrFrame.setLastUpdateTimeRelateObject(this);
        mPtrFrame.setPtrHandler(new PtrHandler() {
            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
                return PtrDefaultHandler.checkContentCanBePulledDown(frame, mWebView, header);
            }

            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                updateData();
            }
        });
        // the following are default settings
        mPtrFrame.setResistance(1.7f);
        mPtrFrame.setRatioOfHeaderHeightToRefresh(1.2f);
        mPtrFrame.setDurationToClose(200);
        mPtrFrame.setDurationToCloseHeader(1000);
        // default is false
        mPtrFrame.setPullToRefresh(false);
        // default is true
        mPtrFrame.setKeepHeaderWhenRefresh(true);
//        mPtrFrame.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                mPtrFrame.autoRefresh();
//            }
//        }, 100);
    }

    private void updateData() {
        if(mListener != null && !isLoading.get()) {
            isLoading.set(true);
            mListener.onPullDown();
        }
    }

    @Override
    public void setPullToRefreshListener(PullToRefreshListener handler) {
        mListener = handler;
    }

    @Override
    public void refreshComplete() {
        isLoading.set(false);
        mPtrFrame.refreshComplete();
    }

    @Override
    public WebView getRefreshableView() {
        return mWebView;
    }

    @Override
    public ILoadingView getBottomLoadingView() {
        return null;
    }

    @Override
    public IEmptyView getEmptyView() {
        return null;
    }
}