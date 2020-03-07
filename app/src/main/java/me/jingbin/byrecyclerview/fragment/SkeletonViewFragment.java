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
import me.jingbin.library.skeleton.BySkeleton;
import me.jingbin.library.skeleton.ByStateViewSkeletonScreen;

/**
 * @author jingbin
 */
public class SkeletonViewFragment extends BaseFragment<FragmentRefreshBinding> {

    private static final String TYPE = "mType";
    private String mType = "Android";
    private boolean mIsPrepared;
    private boolean mIsFirst = true;
    private DataAdapter mAdapter;
    private ByRecyclerView recyclerView;
    private int page = 1;
    private ByStateViewSkeletonScreen skeletonScreen;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    public static SkeletonViewFragment newInstance(String type) {
        SkeletonViewFragment fragment = new SkeletonViewFragment();
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
        recyclerView = getView(R.id.recyclerView);
        mAdapter = new DataAdapter();
        LinearLayoutManager layoutManager = new LinearLayoutManager(activity);
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        // 加了分割线，滚动条才会置顶
        SpacesItemDecoration itemDecoration = new SpacesItemDecoration(recyclerView.getContext(), SpacesItemDecoration.VERTICAL, 1);
        if ("drawable".equals(mType)) {
            recyclerView.addItemDecoration(itemDecoration.setDrawable(R.drawable.shape_line_custom));
        } else {
            recyclerView.addItemDecoration(itemDecoration.setParam(R.color.colorBlue, 10, 10, 10));
        }
        recyclerView.setAdapter(mAdapter);
        recyclerView.setOnLoadMoreListener(new ByRecyclerView.OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                recyclerView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (page == 4) {
                            recyclerView.loadMoreEnd();
                            return;
                        }
                        if (page == 2) {
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
                recyclerView.setRefreshing(true);
            }
        });
        recyclerView.setOnRefreshListener(new ByRecyclerView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                recyclerView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        page = 1;
                        mAdapter.setNewData(DataUtil.getMore(activity, 6, page));
                    }
                }, 1000);
            }
        });

        skeletonScreen = BySkeleton
                .bindView(binding.recyclerView)
                .load(R.layout.layout_by_default_item_skeleton)
                .angle(0)
                .color(R.color.colorWhite)// 动画的颜色
                .duration(1000)
                .shimmer(true)
                .show();

        binding.recyclerView.postDelayed(new Runnable() {
            @Override
            public void run() {
                skeletonScreen.hide();
                mAdapter.setNewData(DataUtil.get(activity, 10));
            }
        }, 3000);
        mIsFirst = false;
    }
}
