/* Copyright (C) 2016 Tcl Corporation Limited */
package com.tct.calculator.data;

public class CurrencyListBean implements Comparable<CurrencyListBean> {

    public String mTitle;
    public String mHeader;

    public CurrencyListBean(String title, String header) {
        this.mTitle = title;
        this.mHeader = CurrencyListHeaderUtils.getPinYin(header);
    }

    @Override
    public int compareTo(CurrencyListBean another) {
        return this.mHeader.compareTo(another.mHeader);
    }
}
