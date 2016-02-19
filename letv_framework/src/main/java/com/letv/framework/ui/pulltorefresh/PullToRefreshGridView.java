package com.letv.framework.ui.pulltorefresh;

import android.content.Context;
import android.util.AttributeSet;

import com.letv.framework.ui.MoreGridView;

/**
 * Created by liuzhuo on 2016/2/4.
 */
public class PullToRefreshGridView extends PullToRefreshAbsListView {

    private MoreGridView mGridView;

    public PullToRefreshGridView(Context context) {
        super(context);
    }

    public PullToRefreshGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PullToRefreshGridView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected MoreGridView initAbsListView(ILoadingView view) {
        mGridView = new MoreGridView(getContext(), view);
        mGridView.setOnLoadListener(getLoaderListener());

        return mGridView;
    }

    @Override
    public MoreGridView getRefreshableView() {
        return mGridView;
    }

    @Override
    public void refreshComplete(){
        mGridView.onLoadComplete();
        super.refreshComplete();
    }
}
