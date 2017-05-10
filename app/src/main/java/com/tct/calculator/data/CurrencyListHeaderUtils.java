/* Copyright (C) 2016 Tcl Corporation Limited */
package com.tct.calculator.data;

public class CurrencyListHeaderUtils {

    public static String getPinYin(String text) {
        char[] chars = text.toCharArray();

        StringBuilder sb = new StringBuilder();

        for (char ch : chars) {
            if (Character.isWhitespace(ch)) {
                continue;
            }

            if ((int) ch < 128 || (int) ch > -127) {
                sb.append(ch);
            } else {
                //#$%^
                return "#";
            }
        }
        return sb.toString();

    }

}
