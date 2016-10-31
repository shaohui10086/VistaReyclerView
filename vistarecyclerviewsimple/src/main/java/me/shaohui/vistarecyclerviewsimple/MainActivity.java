package me.shaohui.vistarecyclerviewsimple;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;

import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import jp.wasabeef.recyclerview.adapters.ScaleInAnimationAdapter;
import me.shaohui.vistarecyclerview.OnItemClickListener;
import me.shaohui.vistarecyclerview.OnMoreListener;
import me.shaohui.vistarecyclerview.VistaRecyclerView;
import me.shaohui.vistarecyclerview.decoration.DividerDecoration;
import me.shaohui.vistarecyclerview.decoration.SpacingDecoration;

public class MainActivity extends AppCompatActivity {

    private VistaRecyclerView recyclerView;
    private List<String> mData;
    private Handler mHandler = new Handler();

    private int state = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("VistaRecyclerView");
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        recyclerView = (VistaRecyclerView) findViewById(R.id.recycler);
        mData = new ArrayList<>();
        SimpleAdapter adapter = new SimpleAdapter(mData);

        recyclerView.setAdapter(adapter);
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

        recyclerView.setRetryListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadData();
            }
        });

        recyclerView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Toast.makeText(MainActivity.this, position + "", Toast.LENGTH_SHORT).show();
            }
        });

        recyclerView.setRefreshing(true);
        loadData();
    }

    private void loadData() {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Collections.addAll(mData, getResources().getStringArray(R.array.image_list));
                recyclerView.notifyDataSetChanged();
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
                recyclerView.loadNoMore();
            }
        }, 1000);
    }


}
