package com.letv.framework.download;

import com.letv.framework.download.DownloadConstant.DownloadType;
import com.letv.framework.download.DownloadManagerFactory.DownloadModule;

import java.io.File;


/**
 * 一个下载
 * @author luxu
 *
 */
public interface IDownload extends IRequest {

	public DownloadType getDownloadType();
	
	/**
	 * 断点下载开始位置
	 * @return
	 */
	public long getStartPosition();
	
	/**
	 * 文件总大小
	 * @return
	 */
	public long getTotalSize();
	
	/**
	 * 本地保存的文件路径
	 * @return
	 */
	public File getLoaclFile();
	
	public String getDownloadUrl();

	public Object getTag();

	public boolean addPublicParams();
	
	public DownloadModule getDownloadModule();
	
	public abstract class BaseDownload implements IDownload {


		@Override
		public DownloadType getDownloadType(){
			return DownloadType.FILE;
		}
		
		@Override
		public String getDownloadUrl() {
			String tag = getUrl();
			return tag;
		}

		@Override
		public Object getTag() {
			return null;
		}

		@Override
		public boolean addPublicParams() {
			return true;
		}
	}
}
