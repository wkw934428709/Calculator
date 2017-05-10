package com.tct.calculator.convert;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tct.calculator.Calculator;
import com.tct.calculator.R;

import java.util.List;


public class ConvertSlideAdapter extends BaseAdapter {
    private Context mContext;
    private LayoutInflater inflater;
    List<Convert> mConverts;
    private static final String TAG = "ConvertSlideAdapter";

    public ConvertSlideAdapter(Context context, List<Convert> converts) {
        this.mContext = context;
        inflater = LayoutInflater.from(context);
        this.mConverts = converts;
    }

    private final int LEFT_TOP_INDEX = 0;
    private final int LEFT_BOTTOM_INDEX = 6;

    @Override
    public int getCount() {
        return mConverts.size();
    }

    @Override
    public Object getItem(int position) {
        return mConverts.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        Convert convert = mConverts.get(position);
        if (convertView == null) {
            holder = new ViewHolder();
            if(Calculator.mScreenState == Calculator.SCREENPORTFULL ){
                convertView = inflater.inflate(R.layout.convert_grid_item_layout, null);
            }else{
                convertView = inflater.inflate(R.layout.convert_grid_item_layout_split,null);
            }
            holder.programItemBg = (RelativeLayout) convertView
                    .findViewById(R.id.grid_item_program_layout);
            holder.convertName = (TextView) convertView.findViewById(R.id.program_name);
            holder.convertIcon = (ImageView) convertView.findViewById(R.id.program_icon);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) holder.programItemBg.getLayoutParams();
        params.height = (mTotalHeight - mDividerHeight * 2) / 3 + 1;
        Log.i(TAG, "===XXX===ConvertActivity=params.height=" + params.height + ",mTotalHeight=" + mTotalHeight + ",mDividerHeight=" + mDividerHeight);
        holder.programItemBg.setTag(convert);
        holder.convertName
                .setText(convert.getDisplayName());
        holder.convertIcon.setImageResource(convert.getConvertImgResId());
        if (position == LEFT_TOP_INDEX) {
            holder.programItemBg.setBackgroundResource(R.drawable.convert_gridview_item_top_left);
        } else if (position == LEFT_BOTTOM_INDEX) {
            holder.programItemBg.setBackgroundResource(R.drawable.convert_gridview_item_bottom_left);
        } else {
            holder.programItemBg.setBackgroundResource(R.drawable.convert_gridview_item_common);
        }
        if (convert.isSelected()) {
            if (position == LEFT_TOP_INDEX) {
                holder.programItemBg.setBackgroundResource(R.drawable.convert_gridview_item_top_left_pressed);
            } else if (position == LEFT_BOTTOM_INDEX) {
                holder.programItemBg.setBackgroundResource(R.drawable.convert_gridview_item_bottom_left_pressed);
            } else {
                holder.programItemBg.setBackgroundResource(R.drawable.convert_gridview_item_common_pressed);
            }
        }
        return convertView;
    }

    private int mTotalHeight;
    private int mDividerHeight;

    public void setTotalHeight(int height) {
        this.mTotalHeight = height;
        mDividerHeight = mContext.getResources().getDimensionPixelSize(R.dimen.default_divider_height);
        Log.i(TAG, "===XXX===ConvertSlideAdapter=setTotalHeight=height=" + height + ",mDividerHeight=" + mDividerHeight);
    }

    private final class ViewHolder {
        private RelativeLayout programItemBg;
        private TextView convertName;
        private ImageView convertIcon;
    }
}
