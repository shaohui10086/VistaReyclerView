package me.shaohui.vistarecyclerview.decoration;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by shaohui on 16/8/3.
 *
 * 目前仅支持LinearLayout
 */
public class DividerDecoration extends RecyclerView.ItemDecoration {

    private ColorDrawable mColorDrawable;
    private int height;
    private int paddingStart;
    private int paddingEnd;

    public DividerDecoration(int color, int height) {
        mColorDrawable = new ColorDrawable(color);
        this.height = height;
    }

    public DividerDecoration(int color, int height, int paddingStart, int paddingEnd) {
        mColorDrawable = new ColorDrawable(color);
        this.height = height;
        this.paddingStart = paddingStart;
        this.paddingEnd = paddingEnd;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        int position = parent.getChildAdapterPosition(view);
        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();

        if (layoutManager instanceof LinearLayoutManager) {
            if (position < parent.getAdapter().getItemCount() - 2) {
                outRect.bottom = height;
            }
        }

    }

    @Override
    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
        RecyclerView.LayoutManager manager = parent.getLayoutManager();
        if (manager instanceof LinearLayoutManager) {
            for (int i = 0 ; i < parent.getAdapter().getItemCount() - 2; i++ ) {
                View child = parent.getChildAt(i);
                int orientation = ((LinearLayoutManager) manager).getOrientation();
                if (orientation == OrientationHelper.VERTICAL) {
                    mColorDrawable.setBounds(
                            child.getLeft() + paddingStart,
                            child.getBottom(),
                            child.getRight() - paddingEnd,
                            child.getBottom() + height);
                } else if (orientation == OrientationHelper.HORIZONTAL) {
                    mColorDrawable.setBounds(
                            child.getRight(),
                            child.getTop() + paddingStart,
                            child.getRight() + height,
                            child.getBottom() - paddingEnd);
                }
                mColorDrawable.draw(c);
            }
        }
    }
}
