package com.letv.letvframework.sample;

import android.content.Context;
import android.os.Bundle;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ImageView;

import com.letv.framework.adapter.MyBaseAdapter;
import com.letv.framework.base.BaseActivity;
import com.letv.framework.request.RequestListener;
import com.letv.framework.ui.MoreGridView;
import com.letv.framework.ui.pulltorefresh.PullToRefreshGridView;
import com.letv.framework.ui.pulltorefresh.PullToRefreshListener;
import com.letv.framework.util.UiUtil;
import com.letv.letvframework.R;
import com.letv.letvframework.sample.request.MyBean;
import com.letv.letvframework.sample.request.TestBaidu;

/**
 * Created by liuzhuo on 2016/2/3.
 */
public class PicListActivity extends BaseActivity {

    private PullToRefreshGridView mListView;
    private MoreGridView mLv;
    private int count = 0, num = 0;
    private MyAdapter mAdapter;
    private WebView mWeb;

    @Override
    public void onCreate(Bundle bundle){
        super.onCreate(bundle);

        this.setContentView(R.layout.pic_list);

        mListView = (PullToRefreshGridView)this.findViewById(R.id.list);
        mListView.setPullToRefreshListener(new PullToRefreshListener() {

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
        mLv = mListView.getRefreshableView();
        mLv.setNumColumns(3);

        mAdapter = new MyAdapter(this);
        mLv.setAdapter(mAdapter);

        testRequest();
    }

    private void testRequest(){
        mWeb = (WebView)this.findViewById(R.id.web);
        TestBaidu req = new TestBaidu(new RequestListener<MyBean>() {
            @Override
            public void onResponse(MyBean result, boolean isCachedData) {
                mWeb.loadDataWithBaseURL(result.url, result.str, "text/html", "utf-8", null);
            }

            @Override
            public void netErr(int errorCode) {
                UiUtil.showToast(PicListActivity.this, "req err");
            }

            @Override
            public void dataErr(int errorCode) {
                UiUtil.showToast(PicListActivity.this, "req err");
            }
        });
        sendRequest(req.getRequest());
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        UiUtil.destroyWebView(mWeb);
    }

    @Override
    protected void handleInfo(Message msg) {
        switch(msg.what){
            case 0:{
                count += 5;
                mAdapter.notifyDataSetChanged();
            }
                break;
            case 1:{
                if(num % 2 == 0)
                    count = 10;
                else
                    count = 0;
                mAdapter.notifyDataSetChanged();
            }
                break;
        }
        ++num;
        mListView.refreshComplete();
    }

    @Override
    public void observeNetWork(String netName, int netType, boolean isHasNetWork) {

    }

    public class MyAdapter extends MyBaseAdapter{

        public MyAdapter(Context context) {
            super(context);
        }

        @Override
        public int getCount() {
            return count;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        protected View getView(ViewGroup parent, int position, View convertView) {
            ImageView image;
            if(convertView == null){
                convertView = LayoutInflater.from(PicListActivity.this).inflate(R.layout.image_item, parent, false);
                image = (ImageView)convertView.findViewById(R.id.image);
                convertView.setTag(image);
            }else
                image = (ImageView)convertView.getTag();

            loadImage("http://10.58.95.5/pic.jpg", image, R.drawable.ic_launcher);

            return convertView;
        }
    }
}
