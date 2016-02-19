package com.letv.framework.download;

import java.util.concurrent.ThreadPoolExecutor;

public class TaskDiscardPolicy extends BaseRejectedPolicy {

	@Override
	public void rejectedExecutionImpl(Runnable r, ThreadPoolExecutor executor) {
		printLog(" DiscardPolicy rejectedExp ");
	}

}
