package com.letv.framework.ui.pulltorefresh;

import android.content.Context;
import android.graphics.Color;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Created by liuzhuo on 2016/2/5.
 */
public class EmptyView implements IEmptyView {

    private Context mContext;
    private TextView mTv;

    public EmptyView(Context context){
        mContext = context;
    }

    @Override
    public View getEmptyView() {
        mTv = new TextView(mContext);
        mTv.setTextColor(Color.BLACK);
        mTv.setGravity(Gravity.CENTER);
        mTv.setText("empty");
        mTv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);
        return mTv;
    }

    @Override
    public void showEmptyView(RelativeLayout rl) {
        if(rl == null || mTv == null)
            return;
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT);
        rl.addView(mTv, params);
    }

    @Override
    public void hideEmptyView(RelativeLayout rl) {
        if(rl == null || mTv == null)
            return;
        rl.removeView(mTv);
    }
}
