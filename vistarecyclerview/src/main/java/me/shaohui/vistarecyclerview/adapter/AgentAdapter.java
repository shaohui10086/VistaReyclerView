package me.shaohui.vistarecyclerview.adapter;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import me.shaohui.vistarecyclerview.OnItemClickListener;
import me.shaohui.vistarecyclerview.OnMoreListener;
import me.shaohui.vistarecyclerview.R;

/**
 * Created by shaohui on 16/7/31.
 */
public class AgentAdapter extends RecyclerView.Adapter {

    private final int BOTTOM_STATE_LOADING = 1;
    private final int BOTTOM_STATE_FAILED = 2;
    private final int BOTTOM_STATE_NO_MORE = 3;
    private final int BOTTOM_STATE_SUCCESS = 4;

    private RecyclerView.Adapter<RecyclerView.ViewHolder> mAdapter;

    private int loadProgressId = R.layout.bottom_load_progress;
    private int loadFailureId = R.layout.bottom_load_failure;
    private int loadNoMoreId = R.layout.bottom_load_no_more;

    private boolean canLoadMore = false;

    private int mCurrentBottomState = BOTTOM_STATE_LOADING;

    private OnMoreListener listener;

    private OnItemClickListener mOnItemClickListener;

    private static final int TYPE_BOTTOM = 798;

    public AgentAdapter() {
    }

    public AgentAdapter(RecyclerView.Adapter adapter) {
        this.mAdapter = adapter;
    }

    public void setAdapter(RecyclerView.Adapter<RecyclerView.ViewHolder> adapter) {
        mAdapter = adapter;
    }

    public void setCanLoadMore(boolean canLoadMore) {
        this.canLoadMore = canLoadMore;
    }

    @Override
    public int getItemViewType(int position) {
        if (canLoadMore && position == mAdapter.getItemCount()) {
            return TYPE_BOTTOM;
        } else {
            return mAdapter.getItemViewType(position);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_BOTTOM) {
            return new BottomViewHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.bottom_layout, parent, false));
        } else {
            return mAdapter.onCreateViewHolder(parent, viewType);
        }
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        if (getItemViewType(position) == TYPE_BOTTOM) {
            BottomViewHolder viewHolder = (BottomViewHolder) holder;
            viewHolder.setLayout(loadProgressId, loadFailureId, loadNoMoreId);

            // 处理StaggeredGrid
            if (viewHolder.itemView.getLayoutParams() instanceof StaggeredGridLayoutManager
                    .LayoutParams) {
                ((StaggeredGridLayoutManager.LayoutParams) viewHolder.itemView.getLayoutParams())
                        .setFullSpan(
                        true);
            }

            switch (mCurrentBottomState) {
                case BOTTOM_STATE_LOADING:
                    viewHolder.showLoading();
                    break;
                case BOTTOM_STATE_NO_MORE:
                    viewHolder.showNoMore();
                    break;
                case BOTTOM_STATE_FAILED:
                    viewHolder.showFailed();
                    viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            loadMore();
                            notifyItemChanged(getItemCount() - 1);
                            listener.onMoreAsked(mAdapter.getItemCount(), 0,
                                    mAdapter.getItemCount() - 1);
                        }
                    });
                    break;
                default:
                    viewHolder.showLoading();
            }
        } else {
            if (mOnItemClickListener != null) {
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mOnItemClickListener.onItemClick(holder.itemView, position);
                    }
                });
            }
            mAdapter.onBindViewHolder(holder, position);
        }
    }

    @Override
    public int getItemCount() {
        return mAdapter.getItemCount() > 0 && canLoadMore ? mAdapter.getItemCount() + 1
                : mAdapter.getItemCount();
    }

    public void loadMore() {
        mCurrentBottomState = BOTTOM_STATE_LOADING;
    }

    public void loadMoreSuccess() {
    }

    public void loadFailure() {
        mCurrentBottomState = BOTTOM_STATE_FAILED;
        notifyItemChanged(getItemCount() - 1);
    }

    public void loadNoMore() {
        mCurrentBottomState = BOTTOM_STATE_NO_MORE;
        notifyItemChanged(getItemCount() - 1);
    }

    public void hideLoadMore() {
        canLoadMore = false;
        notifyItemRemoved(getItemCount());
    }

    public void initBottomState() {
        mCurrentBottomState = BOTTOM_STATE_LOADING;
    }

    public void placeBottomLayout(int bottomProgress, int bottomFailure, int bottomNoMore) {
        loadProgressId = bottomProgress;
        loadFailureId = bottomFailure;
        loadNoMoreId = bottomNoMore;
    }

    public void setOnMoreListener(OnMoreListener listener) {
        this.listener = listener;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mOnItemClickListener = listener;
    }

    private class BottomViewHolder extends RecyclerView.ViewHolder {

        private ViewStub loadMoreProgress;
        private ViewStub loadMoreFailure;
        private ViewStub loadNoMore;

        // 加载布局id
        private int loadProgressId;
        private int loadFailureId;
        private int loadNoMoreId;

        private View mLoadProgress;
        private View mLoadFailure;
        private View mLoadNoMore;

        BottomViewHolder(View itemView) {
            super(itemView);

            loadMoreFailure = (ViewStub) itemView.findViewById(R.id.load_more_failure);
            loadMoreProgress = (ViewStub) itemView.findViewById(R.id.load_more_progress);
            loadNoMore = (ViewStub) itemView.findViewById(R.id.load_no_more);
        }

        void showLoading() {
            mLoadProgress.setVisibility(View.VISIBLE);
        }

        void showFailed() {
            mLoadFailure.setVisibility(View.VISIBLE);
        }

        void showNoMore() {
            mLoadNoMore.setVisibility(View.VISIBLE);
        }

        void setLayout(int progress, int failure, int noMore) {
            loadProgressId = progress;
            loadFailureId = failure;
            loadNoMoreId = noMore;
            initView();
        }

        private void initView() {
            if (loadMoreProgress != null) {
                loadMoreProgress.setLayoutResource(loadProgressId);
                mLoadProgress = loadMoreProgress.inflate();
                loadMoreProgress = null;
            }
            mLoadProgress.setVisibility(View.GONE);

            if (loadMoreFailure != null) {
                loadMoreFailure.setLayoutResource(loadFailureId);
                mLoadFailure = loadMoreFailure.inflate();
                loadMoreFailure = null;
            }
            mLoadFailure.setVisibility(View.GONE);

            if (loadNoMore != null) {
                loadNoMore.setLayoutResource(loadNoMoreId);
                mLoadNoMore = loadNoMore.inflate();
                loadNoMore = null;
            }
            mLoadNoMore.setVisibility(View.GONE);
        }
    }
}
