/* Copyright (C) 2016 Tcl Corporation Limited */
package com.tct.calculator.utils;

/**
 * Created by user on 10/9/16.
 */
public class ConvertConstant {
    public enum Length {
        mm(ConvertRatio.LENGTH.MILLI_METER_RATE, Length_Name.mm),
        cm(ConvertRatio.LENGTH.CENTI_METER_RATE, Length_Name.cm),
        m(ConvertRatio.LENGTH.METER_RATE, Length_Name.m),
        km(ConvertRatio.LENGTH.KILOMETER_RATE, Length_Name.km),
        mi(ConvertRatio.LENGTH.MILE_RATE, Length_Name.mi),
        yd(ConvertRatio.LENGTH.YARD_RATE, Length_Name.yd),
        ft(ConvertRatio.LENGTH.FOOT_RATE, Length_Name.ft),
        in(ConvertRatio.LENGTH.INCH_RATE, Length_Name.in),
        kt(ConvertRatio.LENGTH.NAUTICAL_MILE, Length_Name.kt);
        public float unitRate;
        public String unitName;

        private Length(float unitRate, String unitName) {
            this.unitRate = unitRate;
            this.unitName = unitName;
        }
    }

    public enum Area {
        mm2(ConvertRatio.AREA.SQUARE_MILLIMETER_RATE, Area_Name.mm2),
        cm2(ConvertRatio.AREA.SQUARE_CENTIMETER_RATE, Area_Name.cm2),
        m2(ConvertRatio.AREA.SQUARE_METER_RATE, Area_Name.m2),
        in2(ConvertRatio.AREA.SQUARE_INCH_RATE, Area_Name.in2),
        ft2(ConvertRatio.AREA.SQUARE_FOOT_RATE, Area_Name.ft2),
        yd2(ConvertRatio.AREA.SQUARE_YARD_RATE, Area_Name.yd2),
        ha(ConvertRatio.AREA.HECTARE_RATE, Area_Name.ha),
        km2(ConvertRatio.AREA.SQUARE_KILOMETER_RATE, Area_Name.km2),
        ac(ConvertRatio.AREA.ACRE_RATE, Area_Name.ac),
        mi2(ConvertRatio.AREA.SQUARE_MILE_RATE, Area_Name.mi2);
        public float unitRate;
        public String unitName;

        private Area(float unitRate, String unitName) {
            this.unitRate = unitRate;
            this.unitName = unitName;
        }
    }

    public enum Mass {
        mg(ConvertRatio.MASS.MILLIGRAM_RATE, Mass_Name.mg),
        g(ConvertRatio.MASS.GRAM_RATE, Mass_Name.g),
        kg(ConvertRatio.MASS.KILOGRAM_RATE, Mass_Name.kg),
        t(ConvertRatio.MASS.METRICTON_RATE, Mass_Name.t),
        ton_uk(ConvertRatio.MASS.LONGTON_RATE, Mass_Name.ton_uk),
        ton_us(ConvertRatio.MASS.SHORTTON_RATE, Mass_Name.ton_us),
        lb(ConvertRatio.MASS.POUND_RATE, Mass_Name.lb),
        ounce(ConvertRatio.MASS.OZ_RATE, Mass_Name.ounce),
        st(ConvertRatio.MASS.STONE_RATE, Mass_Name.st),
        ct(ConvertRatio.MASS.CARAT_RATE, Mass_Name.ct);
        public float unitRate;
        public String unitName;

        private Mass(float unitRate, String unitName) {
            this.unitRate = unitRate;
            this.unitName = unitName;
        }
    }

