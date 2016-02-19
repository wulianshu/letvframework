package com.letv.framework.ui.pulltorefresh;

import android.view.View;
import android.widget.RelativeLayout;

/**
 * Created by liuzhuo on 2016/2/5.
 */
public interface IEmptyView {
    View getEmptyView();

    void showEmptyView(RelativeLayout rl);

    void hideEmptyView(RelativeLayout rl);
}
