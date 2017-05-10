package com.tct.calculator.view.scrollwheel;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tct.calculator.R;
import com.tct.calculator.convert.Unit;
import com.tct.calculator.data.Currency;


/**
 * The simple Array wheel adapter
 */
public class CurrencyArrayWheelAdapter extends AbstractWheelTextAdapter {

    // items
    private Currency items[];

    /**
     * Constructor
     *
     * @param context the current context
     * @param items   the items
     */
    public CurrencyArrayWheelAdapter(Context context, Currency items[]) {
        super(context);

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
        return null;
    }

    @Override
    protected Currency getCurrencyItemTextArray(int index) {
        if (index >= 0 && index < items.length) {
            Currency item = items[index];
            if (item instanceof Currency) {
                return (Currency) item;
            }
        }
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
                Currency text = getCurrencyItemTextArray(index);
                TextView full_name = (TextView) convertView.findViewById(R.id.full_name);
                full_name.setText(text.getCountryUnit());
                TextView abbreviation = (TextView) convertView.findViewById(R.id.abbreviation);
                abbreviation.setText(text.getCountryCode());
                if (itemResourceId == TEXT_VIEW_ITEM_RESOURCE) {
//                    configureView(textView);
                    /*
                     * This method is to conpress the height of item's textView
                     * TextPaint tp = textView.getPaint(); float
                     * width = tp.measureText(text,0,text.length());
                     * if(mMaxWidth!=0 && width>mMaxWidth){
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
