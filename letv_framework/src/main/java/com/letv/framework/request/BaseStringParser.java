package com.letv.framework.request;

import com.letv.http.bean.LetvBaseBean;
import com.letv.http.parse.LetvMainParser;

/**
 * Created by liuzhuo on 2016/2/15.
 */
public abstract class BaseStringParser<T extends LetvBaseBean> extends LetvMainParser<T, String> {

    @Override
    protected final boolean canParse(String data) {
        return true;
    }

    @Override
    protected String getData(String data) {
        return data;
    }
}
