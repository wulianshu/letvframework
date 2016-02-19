package com.letv.framework.base;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.Application;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.MemoryCategory;
import com.letv.framework.util.CrashHandler;
import com.letv.framework.util.DiskCacheFactory;
import com.letv.framework.util.StoragePathsManager;

import java.util.List;

public abstract class BaseApplication extends Application {

	@Override
	public void onCreate(){
		super.onCreate();
		
		if(isApplication()) {
			GlideBuilder builder = new GlideBuilder(this);
			StoragePathsManager sm = new StoragePathsManager(this);
			String path = sm.getDefaultDir();
			builder.setDiskCache(new DiskCacheFactory(this, path));
			Glide.get(builder).setMemoryCategory(MemoryCategory.NORMAL);
			onCreateImpl();
		}
		
		CrashHandler crashHandler = CrashHandler.getInstance();
        // 注册crashHandler
        crashHandler.init(getApplicationContext());
	}
	
	protected abstract void onCreateImpl();
	
	private boolean isApplication(){
		int myId = android.os.Process.myPid();
		ActivityManager activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
		List<RunningAppProcessInfo> runningAppProcesses = activityManager.getRunningAppProcesses();
		for (RunningAppProcessInfo info : runningAppProcesses) {
			if (info.pid == myId) {
				String tmp = this.getPackageName();
				if (info.processName != null && info.processName.equals(tmp))
					return true;
			}
		}
		return false;
	}
}
