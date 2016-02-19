package com.letv.framework.ui.pulltorefresh;

/**
 * Created by liuzhuo on 2016/2/3.
 */
public interface RefreshBase<T> {

    void setPullToRefreshListener(PullToRefreshListener handler);

    void refreshComplete();

    T getRefreshableView();

    ILoadingView getBottomLoadingView();

    IEmptyView getEmptyView();
}
