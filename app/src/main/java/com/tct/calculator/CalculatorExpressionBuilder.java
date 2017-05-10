/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ==========================================================================
 *HISTORY
 *
 *Tag            Date         Author        Description
 *============== ============ =============== ==============================
 *BUGFIX_1001813  2015/12/08   kaifeng.lu     [Calculator]The calculation result is not correct
 *BUGFIX_1490013  2016/01/23   kaifeng.lu     [Calculator][GAPP][REG]Can continue input number after press "=" key
 ===========================================================================
 */

package com.tct.calculator;

import android.text.SpannableStringBuilder;
import android.text.TextUtils;

public class CalculatorExpressionBuilder extends SpannableStringBuilder {

    private final CalculatorExpressionTokenizer mTokenizer;
    private boolean mIsEdited;


    public CalculatorExpressionBuilder(
            CharSequence text, CalculatorExpressionTokenizer tokenizer, boolean isEdited) {
        super(text);

        mTokenizer = tokenizer;
        mIsEdited = isEdited;
    }

    @Override
    public SpannableStringBuilder replace(int start, int end, CharSequence tb, int tbstart,
                                          int tbend) {

        String tempExpr = null;
        try {
            if (start != length() || end != length()) {
                mIsEdited = true;
                return super.replace(start, end, tb, tbstart, tbend);
            }

            String appendExpr =
                    mTokenizer.local2Build(tb.subSequence(tbstart, tbend).toString());
            tempExpr = "";
            //PR536081 Start It can input one figure with two decimal points.update by xiaolu.li Aug 24 2015. from  one char appending change to serveral char appending
            for (int i = 0; i < appendExpr.length(); i++) {
                String expr = mTokenizer.local2Build(toString())+tempExpr;
                String singExpr=appendExpr.substring(i,i+1);
                char nextChar = appendExpr.charAt(i);
                switch (nextChar) {
                    case '.':
                        // don't allow two decimals in the same number
                        String noCommaExpr = expr.replace(",", "");
                        final int index = noCommaExpr.lastIndexOf('.');
                        if (index != -1 && TextUtils.isDigitsOnly(noCommaExpr.substring(index + 1, noCommaExpr.length()))&&mIsEdited) {//mIsEdited is state, if false, the state is result . that is replace value, so without replace
                            singExpr = "";
                        }
                        break;
                    case '+':
                    case '*':
                    case '/':
                        // don't allow leading operator
                        if (start == 0) {
                            singExpr = "";
                            break;
                        }
                        // don't allow multiple successive operators
                        while (start > 0  && "+-*/".indexOf(expr.charAt(start - 1)) != -1) {
                            --start;
                        }
                        // fall through
                    case '-':
                        // don't allow -- or +-
                        if (start > 0 && "+-".indexOf(expr.charAt(start - 1)) != -1) {
                            --start;
                        }
                        // mark as edited since operators can always be appended
                        mIsEdited = true;
                       break;
                    default:
                        //[BUGFIX]-ADD-BEGIN by AMNJ.rurong.zhang, 2015-2-11,PR926735
    //                    if (start > 0 && "0".lastIndexOf(expr.charAt(start - 1)) != -1) {
    //                        if ((start > 2 && "+-*/".indexOf(expr.charAt(start - 2)) != -1)||start==1) {//PR536077 [ Calculator_v5.2.1.2.0303.0] “0100”can be shows on calculator update by xiaolu.li Aug 24 2015
    //                            --start;
    //                        }
    //                    }
                        //[BUGFIX]-ADD-END by AMNJ.rurong.zhang, 2015-2-11,PR926735
                        //TS:kaifeng.lu 2016-01-23 Calculator BUGFIX_1490013  MOD_S
                        //TS:kaifeng.lu 2015-12-08 Calculator BUGFIX_1001813  ADD_S
                        if(!Character.isDigit(nextChar) && nextChar != 'e' && nextChar != 'π') {
                            mIsEdited = true;
                        }
                        //TS:kaifeng.lu 2015-12-08 Calculator BUGFIX_1001813  ADD_E
                        //TS:kaifeng.lu 2016-01-23 Calculator BUGFIX_1490013  MOD_E
                        break;
                }
                tempExpr = tempExpr + singExpr;

            }
            //PR536081 End It can input one figure with two decimal points.update by xiaolu.li Aug 24 2015. from  one char appending change to serveral char appending

            // since this is the first edit replace the entire string
            if (!mIsEdited && tempExpr.length() > 0) {
                start = 0;
                mIsEdited = true;
            }
            tempExpr = mTokenizer.build2Local(tempExpr);
            return super.replace(start, end, tempExpr, 0, tempExpr.length());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
