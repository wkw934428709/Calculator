package com.tct.calculator.data;

/*
==========================================================================
 *HISTORY
 *
 *Tag            Date         Author        Description
 *============== ============ =============== ==============================
 *BUGFIX_1239024   2015/12/28   kaifeng.lu      [Calculator]the Paste  is error when the copy is a negative number
 ===========================================================================
 */
import android.util.Log;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by xlli on 8/26/15.
 * //PR1075592 don't show paster button when clipboard hasn't vaild paster content Update by xiaolu.li 26/8/201
 */
public class PatternUtil {
    private static PatternUtil instance = new PatternUtil();
    public static PatternUtil getInstance() {
        return instance;
    }

    /**
     * whether contain 0-9 or e or -
     * @param str
     * @return
     */
    public boolean isContainNumber(String str) {
        //TS:kaifeng.lu 2015-12-28 Calculator BUGFIX_1239024  MOD_S
        Pattern p = Pattern.compile("[0-9|.|e|\\-|−]+");
        //TS:kaifeng.lu 2015-12-28 Calculator BUGFIX_1239024  MOD_E
        Matcher m = p.matcher(str);
        return m.find();
    }

    /**
     * pattern all vaild paster content
     * @param str
     * @return
     */
    public String getVaildPaster(String str) {
        //TS:kaifeng.lu 2015-12-28 Calculator BUGFIX_1239024  MOD_S
        Pattern p = Pattern.compile("[0-9|.|e|\\-|−]+");
        //TS:kaifeng.lu 2015-12-28 Calculator BUGFIX_1239024  MOD_E
        Matcher m = p.matcher(str);
        String re="";
        while(m.find()){
            re=re+m.group();
        }
        return re;
    }
}
