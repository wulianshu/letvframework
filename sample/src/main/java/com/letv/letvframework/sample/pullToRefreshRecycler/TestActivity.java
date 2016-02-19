package com.letv.letvframework.sample.pullToRefreshRecycler;

import android.os.Bundle;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.letv.framework.adapter.BaseRecyclerAdapter;
import com.letv.framework.base.BaseActivity;
import com.letv.framework.ui.WrapRecyclerView;
import com.letv.framework.ui.pulltorefresh.PullToRefreshListener;
import com.letv.framework.ui.pulltorefresh.PullToRefreshRecyclerView;
import com.letv.framework.util.UiUtil;
import com.letv.letvframework.R;

import java.util.ArrayList;
import java.util.List;

/**
 *
 *
 * Created by liuzhuo on 2016/2/4.
 */
public class TestActivity extends BaseActivity {

    private PullToRefreshRecyclerView mRecycler;
    private WrapRecyclerView mRecyclerView;
    private List<String> mList = new ArrayList<>();
    private List<String> tmp = new ArrayList<>() ;
    private List<String> add = new ArrayList<>() ;
    private int num = 0;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.setContentView(R.layout.test);
        mRecycler = (PullToRefreshRecyclerView) this.findViewById(R.id.list);
        mRecyclerView = mRecycler.getRefreshableView();
        RecyclerView.LayoutManager manager = new LinearLayoutManager(this) ;
        mRecyclerView.setLayoutManager(manager);


        for (int i = 0 ; i < 20 ; i ++){
            tmp.add("my love "+ i);
        }

        for (int i = 0 ; i < 5 ; i ++){
            add.add("my love "+ i);
        }
        TestAdapter mAdapter = new TestAdapter(mList);
        mAdapter.setOnItemClickListener(new BaseRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position, long id) {
                UiUtil.showToast(TestActivity.this, "click " + position);
            }
        });
        mRecyclerView.setAdapter(mAdapter);

        mRecycler.setPullToRefreshListener(new PullToRefreshListener() {
            @Override
            public void onPullDown() {
                mHandler.sendEmptyMessageDelayed(1, 5000);
            }

            @Override
            public void onPullUp() {
                mHandler.sendEmptyMessageDelayed(0, 5000);
            }

            @Override
            public boolean isFinished() {
                return false;
            }
        });
    }

    @Override
    protected void handleInfo(Message msg) {
        switch(msg.what){
            case 0:{
                mList.addAll(add);
            }
            break;
            case 1:{
                mList.clear();
                if(num % 2 == 0)
                    mList.addAll(tmp);
                else
                    ;
            }
            break;
        }
        ++num;
        mRecyclerView.getAdapter().notifyDataSetChanged();
        mRecycler.refreshComplete();
    }

    @Override
    public void observeNetWork(String netName, int netType, boolean isHasNetWork) {

    }
}
