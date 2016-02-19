package com.letv.framework.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.letv.framework.util.MyBitmapImageViewTarget;

/**
 * Created by liuzhuo on 2016/1/19.
 */
public abstract class MyBaseAdapter extends BaseAdapter {

    private boolean mScrolling;
    protected Context mContext;

    public MyBaseAdapter(Context context){
        mContext = context;
    }

    public void setScrolling(boolean scrolling){
        mScrolling = scrolling;
    }

    protected boolean isScrolling(){
        return mScrolling;
    }

    protected abstract View getView(ViewGroup parent, int position, View convertView);

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        try{
            return getView(parent, position, convertView);
        }catch(Throwable e){
            e.printStackTrace();
            return new View(mContext);
        }
    }

    public void loadImage(String url, ImageView image, int defaultId){
        Glide.with(mContext).
                load(url).
                asBitmap().centerCrop().
                placeholder(defaultId).into(new MyBitmapImageViewTarget(image));
    }
}
