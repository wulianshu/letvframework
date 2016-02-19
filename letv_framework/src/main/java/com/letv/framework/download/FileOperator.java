package com.letv.framework.download;

import com.letv.framework.util.LogUtil;

import java.io.File;
import java.io.RandomAccessFile;

public class FileOperator {
	
	private boolean inited = false;
	private StringBuffer sb = new StringBuffer();
	
	public FileOperator(File destFile) {
		try {
			init(destFile);
			inited = true;
		} catch (Exception e1) {
			e1.printStackTrace();
			sb.append(e1.toString()).append(" ").append(destFile.getAbsolutePath()).append(", 1\n");
			inited = false;
		}
	}
	
	public String getErrorStack(){
		return sb.toString();
	}
	
	public boolean isInited(){
		return inited;
	}

	private File tmpFile;
	private RandomAccessFile rAccessFile;
	
	private void init(File destFile) throws Exception{
		File parentFile = destFile.getParentFile();
		if(parentFile.exists()){
			if(!parentFile.isDirectory()){
				parentFile.mkdirs();
			}
		}else
			parentFile.mkdirs();
		
		if(!destFile.exists()){
			destFile.createNewFile();
		}
		LogUtil.d("FileOperator", "dest=" + destFile.getAbsolutePath());
		
		this.tmpFile = destFile;
		this.rAccessFile = new RandomAccessFile(tmpFile, "rwd");
	}
	
	public boolean reloadFile(){
		try{
			if(tmpFile.length() == 0)
				return true;
			close();
			File f = new File(tmpFile.getAbsolutePath() + ".a");
			tmpFile.renameTo(f);
			f.delete();
			init(tmpFile);
			return true;
		}catch(Exception e){
			LogUtil.e(e.toString());
			sb.append(e.toString()).append(" ").append(tmpFile.getAbsolutePath()).append(", 2\n");
			return false;
		}
	}
	
	public boolean seekStartPosition(long position) {
		try {
			rAccessFile.seek(position);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			sb.append(e.toString()).append(", 3\n");
			return false;
		}
	}
	
	public boolean writeFile(byte[] buffer, int count) {
		try {
			rAccessFile.write(buffer, 0, count);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			sb.append(e.toString()).append(", 4\n");
			return false;
		}
	}
	
	public void close(){
		try {
			if(rAccessFile != null){
				rAccessFile.close();
				rAccessFile = null;
			}
		} catch (Exception e) {
			e.printStackTrace();
			sb.append(e.toString()).append(", 5\n");
		}
	}
}
