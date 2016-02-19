package com.letv.framework.ui;

import android.content.Context;
import android.database.DataSetObserver;
import android.util.AttributeSet;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ListAdapter;

import com.letv.framework.ui.pulltorefresh.ILoadingView;
import com.letv.framework.ui.pulltorefresh.LoadingView;

public class MoreListView extends MyListView implements OnScrollListener {

	private ILoadingView mLoadingView;
	private OnLoadListener mLoadListener;

	public MoreListView(Context context) {
		super(context);
		init();
	}

	public MoreListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public MoreListView(Context context, ILoadingView view){
		super(context);
		mLoadingView = view;
		init();
	}

	public ILoadingView getLoadingView(){
		return new LoadingView(getContext());
	}

	private void init() {
		if(mLoadingView == null)
			mLoadingView = getLoadingView();
		setOnScrollListener(this);
		addFooterView(mLoadingView.getLoadingView(this), null, false);
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
	}
	
	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		
		if (scrollState == SCROLL_STATE_IDLE) {
			final int lastVsbPosition = getLastVisiblePosition();

			final int count = getCount();

			if ((mLoadListener != null && !mLoadListener.isForbidLoad())
					&& (lastVsbPosition == (count - 1) && count > 0)) {
				onLoad();
			} else {
				if (mLoadListener != null && mLoadListener.isFinished()) {
					onLoadComplete();
				}
			}
		}
	}

	public void setOnLoadListener(OnLoadListener loadListener) {
		this.mLoadListener = loadListener;
	}

	public void onLoad() {
		if (mLoadListener != null) {

			if (mLoadListener.isFinished()) {
				onLoadComplete();
			} else {
				mLoadListener.onLoad();
				mLoadingView.showLoading();
			}
		}
	}

	public void onLoadComplete() {
		mLoadingView.hideLoading(mLoadListener.isFinished());
	}

	public interface OnLoadListener {

		void onLoad();

		boolean isFinished();

		boolean isForbidLoad();

		void showEmptyView(boolean isShow);
	}

	@Override
	public void setAdapter(final ListAdapter adapter){
		if(adapter != null){
			adapter.registerDataSetObserver(new DataSetObserver() {
				@Override
				public void onChanged() {
					super.onChanged();
					if(mLoadListener == null)
						return;
					if(adapter.getCount() <= 0) {
						mLoadListener.showEmptyView(true);
						mLoadingView.hideLoading(false);
					}else
						mLoadListener.showEmptyView(false);
				}
			});
			super.setAdapter(adapter);
		}
	}
}
