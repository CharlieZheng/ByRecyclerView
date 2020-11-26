package me.jingbin.byrecyclerview.fragment;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import me.jingbin.byrecyclerview.R;
import me.jingbin.byrecyclerview.adapter.MultiAdapter;
import me.jingbin.byrecyclerview.bean.DataItemBean;
import me.jingbin.byrecyclerview.databinding.FragmentRefreshBinding;
import me.jingbin.byrecyclerview.utils.DataUtil;
import me.jingbin.byrecyclerview.utils.LogHelper;
import me.jingbin.byrecyclerview.utils.ToastUtil;
import me.jingbin.library.ByRecyclerView;

/**
 * @author jingbin
 */
public class MultiGridFragment extends BaseFragment<FragmentRefreshBinding> {

    private static final String TYPE = "mType";
    private String mType = "Android";
    private boolean mIsFirst = true;
    private MultiAdapter mAdapter;
    private int page = 1;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    public static MultiGridFragment newInstance(String type) {
        MultiGridFragment fragment = new MultiGridFragment();
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

        initAdapter();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mIsFirst) {
            mAdapter.setNewData(DataUtil.getMultiData(getActivity(), 50));
            binding.recyclerView.addHeaderView(R.layout.layout_header_view);
            mIsFirst = false;
        }
    }

    @Override
    protected void loadData() {
    }

    private void initAdapter() {
        mAdapter = new MultiAdapter();
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 6, RecyclerView.VERTICAL, false);
        binding.recyclerView.setLayoutManager(gridLayoutManager);

        binding.recyclerView.setAdapter(mAdapter);
        binding.recyclerView.setOnRefreshListener(new ByRecyclerView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                page = 1;
                mAdapter.setNewData(DataUtil.getMultiData(getActivity(), 20));
            }
        });
        binding.recyclerView.setOnLoadMoreListener(new ByRecyclerView.OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                if (page == 3) {
                    binding.recyclerView.loadMoreEnd();
                    return;
                }
                page++;
                mAdapter.addData(DataUtil.getMultiData(getActivity(), 50));
                binding.recyclerView.loadMoreComplete();
            }
        }, 500);
        binding.recyclerView.setOnItemClickListener(new ByRecyclerView.OnItemClickListener() {
            @Override
            public void onClick(View v, int position) {
                DataItemBean itemData = mAdapter.getItemData(position);
                ToastUtil.showToast(itemData.getDes() + "-" + position);
            }
        });
    }
}
