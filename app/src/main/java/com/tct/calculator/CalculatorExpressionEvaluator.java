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
 *
 *
 *  ==========================================================================
 *HISTORY
 *
 *Tag            Date         Author        Description
 *============== ============ =============== ==============================
 *BUGFIX_982004  2015/12/07   kaifeng.lu       [Android 5.1][Calculator_v5.2.1.7.0310.0]It has no work after press '='key when input number.
 *BUGFIX_1043015  2015/12/24   kaifeng.lu      [Android 6.0][Calculator_v5.2.1.7.0311.0]The 'eln(e)' operation result display 'error'.
 *BUGFIX_1201906 2015/12/25   kaifeng.lu       [Calculator]The calculation result is not correct
 *BUGFIX_1761630 2016/03/18   kaifeng.lu       [Calculator]The result of "2e2=" is 200
 ===========================================================================
 */

package com.tct.calculator;

import android.util.Log;

import java.util.Locale;

import org.javia.arity.Symbols;
import org.javia.arity.SyntaxException;

public class CalculatorExpressionEvaluator {
    private static final String TAG = "CalculatorExpression"; // MODIFIED by qiong.liu1, 2017-03-31,BUG-3621966
    private static final int MAX_DIGITS = 12;
    private static final int ROUNDING_DIGITS = 2;

    private final Symbols mSymbols;
    private final CalculatorExpressionTokenizer mTokenizer;

    //PR 893896 Check the result of calculated some is worng.about(sin(90).soc(90).tan(180)) by ting.ma at 2015.1.26 begin
    private static final String[] CIR_FUNC = {"sin(", "cos(", "tan(", "asin(", "acos(", "atan("};
    private static final String PI = "\u03c0\u00f7180";// PI:   π÷180
    //PR 893896 Check the result of calculated some is worng.about(sin(90).soc(90).tan(180)) by ting.ma at 2015.1.26 end

    private EvaluatorMode currentEvaMode = EvaluatorMode.DEGREE;

    public enum EvaluatorMode {
        DEGREE, RADIUS
    }

    public EvaluatorMode getEvaluatorMode() {
        return currentEvaMode;
    }

    public void setEvaluatorMode(EvaluatorMode evaMode) {
        currentEvaMode = evaMode;
    }

    public CalculatorExpressionEvaluator(CalculatorExpressionTokenizer tokenizer) {
        mSymbols = new Symbols();
        mTokenizer = tokenizer;
    }

    public void evaluate(CharSequence expr, EvaluateCallback callback) {
        evaluate(expr.toString().replace(",", ""), callback);
    }

