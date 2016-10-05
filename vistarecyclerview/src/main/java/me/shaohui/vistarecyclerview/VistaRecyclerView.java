package me.shaohui.vistarecyclerview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.support.annotation.ColorRes;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewStub;
import android.widget.FrameLayout;

import java.util.IllegalFormatFlagsException;

import me.shaohui.vistarecyclerview.adapter.AgentAdapter;

/**
 * Created by shaohui on 16/7/31.
 */
public class VistaRecyclerView extends FrameLayout {

    private SwipeRefreshLayout refreshLayout;
    private RecyclerView mRecycler;
    private AgentAdapter mAdapter;

    private View mEmpty;
    private View mError;
    private View mLoadProgress;

    private int COUNT_LEFT_TO_LOAD_MORE;
    private int mBottomLoadProgressId;
    private int mBottomLoadFailureId;
    private int mBottomLoadNoMoreId;
    private int mEmptyId;
    private int mErrorId;
    private int mLoadingProgressId;
    private int refreshColor;

    private LAYOUT_MANAGER_TYPE layoutManagerType;
    private int[] lastScrollPositions;

    private boolean isLoadingMore;
    private boolean canLoadMore = true;
    private boolean canRefresh = false;

    private RecyclerView.OnScrollListener mInternalOnScrollListener;
    private RecyclerView.OnScrollListener mExternalOnScrollListener;
    private OnMoreListener mOnMoreListener;

    public static final String TAG = "VistaRecyclerView";

    public VistaRecyclerView(Context context) {
        super(context);
        initView(context);
    }

