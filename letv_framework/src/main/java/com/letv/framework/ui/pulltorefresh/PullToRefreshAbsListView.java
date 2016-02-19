package com.letv.framework.ui.pulltorefresh;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.letv.framework.R;
import com.letv.framework.ui.MoreListView;

import java.util.concurrent.atomic.AtomicBoolean;

import in.srain.cube.views.ptr.PtrClassicFrameLayout;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;

/**
 * Created by liuzhuo on 2016/2/3.
 */
public abstract class PullToRefreshAbsListView extends RelativeLayout implements RefreshBase<AbsListView> {

    private IEmptyView mEmptyView;
    private PtrClassicFrameLayout mPtrFrame;
    private AbsListView mListView;
    private PtrHandler mPtrHandler;
    protected PullToRefreshListener mListener;
    protected AtomicBoolean isLoading = new AtomicBoolean(false);
    
    public PullToRefreshAbsListView(Context context) {
        super(context);
        init();
    }

    public PullToRefreshAbsListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public PullToRefreshAbsListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @Override
    public ILoadingView getBottomLoadingView() {
        return new LoadingView(getContext());
    }

    @Override
    public IEmptyView getEmptyView() {
        return new EmptyView(getContext());
    }

    @Override
    public void setPullToRefreshListener(PullToRefreshListener listener) {
        mListener = listener;
    }

    protected abstract AbsListView initAbsListView(ILoadingView view);

    private void init(){
        LayoutInflater.from(getContext()).inflate(R.layout.header_with_list_view_and_empty_view, this);

        mEmptyView = getEmptyView();
        mPtrFrame = (PtrClassicFrameLayout) this.findViewById(R.id.list_view_with_empty_view_fragment_ptr_frame);

        mEmptyView.getEmptyView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPtrFrame.setVisibility(View.VISIBLE);
                mPtrFrame.autoRefresh();
            }
        });

        FrameLayout fl = (FrameLayout)this.findViewById(R.id.container);
        mListView = initAbsListView(getBottomLoadingView());
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT);
        fl.addView(mListView, params);

        // show empty view
        mPtrFrame.setVisibility(View.INVISIBLE);
        mEmptyView.showEmptyView(this);

        mPtrFrame.setLastUpdateTimeRelateObject(this);
        mPtrHandler = new PtrHandler() {
            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
                return PtrDefaultHandler.checkContentCanBePulledDown(frame, mListView, header);
            }

            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                if(mListener != null && !isLoading.get()) {
                    isLoading.set(true);
                    mListener.onPullDown();
                }
            }
        };
        mPtrFrame.setPtrHandler(mPtrHandler);

        // the following are default settings
        mPtrFrame.setResistance(1.7f);
        mPtrFrame.setRatioOfHeaderHeightToRefresh(1.2f);
        mPtrFrame.setDurationToClose(200);
        mPtrFrame.setDurationToCloseHeader(1000);
        // default is false
        mPtrFrame.setPullToRefresh(false);
        // default is true
        mPtrFrame.setKeepHeaderWhenRefresh(true);
    }

    @Override
    public void refreshComplete(){
        mPtrFrame.refreshComplete();
        isLoading.set(false);
    }

    protected void setEmptyView(boolean isShow) {
        if(isShow) {
            mEmptyView.showEmptyView(this);
            mPtrFrame.setVisibility(View.INVISIBLE);
        }else {
            mEmptyView.hideEmptyView(this);
            mPtrFrame.setVisibility(View.VISIBLE);
        }
    }

    protected MoreListView.OnLoadListener getLoaderListener(){
        return new MoreListView.OnLoadListener() {
            @Override
            public void onLoad() {
                if (mListener != null && !isLoading.get()) {
                    isLoading.set(true);
                    mListener.onPullUp();
                }
            }

            @Override
            public boolean isFinished() {
                if (mListener != null)
                    return mListener.isFinished();
                return false;
            }

            @Override
            public boolean isForbidLoad() {
                return isLoading.get();
            }

            @Override
            public void showEmptyView(boolean isShow) {
                setEmptyView(isShow);
            }
        };
    }
}
