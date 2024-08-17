package com.wkclz.common.utils;

import com.wkclz.common.tools.RegularTool;

import java.util.ArrayList;
import java.util.List;

/**
 * Description:
 * Created: wangkaicun @ 2017-10-20 上午12:58
 */
public class IntegerUtil {


    /**
     * str2IntegerList
     *
     * @param str
     * @return
     */
    public static List<Integer> str2IntegerList(String str) {
        String[] split = str.split(",");
        List<Integer> intArr = new ArrayList<>();
        for (String id : split) {
            // 只有是数字型的字符串才加进去
            if (RegularTool.isPositiveInteger(id)) {
                intArr.add(Integer.parseInt(id));
            }
        }
        return intArr;
    }

    public static List<Long> str2LongList(String str) {
        String[] split = str.split(",");
        List<Long> intArr = new ArrayList<>();
        for (String id : split) {
            // 只有是数字型的字符串才加进去
            if (RegularTool.isPositiveInteger(id)) {
                intArr.add(Long.parseLong(id));
            }
        }
        return intArr;
    }


}
