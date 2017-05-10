/* Copyright (C) 2016 Tcl Corporation Limited */
package com.tct.calculator.convert;

import android.util.Log;

import com.tct.calculator.utils.Constant;
import com.tct.calculator.utils.ConvertConstant;


/**
 * Created by user on 10/9/16.
 */
public class ConvertUtil {
    private static final String TAG = "ConvertUtil";
    private static ConvertUtil sConvertUtil;

    private ConvertUtil() {

    }

    public static ConvertUtil getsConvertUtil() {
        if (sConvertUtil == null) {
            sConvertUtil = new ConvertUtil();
        }
        return sConvertUtil;
    }


    public Unit getConvertRate(Constant.ConvertType convertType, Unit from, Unit to) {
        try {
            if (convertType != null) {
                switch (convertType) {
                    case TEMPERATURE:
                        if (from != null && to != null) {
                            Log.i(TAG, "===XXX==getConvertRate=from.getAbbreviation()=" + from.getAbbreviation() + ",name=" + ConvertConstant.Temperature.C.unitName);
                            if (ConvertConstant.Temperature.C.unitName.equalsIgnoreCase(from.getAbbreviation())) {
                                if (ConvertConstant.Temperature.F.unitName.equalsIgnoreCase(to.getAbbreviation())) {//F=℃×1.8 + 32
                                    to.setUnitValue(from.getUnitValue() * Constant.TemperatureRate.TEMP_1_8 + Constant.TemperatureRate.TEMP_32);
                                } else if (ConvertConstant.Temperature.K.unitName.equalsIgnoreCase(to.getAbbreviation())) {//K=℃+273.15
                                    to.setUnitValue(from.getUnitValue() + Constant.TemperatureRate.TEMP_273_15);
                                } else if (ConvertConstant.Temperature.C.unitName.equalsIgnoreCase(to.getAbbreviation())) {
                                    to.setUnitValue(from.getUnitValue());
                                }
                            } else if (ConvertConstant.Temperature.F.unitName.equalsIgnoreCase(from.getAbbreviation())) {
                                if (ConvertConstant.Temperature.F.unitName.equalsIgnoreCase(to.getAbbreviation())) {
                                    to.setUnitValue(from.getUnitValue());
                                } else if (ConvertConstant.Temperature.K.unitName.equalsIgnoreCase(to.getAbbreviation())) {//K=(F+459.67)/1.8
                                    to.setUnitValue((from.getUnitValue() + Constant.TemperatureRate.TEMP_459_67) / Constant.TemperatureRate.TEMP_1_8);
                                } else if (ConvertConstant.Temperature.C.unitName.equalsIgnoreCase(to.getAbbreviation())) {//℃=(F-32)/1.8
                                    to.setUnitValue((from.getUnitValue() - Constant.TemperatureRate.TEMP_32) / Constant.TemperatureRate.TEMP_1_8);
                                }
                            } else if (ConvertConstant.Temperature.K.unitName.equalsIgnoreCase(from.getAbbreviation())) {
                                if (ConvertConstant.Temperature.F.unitName.equalsIgnoreCase(to.getAbbreviation())) {//F=K×1.8-459.67
                                    to.setUnitValue(from.getUnitValue() * Constant.TemperatureRate.TEMP_1_8 - Constant.TemperatureRate.TEMP_459_67);
                                } else if (ConvertConstant.Temperature.K.unitName.equalsIgnoreCase(to.getAbbreviation())) {
                                    to.setUnitValue(from.getUnitValue());
                                } else if (ConvertConstant.Temperature.C.unitName.equalsIgnoreCase(to.getAbbreviation())) {//℃=K-273.15
                                    to.setUnitValue(from.getUnitValue() - Constant.TemperatureRate.TEMP_273_15);
                                }
                            }
                        }
                        return to;
                    case FUEL:
                        if (ConvertConstant.Fuel.km_L.unitName.equalsIgnoreCase(from.getAbbreviation()) && ConvertConstant.Fuel.L_100km.unitName.equalsIgnoreCase(to.getAbbreviation()) ||
                                ConvertConstant.Fuel.L_100km.unitName.equalsIgnoreCase(from.getAbbreviation()) && ConvertConstant.Fuel.km_L.unitName.equalsIgnoreCase(to.getAbbreviation()) ||
                                ConvertConstant.Fuel.gal_UK_100_miles.unitName.equalsIgnoreCase(from.getAbbreviation()) && ConvertConstant.Fuel.MPG_UK.unitName.equalsIgnoreCase(to.getAbbreviation()) ||
                                ConvertConstant.Fuel.gal_US_100_miles.unitName.equalsIgnoreCase(from.getAbbreviation()) && ConvertConstant.Fuel.MPG_US.unitName.equalsIgnoreCase(to.getAbbreviation()) ||
                                ConvertConstant.Fuel.MPG_UK.unitName.equalsIgnoreCase(from.getAbbreviation()) && ConvertConstant.Fuel.gal_UK_100_miles.unitName.equalsIgnoreCase(to.getAbbreviation()) ||
                                ConvertConstant.Fuel.MPG_US.unitName.equalsIgnoreCase(from.getAbbreviation()) && ConvertConstant.Fuel.gal_US_100_miles.unitName.equalsIgnoreCase(to.getAbbreviation())) {
                            //Convert to same kind
                            to.setUnitValue(100 / from.getUnitValue());
                            Log.i(TAG, "===XXX==getConvertRate=2=to=" + to.getUnitValue());
                        } else if ((ConvertConstant.Fuel.L_100km.unitName.equalsIgnoreCase(to.getAbbreviation())
                                && (ConvertConstant.Fuel.MPG_UK.unitName.equalsIgnoreCase(from.getAbbreviation()) ||
                                ConvertConstant.Fuel.MPG_US.unitName.equalsIgnoreCase(from.getAbbreviation()))) ||
                                ((ConvertConstant.Fuel.MPG_US.unitName.equalsIgnoreCase(to.getAbbreviation()) ||
                                        ConvertConstant.Fuel.MPG_UK.unitName.equalsIgnoreCase(to.getAbbreviation()))
                                        && ConvertConstant.Fuel.L_100km.unitName.equalsIgnoreCase(from.getAbbreviation()))) {
                            Log.i(TAG, "===XXX==getConvertRate=4=");
                            try {
                                if (from != null && to != null) {
                                    from = getRateByUnit(convertType, from);
                                    to = getRateByUnit(convertType, to);
                                    float fromRate = from.getConvertRate();
                                    float toRate = to.getConvertRate();
                                    float toValue = fromRate * toRate / from.getUnitValue();
                                    to.setUnitValue(toValue);
                                    Log.i(TAG, "===XXX===ConvertUtil=computeConvertRate=fromRate=" + fromRate + "=toRate=" + toRate + "=fromValue=" + from.getUnitValue() + "=toValue=" + toValue);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            return to;
                        } else {
                            Log.i(TAG, "===XXX==getConvertRate=5=");
                            return computeConvertRateFuel(getRateByUnit(convertType, from), getRateByUnit(convertType, to));
                        }
                        return to;
                    default:
                        return computeConvertRate(getRateByUnit(convertType, from), getRateByUnit(convertType, to));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return computeConvertRate(getRateByUnit(convertType, from), getRateByUnit(convertType, to));
    }

    private Unit getRateByUnit(Constant.ConvertType convertType, Unit unit) {

        try {
            String unitName = unit.getAbbreviation();
            switch (convertType) {
                case LENGTH:
                    ConvertConstant.Length[] lengths = ConvertConstant.Length.values();
                    for (ConvertConstant.Length length : lengths) {
                        if (length.unitName.equalsIgnoreCase(unitName)) {
                            unit.setConvertRate(length.unitRate);
                            return unit;
                        }
                    }
                    break;
                case AREA:
                    ConvertConstant.Area[] areas = ConvertConstant.Area.values();
                    for (ConvertConstant.Area area : areas) {
                        if (area.unitName.equalsIgnoreCase(unitName)) {
                            unit.setConvertRate(area.unitRate);
                            return unit;
                        }
                    }
                    break;
                case MASS:
                    ConvertConstant.Mass[] masses = ConvertConstant.Mass.values();
                    for (ConvertConstant.Mass mass : masses) {
                        if (mass.unitName.equalsIgnoreCase(unitName)) {
                            unit.setConvertRate(mass.unitRate);
                            return unit;
                        }
                    }
                    break;
                case VOLUME:
                    ConvertConstant.Volume[] volumes = ConvertConstant.Volume.values();
                    for (ConvertConstant.Volume volume : volumes) {
                        if (volume.unitName.equalsIgnoreCase(unitName)) {
                            unit.setConvertRate(volume.unitRate);
                            return unit;
                        }
                    }
                    break;
                case TEMPERATURE:
                    break;
                case FUEL:
                    ConvertConstant.Fuel[] fuels = ConvertConstant.Fuel.values();
                    for (ConvertConstant.Fuel fuel : fuels) {
                        if (fuel.unitName.equalsIgnoreCase(unitName)) {
                            unit.setConvertRate(fuel.unitRate);
                            return unit;
                        }
                    }
                    break;
                case SHOES://TODO ts
                    break;
                case CLOTHES://TODO ts
                    break;
                case COOKING:
                    ConvertConstant.Cooking[] cookings = ConvertConstant.Cooking.values();
                    for (ConvertConstant.Cooking cooking : cookings) {
                        if (cooking.unitName.equalsIgnoreCase(unitName)) {
                            unit.setConvertRate(cooking.unitRate);
                            return unit;
                        }
                    }
                    break;
                default:
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private Unit computeConvertRate(Unit from, Unit to) {
        try {
            if (from != null && to != null) {
                float fromRate = from.getConvertRate();
                float toRate = to.getConvertRate();
                float toValue = fromRate * from.getUnitValue() / toRate;
                to.setUnitValue(toValue);
                Log.i(TAG, "===XXX===ConvertUtil=computeConvertRate=fromRate=" + fromRate + "=toRate=" + toRate + "=fromValue=" + from.getUnitValue() + "=toValue=" + toValue);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return to;
    }

    private Unit computeConvertRateFuel(Unit from, Unit to) {
        try {
            if (from != null && to != null) {
                float fromRate = from.getConvertRate();
                float toRate = to.getConvertRate();
                float toValue = toRate * from.getUnitValue() / fromRate;
                to.setUnitValue(toValue);
                Log.i(TAG, "===XXX===ConvertUtil=computeConvertRateFuel=fromRate=" + fromRate + "=toRate=" + toRate + "=fromValue=" + from.getUnitValue() + "=toValue=" + toValue);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return to;
    }
}
