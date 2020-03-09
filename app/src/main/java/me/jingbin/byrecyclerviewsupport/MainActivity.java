package me.jingbin.byrecyclerviewsupport;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;

import me.jingbin.byrecyclerviewsupport.databinding.ActivityMainBinding;
import me.jingbin.library.ByRecyclerView;
import me.jingbin.library.decoration.SpacesItemDecoration;
import me.jingbin.library.skeleton.ByRVItemSkeletonScreen;
import me.jingbin.library.skeleton.BySkeleton;

/**
 * @author jingbin
 */
public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        final DataAdapter dataAdapter = new DataAdapter();
        final ByRecyclerView recyclerView = findViewById(R.id.recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new SpacesItemDecoration(this, SpacesItemDecoration.VERTICAL, 2).setParam(R.color.colorAccent, 1));
        recyclerView.addHeaderView(R.layout.header_view);
//        recyclerView.setAdapter(dataAdapter);

        recyclerView.setOnLoadMoreListener(new ByRecyclerView.OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                dataAdapter.addData(getData());
                recyclerView.loadMoreComplete();
            }
        }, 1000);

        binding.recyclerView.setOnRefreshListener(new ByRecyclerView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                binding.recyclerView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        dataAdapter.setNewData(getData());
                    }
                }, 1000);
            }
        });

        // 骨架图
        final ByRVItemSkeletonScreen skeletonScreen = BySkeleton.bindItem(binding.recyclerView).adapter(dataAdapter).show();
        binding.recyclerView.postDelayed(new Runnable() {
            @Override
            public void run() {
                skeletonScreen.hide();
                dataAdapter.setNewData(getData());
            }
        }, 3000);
    }


    private ArrayList<DataBean> getData() {
        ArrayList<DataBean> dataBeans = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            DataBean dataBean = new DataBean("我是数据");
            dataBeans.add(dataBean);
        }
        return dataBeans;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding.recyclerView.destroy();
    }
}
