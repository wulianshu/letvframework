package com.letv.framework.ui;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.AttributeSet;
import android.view.View;

import com.letv.framework.adapter.RecyclerWrapAdapter;
import com.letv.framework.ui.pulltorefresh.ILoadingView;
import com.letv.framework.ui.pulltorefresh.LoadingView;

import java.util.ArrayList;

/**
 * Created by moon.zhong on 2015/7/20.
 * time : 15:14
 */
public class WrapRecyclerView extends RecyclerView {

    private ArrayList<View> mHeaderViews = new ArrayList<>() ;
    private ArrayList<View> mFootViews = new ArrayList<>() ;
    private Adapter mAdapter ;
    protected LAYOUT_MANAGER_TYPE layoutManagerType;
    private int[] lastScrollPositions;
    private MoreListView.OnLoadListener mLoadListener;
    private ILoadingView mLoadingView;
    private boolean isInited = false;

    public WrapRecyclerView(Context context) {
        super(context);
    }

    public WrapRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public WrapRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public WrapRecyclerView(Context context, ILoadingView view){
        super(context);
        mLoadingView = view;
    }

    public void setOnLoadListener(MoreListView.OnLoadListener loadListener) {
        this.mLoadListener = loadListener;
    }

    public void addHeaderView(View view){
        mHeaderViews.clear();
        mHeaderViews.add(view);
        if (mAdapter != null){
            if (!(mAdapter instanceof RecyclerWrapAdapter)){
                mAdapter = new RecyclerWrapAdapter(mHeaderViews,mFootViews,mAdapter) ;
            }
        }
    }

    public void addFootView(View view){
        mFootViews.clear();
        mFootViews.add(view);
        if (mAdapter != null){
            if (!(mAdapter instanceof RecyclerWrapAdapter)){
                mAdapter = new RecyclerWrapAdapter(mHeaderViews,mFootViews,mAdapter) ;
            }
        }
    }

    @Override
    public void setAdapter(Adapter adapter) {

        if (mHeaderViews.isEmpty()&&mFootViews.isEmpty()){
            super.setAdapter(adapter);
        }else {
            adapter = new RecyclerWrapAdapter(mHeaderViews,mFootViews,adapter) ;
            super.setAdapter(adapter);
        }
        mAdapter = adapter ;
        mAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeChanged(int positionStart, int itemCount) {
                super.onItemRangeChanged(positionStart, itemCount);
                update();
            }

            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                update();
            }

            @Override
            public void onItemRangeRemoved(int positionStart, int itemCount) {
                super.onItemRangeRemoved(positionStart, itemCount);
                update();
            }

            @Override
            public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
                super.onItemRangeMoved(fromPosition, toPosition, itemCount);
                update();
            }

            @Override
            public void onChanged() {
                super.onChanged();
                update();
            }

            private void update() {
                if (mLoadListener == null)
                    return;
                if (mAdapter.getItemCount() <= 1) {
                    mLoadListener.showEmptyView(true);
                    mLoadingView.hideLoading(false);
                } else {
                    mLoadListener.showEmptyView(false);
                }
            }
        });
    }

    @Override
    public Adapter getAdapter(){
        return mAdapter;
    }

    @Override
    public void setLayoutManager(LayoutManager layout){
        if(layout == null)
            return;
        super.setLayoutManager(layout);
        init();
    }

    private void init(){
        if(isInited)
            return;
        isInited = true;
        if(mLoadingView == null)
            mLoadingView = getLoadingView();
        addFootView(mLoadingView.getLoadingView(this));

        this.addOnScrollListener(new OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == SCROLL_STATE_IDLE) {
                    RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
                    int lastVisibleItemPosition = getLastVisiblePosition(layoutManager);
                    int totalItemCount = layoutManager.getItemCount();

                    if ((totalItemCount - lastVisibleItemPosition) <= 1 && totalItemCount > 1
                            && mLoadListener != null && !mLoadListener.isForbidLoad()) {
                        onLoad();
                    } else if (mLoadListener != null && mLoadListener.isFinished()) {
                        onLoadComplete();
                    }
                }
            }
        });
    }

    public ILoadingView getLoadingView(){
        return new LoadingView(getContext());
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

    private int getLastVisiblePosition(RecyclerView.LayoutManager layoutManager) {
        int lastVisibleItemPosition = -1;
        if (layoutManagerType == null) {
            if (layoutManager instanceof LinearLayoutManager) {
                layoutManagerType = LAYOUT_MANAGER_TYPE.LINEAR;
            } else if (layoutManager instanceof GridLayoutManager) {
                layoutManagerType = LAYOUT_MANAGER_TYPE.GRID;
            } else if (layoutManager instanceof StaggeredGridLayoutManager) {
                layoutManagerType = LAYOUT_MANAGER_TYPE.STAGGERED_GRID;
            } else {
                throw new RuntimeException("Unsupported LayoutManager used. Valid ones are LinearLayoutManager, GridLayoutManager and StaggeredGridLayoutManager");
            }
        }

        switch (layoutManagerType) {
            case LINEAR:
                lastVisibleItemPosition = ((LinearLayoutManager) layoutManager).findLastVisibleItemPosition();
                break;
            case GRID:
                lastVisibleItemPosition = ((GridLayoutManager) layoutManager).findLastVisibleItemPosition();
                break;
            case STAGGERED_GRID:
                lastVisibleItemPosition = caseStaggeredGrid(layoutManager);
                break;
        }
        return lastVisibleItemPosition;
    }

    private int caseStaggeredGrid(RecyclerView.LayoutManager layoutManager) {
        StaggeredGridLayoutManager staggeredGridLayoutManager = (StaggeredGridLayoutManager) layoutManager;
        if (lastScrollPositions == null)
            lastScrollPositions = new int[staggeredGridLayoutManager.getSpanCount()];

        staggeredGridLayoutManager.findLastVisibleItemPositions(lastScrollPositions);
        return findMax(lastScrollPositions);
    }

    private int findMax(int[] lastPositions) {
        int max = Integer.MIN_VALUE;
        for (int value : lastPositions) {
            if (value > max)
                max = value;
        }
        return max;
    }

    public enum LAYOUT_MANAGER_TYPE {
        LINEAR,
        GRID,
        STAGGERED_GRID
    }
}
