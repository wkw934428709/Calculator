/* Copyright (C) 2016 Tcl Corporation Limited */
package com.tct.calculator.utils;

/**
 * Created by user on 16-9-19.
 */
public class ConvertRatio {

    //=======================Rate========================
    public static class LENGTH {
        public static final float MILLI_METER_RATE = 0.001f;
        public static final float CENTI_METER_RATE = 0.01f;
        public static final float METER_RATE = 1f;
        public static final float KILOMETER_RATE = 1000f;
        public static final float MILE_RATE = 1609.344f;
        public static final float YARD_RATE = 0.9144f;
        public static final float FOOT_RATE = 0.3048f;
        public static final float INCH_RATE = 0.0254f;
        public static final float NAUTICAL_MILE = 1852f;
    }

    public static class AREA {
        public static final float SQUARE_MILLIMETER_RATE = 0.000001f;
        public static final float SQUARE_CENTIMETER_RATE = 0.0001f;
        public static final float SQUARE_METER_RATE = 1f;
        public static final float SQUARE_INCH_RATE = 0.00064516f;
        public static final float SQUARE_FOOT_RATE = 0.09290304f;
        public static final float SQUARE_YARD_RATE = 0.83612736f;
        public static final float HECTARE_RATE = 10000f;
        public static final float SQUARE_KILOMETER_RATE = 1000000f;
        public static final float ACRE_RATE = 4046.8564224f;
        public static final float SQUARE_MILE_RATE = 2589988.110336f;
    }

    public static class MASS {
        public static final float MILLIGRAM_RATE = 0.000001f;
        public static final float GRAM_RATE = 0.001f;
        public static final float KILOGRAM_RATE = 1f;
        public static final float METRICTON_RATE = 1000f;
        public static final float LONGTON_RATE = 1016.0469088f;
        public static final float SHORTTON_RATE = 907.18474f;
        public static final float POUND_RATE = 0.4535924f;
        public static final float OZ_RATE = 0.0283495f;
        public static final float STONE_RATE = 6.3502932f;
        public static final float CARAT_RATE = 0.0002f;
    }

    public static class VOLUME {
        public static final float CUBIC_MILLIMETER_RATE = 0.000000001f;
        public static final float CUBIC_CENTIMETER_RATE = 0.000001f;
        public static final float CUBIC_DECIMETER_RATE = 0.001f;
        public static final float CUBIC_METER_RATE = 1f;
        public static final float MILLILITER_RATE = 0.000001f;
        public static final float LILITER_RATE = 0.001f;
        public static final float CUBIC_FOOT_RATE = 0.0283168f;
        public static final float CUBIC_INCH_RATE = 0.0000164f;
        public static final float US_GALLON_RATE = 0.0037854f;
        public static final float US_QUART_RATE = 0.0011012f;
        public static final float US_PINT_RATE = 0.0005506f;
        public static final float US_OUNCE_RATE = 0.00002957f;
        public static final float US_CUP_RATE = 0.0002366f;
        public static final float US_TABLESPOON_RATE = 0.00001479f;
        public static final float US_TEASPOON_RATE = 0.000004929f;
        public static final float UK_GALLON_RATE = 0.0045461f;
        public static final float UK_QUART_RATE = 0.0011365f;
        public static final float UK_PINT_RATE = 0.0005683f;
        public static final float UK_OUNCE_RATE = 0.0000284f;
        public static final float UK_CUP_RATE = 0.0002841f;
        public static final float UK_TABLESPOON_RATE = 0.00001775f;
        public static final float UK_TEASPOON_RATE = 0.00000592f;
        public static final float DRAM_RATE = 0.0000037f;
        public static final float BARREL_RATE = 0.1589873f;
        public static final float CORD_RATE = 3.6245564f;
        public static final float GILL_RATE = 0.00011829f;//US:0.00011829 UK:0.00014207
    }

    public static class FUEL {
        public static final float UK_GALLONS_PER_HUNDRED_MILES_RATE = 0.3540132f;
        public static final float US_GALLONS_PER_HUNDRED_MILES_RATE = 0.42514503f;
        public static final float KILOMETER_PER_LITER_RATE = 100f;
        public static final float LITERS_PER_HUNDRED_KILOMETER_RATE = 1f;
        public static final float UK_MILES_PER_GALLON_RATE = 282.4809363f;
        public static final float US_MILES_PER_GALLON_RATE = 235.2145833f;
    }

    public static class FUEL2 {
        public static final float UK_GALLONS_PER_HUNDRED_MILES_RATE = (VOLUME.LILITER_RATE / VOLUME.UK_GALLON_RATE) / (LENGTH.KILOMETER_RATE / LENGTH.MILE_RATE);
        public static final float US_GALLONS_PER_HUNDRED_MILES_RATE = (VOLUME.LILITER_RATE / VOLUME.US_GALLON_RATE) / (LENGTH.KILOMETER_RATE / LENGTH.MILE_RATE);
        public static final float KILOMETER_PER_LITER_RATE = 1f;//translation error//TODO ts
        public static final float LITERS_PER_HUNDRED_KILOMETER_RATE = 1f;
        public static final float UK_MILES_PER_GALLON_RATE = 1 / UK_GALLONS_PER_HUNDRED_MILES_RATE;//translation error//TODO ts
        public static final float US_MILES_PER_GALLON_RATE = 1 / US_GALLONS_PER_HUNDRED_MILES_RATE;//translation error//TODO ts
    }

    public static class TEMPERATURE {
        public static final float CELSIUS = 0f;
        public static final float FAHRENHEIT = 0f;
        public static final float KELVIN = 273.15f;
    }

    public static class COOKING {
        public static final float MILLILITER_RATE = 1f;
        public static final float US_GALLON_RATE = 3785.411784f;
        public static final float US_QUART_RATE = 946.35294600f;
        public static final float US_PINT_RATE = 473.17647300f;
        public static final float US_FLUID_OUNCE_RATE = 29.57352956f;
        public static final float US_CUP_RATE = 236.58823650f;
        public static final float US_TABLESPOON_RATE = 14.78676478f;
        public static final float US_TEASPOON_RATE = 4.92892159f;
        public static final float UK_GALLON_RATE = 4546.09f;
        public static final float UK_QUART_RATE = 1136.5225f;
        public static final float UK_PINT_RATE = 568.26125f;
        public static final float UK_FLUID_OUNCE_RATE = 28.41306250f;
        public static final float UK_CUP_RATE = 284.0000f;
        public static final float UK_TABLESPOON_RATE = 17.75000000f;
        public static final float UK_TEASPOON_RATE = 5.91666667f;
    }


}