    public void evaluate(String expr, EvaluateCallback callback) {
        double result;


        expr = replaceDigitNextToSquare(expr);
        expr = mTokenizer.local2Evaluate(expr);
        // remove any trailing operators
        while (expr.length() > 0 && "+-/*".indexOf(expr.charAt(expr.length() - 1)) != -1) {
            expr = expr.substring(0, expr.length() - 1);
        }
        //TS:kaifeng.lu 2016-03-18 EMAIL BUGFIX_1761630  MOD_S
        if (expr.length() > 0 && expr.contains("e")) {
            expr = splitE(expr);
        }
        //TS:kaifeng.lu 2016-03-18 EMAIL BUGFIX_1761630  MOD_E
        try {
            //TS:kaifeng.lu 2015-12-11 Calculator BUGFIX_1074185  MOD_S
            if (expr.length() == 0 || expr.equals(".")|| Double.valueOf(expr) != null) {
                //TS:kaifeng.lu 2015-12-11 Calculator BUGFIX_1074185  MOD_E
                callback.onEvaluate(expr, null, Calculator.INVALID_RES_ID);
                return;
            }
        } catch (NumberFormatException e) {
            // expr is not a simple number
            Log.d(TAG, "NumberFormatException evaluate : ", e); // MODIFIED by qiong.liu1, 2017-03-31,BUG-3621966
        }

        try {
            switch (currentEvaMode) {
                case DEGREE:
                    expr = changeRadius2Angle(expr);
                    break;
                default:
                    break;
            }
            //TS:kaifeng.lu 2016-03-18 EMAIL BUGFIX_1761630  DEL
            //TS:kaifeng.lu 2015-12-25 Calculator BUGFIX_1201906   ADD_S
//            if(expr.length() > 0 && expr.contains("%")){
//                expr=exprPercent(expr);
//            }
            //TS:kaifeng.lu 2015-12-25 Calculator BUGFIX_1201906   ADD_E
            result = mSymbols.eval(expr);

            /*if (Double.isNaN(result)) {
                callback.onEvaluate(expr, null, R.string.error_nan);
            // MODIFIED-BEGIN by qiong.liu1, 2017-03-31, BUG-3621966
            }*///PR934766 Division by zero is "Error", but not "Infinity" by ting.ma at 2015-03-13 begin
            //PR942395 When dividend is 0 then it shows Infinity (∞) by ting.ma at 2015-03-06 begin
            Log.d(TAG, "evaluate Double.isInfinite(result) : " + Double.isInfinite(result) + ";   Double.isNaN(result) : " + Double.isNaN(result)); // MODIFIED by qiong.liu1, 2017-03-31,BUG-3621966
            /* MODIFIED-END by qiong.liu1,BUG-3621966*/
            if (Double.isInfinite(result) || Double.isNaN(result)) {
                callback.onEvaluate(expr, null, R.string.error_syntax);
            } //PR942395 When dividend is 0 then it shows Infinity (∞) by ting.ma at 2015-03-06 end
            //PR934766 Division by zero is "Error", but not "Infinity" by ting.ma at 2015-03-13 end
            else {
                // The arity library uses floating point arithmetic when evaluating the expression
                // leading to precision errors in the result. The method doubleToString hides these
                // errors; rounding the result by dropping N digits of precision.
                //PR898333 Results of 20/3 is not correct by ting.ma at 2015.01.12 begin
                String resultStr = "";
                for (int precision = MAX_DIGITS; precision > 6; precision--) {
                    resultStr = tryFormattingWithPrecision(result, precision);
                    if (resultStr.length() <= MAX_DIGITS) {
                        break;
                    }
                }
                //PR898333 Results of 20/3 is not correct by ting.ma at 2015.01.12 end

                //PR1018314 '-1*0' is error by tingma at 2015-06-09 begin
                if (resultStr.equals("-0")) {
                    resultStr = "0";
                }
                //PR1018314 '-1*0' is error by tingma at 2015-06-09 end

                final String resultString = mTokenizer.evaluate2Local(
                        resultStr);
                /* MODIFIED-BEGIN by qiong.liu1, 2017-03-31,BUG-3621966*/
                Log.d(TAG, "evaluate resultString : " + resultString);
                callback.onEvaluate(expr, resultString, Calculator.INVALID_RES_ID);
            }
        } catch (SyntaxException e) {
            Log.d(TAG, "SyntaxException evaluate : ", e);
            /* MODIFIED-END by qiong.liu1,BUG-3621966*/
            callback.onEvaluate(expr, null, R.string.error_syntax);
        }
    }


