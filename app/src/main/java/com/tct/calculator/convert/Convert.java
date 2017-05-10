package com.tct.calculator.convert;

import android.content.Context;
import android.content.res.Resources;
import android.text.TextUtils;
import android.util.Log;

import com.tct.calculator.R;
import com.tct.calculator.utils.Constant;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by user on 9/29/16.
 */
public class Convert {
    private static final String TAG = "Convert";
    private List<Unit> unit;
    private String convertName;
    private int convertImgResId;
    private int convertHistoryImgResId;
    private boolean isSelected;
    private int mUnitsRes;
    private Context mContext;
    private int defaultRightUnitIndex = -1;
    private int defaultLeftUnitIndex = -1;
    private Unit defaultLeftUnit, defaultRightUnit;
    private Constant.ConvertType currentType;
    private int convertIndex;
    private String displayName;

    public Convert(Context context, String convertName, String displayName, int convertImgResId, int convertHistoryImgResId, int unitsRes) {
        this.mContext = context;
        this.convertName = convertName;
        this.displayName = displayName;
        this.convertImgResId = convertImgResId;
        this.convertHistoryImgResId = convertHistoryImgResId;
        this.mUnitsRes = unitsRes;
        initUnits();
    }


    private void initUnits() {
        try {

            if (!TextUtils.isEmpty(convertName)) {
                Constant.ConvertType[] convertTypes = Constant.ConvertType.values();
                for (Constant.ConvertType type : convertTypes) {
                    if (type.name().equalsIgnoreCase(convertName)) {
                        currentType = type;
                        break;
                    }
                }
            }

            unit = new ArrayList<Unit>();
            String[] units = mContext.getResources().getStringArray(mUnitsRes);
            for (int i = 0; i < units.length; i++) {
                String unitString = units[i];
                if (!TextUtils.isEmpty(unitString)) {
                    String[] names = unitString.split("\\$");
                    int defaultValue = Integer.parseInt(names[2]);
                    Log.i(TAG, "===XXX===unitString" + unitString);
                    unit.add(new Unit(mContext,names[0], names[1], defaultValue));
                }
            }
            if (!TextUtils.isEmpty(convertName)) {
                convertIndex = Constant.ConvertType.valueOf(convertName.toUpperCase()).ordinal();
            }
        } catch (Resources.NotFoundException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public List<Unit> getUnit() {
        return unit;
    }

    public void setUnit(List<Unit> unit) {
        this.unit = unit;
    }

    public String getConvertName() {
        return convertName;
    }

    public void setConvertName(String convertName) {
        this.convertName = convertName;
    }

    public int getConvertImgResId() {
        return convertImgResId;
    }

    public void setConvertImgResId(int convertImgResId) {
        this.convertImgResId = convertImgResId;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public String toString() {
        return "Convert convertName:" + convertName + ",currentType:" + currentType + ",isSelected:" + isSelected + ",convertIndex=" + convertIndex + ",currentType=" + currentType;
    }

    public int getDefaultRightUnitIndex() {
        try {
            if (defaultRightUnitIndex == -1) {
                for (int i = 0; i < unit.size(); i++) {
                    Unit u = unit.get(i);
                    if (u.getDefaultUnitType() == 0) {
                        defaultRightUnitIndex = i;
                        defaultRightUnit = u;
                        break;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return defaultRightUnitIndex;
    }

    public Unit getDefaultLeftUnit() {
        for (int i = 0; i < unit.size(); i++) {
            if (i == defaultLeftUnitIndex) {
                defaultLeftUnit = unit.get(i);
                return defaultLeftUnit;
            }
        }
        return null;
    }

    public Unit getDefaultRightUnit() {
        for (int i = 0; i < unit.size(); i++) {
            if (i == defaultRightUnitIndex) {
                defaultRightUnit = unit.get(i);
                return defaultRightUnit;
            }
        }
        return null;
    }

    public void setDefaultRightUnitIndex(int defaultRightUnitIndex) {
        this.defaultRightUnitIndex = defaultRightUnitIndex;
    }

    public void setDefaultLeftUnitIndex(int defaultLeftUnitIndex) {
        this.defaultLeftUnitIndex = defaultLeftUnitIndex;
    }

    public int getDefaultLeftUnitIndex() {
        try {
            if (defaultLeftUnitIndex == -1) {
                for (int i = 0; i < unit.size(); i++) {
                    Unit u = unit.get(i);
                    if (u.getDefaultUnitType() == 1) {
                        defaultLeftUnitIndex = i;
                        defaultLeftUnit = u;
                        break;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return defaultLeftUnitIndex;
    }

    public int getConvertHistoryImgResId() {
        return convertHistoryImgResId;
    }

    public void setConvertHistoryImgResId(int convertHistoryImgResId) {
        this.convertHistoryImgResId = convertHistoryImgResId;
    }

    public Constant.ConvertType getCurrentType() {
        return currentType;
    }

    public void setCurrentType(Constant.ConvertType currentType) {
        this.currentType = currentType;
    }

    public int getConvertIndex() {
        return convertIndex;
    }

    public void setConvertIndex(int convertIndex) {
        this.convertIndex = convertIndex;
    }

    public Unit getUnitByName(String unitName) {
        try {
            for (Unit u : unit) {
                if (u != null && u.getAbbreviation().equalsIgnoreCase(unitName)) {
                    return u;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public int getUnitIndexByName(String unitName) {
        int unitIndex = 0;
        try {
            for (int i = 0; i < unit.size(); i++) {
                if (unit.get(i).getAbbreviation().equalsIgnoreCase(unitName)) {
                    return i;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return unitIndex;
    }
}
