package com.tct.calculator.fragment;

import android.app.Activity; // MODIFIED by qiong.liu1, 2017-04-26,BUG-4598661
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log; // MODIFIED by qiong.liu1, 2017-04-21,BUG-4452809
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import com.tct.calculator.Calculator;
import android.view.inputmethod.InputMethodManager;
import com.tct.calculator.R;
import com.tct.calculator.data.DBOperation;

import java.util.ArrayList;
import java.util.List;
import android.text.TextUtils;
import com.tct.calculator.adapter.CurrencyListAdapter;
import com.tct.calculator.data.CurrencyListBean;
import com.tct.calculator.adapter.CurrencyListAdapter.OnRecyclerViewItemClickListener;
import com.tct.calculator.utils.Constant; // MODIFIED by qiong.liu1, 2017-04-26,BUG-4598661
import com.tct.calculator.utils.Utils;
import android.view.WindowManager;
import ca.barrenechea.widget.recyclerview.decoration.DividerDecoration;
import ca.barrenechea.widget.recyclerview.decoration.StickyHeaderDecoration;

public class CurrencyList_Fragment extends Fragment implements View.OnClickListener{

    private View mBackImage;
    private EditText mSearchEditText;
    private View mCancelText;
    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLinearLayoutManager;
    private List<CurrencyListBean> mAllCurrencyNameList;
    private List<CurrencyListBean> mQueryList;
    private CurrencyListAdapter mAdapter;
    private DBOperation mDBOperation;
    private QueryAllCurrencyTask mQueryAllCurrencyTask;
    /* MODIFIED-BEGIN by qiong.liu1, 2017-04-26,BUG-4598661*/
    private static Calculator mActivity;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = (Calculator) activity;
    }
    /* MODIFIED-END by qiong.liu1,BUG-4598661*/

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_currency_list,container,false);
        intiView(view);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ((Calculator) getActivity()).hidGuideButton(0.0f);
        ((Calculator) getActivity()).setShowStatusForGuideLayout(true);
        setStatusForSoftInputMethod(false);
    }

    private void intiView(View view) {

        mBackImage = view.findViewById(R.id.back_imageview);
        mSearchEditText = (EditText) view.findViewById(R.id.search_edit);
        mCancelText = view.findViewById(R.id.cancel_text);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_list);
        mBackImage.setOnClickListener(this);
        mCancelText.setOnClickListener(this);
        mCancelText.setVisibility(View.GONE);
        mSearchEditText.addTextChangedListener(new MyTextWatcher());
        mSearchEditText.requestFocus();
        setStatusForSoftInputMethod(true);
        /* MODIFIED-BEGIN by qiong.liu1, 2017-05-04,BUG-4656997*/
        mLinearLayoutManager = new LinearLayoutManager(this.getActivity(),
                LinearLayoutManager.VERTICAL, false); // MODIFIED by qiong.liu1, 2017-05-04,BUG-4656997
                /* MODIFIED-END by qiong.liu1,BUG-4656997*/
        mDBOperation = DBOperation.getInstance(getActivity());
        mQueryAllCurrencyTask = new QueryAllCurrencyTask();
        mQueryAllCurrencyTask.execute();

        DividerDecoration divider = new DividerDecoration.Builder(this.getActivity())
            .setHeight(R.dimen.default_divider_height)
            .setPadding(R.dimen.default_divider_padding)
            .setColorResource(android.R.color.white)
            .build();

        mLinearLayoutManager = new LinearLayoutManager(this.getActivity());
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mRecyclerView.addItemDecoration(divider);
        mAdapter = new CurrencyListAdapter(getActivity(), mAllCurrencyNameList, this);

        StickyHeaderDecoration decor = new StickyHeaderDecoration(mAdapter);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addItemDecoration(decor, 1);
        mAdapter.setOnItemClickListener(new OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View view, String data) {
                getActivity().getSupportFragmentManager().popBackStack();
                ((Calculator) getActivity()).starItemClick(data);
                mDBOperation.updateCurrencyWheelData(data);
                // Update DB when search content is not null.
                if (!TextUtils.isEmpty(mSearchEditText.getText().toString())) {
                    mDBOperation.updateHistoryCurrencyListData(data);
                }
            }
        });
        /* MODIFIED-BEGIN by qiong.liu1, 2017-04-26,BUG-4598661*/
        ((Calculator) getActivity()).setShowStatusForGuideLayout(false);
        mActivity.setShowExpandLayout(true);
        /* MODIFIED-END by qiong.liu1,BUG-4598661*/
    }

    /**
     * Set currency search history data.
     */
    private void setSearchHistoryData() {
        int index = -1;
        ArrayList<CurrencyListBean> historyData = mDBOperation.queryHistoryCurrencyListData();

        if (historyData != null && historyData.size() > 0) {
            for (int i = 0; i < historyData.size(); i++) {
                index++;
                if (mAllCurrencyNameList != null) {
                    mAllCurrencyNameList.add(index, historyData.get(i));
                }
            }
        }
    }

    public String getCurrencyFlag() {
        return ((Calculator) getActivity()).getCurrencySelectData();
    }

    /**
     * Set input method status.
     *
     * @param isShow true:show, false:hide
     */
    private void setStatusForSoftInputMethod(boolean isShow) {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(getActivity().INPUT_METHOD_SERVICE);
        if (isShow) {
            imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, InputMethodManager.HIDE_NOT_ALWAYS);
        } else {
            if (imm.isActive()) {
                imm.hideSoftInputFromWindow(mSearchEditText.getApplicationWindowToken(), 0);
            }
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.back_imageview:
                this.getActivity().getSupportFragmentManager().popBackStack();
                break;
            case R.id.cancel_text:
                if (mSearchEditText != null) {
                    mSearchEditText.setText("");
                }
                break;
            default:
                /* MODIFIED-BEGIN by qiong.liu1, 2017-05-02,BUG-4598039*/
                if (Calculator.mScreenState != Constant.SCREEN_PORT_FULL
                        && Calculator.mScreenState != Constant.SCREEN_LAND_FULL) {
                    mActivity.setShowExpandLayout(false);
                }
                /* MODIFIED-END by qiong.liu1,BUG-4598039*/
                break;
        }
    }

    private class MyTextWatcher implements TextWatcher {


        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {


        }

        @Override
        public void afterTextChanged(Editable editable) {
            if(null == editable.toString() || editable.length() == 0){
                mCancelText.setVisibility(View.GONE);
            }else{
                mCancelText.setVisibility(View.VISIBLE);
            }
            queryNameList(editable.toString());
        }
    }

    /**
     * Query name according to content of input.
     *
     * @param queryStr value of input
     **/
    private void queryNameList(String queryStr) {
        if (!TextUtils.isEmpty(queryStr)) {
            mQueryList = mDBOperation.queryCurrencyListSearchData(queryStr);
            if (mQueryList != null && mQueryList.size() > 0) {
                notifyAdapter(mQueryList);
            }
        } else {
            notifyAdapter(mAllCurrencyNameList);
        }
    }

    /**
     * Update data for CurrencyListAdapter.
     *
     * @param queryList result of query
     */
    private void notifyAdapter(List<CurrencyListBean> queryList) {
        if (mAdapter != null && queryList != null) {
            mAdapter.setNameList(queryList);
            mAdapter.notifyDataSetChanged();
        }
    }

    class QueryAllCurrencyTask extends AsyncTask<ArrayList<CurrencyListBean>, Object, Boolean> {

        @Override
        protected Boolean doInBackground(ArrayList<CurrencyListBean>... arrayLists) {
            mAllCurrencyNameList = mDBOperation.queryAllCurrencyList();
            setSearchHistoryData();
            return mAllCurrencyNameList != null ? true : false;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            if (result) {
                notifyAdapter(mAllCurrencyNameList);
            }
        }
    }
}
