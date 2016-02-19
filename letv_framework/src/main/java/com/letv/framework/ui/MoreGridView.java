package com.letv.framework.ui;

import android.content.Context;
import android.database.DataSetObserver;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AbsListView;
import android.widget.GridView;
import android.widget.ListAdapter;

import com.letv.framework.adapter.MyBaseAdapter;
import com.letv.framework.ui.pulltorefresh.ILoadingView;
import com.letv.framework.ui.pulltorefresh.LoadingView;
import com.letv.framework.util.UiUtil;

import java.util.HashSet;
import java.util.Iterator;

/**
 * Created by liuzhuo on 2016/2/4.
 */
public class MoreGridView extends GridViewWithHeaderAndFooter {

    private OnScrollListener mOnScrollListener;
    private HashSet<OnScrollListener> mSet = new HashSet<OnScrollListener>();
    private MoreListView.OnLoadListener mLoadListener;
    private ILoadingView mLoadingView;

    public MoreGridView(Context context) {
        super(context);
        init();
    }

    public MoreGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MoreGridView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public MoreGridView(Context context, ILoadingView view){
        super(context);
        mLoadingView = view;
        init();
    }

    @Override
    public void setOnScrollListener(OnScrollListener l) {
        if(l == null)
            return;
        mSet.add(l);
    }

    public ILoadingView getLoadingView(){
        return new LoadingView(getContext());
    }

    private void init(){
        this.setOverScrollMode(View.OVER_SCROLL_NEVER);
        this.setHorizontalFadingEdgeEnabled(false);
        this.setVerticalFadingEdgeEnabled(false);
        this.setStretchMode(GridView.STRETCH_COLUMN_WIDTH);
        int s = UiUtil.dip2px(getContext(), 5);
        this.setVerticalSpacing(s);
        this.setHorizontalSpacing(s);
        if(mLoadingView == null)
            mLoadingView = getLoadingView();
        addFooterView(mLoadingView.getLoadingView(this));

        mOnScrollListener = new OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                Iterator<OnScrollListener> it = mSet.iterator();
                while (it.hasNext()){
                    it.next().onScrollStateChanged(view, scrollState);
                }
                if(scrollState == SCROLL_STATE_IDLE)
                    updateState(false);
                else
                    updateState(true);

                if (scrollState == SCROLL_STATE_IDLE) {
                    final int lastVsbPosition = getLastVisiblePosition();

                    final int count = getCount();

                    if ((mLoadListener != null && !mLoadListener.isForbidLoad())
                            && (lastVsbPosition == (count - 1) && count > 0)) {
                        onLoad();
                    } else {
                        if (mLoadListener != null && mLoadListener.isFinished()) {
                            onLoadComplete();
                        }
                    }
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                Iterator<OnScrollListener> it = mSet.iterator();
                while (it.hasNext()){
                    it.next().onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
                }
            }
        };

        super.setOnScrollListener(mOnScrollListener);
    }

    private void updateState(boolean scrolling){
        if(getAdapter() instanceof MyBaseAdapter)
            ((MyBaseAdapter)getAdapter()).setScrolling(scrolling);
    }

    public void setOnLoadListener(MoreListView.OnLoadListener loadListener) {
        this.mLoadListener = loadListener;
    }

    public void onLoad() {
        if (mLoadListener != null) {

            if (mLoadListener.isFinished()) {
                onLoadComplete();
            } else {
                mLoadListener.onLoad();
                mLoadingView.showLoading();
            }
        }
    }

    public void onLoadComplete() {
        mLoadingView.hideLoading(mLoadListener.isFinished());
    }

    @Override
    public void setAdapter(final ListAdapter adapter){
        if(adapter != null){
            adapter.registerDataSetObserver(new DataSetObserver() {
                @Override
                public void onChanged() {
                    super.onChanged();
                    if(mLoadListener == null)
                        return;
                    if(adapter.getCount() <= 0) {
                        mLoadListener.showEmptyView(true);
                        mLoadingView.hideLoading(false);
                    }else
                        mLoadListener.showEmptyView(false);
                }
            });
            super.setAdapter(adapter);
        }
    }
}
