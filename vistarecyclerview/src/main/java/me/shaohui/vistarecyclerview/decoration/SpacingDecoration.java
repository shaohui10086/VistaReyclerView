package me.shaohui.vistarecyclerview.decoration;

import android.graphics.Rect;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;

/**
 * Created by shaohui on 16/8/3.
 */
public class SpacingDecoration extends RecyclerView.ItemDecoration{

    private int horizontalSpacing;
    private int verticalSpacing;
    private int halfH;
    private int halfV;

    public SpacingDecoration(int spacing) {
        horizontalSpacing = spacing;
        verticalSpacing = spacing;
        halfH = horizontalSpacing/2;
        halfV = verticalSpacing/2;
    }

    public SpacingDecoration(int horizontalSpacing, int verticalSpacing) {
        this.horizontalSpacing = horizontalSpacing;
        this.verticalSpacing = verticalSpacing;
        halfH = horizontalSpacing/2;
        halfV = verticalSpacing/2;
    }

    public void setHorizontalSpacing(int horizontalSpacing) {
        this.horizontalSpacing = horizontalSpacing;
        halfH = horizontalSpacing/2;
    }

    public void setVerticalSpacing(int verticalSpacing) {
        this.verticalSpacing = verticalSpacing;
        halfV = verticalSpacing/2;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        int position = parent.getChildAdapterPosition(view);
        int orientation = 0;
        int spanCount = 0;
        int spanIndex = 0;

        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
        if (layoutManager instanceof StaggeredGridLayoutManager) {
            orientation = ((StaggeredGridLayoutManager) layoutManager).getOrientation();
            spanCount = ((StaggeredGridLayoutManager) layoutManager).getSpanCount();
            spanIndex = ((StaggeredGridLayoutManager.LayoutParams)
                    view.getLayoutParams()).getSpanIndex();
        } else if (layoutManager instanceof GridLayoutManager) {
            orientation = ((GridLayoutManager) layoutManager).getOrientation();
            spanCount = ((GridLayoutManager) layoutManager).getSpanCount();
            spanIndex = ((GridLayoutManager.LayoutParams)view.getLayoutParams()).getSpanIndex();
        } else if (layoutManager instanceof LinearLayoutManager) {
            orientation = ((LinearLayoutManager) layoutManager).getOrientation();
        }

        if (position > 0 && position < parent.getAdapter().getItemCount() - 2) {
            if (spanCount != 0 && spanIndex == 0) {
                if (orientation == OrientationHelper.VERTICAL) {
                    outRect.right = halfH;
                    outRect.top = halfH;
                    outRect.bottom = halfH;
                } else if (orientation == OrientationHelper.HORIZONTAL) {
                    outRect.bottom = halfV;
                }
            } else if (spanIndex == spanCount - 1) {
                if (orientation == OrientationHelper.VERTICAL) {
                    outRect.left = halfH;
                    outRect.top = halfV;
                    outRect.bottom = halfV;
                } else if (orientation == OrientationHelper.HORIZONTAL) {
                    outRect.top = halfV;
                    outRect.right = halfH;
                    outRect.left = halfH;
                }
            } else if (spanCount != 0){
                if (orientation == OrientationHelper.VERTICAL) {
                    outRect.left = halfH;
                    outRect.right = halfH;
                    outRect.top = halfV;
                    outRect.bottom = halfV;
                } else if (orientation == OrientationHelper.HORIZONTAL) {
                    outRect.top = halfV;
                    outRect.bottom = halfV;
                    outRect.right = halfH;
                    outRect.left = halfH;
                }
            }
            if (spanCount == 0 ) {
                if (orientation == OrientationHelper.VERTICAL) {
                    outRect.bottom = halfV;
                    outRect.top = halfV;
                } else if (orientation == OrientationHelper.HORIZONTAL) {
                    outRect.top = halfH;
                    outRect.bottom = halfH;
                }
            }
        } else {
            if (position == 0) {
                if (orientation == OrientationHelper.VERTICAL) {
                    if (spanCount != 0) {
                        outRect.right = halfH;
                        outRect.bottom = halfV;
                        outRect.top = halfH;
                    } else {
                        outRect.bottom = halfV;
                    }
                } else {
                    if (spanCount != 0) {
                        outRect.left = halfH;
                        outRect.right = halfH;
                        outRect.bottom = halfV;
                    } else {
                        outRect.right = halfH;
                    }
                }
            } else if (position == parent.getAdapter().getItemCount() - 2) {
                if (orientation == OrientationHelper.VERTICAL) {
                    if (spanCount != 0) {
                        if (spanIndex == 0) {
                            outRect.right = halfV;
                        } else if (spanIndex == spanCount - 1){
                            outRect.left = halfH;
                        } else {
                            outRect.right = halfV;
                            outRect.left = halfV;
                        }
                        outRect.top = halfV;
                        outRect.bottom = halfV;
                    } else {
                        outRect.top = halfV;
                    }
                } else {
                    if (spanCount != 0) {
                        if (spanIndex == 0) {
                            outRect.bottom = halfV;
                        } else if (spanIndex == spanCount - 1) {
                            outRect.top = halfV;
                        } else {
                            outRect.bottom = halfV;
                            outRect.top = halfV;
                        }
                        outRect.right = halfH;
                        outRect.left = halfH;
                    } else {
                        outRect.left = halfH;
                    }
                }
            }
        }

    }
}
