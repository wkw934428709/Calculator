/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the 'License');
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an 'AS IS' BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.tct.calculator;

import android.content.Context;
/* MODIFIED-BEGIN by kaifeng.lu, 2016-06-16,BUG-2285401*/
import android.util.Log;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class CalculatorExpressionTokenizer {
    private static final String TAG = "Calculator";
    /* MODIFIED-END by kaifeng.lu,BUG-2285401*/
    private final Map<String, String> buildReplacementMap;
    private final Map<String, String> evaluateReplacementMap;

    public CalculatorExpressionTokenizer(Context context) {
        buildReplacementMap = new HashMap<String,String>();
        evaluateReplacementMap = new HashMap<String,String>();

        buildReplacementMap.put(".", context.getString(R.string.dec_point));
        buildReplacementMap.put("0", context.getString(R.string.digit_0));
        buildReplacementMap.put("1", context.getString(R.string.digit_1));
        buildReplacementMap.put("2", context.getString(R.string.digit_2));
        buildReplacementMap.put("3", context.getString(R.string.digit_3));
        buildReplacementMap.put("4", context.getString(R.string.digit_4));
        buildReplacementMap.put("5", context.getString(R.string.digit_5));
        buildReplacementMap.put("6", context.getString(R.string.digit_6));
        buildReplacementMap.put("7", context.getString(R.string.digit_7));
        buildReplacementMap.put("8", context.getString(R.string.digit_8));
        buildReplacementMap.put("9", context.getString(R.string.digit_9));
        buildReplacementMap.put("/", context.getString(R.string.op_div));
        buildReplacementMap.put("*", context.getString(R.string.op_mul));
        buildReplacementMap.put("-", context.getString(R.string.op_sub));
        buildReplacementMap.put("cos", context.getString(R.string.fun_cos));
        buildReplacementMap.put("ln", context.getString(R.string.fun_ln));
        buildReplacementMap.put("log", context.getString(R.string.fun_log));
        buildReplacementMap.put("sin", context.getString(R.string.fun_sin));
        buildReplacementMap.put("tan", context.getString(R.string.fun_tan));
        buildReplacementMap.put("Infinity", context.getString(R.string.inf));

        evaluateReplacementMap.putAll(buildReplacementMap);
        evaluateReplacementMap.put("acos", context.getString(R.string.fun_arccos));
        evaluateReplacementMap.put("asin", context.getString(R.string.fun_arcsin));
        evaluateReplacementMap.put("atan", context.getString(R.string.fun_arctan));
        evaluateReplacementMap.put("^2", context.getString(R.string.op_sqr));
    }



    public String local2Build(String expr) {
        for (Entry<String, String> replacementEntry : buildReplacementMap.entrySet()) {
            expr = expr.replace(replacementEntry.getValue(), replacementEntry.getKey());
        }
        Log.i(TAG,"local2Build expr =" +expr); // MODIFIED by kaifeng.lu, 2016-06-16,BUG-2285401
        return expr;
    }

    public String build2Local(String expr) {
        for (Entry<String, String> replacementEntry : buildReplacementMap.entrySet()) {
            expr = expr.replace(replacementEntry.getKey(), replacementEntry.getValue());
        }
        return expr;
    }


    public String local2Evaluate(String expr) {
        for (Entry<String, String> replacementEntry : evaluateReplacementMap.entrySet()) {
            expr = expr.replace(replacementEntry.getValue(), replacementEntry.getKey());
        }
        return expr;
    }

    public String evaluate2Local(String expr) {
        for (Entry<String, String> replacementEntry : evaluateReplacementMap.entrySet()) {
            expr = expr.replace(replacementEntry.getKey(), replacementEntry.getValue());
        }
        return expr;
    }
}
