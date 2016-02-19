package com.letv.framework.util;

import android.app.Application;
import android.content.Context;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;

import java.io.File;
import java.io.FileFilter;
import java.lang.reflect.Field;
import java.util.regex.Pattern;

/**
 * <p>设备相关工具类
 * <P>由于获取设备相关信息需要上下文对象，考虑到有些地方不一定有上下文对象，所以在应用初始化时，需要调用{@link #init}方法进行初始化
 */
public class DeviceUtil {
	
	private Context mContext;
	private int mWidth = 0;
	private int mHeight = 0;
	private float mDensity = 0;
	
	private static DeviceUtil mUtil;
	
	private DeviceUtil(){
	}
	
	public synchronized static DeviceUtil getInstance(Context context){
		if(mUtil == null){
			mUtil = new DeviceUtil();
			mUtil.init(context);
		}
		return mUtil;
	}
	
	/**
	 * 初始化该工具类的方法
	 * @param context		上下文对象，不能为空，否则会抛RuntimeException
	 */
	private void init(Context context){
		if(context == null){
			throw new RuntimeException("context is null");
		}
		if(mContext != null)
			return;
		
		mContext = context.getApplicationContext();		
		DisplayMetrics dm = ((Application)mContext).getResources().getDisplayMetrics();
		mWidth = dm.widthPixels;
		mHeight = dm.heightPixels;
		mDensity = dm.density;
	}
	
	/**
	 * 获取屏幕宽度
	 * @return
	 */
	public int getDisplayWidth(){
		return mWidth;
	}
	
	/**
	 * 获取屏幕高度
	 * @return
	 */
	public int getDisplayHeight(){
		return mHeight;
	}
	
	/**
	 * 获取屏幕密度
	 * @return
	 */
	public float getDensity(){
		return  mDensity < 1 ? 1 : mDensity;
	}
	
	/**
	 * 获取状态栏高度
	 * @return
	 */
	public int getStatusHeight(){
		int height = 0;
		if(mContext != null){
			try {
				@SuppressWarnings("rawtypes")
				Class c = Class.forName("com.android.internal.R$dimen");  
				Object obj = c.newInstance();  
				Field field = c.getField("status_bar_height");  
				int x = Integer.parseInt(field.get(obj).toString());  
				height = mContext.getResources().getDimensionPixelSize(x);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return height;
	}
	
	/**
	 * 获取手机型号
	 * @return
	 */
	public String getPhoneModel(){
		String model = android.os.Build.MODEL;
		return model;
	}

	/**
	 * Gets the number of cores available in this device, across all processors.
	 * Requires: Ability to peruse the filesystem at "/sys/devices/system/cpu"
	 * @return The number of cores, or 1 if failed to get result
	 */
	public static int getCPUCoreCounts() {
		//Private Class to display only CPU devices in the directory listing
		class CpuFilter implements FileFilter {
			@Override
			public boolean accept(File pathname) {
				//Check if filename is "cpu", followed by a single digit number
				if(Pattern.matches("cpu[0-9]+", pathname.getName())) {
					return true;
				}
				return false;
			}
		}

		try {
			//Get directory containing CPU info
			File dir = new File("/sys/devices/system/cpu/");
			//Filter to only list the devices we care about
			File[] files = dir.listFiles(new CpuFilter());
			//Return the number of cores (virtual CPU devices)
			return files.length;
		} catch(Exception e) {
			//Default to return 1 core
			return 1;
		}
	}
	
	/**
	 * 获取手机IMEI
	 * @return
	 */
	public String getIMEI(){
		TelephonyManager tm = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
		return tm.getDeviceId();
	}

	public int getDisplayDPI() {
		return (int)(160 * mDensity);
	}
}
