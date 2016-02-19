package com.letv.framework.ui.pulltorefresh;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.letv.framework.R;

/**
 * Created by liuzhuo on 2016/2/4.
 */
public class LoadingView implements ILoadingView {

    private Context mContext;
    private View mView;
    private ProgressBar moreProgressBar;
    private TextView mLoadMoreView;

    public LoadingView(Context context){
        mContext = context;
    }

    @Override
    public View getLoadingView(ViewGroup group) {
        mView = LayoutInflater.from(mContext).inflate(R.layout.more_list_footer, group, false);
        moreProgressBar = (ProgressBar) mView
                .findViewById(R.id.load_more_progress);
        mLoadMoreView = (TextView) mView.findViewById(R.id.load_more);
        mView.setVisibility(View.INVISIBLE);

        return mView;
    }

    @Override
    public void showLoading(){
        mView.setVisibility(View.VISIBLE);
        mLoadMoreView.setText(R.string.listview_footer_loading);
        moreProgressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideLoading(boolean isFinished){
        if (isFinished){
            mView.setVisibility(View.VISIBLE);
            mLoadMoreView.setText(R.string.listview_footer_loaded);
            moreProgressBar.setVisibility(View.GONE);
        }else
            mView.setVisibility(View.INVISIBLE);
    }
}