    public enum Volume {
        mm3(ConvertRatio.VOLUME.CUBIC_MILLIMETER_RATE, Volume_Name.mm3),
        cm3(ConvertRatio.VOLUME.CUBIC_CENTIMETER_RATE, Volume_Name.cm3),
        dm3(ConvertRatio.VOLUME.CUBIC_DECIMETER_RATE, Volume_Name.dm3),
        m3(ConvertRatio.VOLUME.CUBIC_METER_RATE, Volume_Name.m3),
        mL_cc(ConvertRatio.VOLUME.MILLILITER_RATE, Volume_Name.mL_cc),
        L(ConvertRatio.VOLUME.LILITER_RATE, Volume_Name.L),
        ft3(ConvertRatio.VOLUME.CUBIC_FOOT_RATE, Volume_Name.ft3),
        in3(ConvertRatio.VOLUME.CUBIC_INCH_RATE, Volume_Name.in3),
        gal_US(ConvertRatio.VOLUME.US_GALLON_RATE, Volume_Name.gal_US),
        qt_US(ConvertRatio.VOLUME.US_QUART_RATE, Volume_Name.qt_US),
        pt_US(ConvertRatio.VOLUME.US_PINT_RATE, Volume_Name.pt_US),
        oz_US(ConvertRatio.VOLUME.US_OUNCE_RATE, Volume_Name.oz_US),
        cup_US(ConvertRatio.VOLUME.US_CUP_RATE, Volume_Name.cup_US),
        tbsp_US(ConvertRatio.VOLUME.US_TABLESPOON_RATE, Volume_Name.tbsp_US),
        tsp_US(ConvertRatio.VOLUME.US_TEASPOON_RATE, Volume_Name.tsp_US),
        gal_UK(ConvertRatio.VOLUME.UK_GALLON_RATE, Volume_Name.gal_UK),
        qt_UK(ConvertRatio.VOLUME.UK_QUART_RATE, Volume_Name.qt_UK),
        pt_UK(ConvertRatio.VOLUME.UK_PINT_RATE, Volume_Name.pt_UK),
        oz_UK(ConvertRatio.VOLUME.UK_OUNCE_RATE, Volume_Name.oz_UK),
        cup_UK(ConvertRatio.VOLUME.UK_CUP_RATE, Volume_Name.cup_UK),
        tbsp_UK(ConvertRatio.VOLUME.UK_TABLESPOON_RATE, Volume_Name.tbsp_UK),
        tsp_UK(ConvertRatio.VOLUME.UK_TEASPOON_RATE, Volume_Name.tsp_UK),
        dr(ConvertRatio.VOLUME.DRAM_RATE, Volume_Name.dr),
        bbl(ConvertRatio.VOLUME.BARREL_RATE, Volume_Name.bbl),
        cord(ConvertRatio.VOLUME.CORD_RATE, Volume_Name.cord),
        gill(ConvertRatio.VOLUME.GILL_RATE, Volume_Name.gill);
        public float unitRate;
        public String unitName;

        private Volume(float unitRate, String unitName) {
            this.unitRate = unitRate;
            this.unitName = unitName;
        }
    }

    public enum Fuel {
        gal_UK_100_miles(ConvertRatio.FUEL.UK_GALLONS_PER_HUNDRED_MILES_RATE, Fuel_Name.gal_UK_100_miles),
        gal_US_100_miles(ConvertRatio.FUEL.US_GALLONS_PER_HUNDRED_MILES_RATE, Fuel_Name.gal_US_100_miles),
        km_L(ConvertRatio.FUEL.KILOMETER_PER_LITER_RATE, Fuel_Name.km_L),
        L_100km(ConvertRatio.FUEL.LITERS_PER_HUNDRED_KILOMETER_RATE, Fuel_Name.L_100km),
        MPG_UK(ConvertRatio.FUEL.UK_MILES_PER_GALLON_RATE, Fuel_Name.MPG_UK),
        MPG_US(ConvertRatio.FUEL.US_MILES_PER_GALLON_RATE, Fuel_Name.MPG_US);
        public float unitRate;
        public String unitName;

        private Fuel(float unitRate, String unitName) {
            this.unitRate = unitRate;
            this.unitName = unitName;
        }
    }

    public enum Temperature {
        C(ConvertRatio.TEMPERATURE.CELSIUS, Temperature_Name.C),
        F(ConvertRatio.TEMPERATURE.FAHRENHEIT, Temperature_Name.F),
        K(ConvertRatio.TEMPERATURE.KELVIN, Temperature_Name.K);
        public float unitRate;
        public String unitName;

        private Temperature(float unitRate, String unitName) {
            this.unitRate = unitRate;
            this.unitName = unitName;
        }
    }

    public enum Shoes {
//        public float unitRate;
//        public String unitName;
//
//        private Shoes(float unitRate, String unitName) {
//            this.unitRate = unitRate;
//            this.unitName = unitName;
//        }
    }

    public enum Cooking {
        mL_cc(ConvertRatio.COOKING.MILLILITER_RATE, Cooking_Name.mL_cc),
        gal_US(ConvertRatio.COOKING.US_GALLON_RATE, Cooking_Name.gal_US),
        qt_US(ConvertRatio.COOKING.US_QUART_RATE, Cooking_Name.qt_US),
        pt_US(ConvertRatio.COOKING.US_PINT_RATE, Cooking_Name.pt_US),
        fl_oz_US(ConvertRatio.COOKING.US_FLUID_OUNCE_RATE, Cooking_Name.fl_oz_US),
        cup_US(ConvertRatio.COOKING.US_CUP_RATE, Cooking_Name.cup_US),
        tbsp_US(ConvertRatio.COOKING.US_TABLESPOON_RATE, Cooking_Name.tbsp_US),
        tsp_US(ConvertRatio.COOKING.US_TEASPOON_RATE, Cooking_Name.tsp_US),
        gal_UK(ConvertRatio.COOKING.UK_GALLON_RATE, Cooking_Name.gal_UK),
        qt_UK(ConvertRatio.COOKING.UK_QUART_RATE, Cooking_Name.qt_UK),
        pt_UK(ConvertRatio.COOKING.UK_PINT_RATE, Cooking_Name.pt_UK),
        fl_oz_UK(ConvertRatio.COOKING.UK_FLUID_OUNCE_RATE, Cooking_Name.fl_oz_UK),
        cup_UK(ConvertRatio.COOKING.UK_CUP_RATE, Cooking_Name.cup_UK),
        tbsp_UK(ConvertRatio.COOKING.UK_TABLESPOON_RATE, Cooking_Name.tbsp_UK),
        tsp_UK(ConvertRatio.COOKING.UK_TEASPOON_RATE, Cooking_Name.tsp_UK);
        public float unitRate;
        public String unitName;

