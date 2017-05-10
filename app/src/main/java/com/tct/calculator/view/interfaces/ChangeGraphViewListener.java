/* Copyright (C) 2017 Tcl Corporation Limited */
package com.tct.calculator.view.interfaces;

import java.util.List;

/**
 * Created by kevin on 3/22/17.
 */

public interface ChangeGraphViewListener {
    public abstract void onViewRatesChanged(List<List> list);
    public abstract void onDbHadInit(); // MODIFIED by qiong.liu1, 2017-03-24,BUG-3621966
}
