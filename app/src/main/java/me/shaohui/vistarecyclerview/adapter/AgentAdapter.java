package me.shaohui.vistarecyclerview.adapter;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;

import me.shaohui.vistarecyclerview.R;

/**
 * Created by shaohui on 16/7/31.
 */
public class AgentAdapter extends RecyclerView.Adapter{

    private RecyclerView.Adapter<RecyclerView.ViewHolder> mAdapter;
    private BottomViewHolder bottomViewHolder;

    public static final int TYPE_BOTTOM = 798;

    public AgentAdapter(RecyclerView.Adapter<RecyclerView.ViewHolder> adapter) {
        mAdapter = adapter;
    }

    public void setAdapter(RecyclerView.Adapter<RecyclerView.ViewHolder> adapter) {
        mAdapter = adapter;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == mAdapter.getItemCount()) {
            return TYPE_BOTTOM;
        } else {
            return mAdapter.getItemViewType(position);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_BOTTOM) {
            if (bottomViewHolder == null) {
                bottomViewHolder =  new BottomViewHolder(LayoutInflater
                        .from(parent.getContext()).inflate(R.layout.bottom_layout, parent, false));
            }
            return bottomViewHolder;
        } else {
            return mAdapter.onCreateViewHolder(parent, viewType);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == TYPE_BOTTOM) {
            //TODO

            // 处理StaggeredGrid
            BottomViewHolder viewHolder = (BottomViewHolder) holder;
            if (viewHolder.itemView.getLayoutParams() instanceof StaggeredGridLayoutManager.LayoutParams) {
                ((StaggeredGridLayoutManager.LayoutParams) viewHolder.itemView.getLayoutParams()).setFullSpan(true);
            }
        } else {
            mAdapter.onBindViewHolder(holder, position);
        }
    }

    @Override
    public int getItemCount() {
        return mAdapter.getItemCount() + 1;
    }

    public void loadMore() {
        bottomViewHolder.mLoadFailure.setVisibility(View.GONE);
        bottomViewHolder.mLoadNoMore.setVisibility(View.GONE);
        bottomViewHolder.mLoadProgress.setVisibility(View.VISIBLE);
    }

    public void loadMoreSuccess() {
        bottomViewHolder.mLoadProgress.setVisibility(View.GONE);
    }

    public void loadFailure() {
        bottomViewHolder.mLoadProgress.setVisibility(View.GONE);
        bottomViewHolder.mLoadFailure.setVisibility(View.VISIBLE);
    }

    public void loadNoMore() {
        bottomViewHolder.mLoadProgress.setVisibility(View.GONE);
        bottomViewHolder.mLoadNoMore.setVisibility(View.VISIBLE);
    }

    public void setLoadProgressId(int loadProgressId) {
        bottomViewHolder.loadProgressId = loadProgressId;
    }

    public void setLoadFailureId(int loadFailureId) {
        bottomViewHolder.loadFailureId = loadFailureId;
    }

    public void setLoadNoMoreId(int loadNoMoreId) {
        bottomViewHolder.loadNoMoreId = loadNoMoreId;
    }

    public class BottomViewHolder extends RecyclerView.ViewHolder {

        private ViewStub loadMoreProgress;
        private ViewStub loadMoreFailure;
        private ViewStub loadNoMore;

        // 默认布局
        private int loadProgressId = R.layout.bottom_load_progress;
        private int loadFailureId = R.layout.bottom_load_failure;
        private int loadNoMoreId = R.layout.bottom_load_no_more;

        private View mLoadProgress;
        private View mLoadFailure;
        private View mLoadNoMore;

        private View itemView;

        public BottomViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;

            loadMoreFailure = (ViewStub) itemView.findViewById(R.id.load_more_failure);
            loadMoreProgress = (ViewStub) itemView.findViewById(R.id.load_more_progress);
            loadNoMore = (ViewStub) itemView.findViewById(R.id.load_no_more);

            initView();
        }

        private void initView() {
            loadMoreProgress.setLayoutResource(loadProgressId);
            mLoadProgress = loadMoreProgress.inflate();
            loadMoreProgress.setVisibility(View.GONE);
            mLoadProgress.setVisibility(View.GONE);

            loadMoreFailure.setLayoutResource(loadFailureId);
            mLoadFailure = loadMoreFailure.inflate();
            loadMoreFailure.setVisibility(View.GONE);
            mLoadFailure.setVisibility(View.GONE);

            loadNoMore.setLayoutResource(loadNoMoreId);
            mLoadNoMore = loadNoMore.inflate();
            loadNoMore.setVisibility(View.GONE);
            mLoadNoMore.setVisibility(View.GONE);
        }
    }
}
