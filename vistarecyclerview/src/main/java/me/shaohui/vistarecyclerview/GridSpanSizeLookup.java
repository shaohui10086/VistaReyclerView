package me.shaohui.vistarecyclerview;

import android.support.v7.widget.RecyclerView;

/**
 * Created by shaohui on 16/7/31.
 */
public abstract class GridSpanSizeLookup {
    private int maxSize;
    private RecyclerView.Adapter mAdapter;

    public GridSpanSizeLookup(int max) {
        this.maxSize = max;
    }

    public void bindAdapter(RecyclerView.Adapter adapter) {
        this.mAdapter = adapter;
    }

    public abstract int getSpanSize(int position);

    public int returnSpanSize(int position) {
        if (position == mAdapter.getItemCount() - 1) {
            return maxSize;
        } else {
            return getSpanSize(position);
        }
    }

}
