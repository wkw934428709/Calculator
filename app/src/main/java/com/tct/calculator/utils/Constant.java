package com.tct.calculator.utils;

/**
 * Created by user on 9/28/16.
 */
public class Constant {

    public enum CalculatorType {
        CALCULATOR, CONVERTER, CURRENCY, CURRENCY_LIST, CALCULATORLANDSCAPE
    }


    public enum ConvertType {
        LENGTH, AREA, MASS, VOLUME, TEMPERATURE, FUEL, SHOES, CLOTHES, COOKING
    }

    public enum Temperature {
        CELSIUS, FAHRENHEIT, KELVIN
    }

    /* MODIFIED-BEGIN by kaifeng.lu, 2016-11-17,BUG-3005276*/
    public enum CalculatorState {
        INPUT, EVALUATE, RESULT, ERROR
    }
    /* MODIFIED-END by kaifeng.lu,BUG-3005276*/

    public interface TemperatureRate {
        float TEMP_1_8 = 1.8f;
        float TEMP_459_67 = 459.67f;
        float TEMP_273_15 = 273.15f;
        float TEMP_32 = 32f;
    }

    public static final String DEFAULT_VALUE = "0";
    public static final String XE_WEB_SITE = "http://www.xe.com";
    public static final String CURRENCY_COUNTRY_CNY = "CNY";
    public static final String CURRENCY_COUNTRY_USD ="USD";
    public static final String CURRENCY_ORG_DEFAULT = "0.00"; // MODIFIED by qiong.liu1, 2017-03-27,BUG-3621966
    public static final String CURRENCY_ES = "es";
    public static final String CURRENCY_FR_BE = "fr-be";
    public static final String CURRENCY_PT_BR = "pt-br";
    public static final String CURRENCY_EURO = "euro";
    public static final String CURRENCY_EUROPEAN_UNION = "european_union";
    public static final String CURRENCY_SINGAL = "€";
    public static final String CURRENCY_EUR = "EUR";
    public static final String CURRENCY_ADD_ZERZO_TIME = "0";

    /** Type of screen. */
    // Portrait full screen.
    public final static int SCREEN_PORT_FULL = 0x0001;
    // Portrait 3/4.
    public final static int SCREEN_PORT_QUTER_THREE = 0x0002;
    // Portrait 1/2
    public final static int SCREEN_PORT_HALF = 0x0003;
    // Portrait 1/4.
    public final static int SCREEN_PORT_QUTER = 0x0004;
    // Landscape full screen.
    public final static int SCREEN_LAND_FULL = 0x0005;
    // // Landscape 1/2.
    public final static int SCREEN_LAND_HALF = 0x0006;
}
