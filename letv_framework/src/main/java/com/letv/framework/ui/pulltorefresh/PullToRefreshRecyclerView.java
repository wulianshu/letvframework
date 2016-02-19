package com.letv.framework.ui.pulltorefresh;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.letv.framework.R;
import com.letv.framework.ui.MoreListView;
import com.letv.framework.ui.WrapRecyclerView;

import java.util.concurrent.atomic.AtomicBoolean;

import in.srain.cube.views.ptr.PtrClassicFrameLayout;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;

/**
 * Created by liuzhuo on 2016/2/4.
 */
public class PullToRefreshRecyclerView extends RelativeLayout implements RefreshBase<WrapRecyclerView> {

    private PullToRefreshListener mListener;
    private WrapRecyclerView mRecyclerView;
    private IEmptyView mEmptyView;
    private PtrClassicFrameLayout mPtrFrame;
    private PtrHandler mPtrHandler;
    private AtomicBoolean isLoading = new AtomicBoolean(false);

    public PullToRefreshRecyclerView(Context context) {
        super(context);
        init();
    }

    public PullToRefreshRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public PullToRefreshRecyclerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

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
        mRecyclerView = initRecyclerView();
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT);
        fl.addView(mRecyclerView, params);

        // show empty view
        mPtrFrame.setVisibility(View.INVISIBLE);
        mEmptyView.showEmptyView(this);

        mPtrFrame.setLastUpdateTimeRelateObject(this);
        mPtrHandler = new PtrHandler() {
            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
                return PtrDefaultHandler.checkContentCanBePulledDown(frame, mRecyclerView, header);
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

    private WrapRecyclerView initRecyclerView(){
        WrapRecyclerView view = new WrapRecyclerView(getContext(), getBottomLoadingView());
        view.setOnLoadListener(new MoreListView.OnLoadListener() {
            @Override
            public void onLoad() {
                if(mListener != null && !isLoading.get()) {
                    isLoading.set(true);
                    mListener.onPullUp();
                }
            }

            @Override
            public boolean isFinished() {
                if(mListener != null)
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
        });
        return view;
    }

    @Override
    public void setPullToRefreshListener(PullToRefreshListener handler) {
        mListener = handler;
    }

    @Override
    public void refreshComplete() {
        mRecyclerView.onLoadComplete();
        mPtrFrame.refreshComplete();
        isLoading.set(false);
    }

    @Override
    public WrapRecyclerView getRefreshableView() {
        return mRecyclerView;
    }

    @Override
    public ILoadingView getBottomLoadingView() {
        return new LoadingView(getContext());
    }

    @Override
    public IEmptyView getEmptyView() {
        return new EmptyView(getContext());
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
}
