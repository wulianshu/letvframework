package com.letv.framework.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.support.v4.util.LongSparseArray;
import android.text.TextUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class StoragePathsManager {
	
	private String DIR; 
	private Context mContext;
    private SharedPreferences mRef;
    private final String STORE = "store_dir";
    private String FOLDER;

	public StoragePathsManager(Context context) {
		mContext = context.getApplicationContext();
        FOLDER = mContext.getPackageName();
		DIR = "/Android/data/" + mContext.getPackageName() + "/files";
        mRef = mContext.getSharedPreferences(STORE, Context.MODE_PRIVATE);
	}

    public String getDefaultDir(){
        String path = mRef.getString(STORE, "");
        if(TextUtils.isEmpty(path)){
            try {
                path = getStoragePaths().get(0);
            }catch(Throwable e){
                path = Environment.getExternalStorageDirectory().getAbsolutePath() + DIR;
                boolean bo = new File(path).mkdirs();
                if(!bo) {
                    path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + FOLDER;
                    bo = new File(path).mkdirs();
                    if(!bo)
                        path = "";
                }
            }
            SharedPreferences.Editor ed = mRef.edit();
            ed.putString(STORE, path);
            ed.commit();
        }
        return path;
    }

	private boolean isValid(File file){
		try{
			if(!file.exists() && !file.mkdirs())
				return false;
			File tmp = new File(file, System.currentTimeMillis() + "");
			if(tmp.createNewFile()){
				tmp.delete();
				return true;
			}
			return false;
		}catch(Throwable e){
			e.printStackTrace();
		}
		return false;
	}
	
	@SuppressLint("NewApi")
	private List<String> getStoragePaths(){
		List<String> list = new ArrayList<String>();
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			File[] fs = mContext.getExternalFilesDirs("files");
			for(File f : fs){
				if(isValid(f))
					list.add(f.getAbsolutePath());
			}
			if(list.isEmpty())
				list.add(Environment.getExternalStorageDirectory().getAbsolutePath() + DIR);
		}else{
			LongSparseArray<String> a = getExterPath();
			int size = a.size();
			for(int i=0; i<size; i++){
				list.add(a.valueAt(i) + DIR);
			}
		}
		return list;
	}
		
	private LongSparseArray<String> getExterPath() {
        LongSparseArray<String> path = new LongSparseArray<>();
        try {
            File f = Environment.getExternalStorageDirectory();
            long space = useful(f.getAbsolutePath(), true);
            if (space > 0)
                path.put(space, f.getAbsolutePath());

            Runtime runtime = Runtime.getRuntime();
            Process proc = runtime.exec("mount");
            InputStream is = proc.getInputStream();
            InputStreamReader isr = new InputStreamReader(is);
            String line;
            BufferedReader br = new BufferedReader(isr);
            while ((line = br.readLine()) != null) {
                if (line.contains("secure"))
                    continue;
                if (line.contains("asec"))
                    continue;
                if (line.contains("fat")) {
                    String columns[] = line.split(" ");
                    if (columns != null) {
                        for (int j = 0; j < columns.length; j++) {
                            long tmp = useful(columns[j], true);
                            if (tmp > 0)
                                path.put(tmp, columns[j]);
                        }
                    }
                } else if (line.contains("fuse")) {
                    String columns[] = line.split(" ");
                    if (columns != null) {
                        for (int j = 0; j < columns.length; j++) {
                            long tmp = useful(columns[j], true);
                            if (tmp > 0)
                                path.put(tmp, columns[j]);
                        }
                    }
                }
            }

            br.close();
            isr.close();
            is.close();
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return path;
    }

	public boolean mkDir(File f){
		try{
			if(f.exists() && f.isDirectory())
				return true;
			if(f.mkdirs())
				return true;
			MediaFile tmp = new MediaFile(mContext.getContentResolver(), f);
			if(tmp.mkdir())
				return true;
		}catch(Throwable e){
			e.printStackTrace();
		}		
		return false;
	}
	
    @SuppressLint("NewApi")
	private long useful(String path, boolean child) {
        try {
        	if(TextUtils.isEmpty(path) || !path.startsWith("/"))
        		return -1;
            File f = new File(path);
            if (f.canWrite() && f.canRead()) {
                try {
                    File tmp;
                    if (child)
                        tmp = new File(path, DIR);
                    else
                        tmp = new File(path);
                    if(!mkDir(tmp))
                    	return -1;
                    f = new File(tmp, System.currentTimeMillis() + "");
                    f.createNewFile();
                    FileOutputStream fout = new FileOutputStream(f);
                    fout.write("a".getBytes());
                    fout.close();
                    f.delete();
                } catch (Throwable e) {
                    e.printStackTrace();
                    return -1;
                }
                if (!child)
                    return 1;
                StatFs statFs = new StatFs(path);
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2)
                	return statFs.getAvailableBytes();
                else{
                	long blockSize = statFs.getBlockSize();
                	long availableBlocks = statFs.getAvailableBlocks();
                	if (blockSize > 0 && availableBlocks > 0)
                		return blockSize * availableBlocks;
                }
            }
            return -1;
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return -1;
    }
}
