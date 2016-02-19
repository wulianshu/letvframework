package com.letv.framework.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.letv.framework.interfaces.INetWorkObServe;
import com.letv.framework.util.NetWorkUtil;

public class NetWorkStateReceiver extends BroadcastReceiver {

    private INetWorkObServe mNetWorkObserve;
    private NetworkInfo mInfo;
    private Context mContext;

    public NetWorkStateReceiver(Context context, INetWorkObServe mNetWorkObserve) {
    	this.mNetWorkObserve = mNetWorkObserve;
    	mContext = context;
    }
    
    public void regist(){
    	IntentFilter filter = new IntentFilter();
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        try{
        	mContext.registerReceiver(this, filter);
        }catch(Throwable e){
        	e.printStackTrace();
        }
    }
    
    public void unRegist(){
    	try{
    		mContext.unregisterReceiver(this);
    	}catch(Throwable e){
    		e.printStackTrace();
    	}
    }

    private boolean isChange(NetworkInfo info){
    	if(info == null)
    		return false;
    	if(mInfo == null){
    		mInfo = info;
    		return true;
    	}else{
    		if(mInfo.getTypeName().equalsIgnoreCase(info.getTypeName())
    				&& mInfo.getType() == info.getType()
    				&& mInfo.isAvailable() == info.isAvailable())
    			return false;
    		else{
    			mInfo = info;
    			return true;
    		}
    	}    	
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        //刚进入界面会读取网络状态
        if(intent.getAction().equalsIgnoreCase( ConnectivityManager.CONNECTIVITY_ACTION)){
            try {       
            	if(context == null || mNetWorkObserve == null)
            		return;
            	NetworkInfo networkInfo = NetWorkUtil.getAvailableNetWorkInfo(context);
            	if(!isChange(networkInfo))
            		return;
                if (null != context && null != networkInfo) {
                    //获取网络名称、网络类型、以及网络是否连接
                    mNetWorkObserve.observeNetWork(networkInfo.getTypeName(), networkInfo.getType(), networkInfo.isAvailable());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
