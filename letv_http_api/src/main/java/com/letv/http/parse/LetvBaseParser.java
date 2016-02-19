package com.letv.http.parse;

import android.text.TextUtils;

import com.letv.http.bean.LetvBaseBean;
import com.letv.http.exception.DataIsErrException;
import com.letv.http.exception.DataIsNullException;
import com.letv.http.exception.DataNoUpdateException;
import com.letv.http.exception.JsonCanNotParseException;
import com.letv.http.exception.ParseException;

/**
 * 瑙ｆ瀽鍣ㄦ帴鍙�
 * */
public abstract class LetvBaseParser<T extends LetvBaseBean, D> {
	
	/**
	 * 閿欒code
	 * */
	private int errorMsg ;
	
	/**
	 * 鏈嶅姟淇℃伅
	 * */
	private String message ;
	
	/**
	 * 鏁版嵁鏉ユ簮锛屽尯鍒В鏋�榛樿涓�
	 * */
	private int from ;
	
	public LetvBaseParser(int from){
		this.from = from ;
	}
	
	public T initialParse(String data) throws JsonCanNotParseException, DataIsNullException, ParseException , DataIsErrException, DataNoUpdateException{
		if (TextUtils.isEmpty(data)) {
			throw new DataIsNullException("json string is null");
		}
		if (canParse(data)) {
			D d = null ;
			try{
				d = getData(data);
			}catch(Exception e){
				throw new DataIsErrException("Data is Err");
			}
			if(d != null){
				T t;
				try {
					t = parse(d);
					return t;
				} catch (Exception e) {
					throw new ParseException("Parse Exception");
				}
			}else{
				throw new ParseException("Data is Err");
			}
		} else {
			boolean hasUpdate = hasUpdate() ;
			if(!hasUpdate){
				throw new DataNoUpdateException("data has not update");
			}else{
				throw new JsonCanNotParseException("canParse is return false");
			}
		}
	}

	public abstract T parse(D data) throws Exception;
	
	/**
	 * 閽堝涓嶅悓鐨勬帴鍙ｇ被鍨嬶紙濡傦細绉诲姩绔帴鍙ｏ紝涓荤珯鎺ュ彛锛屾敮浠樻帴鍙ｇ瓑锛夎繘琛屼笉鍚屽疄鐜帮紝
	 * 濡傛灉鐙珛鎺ュ彛锛岃瀹炵幇涓鸿繑鍥� true锛屽惁鍒欎笉浼氳繘鍏ヨВ鏋愭柟娉曪紝骞舵姏鍑篔sonCanNotParseException
	 * */
	protected abstract boolean canParse(String data);
	
	/**
	 * 閽堝涓嶅悓鐨勬帴鍙ｇ被鍨嬶紝缁檖arse鏂规硶鍚愬嚭涓嶅悓鐨勬暟鎹�
	 * */
	protected abstract D getData(String data) throws Exception;
	
	/**
	 * 寰楀埌閿欒淇℃伅id
	 * */
	public int getErrorMsg(){
		return errorMsg ;
	}
	
	/**
	 * 璁剧疆閿欒淇℃伅鐨刬d
	 * */
	protected void setErrorMsg(int errorMsg) {
		this.errorMsg = errorMsg;
	}
	
	/**
	 * 璁剧疆鏈嶅姟鍣ㄦ秷鎭�
	 * */
	protected void setMessage(String message){
		this.message = message ;
	}
	
	/**
	 * 寰楀埌鏈嶅姟鍣ㄤ俊鎭�
	 * */
	public String getMessage(){
		return this.message ;
	}
	
	/**
	 * 寰楀埌瑙ｆ瀽鍣ㄦ暟鎹潵婧愶紝榛樿涓�锛屼笉杩涜鍖哄埆瑙ｆ瀽
	 * */
	public int getFrom() {
		return from;
	}
	
	/**
	 * 鍒ゆ柇鎺ュ彛鏄惁鏈夋洿鏂�
	 * */
	public boolean hasUpdate(){
		return true ;
	}
	
		}
