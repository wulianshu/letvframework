package com.letv.framework.download;

import com.letv.framework.download.DownloadConstant.DownloadType;
import com.letv.framework.download.DownloadManagerFactory.DownloadModule;

import java.io.File;
import java.io.Serializable;


public class Download implements IDownload, Serializable{

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public Download(){
	}

	@Override
	public String getUrl() {
		return null;
	}

	@Override
	public long getStartPosition() {
		return 0;
	}

	@Override
	public long getTotalSize() {
		return 0;
	}

	@Override
	public File getLoaclFile() {
		return null;
	}

	@Override
	public DownloadModule getDownloadModule() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DownloadType getDownloadType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getDownloadUrl() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object getTag() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean addPublicParams() {
		// TODO Auto-generated method stub
		return false;
	}
}
