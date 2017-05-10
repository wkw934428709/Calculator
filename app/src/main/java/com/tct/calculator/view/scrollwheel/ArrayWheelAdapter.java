package com.tct.calculator.view.scrollwheel;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.tct.calculator.R;
import com.tct.calculator.data.Currency;

import com.tct.calculator.convert.Unit;


/**
 * The simple Array wheel adapter
 */
public class ArrayWheelAdapter extends AbstractWheelTextAdapter {

    // items
    private Unit items[];

    /**
     * Constructor
     *
     * @param context the current context
     * @param items   the items
     */
    public ArrayWheelAdapter(Context context, Unit items[]) {
        super(context);

        // setEmptyItemResource(TEXT_VIEW_ITEM_RESOURCE);
        this.items = items;
    }

    @Override
    public CharSequence getItemText(int index) {
//        if (index >= 0 && index < items.length) {
//            T item = items[index];
//            if (item instanceof CharSequence) {
//                return (CharSequence) item;
//            }
//            return item.toString();
//        }
        return null;
    }

    @Override
    protected Unit getItemTextArray(int index) {
        if (index >= 0 && index < items.length) {
            Unit item = items[index];
            if (item instanceof Unit) {
                return (Unit) item;
            }
        }
        return null;
    }

    @Override
    protected Currency getCurrencyItemTextArray(int index) {
        return null;
    }

    @Override
    public int getItemsCount() {
        return items.length;
    }

    @Override
    public View getItem(int index, View convertView, ViewGroup parent) {
        super.getItem(index, convertView, parent);
        if (index >= 0 && index < getItemsCount()) {
            if (convertView == null) {
                convertView = getView(itemResourceId, parent);
            }
            if (convertView != null) {
                Unit text = getItemTextArray(index);
                TextView full_name = (TextView) convertView.findViewById(R.id.full_name);
                full_name.setText(text.getFullName());
                TextView abbreviation = (TextView) convertView.findViewById(R.id.abbreviation);
                abbreviation.setText(text.getAbbreviation());
                if (itemResourceId == TEXT_VIEW_ITEM_RESOURCE) {
//                    configureView(textView);
                    /*
                     * 目前不需要压缩item的宽度 TextPaint tp = textView.getPaint(); float
                     * width = tp.measureText(text,0,text.length());
                     * if(mMaxWidth!=0 && width>mMaxWidth){ //TODO 我们应该把这个值缓存以下
                     * tp.setTextScaleX(((float)mMaxWidth)/ (desity*width) );
                     * }else{ tp.setTextScaleX(1.0f); }
                     */
                }
            }
            return convertView;
        }
        return null;
    }
}
