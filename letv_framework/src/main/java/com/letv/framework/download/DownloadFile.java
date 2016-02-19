package com.letv.framework.download;

import android.text.TextUtils;

import com.letv.framework.download.DownloadQueue.DownloadCallback;
import com.letv.framework.util.LogUtil;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.atomic.AtomicBoolean;

public class DownloadFile implements IDownloadFile {

	private final LogUtil logger = new LogUtil(DownloadTask.class);
	private final int timeout = 20*1000;
	
	private long mProgress;
	private long mTotal;
	private URL mDownUrl;
	private HttpURLConnection mHttpClient;
	private int responseCode;
	
	private IDownloadManager.Progress mProgrezz;
	private IDownload mRequest;
	private DownloadCallback mCallback;
	private FileOperator mFileOperators;
	
	private AtomicBoolean isPause = new AtomicBoolean(false); //stop task
	private AtomicBoolean downloadFinish = new AtomicBoolean(false);
	private AtomicBoolean isStarting = new AtomicBoolean(false);
	private long mCurrent = 0, mOffset = 0;
	
	public DownloadFile(IDownload request, DownloadCallback callback) {
		super();
		
		this.mProgress = request.getStartPosition();
		this.mTotal = request.getTotalSize();

		this.mRequest = request;
		this.mCallback = callback;
		
		this.mProgrezz = new IDownloadManager.Progress();
	}
	
	@Override
	public int download() {
		// TODO Auto-generated method stub
		mFileOperators = new FileOperator(mRequest.getLoaclFile());
		if(!mFileOperators.isInited())
			return IDownloadFile.DOWNLOAD_FILE_ERROR;
		
		startDownload();		
		
		return 0;
	}

    @Override
    public boolean pauseTask(){
        printLog(" =========pauseTask.url[" + mRequest.getUrl() + "]");
        if(!downloadFinish.get()){

            pause();

            return true;
        }
        return false;
    }

    protected void pause(){
        setPause();

        if(!isStart()){
            mProgrezz.progress = mProgress;
            mProgrezz.total = mTotal;
            mCallback.onPauseDownload(mProgrezz, 0, mRequest);
            closeConnection();
        }
    }

    protected void setPause(){
        isPause.set(true);
    }
    
    protected boolean isPause(){
    	return isPause.get();
    }
    
    private boolean isStart(){
        return isStarting.get();
    }