    // PR898333 Results of 20/3 is not correct by ting.ma at 2015.01.12 begin
    //TS:kaifeng.lu 2015-12-07 Calculator BUGFIX_982004  MOD_S
    public String tryFormattingWithPrecision(double result, int precision) {
        //TS:kaifeng.lu 2015-12-07 Calculator BUGFIX_982004  MOD_E
        // The standard scientific formatter is basically what we need. We will
        // start with what it produces and then massage it a bit.
        String resultStr = String.format(Locale.US, "%" + MAX_DIGITS + "." + precision + "g", result);
        String mantissa = resultStr;
        String exponent = null;
        int e = resultStr.indexOf('e');
        if (e != -1) {
            mantissa = resultStr.substring(0, e);
            // Strip "+"  from the exponent for example exponent = "+08"
            exponent = resultStr.substring(e + 1);
            if (exponent.startsWith("+")) {
                exponent = exponent.substring(1);
            }
            // Strip unnecessary 0's from the exponent for example exponent = "08"
            exponent = String.valueOf(Integer.parseInt(exponent));
        } else {
            mantissa = resultStr;
        }
        int period = mantissa.indexOf('.');
        if (period == -1) {
            period = mantissa.indexOf(','); // the dot'.' is ',' in language of others for example Deutsch // MODIFIED by qiong.liu1, 2017-03-31,BUG-3621966
        }
        if (period != -1) {
            // Strip trailing 0's for example "1.0000" or "1.000eX"
            while (mantissa.length() > 0 && mantissa.endsWith("0")) {
                mantissa = mantissa.substring(0, mantissa.length() - 1);
            }
            // Strip trailing '.' for example "1."
            if (mantissa.length() == period + 1) {
                mantissa = mantissa.substring(0, mantissa.length() - 1);
            }
        }

        if (exponent != null) {
            resultStr = mantissa + 'e' + exponent;
        } else {
            resultStr = mantissa;
        }
        return resultStr;
    }
    //PR898333 Results of 20/3 is not correct by ting.ma at 2015.01.12 end

    /**
     * Cause we replace the square operator ² with power ^2, case like this 4²2 will be 4^22,it should be 4^2*2.
     * @param expr  expression to be replace
     * @return  expression after replaced
     */
    public static String replaceDigitNextToSquare(String expr){
        //  local expr 4²2 change to normal expr 4^22,should replace it with 4²*2.
        int index = expr.lastIndexOf("²");
        while (index != -1) {
            if (index + 1 != expr.length() && Character.isDigit(expr.charAt(index + 1))) {
                expr = expr.substring(0, index + 1) + "*" + expr.substring(index + 1);
            }
            index = expr.substring(0, index).lastIndexOf("²");
        }
        return expr;
    }

    /**
     * Change radius to angel in expression.
     * For circular function, multiply function argument with π/180:   sin(rad) -> sin(π/180(rad))
     * For inverse circular function, multiply function result with 180/π:  asin(rad) -> (180/πasin(rad))
     * @param expr  expression to be change
     * @return  expression after been changed
     * @author  boyang@tcl.com
     * @date    Nov 6th 2015
     */
    public static String changeRadius2Angle(String expr) {
        StringBuilder builder = new StringBuilder();
        int funcLength = 0;
        int index = 0, indexRparen = 0;
        while (index < expr.length() - 4) {
            for (String triFunc : CIR_FUNC) {
                if (expr.startsWith(triFunc, index)) {
                    funcLength = triFunc.length();
                    indexRparen = findRightParenMatchFirstLeftParen(expr, index + funcLength - 1);
                    builder.delete(0,builder.length());
                    builder.append(expr.substring(0, index));
                    if (funcLength == 4) {
                        // trigonometric function: sin( cos( tan(
                        builder.append(triFunc);
                        builder.append("π/180(");
                    } else if (funcLength == 5) {
                        // inverse trigonometric function: asin( acos( atan(
                        builder.append("(180/π");
                        builder.append(triFunc);
                    }
                    builder.append(expr.substring(index+funcLength,indexRparen));
                    builder.append(")");
                    builder.append(expr.substring(indexRparen));
                    expr=builder.toString();
                    index += funcLength + 5; //triFunc.length()+"π/180(".length()  or  "(180/π".length()+triFunc.length(),it should be 6,and i made index++ out of the circle,make sure index will increase in case this "if" wouldn't run.I'm such a genius!
                    break;
                }
            }
            index++;
        }
        return expr;
    }

