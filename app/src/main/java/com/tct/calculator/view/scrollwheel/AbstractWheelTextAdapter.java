package com.tct.calculator.view.scrollwheel;

import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tct.calculator.Calculator;
import com.tct.calculator.R;
import com.tct.calculator.convert.Unit;
import com.tct.calculator.data.Currency;


/**
 * Abstract wheel adapter provides common functionality for adapters.
 */
public abstract class AbstractWheelTextAdapter extends AbstractWheelAdapter {
    private static final String TAG = "AbstractWheelTextAdapter";
    /**
     * Text view resource. Used as a default view for adapter.
     */
    public static final int TEXT_VIEW_ITEM_RESOURCE = -1;

    /**
     * No resource constant.
     */
    protected static final int NO_RESOURCE = 0;

    /**
     * Default text color
     */
    public static final int DEFAULT_TEXT_COLOR = 0xFF101010;

    /**
     * Default text color
     */
    public static final int LABEL_COLOR = 0xFF700070;

    /**
     * Default text size
     */
    public static final int DEFAULT_TEXT_SIZE = 24;

    // Text settings
    private int textColor = DEFAULT_TEXT_COLOR;
    private int textSize = DEFAULT_TEXT_SIZE;

    // Current context
    protected Context context;
    // Layout inflater
    protected LayoutInflater inflater;

    // Items resources
    protected int itemResourceId;
    protected int itemTextResourceId;

    // Empty items resources
    protected int emptyItemResourceId;
    protected float desity;

    private Typeface mDinTf;
    private Context mContext;

    /**
     * Constructor
     *
     * @param context the current context
     */
    protected AbstractWheelTextAdapter(Context context) {
        this(context, TEXT_VIEW_ITEM_RESOURCE);
        this.mContext = context;
        init(context);
    }

    /**
     * Constructor
     *
     * @param context      the current context
     * @param itemResource the resource ID for a layout file containing a TextView to use
     *                     when instantiating items views
     */
    protected AbstractWheelTextAdapter(Context context, int itemResource) {
        this(context, itemResource, NO_RESOURCE);
        init(context);
    }

    /**
     * Constructor
     *
     * @param context          the current context
     * @param itemResource     the resource ID for a layout file containing a TextView to use
     *                         when instantiating items views
     * @param itemTextResource the resource ID for a text view in the item layout
     */
    protected AbstractWheelTextAdapter(Context context, int itemResource, int itemTextResource) {
        this.mContext = context;
        itemResourceId = itemResource;
        itemTextResourceId = itemTextResource;
        itemResourceId = R.layout.calculator_wheel_item;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        init(context);
    }

    private void init(Context context) {
        desity = context.getResources().getDisplayMetrics().density;
//        mDinTf = FontUtil.getDinBoldTypeface(context);
    }

    /**
     * Gets text color
     *
     * @return the text color
     */
    public int getTextColor() {
        return textColor;
    }

    /**
     * Sets text color
     *
     * @param textColor the text color to set
     */
    public void setTextColor(int textColor) {
        this.textColor = textColor;
    }

    /**
     * Gets text size
     *
     * @return the text size
     */
    public int getTextSize() {
        return textSize;
    }

    /**
     * Sets text size
     *
     * @param textSize the text size to set
     */
    public void setTextSize(int textSize) {
        this.textSize = textSize;
    }

    /**
     * Gets resource Id for items views
     *
     * @return the item resource Id
     */
    public int getItemResource() {
        return itemResourceId;
    }

    /**
     * Sets resource Id for items views
     *
     * @param itemResourceId the resource Id to set
     */
    public void setItemResource(int itemResourceId) {
        this.itemResourceId = itemResourceId;
    }

    /**
     * Gets resource Id for text view in item layout
     *
     * @return the item text resource Id
     */
    public int getItemTextResource() {
        return itemTextResourceId;
    }

    /**
     * Sets resource Id for text view in item layout
     *
     * @param itemTextResourceId the item text resource Id to set
     */
    public void setItemTextResource(int itemTextResourceId) {
        this.itemTextResourceId = itemTextResourceId;
    }

    /**
     * Gets resource Id for empty items views
     *
     * @return the empty item resource Id
     */
    public int getEmptyItemResource() {
        return emptyItemResourceId;
    }

    /**
     * Sets resource Id for empty items views
     *
     * @param emptyItemResourceId the empty item resource Id to set
     */
    public void setEmptyItemResource(int emptyItemResourceId) {
        this.emptyItemResourceId = emptyItemResourceId;
    }

    /**
     * Returns text for specified item
     *
     * @param index the item index
     * @return the text of specified items
     */
    protected abstract CharSequence getItemText(int index);

    protected abstract Unit getItemTextArray(int index);

    protected abstract Currency getCurrencyItemTextArray(int index);

    @Override
    public View getItem(int index, View convertView, ViewGroup parent) {
        if (index >= 0 && index < getItemsCount()) {
            if (convertView == null) {
                convertView = getView(itemResourceId, parent);
            }
            return convertView;
        }
        return null;
    }

    public void setMaxWidth(int width) {
        mMaxWidth = width;
    }

    private int mMaxWidth;

    @Override
    public View getEmptyItem(View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = getView(emptyItemResourceId, parent);
        }
        if (emptyItemResourceId == TEXT_VIEW_ITEM_RESOURCE && convertView instanceof TextView) {
            configureTextView((TextView) convertView);
        }

        return convertView;
    }

    /**
     * Configures text view. Is called for the TEXT_VIEW_ITEM_RESOURCE views.
     *
     * @param view the text view to be configured
     */
    protected void configureTextView(TextView view) {
        view.setTextColor(textColor);
        view.setGravity(Gravity.CENTER);
        view.setTextSize(textSize);
        view.setLines(1);

        if (mDinTf != null) {
            view.setTypeface(mDinTf);
        } else {
            view.setTypeface(Typeface.SANS_SERIF, Typeface.BOLD);
        }
    }

    protected void configureView(TextView view) {
        view.setTextColor(textColor);
        view.setGravity(Gravity.CENTER);
        view.setTextSize(textSize);
        view.setLines(1);

        if (mDinTf != null) {
            view.setTypeface(mDinTf);
        } else {
            view.setTypeface(Typeface.SANS_SERIF, Typeface.BOLD);
        }
    }

    /**
     * Loads a text view from view
     *
     * @param view         the text view or layout containing it
     * @param textResource the text resource Id in layout
     * @return the loaded text view
     */
    private TextView getTextView(View view, int textResource) {
        TextView text = null;
        try {
            if (textResource == NO_RESOURCE && view instanceof TextView) {
                text = (TextView) view;
            } else if (textResource != NO_RESOURCE) {
                text = (TextView) view.findViewById(textResource);
            }
        } catch (ClassCastException e) {
            Log.e("AbstractWheelAdapter", "You must supply a resource ID for a TextView");
            throw new IllegalStateException(
                    "AbstractWheelAdapter requires the resource ID to be a TextView", e);
        }

        return text;
    }

    /**
     * Loads view from resources
     *
     * @param resource the resource Id
     * @return the loaded view or null if resource is not set
     */
    public View getView(int resource, ViewGroup parent) {
        return LayoutInflater.from(mContext).inflate(R.layout.calculator_wheel_item, null);
    }
}
