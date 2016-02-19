package com.letv.framework.ui.pulltorefresh;

/**
 * Created by liuzhuo on 2016/2/3.
 */
public interface PullToRefreshListener {
    void onPullDown();

    void onPullUp();

    boolean isFinished();
}
