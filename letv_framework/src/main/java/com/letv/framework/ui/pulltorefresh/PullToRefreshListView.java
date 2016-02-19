package com.letv.framework.ui.pulltorefresh;

import android.content.Context;
import android.util.AttributeSet;

import com.letv.framework.ui.MoreListView;

/**
 * Created by liuzhuo on 2016/2/4.
 */
public class PullToRefreshListView extends PullToRefreshAbsListView {

    private MoreListView mListView;

    public PullToRefreshListView(Context context) {
        super(context);
    }

    public PullToRefreshListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PullToRefreshListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected MoreListView initAbsListView(ILoadingView view) {
        mListView = new MoreListView(getContext(), view);
        mListView.setOnLoadListener(getLoaderListener());
        return mListView;
    }

    @Override
    public MoreListView getRefreshableView() {
        return mListView;
    }

    @Override
    public void refreshComplete(){
        mListView.onLoadComplete();
        super.refreshComplete();
    }
}
