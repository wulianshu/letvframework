package com.letv.share.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class PreferenceUtil {

	private static final String PREFERENCE_NAME = "share_auth_data";
	private static PreferenceUtil mPreferenceUtil;
	private static SharedPreferences mSp;
	private static Editor mEditor;

	private PreferenceUtil(Context context) {
		init(context);
	}

	public static PreferenceUtil getInstance(Context context) {
		if (mPreferenceUtil == null) {
			mPreferenceUtil = new PreferenceUtil(context.getApplicationContext());
		}
		return mPreferenceUtil;
	}

	private void init(Context context) {
		if(context == null){
			return;
		}
		if(mSp == null){
			mSp = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
		}
		if (mEditor == null) {
			mEditor = mSp.edit();
		}
	}
	
	public void saveLong(String key, long value) {
		if(mEditor != null){
			mEditor.putLong(key, value);
			mEditor.commit();
		}
	}

	public long getLong(String key, long defaultlong) {
		if(mSp != null){
			return mSp.getLong(key, defaultlong);
		}
		return defaultlong;
	}

	public void saveBoolean(String key, boolean value) {
		if(mEditor != null){
			mEditor.putBoolean(key, value);
			mEditor.commit();
		}
	}

	public boolean getBoolean(String key, boolean defaultboolean) {
		if(mSp != null){
			return mSp.getBoolean(key, defaultboolean);
		}
		return defaultboolean;
	}

	public void saveInt(String key, int value) {
		if(mEditor != null){
			mEditor.putInt(key, value);
			mEditor.commit();
		}
	}

	public int getInt(String key, int defaultInt) {
		if(mSp != null){
			return mSp.getInt(key, defaultInt);
		}
		return defaultInt;
	}
	
	public void saveString(String key, String value) {
		if(mEditor != null){
			mEditor.putString(key, value);
			mEditor.commit();
		}
	}

	public String getString(String key, String defaultString) {
		if(mSp != null){
			return mSp.getString(key, defaultString);
		}
		return defaultString;
	}

	public void remove(String key) {
		if(mEditor != null){
			mEditor.remove(key);
			mEditor.commit();
		}
	}
}