        private Cooking(float unitRate, String unitName) {
            this.unitRate = unitRate;
            this.unitName = unitName;
        }
    }

    //=======================Name========================
    public static class Length_Name {
        public static final String mm = "mm";
        public static final String cm = "cm";
        public static final String m = "m";
        public static final String km = "km";
        public static final String mi = "mi";
        public static final String yd = "yd";
        public static final String ft = "ft";
        public static final String in = "in";
        public static final String kt = "kt";
    }

    public static class Area_Name {
        public static final String mm2 = "mm2";
        public static final String cm2 = "cm2";
        public static final String m2 = "m2";
        public static final String in2 = "in2";
        public static final String ft2 = "ft2";
        public static final String yd2 = "yd2";
        public static final String ha = "ha";
        public static final String km2 = "km2";
        public static final String ac = "ac";
        public static final String mi2 = "mi2";
    }

    public static class Mass_Name {
        public static final String mg = "mg";
        public static final String g = "g";
        public static final String kg = "kg";
        public static final String t = "t";
        public static final String ton_uk = "ton uk";
        public static final String ton_us = "ton us";
        public static final String lb = "lb";
        public static final String ounce = "ounce";
        public static final String st = "st";
        public static final String ct = "ct";
    }

    public static class Volume_Name {
        public static final String mm3 = "mm3";
        public static final String cm3 = "cm3";
        public static final String dm3 = "dm3";
        public static final String m3 = "m3";
        public static final String mL_cc = "mL (cc)";
        public static final String L = "L";
        public static final String ft3 = "ft3";
        public static final String in3 = "in3";
        public static final String gal_US = "gal (US)";
        public static final String qt_US = "qt (US)";
        public static final String pt_US = "pt (US)";
        public static final String oz_US = "oz (US)"; //Rate in the Definition of the food labeling is 240ml
        public static final String cup_US = "cup (US)";
        public static final String tbsp_US = "tbsp (US)";
        public static final String tsp_US = "tsp (US)";
        public static final String gal_UK = "gal (UK)";
        public static final String qt_UK = "qt (UK)";
        public static final String pt_UK = "pt (UK)";
        public static final String oz_UK = "oz (UK)";
        public static final String cup_UK = "cup (UK)";
        public static final String tbsp_UK = "tbsp (UK)";
        public static final String tsp_UK = "tsp (UK)";
        public static final String dr = "dr";
        public static final String bbl = "bbl";
        public static final String cord = "cord";
        public static final String gill = "gill";

    }

    public static class Fuel_Name {
        public static final String gal_UK_100_miles = "gal(UK)/100 miles";
        public static final String gal_US_100_miles = "gal(US)/100 miles";
        public static final String km_L = "km/L";//translation error
        public static final String L_100km = "L/100km";
        public static final String MPG_UK = "MPG(UK)";//translation error
        public static final String MPG_US = "MPG(US)";//translation error
    }

    public static class Temperature_Name {
        public static final String C = "C";
        public static final String F = "F";
        public static final String K = "K";
    }

    public static class Cooking_Name {
        public static final String mL_cc = "mL (cc)";
        public static final String gal_US = "gal (US)";
        public static final String qt_US = "qt (US)";
        public static final String pt_US = "pt (US)";
        public static final String fl_oz_US = "fl. oz (US)";
        public static final String cup_US = "cup (US)"; //Rate in the Definition of the food labeling is 240ml
        public static final String tbsp_US = "tbsp (US)";
        public static final String tsp_US = "tsp (US)";
        public static final String gal_UK = "gal (UK)";
        public static final String qt_UK = "qt (UK)";
        public static final String pt_UK = "pt (UK)";
        public static final String fl_oz_UK = "fl oz (UK)";
        public static final String cup_UK = "cup (UK)";
        public static final String tbsp_UK = "tbsp (UK)";
        public static final String tsp_UK = "tsp (UK)";
    }
}
