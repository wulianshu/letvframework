package com.letv.framework.request;

import android.support.annotation.Nullable;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.letv.framework.util.LogUtil;
import com.letv.http.bean.LetvBaseBean;
import com.letv.http.bean.LetvDataHull;
import com.letv.http.exception.DataIsErrException;
import com.letv.http.exception.DataIsNullException;
import com.letv.http.exception.DataNoUpdateException;
import com.letv.http.exception.JsonCanNotParseException;
import com.letv.http.exception.ParseException;
import com.letv.http.parse.LetvMainParser;

import java.io.IOException;
import java.util.Map;

/**
 * 请求类封装
 */
public class BaseRequest<T extends LetvBaseBean> extends
		Request<LetvDataHull<T>> {
	private static final String TAG = "Request";

	private LetvMainParser<T, ?> mParser;
	private RequestListener<T> mRequestListener;
	private Map<String, String> mRequestParams;
	private Map<String, String> mHeaders;
	private LogUtil mLog = new LogUtil(this.getClass());

	public BaseRequest(String url, @Nullable  LetvMainParser<T, ?> parser,
			Map<String, String> params, Map<String, String> headers, @Nullable RequestListener<T> requestListener) {
		super(Method.GET, url, null);
		
		mRequestParams = params;
		mParser = parser;
		mHeaders = headers;
		mRequestListener = requestListener;
		
		// 增加超时时间
		setRetryPolicy(new DefaultRetryPolicy());
	}

	public BaseRequest(String url, @Nullable  LetvMainParser<T, ?> parser,
			Map<String, String> params, @Nullable RequestListener<T> requestListener) {
		super(Method.GET, url, null);
		
		mRequestParams = params;
		mParser = parser;
		mRequestListener = requestListener;
		
		// 增加超时时间
		setRetryPolicy(new DefaultRetryPolicy());
	}
	
	public BaseRequest(String url,@Nullable  LetvMainParser<T, ?> parser,
			@Nullable RequestListener<T> requestListener) {
		super(Method.GET, url, null);
		
		this.mParser = parser;
		mRequestListener = requestListener;
		// 增加超时时间
		setRetryPolicy(new DefaultRetryPolicy());
	}
	
	public BaseRequest(int method, String url, @Nullable  LetvMainParser<T, ?> parser,
			Map<String, String> params, Map<String, String> headers, @Nullable RequestListener<T> requestListener) {
		super(method, url, null);
		
		mRequestParams = params;
		mParser = parser;
		mHeaders = headers;
		mRequestListener = requestListener;
		
		// 增加超时时间
		setRetryPolicy(new DefaultRetryPolicy());
	}

	public BaseRequest(int method, String url, @Nullable  LetvMainParser<T, ?> parser,
			Map<String, String> params, @Nullable RequestListener<T> requestListener) {
		super(method, url, null);
		
		mRequestParams = params;
		mParser = parser;
		mRequestListener = requestListener;
		
		// 增加超时时间
		setRetryPolicy(new DefaultRetryPolicy());
	}
	
	public BaseRequest(int method, String url,@Nullable  LetvMainParser<T, ?> parser,
			@Nullable RequestListener<T> requestListener) {
		super(method, url, null);
		
		this.mParser = parser;
		mRequestListener = requestListener;

		// 增加超时时间
		setRetryPolicy(new DefaultRetryPolicy());
	}
	
	@Override
	public Map<String, String> getHeaders() throws AuthFailureError{
		if(mHeaders == null)
			return super.getHeaders();
		return mHeaders;
	}

	@Override
	protected Map<String, String> getParams() throws AuthFailureError {
		return mRequestParams;
	}

	@Override
	protected Response<LetvDataHull<T>> parseNetworkResponse(
			NetworkResponse response) {
		LetvDataHull<T> dataHull = new LetvDataHull<T>();
		String json = "";
		try {

			json = new String(response.data, HttpHeaderParser.parseCharset(
					response.headers, "UTF-8"));
			mLog.d("result :" + json);
			if (mParser != null) {
				dataHull.setDataEntity(mParser.initialParse(json));
				dataHull.setDataType(LetvDataHull.DataType.DATA_IS_INTEGRITY);
				dataHull.setSourceData(json);
			}
			return Response.success(dataHull,
					HttpHeaderParser.parseCacheHeaders(response));
		}

		catch (IOException e) {
			dataHull.setDataType(LetvDataHull.DataType.CONNECTION_FAIL);
			LogUtil.w(TAG, "connected is fail " + e);
			return Response.error(new ServerError());
		} catch (ParseException e) {
			dataHull.setDataType(LetvDataHull.DataType.DATA_PARSE_EXCEPTION);
			LogUtil.w(TAG, "parse error " + e);
			return Response.error(new RequestParseException(LetvDataHull.DataType.DATA_PARSE_EXCEPTION));
		} catch (DataIsNullException e) {
			dataHull.setDataType(LetvDataHull.DataType.DATA_IS_NULL);
			LogUtil.w(TAG, "data is null " + e);
			return Response.error(new RequestParseException(LetvDataHull.DataType.DATA_IS_NULL));
		} catch (JsonCanNotParseException e) {
			dataHull.setDataType(LetvDataHull.DataType.DATA_CAN_NOT_PARSE);
			dataHull.setErrMsg(mParser.getErrorMsg());
			LogUtil.w(TAG, "canParse is false " + e);
			return Response.error(new RequestParseException(LetvDataHull.DataType.DATA_CAN_NOT_PARSE));
		} catch (DataIsErrException e) {
			dataHull.setDataType(LetvDataHull.DataType.DATA_IS_ERR);
			dataHull.setSourceData(json);
			LogUtil.w(TAG, "data is err " + e);
			return Response.error(new RequestParseException(LetvDataHull.DataType.DATA_IS_ERR));
		} catch (DataNoUpdateException e) {
			dataHull.setDataType(LetvDataHull.DataType.DATA_NO_UPDATE);
			LogUtil.w(TAG, "data has not update " + e);
			return Response.error(new RequestParseException(LetvDataHull.DataType.DATA_NO_UPDATE));
		} catch (Exception e) {
			LogUtil.w(TAG, "parseException " + e);
			dataHull.setDataType(LetvDataHull.DataType.DATA_CAN_NOT_PARSE);
			return Response.error(new RequestParseException(LetvDataHull.DataType.DATA_CAN_NOT_PARSE));
		}
	}

	@Override
	protected void deliverResponse(LetvDataHull<T> response) {
		if (mRequestListener != null) {
			int errorCode = response.getDataType();
			switch (errorCode) {
			case LetvDataHull.DataType.DATA_IS_INTEGRITY:
				mRequestListener.onResponse(response.getDataEntity(), false);
				break;
			default:
				mRequestListener.dataErr(errorCode);
				break;
			}
		}
	}

	@Override
	public void deliverError(VolleyError error) {
		super.deliverError(error);
		if (mRequestListener == null) {
			return;
		}
		if (error instanceof TimeoutError || error instanceof NoConnectionError) {
			mRequestListener.netErr(RequestListener.ERROR_NET_ERROR);
		} else if (error instanceof AuthFailureError) {
			mRequestListener.dataErr(RequestListener.ERROR_DATA_ERROR);
		} else if (error instanceof ServerError) {
			mRequestListener.netErr(RequestListener.ERROR_SERVER_ERROR);
		} else if (error instanceof NetworkError) {
			mRequestListener.netErr(RequestListener.ERROR_NET_ERROR);
		} else if (error instanceof RequestParseException) {
			mRequestListener.dataErr(((RequestParseException)error).getErrorCode());
		} else {
			mRequestListener.dataErr(RequestListener.ERROR_UNKNOWN);
		}
	}

	@Override
	protected Map<String, String> getPostParams() throws AuthFailureError {
		return this.mRequestParams;
	}
}
