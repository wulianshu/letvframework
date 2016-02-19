package com.letv.framework.ui.pulltorefresh;

import android.view.View;
import android.view.ViewGroup;

/**
 * Created by liuzhuo on 2016/2/4.
 */
public interface ILoadingView {

    View getLoadingView(ViewGroup group);

    void showLoading();

    void hideLoading(boolean isFinished);
}