    //TS:kaifeng.lu 2015-12-24 EMAIL BUGFIX_1043015  ADD_S
    //TS:kaifeng.lu 2016-03-18 EMAIL BUGFIX_1761630  MOD_S
    public static String splitE(String expr){
        StringBuilder builder = new StringBuilder();
        int indexRparen = expr.length();
        for (int i = 0; i < indexRparen; i++) {
            char previousChar = 0;
            char nextChar = 0;
            char letter = expr.charAt(i);
            if(letter =='e'){
                if(i - 1 >=0) {
                    previousChar =expr.charAt(i-1);
                } if(i+1<expr.length()) {
                    nextChar = expr.charAt(i+ 1 );
                }
                if((Character.isDigit(previousChar) && Character.isDigit(nextChar))|| (! Character.isDigit(previousChar)&& !Character.isDigit(nextChar))) {
                    if(previousChar==letter && (nextChar!='l'||nextChar!='t'||nextChar!='a'||nextChar!='c'||nextChar!='s')){
                        builder.append("*e");
                        if(nextChar=='l'||nextChar=='t'||nextChar=='a'||nextChar=='c'||nextChar=='s'){
                            builder.append("*e*");
                        }
                    }
                    else if(! Character.isDigit(previousChar)){
                        if(nextChar=='l'||nextChar=='t'||nextChar=='a'||nextChar=='c'||nextChar=='s'){
                            builder.append("e*");
                        }else {builder.append(letter);}
                    }
                    else if(! Character.isDigit(nextChar)){

                    }
                    else{
                        builder.append(letter);
                    }
                }
                if(Character.isDigit(previousChar)&& !Character.isDigit(nextChar)){
                    if(nextChar=='l'||nextChar=='t'||nextChar=='a'||nextChar=='c'||nextChar=='s'){
                        builder.append("*e*");
                    }else {
                        builder.append("*e");
                    }
                }
                if (! Character.isDigit(previousChar)&& Character.isDigit(nextChar)){
                    if(previousChar == letter){
                        builder.append("*e*");
                    }else{
                        builder.append("e*");}
                }
//                if(nextChar=='l'||nextChar=='t'||nextChar=='a'||nextChar=='c'||nextChar=='s'){
//                    builder.append("e*");
//                }
            }
            else {
                builder.append(letter);
            }
        }
        expr = builder.toString();
        return expr;
    }
    //TS:kaifeng.lu 2016-03-18 EMAIL BUGFIX_1761630  MOD_E
    //TS:kaifeng.lu 2015-12-24 EMAIL BUGFIX_1043015  ADD_E

    /**
     * Find the right paren match first left paren in expression,begins from start position.
     * @param expr  expression to be search
     * @param start start searching position
     * @return  index of the right match paren,
     *          or index after the last character of the expr if no right match paren exists:eg. sin(1
     *          or -1 if only right paren exists:eg. sin1)
     * @author  boyang@tcl.com
     * @date    Nov 6th 2015
     */
    private static int findRightParenMatchFirstLeftParen(String expr, int start) {
        int unmatchLeftParen=0;
        int length=expr.length();
        int index=start;
        while(index<length){
            if (expr.charAt(index)=='('){
                unmatchLeftParen++;
            }else if(expr.charAt(index)==')'){
                unmatchLeftParen--;
                if (unmatchLeftParen==0)break;
            }
            index++;
        }
        if (unmatchLeftParen<0) return -1;
        return index;
    }

    public interface EvaluateCallback {
        public void onEvaluate(String expr, String result, int errorResourceId);
    }
    //TS:kaifeng.lu 2015-12-07 Calculator BUGFIX_982004  ADD_S
    public synchronized Symbols getmSymbols(){
        return mSymbols==null? new Symbols():mSymbols;
    }
    //TS:kaifeng.lu 2015-12-07 Calculator BUGFIX_982004  ADD_E
    //TS:kaifeng.lu 2015-12-25 Calculator BUGFIX_1201906   ADD_S
//    public String exprPercent(String expr){
//        StringBuilder builder = new StringBuilder();
//        int indexRparen = expr.length();
//        for (int i = 0 ; i< indexRparen ; i++){
//            char letter = expr.charAt(i);
//            if (expr.charAt(i) =='%'){
//                builder.append("*1%");
//            }else {
//                builder.append(letter);
//            }
//        }
//        expr = builder.toString();
//        return expr;
//    }
    //TS:kaifeng.lu 2015-12-25 Calculator BUGFIX_1201906   ADD_E
}