    private void closeConnection() {
        try {
            if(mHttpClient != null){
                mHttpClient.disconnect();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
	private void startDownload() {
		try{
			executeDownload();
		}catch(Throwable e){
			mCallback.onDownloadFailed(responseCode, IDownloadManager.DownloadExp.CODE_NET_SERVER, e.toString(), mRequest);
			e.printStackTrace();
		}
		
		mFileOperators.close();//TODO close resource
		closeConnection();
	}
	
	protected String getDownloadUrl(){
		return mRequest.getUrl();
	}
	
	private void executeDownload() throws Exception {
        if(mTotal > 0 && mProgress == mTotal){
            doDownloadFinish(responseCode);
            return;
        }
        String publicParams = "";
        if (mRequest.addPublicParams()) {
            //TODO
        }
        final String url = getDownloadUrl() + publicParams;

        InputStream inStream = null;
        try {
            printLog("[downloadStart] "+url);

            if(mProgress > mTotal){
                mProgress = 0;
                mTotal = 0;
            }
            if((mProgress == 0 || mProgress < mTotal) && !isPause()){

                printLog("[range:bytes=" + mProgress + "-" + mTotal + "]");
                HttpURLConnection response = getResponse(url);
                responseCode = response.getResponseCode();
                inStream = new BufferedInputStream(response.getInputStream());

                boolean isFinish = true;

                if(!mFileOperators.seekStartPosition(mProgress)){
                    throw new RuntimeException(mFileOperators.getErrorStack());
                }

                final int buffSize = 8192*2;
                byte[] buffer = new byte[buffSize];
                int offset = 0;

                while ((offset = inStream.read(buffer, 0, buffSize)) != -1) {
                    if(!progressCallback(responseCode, buffer, offset)){
                        isFinish = false;
                        break;
                    }
                }
                if(isFinish){
                    doDownloadFinish(responseCode);
                    printLog("[downloadEnd] "+url);
                }
            } else {
                if(!isPause()){
                    throw new Exception("wrong progress " + mProgress + ", " + mTotal);
                }
            }
            printLog(" downloading ResponseCode: " + responseCode);

        } catch(HeaderException he) {
            responseCode = 408; //request timeout responseCode.
        } catch(Exception e) {
            e.printStackTrace();
            responseCode = 408; //request timeout responseCode.
            if(!isPause()){
                throw e;
            }else{
                mCallback.onPauseDownload(mProgrezz, 0, mRequest);
            }
        } finally{
            if(inStream != null){
                try {
                    inStream.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
	
	protected String getHeader(){
		return "";
	}
	
	protected HttpURLConnection getHttpClient()
            throws IOException {
        HttpURLConnection conn = (HttpURLConnection) mDownUrl.openConnection();
        conn.setReadTimeout(timeout);
        conn.setConnectTimeout(timeout);
        conn.setUseCaches(false);
        conn.setDoInput(true);
        conn.setRequestProperty("connection", "Keep-Alive");
        conn.setRequestProperty("Referer", mDownUrl.toString());
        String str = getHeader();
        if(!TextUtils.isEmpty(str))
            conn.setRequestProperty("User-Agent", str);
        return conn;
    }
	
	private HttpURLConnection getResponse(String url) throws Exception {

        this.mDownUrl = new URL(url);
        this.mHttpClient = getHttpClient();

        if(mProgress > 0){
        	if(mTotal > mProgress)
        		mHttpClient.setRequestProperty("Range", "bytes=" + mProgress + "-"+ mTotal);
        	else
        		mHttpClient.setRequestProperty("Range", "bytes=" + mProgress + "-");
        }

        final boolean isFailed = processHeader(mHttpClient);
        if(isFailed){
            throw new HeaderException("process header error");
        }

        mHttpClient.connect();
        final long contentLen = mHttpClient.getContentLength();
        if(mProgress == 0){
            if(contentLen <= 0)
                ;//throw new RuntimeException("get content length error, " + contentLen);
            else{
                mTotal = contentLen;

                mProgrezz.progress = mProgress;
                mProgrezz.total = mTotal;
                mCallback.onFileTotalSize(mProgrezz, responseCode, mRequest); //TODO nofity file totalsize
            }
        }
        setStarting();

        printLog("[contentLength=" + contentLen + "]");

        return mHttpClient;
    }
	
	protected boolean processHeader(HttpURLConnection con){
		return true;
	}
	
	protected void doDownloadFinish(int responseCode) {
        setDownloadFinish();
        mCallback.onDownloadFinish(mProgrezz, responseCode, mRequest);
        mFileOperators.close();//TODO close resource
    }
	
	private void setDownloadFinish() {
        downloadFinish.set(true);
    }
	
	private void setStarting(){
		isStarting.set(true);
	}
	
	private void printLog(String str){
		logger.d(str);
	}
	
	private boolean progressCallback(int responseCode, byte[] data, int offset) throws IOException {
        //TODO if isPause ?
        if(!isPause()){
            if(responseSuccess(responseCode)){
                mProgress+=offset;

                if(!mFileOperators.writeFile(data, offset)){
                    mCallback.onDownloadFailed(responseCode, IDownloadManager.DownloadExp.CODE_WRITEFILE, mFileOperators.getErrorStack(), mRequest);
                    return false;
                }

                if(mTotal <= 0){
                    mProgrezz.progress = 50;
                    mProgrezz.total = 100;
                } else {
                    mProgrezz.progress = mProgress;
                    mProgrezz.total = mTotal;
                }
                long rate = getRate(offset);
                if(rate == -1)
                    return true;
                onDownloading(rate);
                return true;
            } else {
                mCallback.onDownloadFailed(responseCode, IDownloadManager.DownloadExp.CODE_NET_SERVER, "", mRequest);
            }
        } else {
            mProgrezz.progress = mProgress;
            mProgrezz.total = mTotal;
            mCallback.onPauseDownload(mProgrezz, 0, mRequest);
            closeConnection();

            printLog("onPause[offset="+ offset +", progress="+ mProgress
                    + ", total=" + mTotal + ",isPause=" + isPause() +"], url=" + mRequest.getUrl());
        }
        return false;
    }

	protected void onDownloading(long rate){
        boolean bo = mCallback.onDownloading(mProgrezz, rate, responseCode, mRequest);
        if(!bo)
            pauseTask();
    }

	private boolean responseSuccess(int responseCode){
        return responseCode == HttpURLConnection.HTTP_OK
                || responseCode == HttpURLConnection.HTTP_PARTIAL;
    }
	
	protected long getRate(long offset){
        long time = System.currentTimeMillis();
        if(mCurrent == 0){
            mCurrent = time;
            mOffset = offset;
        }else{
            if(time - mCurrent < 1000) {
                mOffset += offset;
                return -1;
            }
        }
        int tmp = (int)(time - mCurrent);
        long rate;
        if(tmp <= 0)
            rate = 0;
        else
            rate = mOffset * 1000 / (time - mCurrent);
        mCurrent = time;
        mOffset = 0;

        return rate;
    }
}
