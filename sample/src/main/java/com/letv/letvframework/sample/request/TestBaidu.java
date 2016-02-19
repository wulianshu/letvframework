package com.letv.letvframework.sample.request;

import android.support.annotation.Nullable;

import com.letv.framework.request.BaseRequest;
import com.letv.framework.request.BaseStringParser;
import com.letv.framework.request.RequestListener;

/**
 * Created by liuzhuo on 2016/2/15.
 */
public class TestBaidu {

    private String url = "http://www.baidu.com";
    private MyRequest req;

    public TestBaidu(RequestListener<MyBean> listener){
        req = new MyRequest(url, new MyParser(), listener);
    }

    public MyRequest getRequest(){
        return req;
    }

    class MyParser extends BaseStringParser<MyBean> {

        private MyBean bean;

        public MyParser(){
            bean = new MyBean();
            bean.url = url;
        }

        @Override
        public MyBean parse(String data) throws Exception {
            bean.str = data;
            return bean;
        }
    }

    class MyRequest extends BaseRequest<MyBean> {

        public MyRequest(String url, @Nullable BaseStringParser<MyBean> parser, @Nullable RequestListener<MyBean> requestListener) {
            super(url, parser, requestListener);
        }
    }
}
