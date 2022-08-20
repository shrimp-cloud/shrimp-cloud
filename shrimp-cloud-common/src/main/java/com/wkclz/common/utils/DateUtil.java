package com.wkclz.common.utils;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Description:
 * Created: wangkaicun @ 2017-10-18 下午10:21
 */
public class DateUtil {

    private static final Logger logger = LoggerFactory.getLogger(DateUtil.class);

    private static final String SDF_YYYY_MM_DD = "yyyy-MM-dd";
    private static final String SDF_YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";
    private static final String SDF_YYYY_MM_DD_HH_MM_SS_SSS = "yyyy-MM-dd HH:mm:ss.SSS";


    private final static long DAY = 24 * 60 * 60 * 1000L;
    private final static long HOUR = 60 * 60 * 1000L;
    private final static long MIN = 60 * 1000L;
    private final static long SEC = 1000L;


    /**
     * 字符串格式化为时间
     *
     * @param dateStr
     * @return
     */
    public static final Date getDate(String dateStr) {
        if (dateStr == null) {
            return null;
        }
        if (dateStr.length() == SDF_YYYY_MM_DD.length()) {
            dateStr += " 00:00:00";
        }
        if (dateStr.length() == SDF_YYYY_MM_DD_HH_MM_SS.length()) {
            SimpleDateFormat sdfYmdhms = new SimpleDateFormat(SDF_YYYY_MM_DD_HH_MM_SS);
            try {
                Date date = sdfYmdhms.parse(dateStr);
                return date;
            } catch (ParseException e) {
                logger.error(e.getMessage(), e);
            }
        }
        throw new RuntimeException("Error DateTime string");
    }


    // 获取今天 0 时
    public static Date getDayBegin() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        return calendar.getTime();
    }


    /**
     * 计算历史到两套上的时间，转换为直观的文字描述
     * @param history
     * @return
     */
    public static String getTimeDifference(Date history){
        return getTimeDifference(history, null);
    }

    /**
     * 计算历史到两套上的时间，转换为直观的文字描述
     * @param history
     * @param future
     * @return
     */
    public static String getTimeDifference(Date history, Date future){
        long now = System.currentTimeMillis();

        long his = now;
        long fut = now;

        if (history != null){
            his = history.getTime();
        }
        if (future != null){
            fut = future.getTime();
        }

        long timeLess = fut - his;

        long day = timeLess / DAY;
        timeLess = timeLess % DAY;

        long hour = timeLess / HOUR;
        timeLess = timeLess % HOUR;

        long min = timeLess / MIN;
        timeLess = timeLess % MIN;

        long sec = timeLess / SEC;

        StringBuffer sb = new StringBuffer();
        if (day > 0){
            sb.append(day + "天 ");
        }
        if (day > 0 || hour > 0){
            sb.append(hour + "时 ");
        }
        if (day > 0 || hour > 0 || min > 0){
            sb.append(min + "分 ");
        }
        if (day > 0 || hour > 0 || min > 0 || sec > 0){
            sb.append(sec + "秒");
        }
        if (sb.length() == 0){
            sb.append("0秒");
        }
        return sb.toString();
    }



}
