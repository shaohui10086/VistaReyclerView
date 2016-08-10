package me.shaohui.vistarecyclerviewsimple;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import jp.wasabeef.recyclerview.adapters.ScaleInAnimationAdapter;
import me.shaohui.vistarecyclerview.OnMoreListener;
import me.shaohui.vistarecyclerview.VistaRecyclerView;
import me.shaohui.vistarecyclerview.decoration.SpacingDecoration;

public class MainActivity extends AppCompatActivity {

    private VistaRecyclerView recyclerView;
    private List<String> data;
    private Handler mHandler = new Handler();

    private int state = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("VistaRecyclerView");
        recyclerView = (VistaRecyclerView) findViewById(R.id.recycler);
        data = new ArrayList<>();
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        SimpleAdapter adapter = new SimpleAdapter(data);

        recyclerView.setAdapter(new ScaleInAnimationAdapter(adapter));
//        recyclerView.addItemDecoration(
//                new DividerDecoration(getResources().getColor(R.color.window_background), 20));
        recyclerView.addItemDecoration(new SpacingDecoration(20));

        recyclerView.setRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadData();
            }
        });

        recyclerView.setOnMoreListener(new OnMoreListener() {
            @Override
            public void noMoreAsked(int total, int left, int current) {
                loadNoMore();
            }
        });


        loadEmptyData();
    }

    private void loadData() {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (state == 5) {
                    Collections.addAll(data, getResources().getStringArray(R.array.image_list));
                    recyclerView.notifyDataSetChanged();
                } else {
                    recyclerView.showErrorView();
                    state = 5;
                }
            }
        }, 1000);
    }

    private void loadEmptyData() {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                recyclerView.showEmptyView();
            }
        }, 1000);
    }

    private void loadNoMore() {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (state == 2) {
                    recyclerView.loadNoMore();
                } else if (state == 1) {
                    Collections.addAll(data, getResources().getStringArray(R.array.image_list_2));
                    recyclerView.notifyDataSetChanged();
                    state = 2;
                } else {
                    recyclerView.loadMoreFailure();
                    state = 1;
                }
            }
        }, 1000);
    }


}