    public VistaRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAttrs(attrs);
        initView(context);
    }

    public VistaRecyclerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttrs(attrs);
        initView(context);
    }

    private void initAttrs(AttributeSet attr) {
        TypedArray a = getContext().obtainStyledAttributes(attr, R.styleable.VistaRecyclerView);
        try {
            mBottomLoadProgressId = a.getResourceId(R.styleable.VistaRecyclerView_bottom_load_progress, R.layout.bottom_load_progress);
            mBottomLoadFailureId = a.getResourceId(R.styleable.VistaRecyclerView_bottom_load_failure, R.layout.bottom_load_failure);
            mBottomLoadNoMoreId = a.getResourceId(R.styleable.VistaRecyclerView_bottom_load_no_more, R.layout.bottom_load_no_more);
            mEmptyId = a.getResourceId(R.styleable.VistaRecyclerView_empty_layout, R.layout.empty_layout);
            mErrorId = a.getResourceId(R.styleable.VistaRecyclerView_error_layout, R.layout.error_layout);
            mLoadingProgressId = a.getResourceId(R.styleable.VistaRecyclerView_load_layout, 0);
            COUNT_LEFT_TO_LOAD_MORE = a.getInt(R.styleable.VistaRecyclerView_preload_size, 0);
            refreshColor = a.getColor(R.styleable.VistaRecyclerView_refresh_color, 0);
        } finally {
            a.recycle();
        }
    }

    private void initView(Context context) {
        View v = LayoutInflater.from(context)
                .inflate(R.layout.vista_recycler_view, this);
        refreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.refresh_layout);
        refreshLayout.setEnabled(false);
        if (refreshColor != 0) {
            refreshLayout.setColorSchemeColors(new int[]{refreshColor});
        }

        ViewStub loadProgress = (ViewStub) v.findViewById(R.id.load_progress);
        if (mLoadingProgressId != 0) {
            loadProgress.setLayoutResource(mLoadingProgressId);
            mLoadProgress = loadProgress.inflate();
        }
        loadProgress.setVisibility(GONE);

        ViewStub emptyView = (ViewStub) v.findViewById(R.id.empty_view);
        if (mEmptyId != 0) {
            emptyView.setLayoutResource(mEmptyId);
            mEmpty = emptyView.inflate();
        }
        emptyView.setVisibility(GONE);

        ViewStub errorView = (ViewStub) v.findViewById(R.id.error_view);
        if (mErrorId != 0) {
            errorView.setLayoutResource(mErrorId);
            mError = errorView.inflate();
        }
        errorView.setVisibility(GONE);

        initRecycler(v);
    }

    private void initRecycler(View v) {
        mRecycler = (RecyclerView) v.findViewById(R.id.vista_recycler);
        mAdapter = new AgentAdapter(null);
        mAdapter.placeBottomLayout(mBottomLoadProgressId, mBottomLoadFailureId, mBottomLoadNoMoreId);

        mRecycler.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
                super.onDraw(c, parent, state);
            }
        });

        mInternalOnScrollListener = new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (mExternalOnScrollListener != null) {
                    mExternalOnScrollListener.onScrollStateChanged(recyclerView, newState);
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                processOnMore();

                if (mExternalOnScrollListener != null) {
                    mExternalOnScrollListener.onScrolled(recyclerView, dx, dy);
                }

            }
        };
        mRecycler.addOnScrollListener(mInternalOnScrollListener);
        mRecycler.setLayoutManager(new LinearLayoutManager(getContext(),
                LinearLayoutManager.VERTICAL, false));

    }

    private void processOnMore() {
        RecyclerView.LayoutManager manager = mRecycler.getLayoutManager();
        int lastVisibleItemPosition = getLastVisibleItemPosition(manager) + 1;
        int visibleItemCount = manager.getChildCount();
        int totalItemCount = manager.getItemCount() - 1;

        if (totalItemCount - lastVisibleItemPosition <= COUNT_LEFT_TO_LOAD_MORE
                && !isLoadingMore && !isRefreshing() && canLoadMore) {
            isLoadingMore = true;
            refreshLayout.setEnabled(false);

            if (mOnMoreListener != null) {
                mAdapter.loadMore();
                mOnMoreListener.noMoreAsked(totalItemCount, COUNT_LEFT_TO_LOAD_MORE, lastVisibleItemPosition);
            } else {
                mAdapter.hideLoadMore();
            }
        }
    }

    private int getLastVisibleItemPosition(RecyclerView.LayoutManager manager) {
        int lastVisibleItemPosition = -1;
        if (layoutManagerType == null) {
            if (manager instanceof GridLayoutManager) {
                layoutManagerType = LAYOUT_MANAGER_TYPE.GRID;
            } else if (manager instanceof LinearLayoutManager) {
                layoutManagerType = LAYOUT_MANAGER_TYPE.LINEAR;
            } else if (manager instanceof StaggeredGridLayoutManager) {
                layoutManagerType = LAYOUT_MANAGER_TYPE.STAGGERED_GRID;
            } else {
                throw new RuntimeException("UnSupported layout manager");
            }
        }
        switch (layoutManagerType) {
            case LINEAR:
                lastVisibleItemPosition = ((LinearLayoutManager) manager).findLastVisibleItemPosition();
                break;
            case GRID:
                lastVisibleItemPosition = ((GridLayoutManager) manager).findLastVisibleItemPosition();
                break;
            case STAGGERED_GRID:
                lastVisibleItemPosition = caseStaggeredGrid(manager);
                break;
        }
        return lastVisibleItemPosition;
    }

    private int caseStaggeredGrid(RecyclerView.LayoutManager layoutManager) {
        StaggeredGridLayoutManager gridLayoutManager = (StaggeredGridLayoutManager) layoutManager;
        if (lastScrollPositions == null){
            lastScrollPositions = new int[gridLayoutManager.getSpanCount()];
        }
        gridLayoutManager.findLastVisibleItemPositions(lastScrollPositions);
        return getMax(lastScrollPositions);
    }

    private int getMax(int[] positions) {
        int max = Integer.MIN_VALUE;
        for (int value:positions) {
            if (value > max) {
                max = value;
            }
        }
        return max;
    }

    private void setInternalAdapter(RecyclerView.Adapter<RecyclerView.ViewHolder> adapter,
                                    boolean compatibleWithPrevious,
                                    boolean removeAndRecyclerExistingViews) {
        mAdapter.setAdapter(adapter);
        if (compatibleWithPrevious) {
            mRecycler.swapAdapter(mAdapter, removeAndRecyclerExistingViews);
        } else {
            mRecycler.setAdapter(mAdapter);
        }

        mRecycler.setVisibility(VISIBLE);

        if (null != adapter) {
            mAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
                @Override
                public void onChanged() {
                    super.onChanged();
                    update();
                }

                @Override
                public void onItemRangeChanged(int positionStart, int itemCount) {
                    super.onItemRangeChanged(positionStart, itemCount);
                    update();
                }

                @Override
                public void onItemRangeChanged(int positionStart, int itemCount, Object payload) {
                    super.onItemRangeChanged(positionStart, itemCount, payload);
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

                private void update() {
                    refreshLayout.setRefreshing(false);
                    isLoadingMore = false;
                    canLoadMore = true;
                    mAdapter.loadMoreSuccess();

                    if (mRecycler.getAdapter().getItemCount() == 0 && mEmpty != null) {
                        mEmpty.setVisibility(VISIBLE);
                    } else if (mEmpty != null){
                        mEmpty.setVisibility(GONE);
                        mRecycler.setVisibility(VISIBLE);
                    }

                    if (canRefresh && !refreshLayout.isEnabled()) {
                        refreshLayout.setEnabled(true);
                    }

                    if (mLoadProgress != null) {
                        mLoadProgress.setVisibility(GONE);
                    }

                    if (mError != null) {
                        mError.setVisibility(GONE);
                    }
                }
            });

        }
        if (mEmpty != null && !isRefreshing()) {
            mEmpty.setVisibility(null != adapter
                    && mRecycler.getAdapter().getItemCount() > 0 ? GONE:VISIBLE);
        }
    }

    private boolean isRefreshing() {
        return refreshLayout.isRefreshing();
    }

    public void loadMoreFailure() {

        if (canRefresh) {
            refreshLayout.setEnabled(true);
        }

        isLoadingMore = false;
        mAdapter.loadFailure();
    }

    public void loadNoMore() {
        canLoadMore = false;
        isLoadingMore = false;

        if (canRefresh) {
            refreshLayout.setEnabled(true);
        }

        mAdapter.loadNoMore();
    }

    public void setLayoutManager(final RecyclerView.LayoutManager layoutManager) {
        mRecycler.setLayoutManager(layoutManager);

        // Grid 处理
        if (layoutManager instanceof GridLayoutManager) {
            GridLayoutManager gridLayoutManager = (GridLayoutManager) layoutManager;
            setGridSpanSizeLookup(new GridSpanSizeLookup(gridLayoutManager.getSpanCount()) {
                @Override
                public int getSpanSize(int position) {
                    return 1;
                }
            });
        }
    }

    public void setGridSpanSizeLookup(final GridSpanSizeLookup spanSizeLookup) {
        if (mRecycler.getLayoutManager() != null
                && mRecycler.getLayoutManager() instanceof GridLayoutManager) {
            spanSizeLookup.bindAdapter(mAdapter);

            ((GridLayoutManager) mRecycler.getLayoutManager()).setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    return spanSizeLookup.returnSpanSize(position);
                }
            });

        } else {
            throw new IllegalFormatFlagsException("The Layout must be GridLayout");
        }
    }

    public void setAdapter(RecyclerView.Adapter adapter) {
        setInternalAdapter(adapter, false, true);
    }

    public void swapAdapter(RecyclerView.Adapter<RecyclerView.ViewHolder> adapter,
                            boolean removeAndRecyclerExistingViews) {
        setInternalAdapter(adapter, true, removeAndRecyclerExistingViews);
    }

    public void notifyDataSetChanged() {
        mAdapter.notifyDataSetChanged();
    }

    public void showEmptyView() {
        refreshLayout.setRefreshing(false);
        if (mEmpty != null) {
            mEmpty.setVisibility(VISIBLE);
        }
        dismissErrorView();
    }

    public void dismissEmptyView() {
        if (mEmpty != null) {
            mEmpty.setVisibility(GONE);
        }
    }

    public void showErrorView() {
        refreshLayout.setRefreshing(false);
        if (mError != null) {
            mError.setVisibility(VISIBLE);
        }
        dismissEmptyView();
    }

    public void dismissErrorView() {

    }

    public void setErrorListener(OnClickListener listener) {
        if (mError != null) {
            mError.setOnClickListener(listener);
        }
    }

    public void showProgressView() {
        if (mLoadProgress != null) {
            mLoadProgress.setVisibility(VISIBLE);
        }
    }

    public void clear() {
        refreshLayout.setRefreshing(false);
        isLoadingMore = false;
        setAdapter(null);
    }

    public void setRefreshing(final boolean isRefreshing) {
        postDelayed(new Runnable() {
            @Override
            public void run() {
                refreshLayout.setRefreshing(isRefreshing);
            }
        }, 1);
    }

    public void setRefreshListener(final SwipeRefreshLayout.OnRefreshListener listener) {
        refreshLayout.setEnabled(true);
        canRefresh = true;
        refreshLayout.setOnRefreshListener(listener);

        mError.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                refreshLayout.setRefreshing(true);
                listener.onRefresh();
            }
        });
    }

    public RecyclerView getRecycler() {
        return mRecycler;
    }

    public RecyclerView.Adapter getAdapter() {
        return mAdapter;
    }

    public void setItemAnimator(RecyclerView.ItemAnimator itemAnimator) {
        mRecycler.setItemAnimator(itemAnimator);
    }


    public void addOnItemTouchListener(RecyclerView.OnItemTouchListener listener) {
        mRecycler.addOnItemTouchListener(listener);
    }

    public void setOnMoreListener(OnMoreListener listener) {
        this.mOnMoreListener = listener;
        mAdapter.setOnMoreListener(listener);
    }

    public void removeOnMoreListtener() {
        this.mOnMoreListener = null;
        mAdapter.setOnMoreListener(null);
        isLoadingMore = false;
        if (canRefresh) {
            refreshLayout.setEnabled(true);
        }
        mAdapter.hideLoadMore();
    }

    public void setOnMoreListener(OnMoreListener listener, int preLoad) {
        this.mOnMoreListener = listener;
        COUNT_LEFT_TO_LOAD_MORE = preLoad;
    }

    public void setRefreshColorSchemeColors(int... colors) {
        refreshLayout.setColorSchemeColors(colors);
    }

    public void setRefreshColorSchemeResources(@ColorRes int... colorResources) {
        refreshLayout.setColorSchemeResources(colorResources);
    }

    public void setOnScrollListener(RecyclerView.OnScrollListener listener) {
        mExternalOnScrollListener = listener;
    }

    public void hideProgress() {
        if (mLoadProgress != null) {
            mLoadProgress.setVisibility(GONE);
        }
    }

    public RecyclerView.Adapter geAdapter() {
        return mRecycler.getAdapter();
    }

    public boolean isLoadingMore() {
        return isLoadingMore;
    }

    public void addItemDecoration(RecyclerView.ItemDecoration itemDecoration) {
        mRecycler.addItemDecoration(itemDecoration);
    }

    public void addItemDecoration(RecyclerView.ItemDecoration itemDecoration, int index) {
        mRecycler.addItemDecoration(itemDecoration, index);
    }

    public void removeItemDecoration(RecyclerView.ItemDecoration itemDecoration) {
        mRecycler.removeItemDecoration(itemDecoration);
    }

    public void smoothScrollBy(int dx, int dy) {
        mRecycler.smoothScrollBy(dx, dy);
    }

    private SwipeRefreshLayout getRefreshLayout() {
        return refreshLayout;
    }

    private RecyclerView getRecyclrerView() {
        return mRecycler;
    }

    public enum LAYOUT_MANAGER_TYPE {
        LINEAR,
        GRID,
        STAGGERED_GRID
    }
}
