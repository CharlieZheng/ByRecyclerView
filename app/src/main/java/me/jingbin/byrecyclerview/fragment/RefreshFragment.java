package me.jingbin.byrecyclerview.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import me.jingbin.byrecyclerview.R;
import me.jingbin.byrecyclerview.adapter.DataAdapter;
import me.jingbin.byrecyclerview.bean.DataItemBean;
import me.jingbin.byrecyclerview.databinding.FragmentRefreshBinding;
import me.jingbin.byrecyclerview.utils.DataUtil;
import me.jingbin.byrecyclerview.utils.ToastUtil;
import me.jingbin.library.ByRecyclerView;
import me.jingbin.library.decoration.SpacesItemDecoration;

/**
 * @author jingbin
 */
public class RefreshFragment extends BaseFragment<FragmentRefreshBinding> {

    private static final String TYPE = "mType";
    private String mType = "Android";
    private boolean mIsPrepared;
    private boolean mIsFirst = true;
    private DataAdapter mAdapter;
    private ByRecyclerView recyclerView;
    private int page = 1;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    public static RefreshFragment newInstance(String type) {
        RefreshFragment fragment = new RefreshFragment();
        Bundle args = new Bundle();
        args.putString(TYPE, type);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mType = getArguments().getString(TYPE);
        }
    }

    @Override
    public int setContent() {
        return R.layout.fragment_refresh;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // 准备就绪
        mIsPrepared = true;
        initAdapter();
    }

    @Override
    protected void loadData() {
        if (!mIsPrepared || !mIsVisible || !mIsFirst) {
            return;
        }
        initAdapter();
    }

    private void initAdapter() {
        // 是否是自动加载更多
        boolean isAutoLoadMore = "auto".equals(mType);

        recyclerView = getView(R.id.recyclerView);
        mAdapter = new DataAdapter(DataUtil.get(activity, 10));
        LinearLayoutManager layoutManager = new LinearLayoutManager(activity);
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        // 加了分割线，滚动条才会置顶
        recyclerView.addItemDecoration(new SpacesItemDecoration(recyclerView.getContext(), SpacesItemDecoration.VERTICAL, 1));
        recyclerView.setAdapter(mAdapter);
        /**
         * 设置自动加载更多一定要通过 【setOnLoadMoreListener】 方式设置！
         *      isAutoLoadMore： 是否自动加载更多，之后的打开/关闭加载更多还是使用 setLoadMoreEnabled()
         *      2： 滑动到倒数第 2 条数据加载，默认1
         *
         * 也可以使用：setOnLoadMoreListener(boolean isAutoLoadMore, OnLoadMoreListener listener)
         */
        recyclerView.setOnLoadMoreListener(isAutoLoadMore, 2, new ByRecyclerView.OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                recyclerView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (page == 4) {
                            recyclerView.loadMoreEnd();
                            return;
                        }
                        if (page == 1) {
                            page++;
                            recyclerView.loadMoreFail();
                            return;
                        }
                        page++;
                        mAdapter.addData(DataUtil.getMore(activity, 6, page));
                        recyclerView.loadMoreComplete();
                    }
                }, 1000);
            }
        });
        recyclerView.setOnItemClickListener(new ByRecyclerView.OnItemClickListener() {
            @Override
            public void onClick(View v, int position) {
                DataItemBean itemData = mAdapter.getItemData(position);
                ToastUtil.showToast(itemData.getTitle());
//                recyclerView.setRefreshing(true);
            }
        });
        recyclerView.setOnRefreshListener(new ByRecyclerView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                page = 1;
                mAdapter.setNewData(DataUtil.getMore(activity, 10, page));
            }
        }, 500);
        mIsFirst = false;
    }
}
