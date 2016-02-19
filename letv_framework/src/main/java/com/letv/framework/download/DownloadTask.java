package com.letv.framework.download;

import com.letv.framework.download.DownloadQueue.DownloadCallback;

public class DownloadTask implements Runnable {

	private IDownloadFile mDown;
	private IDownload mRequest;
	private DownloadCallback mCallback;
	
	public DownloadTask(IDownload request, DownloadCallback callback) {
		super();
		this.mRequest = request;
		this.mCallback = callback;
		mDown = new DownloadFile(request, callback);
	}

	public DownloadTask(IDownload request, IDownloadFile download, DownloadCallback callback) {
		super();
		this.mRequest = request;
		this.mCallback = callback;
		mDown = download;
	}
	
	public IDownload getRequest() {
		return mRequest;
	}

	public boolean pauseTask(){
		if(mDown != null){
			return mDown.pauseTask();
		}
		return true;
	}

	@Override
	public void run() {
		try {
			if(!initDownload()){
				mCallback.onDownloadFailed(0, IDownloadManager.DownloadExp.CODE_NET_SERVER, "init download error", mRequest);
				return;
			}
			startDownload();
		}catch(Throwable e){
			e.printStackTrace();
			mCallback.onDownloadFailed(0, IDownloadManager.DownloadExp.CODE_NET_SERVER, e.toString(), mRequest);
			return;
		}
	}
	
	protected boolean initDownload(){
		return true;
	}
	
	protected int startDownload(){
		if(mDown != null)
			return mDown.download();
		return -1;
	}
}
