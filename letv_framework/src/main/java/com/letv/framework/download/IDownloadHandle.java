package com.letv.framework.download;

import com.letv.framework.download.DownloadManagerFactory.DownloadModule;

public interface IDownloadHandle {

	public void startDownload(DownloadModule downloadModule, Download download,
			Object... params);

	public void pauseDownload(DownloadModule downloadModule, Download download,
			Object... params);
}
