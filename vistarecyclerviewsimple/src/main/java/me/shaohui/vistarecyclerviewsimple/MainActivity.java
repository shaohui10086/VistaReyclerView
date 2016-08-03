package me.shaohui.vistarecyclerviewsimple;

import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import me.shaohui.vistarecyclerview.GridSpanSizeLookup;
import me.shaohui.vistarecyclerview.OnMoreListener;
import me.shaohui.vistarecyclerview.VistaRecyclerView;
import me.shaohui.vistarecyclerview.decoration.SpacingDecoration;

public class MainActivity extends AppCompatActivity {

    private VistaRecyclerView recyclerView;
    private List<String> data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = (VistaRecyclerView) findViewById(R.id.recycler);
        data = new ArrayList<>();

        SimpleAdapter adapter = new SimpleAdapter(data);
        recyclerView.setAdapter(adapter);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(gridLayoutManager);
//        recyclerView.setGridSpanSizeLookup(new GridSpanSizeLookup(2) {
//            @Override
//            public int getSpanSize(int position) {
//                if (position == 3) {
//                    return 2;
//                } else {
//                    return 1;
//                }
//            }
//        });

//        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.addItemDecoration(new SpacingDecoration(20));

        recyclerView.setRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        for (int i = 1; i< 10; i++) {
                            data.add("测试数据Refresh");
                        }
                        recyclerView.notifyDataSetChanged();
                    }
                }, 2000);
            }
        });
        recyclerView.setOnMoreListener(new OnMoreListener() {
            @Override
            public void noMoreAsked(int total, int left, int current) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
//                        for (int i = 1; i< 10; i++) {
//                            data.add("测试数据BottomMore");
//                        }
//                        recyclerView.notifyDataSetChanged();
                        recyclerView.loadNoMore();
                    }
                }, 5000);
            }
        }, 2);

        loadData();

        recyclerView.setRefreshing(true);
        recyclerView.setErrorListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recyclerView.setRefreshing(true);
                loadData();
            }
        });
//        recyclerView.setRefreshColorSchemeColors(new int[]{getResources().getColor(R.color.colorAccent)});
    }

    private void loadData() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                for (int i = 1; i< 10; i++) {
                    data.add("测试数据");
                }
                recyclerView.notifyDataSetChanged();
//                recyclerView.showErrorView();
            }
        }, 1000);
    }

}
