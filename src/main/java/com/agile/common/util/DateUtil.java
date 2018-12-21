package com.agile.common.util;

import org.apache.commons.lang.time.DateUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * @author 佟盟 on TWO017/7/13
 */
public class DateUtil extends DateUtils {
    private static final int DATE_AREA = 8;
    private static final int DATE_UNIT = 60;
    private static final int MIN_UNIT = 1000;
    private static final int HOUR_UNIT = 24;
    private static final int TWO = 2;
    /**
     * 5.TWO.ES 日期格式指定（yyyy.MM.dd）
     */
    public static final String ES_YYYYMMDD = "yyyy.MM.dd";
    /**
     * 日期格式指定（yyyy）
     */
    public static final String YYYY = "yyyy";
    /**
     * 日期格式指定（yyyyMM）
     */
    public static final String YYYYMM = "yyyyMM";
    /**
     * 日期格式指定（yyyy-MM）
     */
    public static final String YYYYMM_HYPHEN = "yyyy-MM";
    /**
     * 日期格式指定（yyyy/MM）
     */
    public static final String YYYYMM_SLASH = "yyyy/MM";
    /**
     * 日期格式指定（yyyyMMdd）
     */
    public static final String YYYYMMDD = "yyyyMMdd";
    /**
     * 日期格式指定(yyyy-MM-dd)
     */
    public static final String YYYYMMDD_HYPHEN = "yyyy-MM-dd";
    /**
     * 日期格式指定(MM-dd)
     */
    public static final String MMDD_HYPHEN = "MM-dd";
    /**
     * 日期格式指定(MM/dd)
     */
    public static final String MMDD_SLASH = "MM/dd";
    /**
     * 日期格式指定（yyyyMMddHHmmss）
     */
    public static final String YMD_HMS_NOSLASH = "yyyyMMddHHmmss";
    /**
     * 日期格式指定（yyMMddHHmmss）
     */
    public static final String YYMMDD_HMS_NOSLASH = "yyMMddHHmmss";
    /**
     * 日期格式指定（yyyy/MM/dd）
     */
    public static final String YYYYMMDD_SLASH = "yyyy/MM/dd";
    /**
     * 日期格式指定（HH:mm:ss）
     */
    public static final String HHMMSS_COLON = "HH:mm:ss";
    /**
     * 日期格式指定（HH:mm）
     */
    public static final String HHMM_COLON = "HH:mm";
    /**
     * 日期格式指定（yyyyMMddHHmm）
     */
    public static final String YYYYMMDDHHMM = "yyyyMMddHHmm";
    /**
     * 日期格式指定（yyyy-MM-dd HH:mm）
     */
    public static final String YYYYMMDDHHMM_HYPHEN = "yyyy-MM-dd HH:mm";
    /**
     * 日期格式指定（yyyy/MM/dd HH:mm）
     */
    public static final String YYYYMMDDHHMM_SLASH = "yyyy/MM/dd HH:mm";
    /**
     * 日期格式指定（yyyy/MM/ddHHmm）
     */
    public static final String YYYYMMDDHHMM_SLASA = "yyyy/MM/ddHHmm";
    /**
     * 日期格式指定（yyyyMMddHHmmssSSS）
     */
    public static final String YYYYMMDDHHMMSSSSS = "yyyyMMddHHmmssSSS";
    /**
     * 日期格式指定（YYYYMMDDHHMMSS_HYPHEN_SSSSSS）
     */
    public static final String YYYYMMDDHHMMSS_HYPHEN_SSSSSS = "yyyy-MM-dd HH:mm:ss.SSSSSS";
    /**
     * 日期格式指定（yyyy-MM-dd HH:mm:ss）
     */
    public static final String YYYYMMDDHHMMSS_HYPHEN = "yyyy-MM-dd HH:mm:ss";
    /**
     * 日期格式指定（yyyy-MM-dd HH:mm:ss.SSS）
     */
    public static final String YYYYMMDDHHMMSS_HYPHEN_SSS = "yyyy-MM-dd HH:mm:ss.SSS";
    /**
     * 日期格式指定（yyyy/MM/dd HH:mm:ss）
     */
    public static final String YYYYMMDDHHMMSS_SLASH = "yyyy/MM/dd HH:mm:ss";
    /**
     * 日期格式指定（yyyy/MM/dd HH:mm:ss）
     */
    public static final String YYMMDDHHMMSS_SLASH = "yy-MM-dd HH:mm:ss";
    /**
     * 日期格式指定（yyyy年）
     */
    public static final String YYYY_KANNJI = "yyyy年";
    /**
     * *日期格式指定（dd）
     */
    public static final String DD_KANNJI = "dd";
    /**
     * *日期格式指定（EEEEs）
     */
    public static final String EEEE_KANNJI = "EEEE";
    /**
     * 日期格式指定（yyyy年MM月）
     */
    public static final String YYYYMM_KANNJI = "yyyy年MM月";
    /**
     * 日期格式指定（yyyy年MM月dd日）
     */
    public static final String YYYYMMDD_KANNJI = "yyyy年MM月dd日";
    /**
     * 日期格式指定（yyyy/MM/dd (E)）
     */
    public static final String YYYYMMDD_E = "yyyy/MM/dd (E)";
    /**
     * 日期格式指定（yyyy年MM月dd日）
     */
    public static final String YYYYMMDD_KANNJI_E = "yyyy年MM月dd日 (E)";
    /**
     * 日期格式指定（yyyy/MM/dd(E)）
     */
    public static final String YYYYMMDDE = "yyyy/MM/dd(E)";
    /**
     * 日期格式指定（yyyy年MM月dd日(E)）
     */
    public static final String YYYYMMDD_KANNJIE = "yyyy年MM月dd日(E)";
    /**
     * 日期格式指定（yyyy年MM月dd日(E) HH:mm）
     */
    public static final String YYYYMMDDHHMM_KANNJIE = "yyyy年MM月dd日(E) HH:mm";
    /**
     * 日期格式指定（yyyy/MM/dd（E））
     */
    public static final String YYYYMMDD_ZENNKAKU_E = "yyyy/MM/dd（E）";
    /**
     * 日期格式指定（yyyy-MM-dd'T'HH:mm:ss.SSS'Z'）
     */
    public static final String YYYYMMDDTHHMMSSSSSZ = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
    /**
     * 日期格式指定（yyyy-MM-dd'T'HH:mm:ss.SSS+mm:ss）
     */
    public static final String YYYYMMDDTHHMMSSSSSXXX = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX";
    /**
     * mysql查询日期格式指定（%Y-%m）
     */
    public static final String YYYYMM_MYSQL = "%Y-%m";
    /**
     * mysql查询日期格式指定（%Y-%m-%d）
     */
    public static final String YYYYMMDD_MYSQL = "%Y-%m-%d";
    /**
     * mysql查询日期格式指定（%Y-%m-%d %H）
     */
    public static final String YYYYMMDDHH_MYSQL = "%Y-%m-%d %H";
    /**
     * mysql查询日期格式指定（%Y-%m-%d %H:%M）
     */
    public static final String YYYYMMDDHHII_MYSQL = "%Y-%m-%d %H:%i";
    /**
     * mysql查询日期格式指定（%Y-%m-%d %H:%M:%S）
     */
    public static final String YYYYMMDDHHIISS_MYSQL = "%Y-%m-%d %H:%i:%s";

    /**
     * 获取Long型时间戳
     */
    public static long getTimeStamp() {
        return getTimeStamp(new Date());
    }

    /**
     * 获取Long型时间戳
     */
    public static long getTimeStamp(Date date) {
        return date.getTime();
    }

    /**
     * 获取时间戳字符串
     */
    public static String getTimeStampStr() {
        return getTimeStampStr(new Date());
    }

    /**
     * 获取时间戳字符串
     */
    public static String getTimeStampStr(Date date) {
        return Long.toString(date.getTime());
    }

    /**
     * 获取时间戳字符串
     */
    public static Date getCurrentDate() {
        return new Date(System.currentTimeMillis());
    }

    /**
     * 字符串转日期
     *
     * @param date   日期字符串
     * @param format 格式
     */
    public static Date toDateByFormat(String date, String format) throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat(format);
        return dateFormat.parse(date);
    }

    public static String toFormatByDate(Date date, String format) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(format);
        return dateFormat.format(date);
    }

    /**
     * 获取系统时间
     *
     * @return Date 系统时间
     */
    public static Date getSysdate() {
        return Calendar.getInstance().getTime();
    }

    /**
     * 获取指定format格式的系统时间
     *
     * @param format 时间格式
     * @return 指定format格式的系统时间
     */
    public static String getCurrentDateString(String format) {
        if (StringUtil.isEmpty(format)) {
            format = YYYYMMDD;
        }
        return convertToString(getSysdate(), format);
    }

    /**
     * 获取指定format格式的系统时间
     *
     * @param format 时间格式
     * @return 指定format格式的系统时间
     */
    public static Date getCurrentDate(String format) throws ParseException {
        if (StringUtil.isEmpty(format)) {
            format = YYYYMMDD;
        }
        return convertToDate(convertToString(getSysdate(), format), format);
    }

    /**
     * 将文字串时间按照指定format格式转为Date类型时间
     *
     * @param dateStr 文字串时间
     * @param format  时间格式
     * @return Date类型时间
     */
    public static Date convertToDate(String dateStr, String format) throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        return simpleDateFormat.parse(dateStr);
    }

    /**
     * 将文字串时间按照指定format格式转为Date类型时间(涉及时区转换{中国时间})
     *
     * @param dateStr 文字串时间
     * @param format  时间格式
     * @return Date类型时间
     */
    public static Date convertToDate(String dateStr, String format, boolean timeZone) throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        if (timeZone) {
            //增加dateArea个时区的时间
            return addMin(simpleDateFormat.parse(dateStr), DATE_AREA * DATE_UNIT);
        } else {
            return simpleDateFormat.parse(dateStr);
        }
    }

    /**
     * 将Date类型时间按照指定format格式转为字符串类型的时间
     *
     * @param date   Date类型时间
     * @param format 时间格式
     * @return 字符串类型时间
     */

    public static String convertToString(Date date, String format) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        return simpleDateFormat.format(date);
    }

    /**
     * 将Date类型时间按照指定format格式转为字符串类型的时间(时区转换{中国时区})
     *
     * @param date     Date类型时间
     * @param format   时间格式
     * @param timeZone 是否时区转换
     * @return
     */
    public static String convertToString(Date date, String format, boolean timeZone) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        if (timeZone) {
            return simpleDateFormat.format(addMin(date, -(DATE_AREA * DATE_UNIT)));
        } else {
            return simpleDateFormat.format(date);
        }
    }

    /**
     * 比较两个Date类型时间是否相同
     *
     * @param date1 Date类型时间1
     * @param dateTWO Date类型时间TWO
     * @return 判断结果(true ： 相同 ， flase ： 不同)
     */
    public static boolean isSameDay(Date date1, Date dateTWO) {
        if (date1 == null || dateTWO == null) {
            throw new IllegalArgumentException("The date must not be null");
        }
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date1);
        Calendar calTWO = Calendar.getInstance();
        calTWO.setTime(dateTWO);
        return cal1.get(Calendar.ERA) == calTWO.get(Calendar.ERA)
                && cal1.get(Calendar.YEAR) == calTWO.get(Calendar.YEAR) && cal1
                .get(Calendar.DAY_OF_YEAR) == calTWO.get(Calendar.DAY_OF_YEAR);
    }

    /**
     * 验证当前时间在指定天数后是否满足截至日期范围内
     *
     * @param nowYmd 当前时间
     * @param endYmd 截止时间
     * @param days   天数
     * @return 判断结果(true ： 当前时间在指定天数后大于等于截至日期 ， flase ： 当前时间在指定天数后小于截至日期)
     */
    public static boolean isRangeDay(Date nowYmd, Date endYmd, int days) {
        if (nowYmd == null || endYmd == null) {
            throw new IllegalArgumentException("The date must not be null");
        }
        Date targetYmd = addDay(nowYmd, days);
        return compareDate(targetYmd, endYmd) >= 0;
    }

    /**
     * 计算两个日期之间相差的天数
     *
     * @param smdate 较小的时间
     * @param bdate  较大的时间
     * @return 相差天数
     */
    public static String getDaysBetween(Date smdate, Date bdate) throws ParseException {
        smdate = convertToDate(convertToString(smdate, "yyyy-MM-dd"), "yyyy-MM-dd");
        bdate = convertToDate(convertToString(bdate, "yyyy-MM-dd"), "yyyy-MM-dd");
        Calendar cal = Calendar.getInstance();
        cal.setTime(smdate);
        long time1 = cal.getTimeInMillis();
        cal.setTime(bdate);
        long timeTWO = cal.getTimeInMillis();
        long betweenDays = (timeTWO - time1) / (MIN_UNIT * DATE_UNIT * DATE_UNIT * HOUR_UNIT);

        return String.valueOf(betweenDays);
    }

    /**
     * 根据传递的时间段计算  返回显示时间粒度
     *
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @return
     */
    public static String getDaysBetween(long startTime, long endTime) {
        //计算TWO个long型时间差值 大于1天时，按天级返回， 小于1天，按小时返回
        if ((endTime - startTime) / MIN_UNIT / DATE_UNIT * DATE_UNIT / HOUR_UNIT > 1) {
            return YYYYMMDD_MYSQL;
        } else {
            return YYYYMMDDHH_MYSQL;
        }
    }

    /**
     * Date类型日期比较大小处理
     *
     * @param targetYmd Date类型日期target
     * @param baseYmd   Date类型日期base
     * @return 比较结果（0：相同，&gt;0：base日期小于target日期，&lt;0：base日期大于target日期）
     */
    public static int compareDate(Date targetYmd, Date baseYmd) {
        return targetYmd.compareTo(baseYmd);
    }

    /**
     * 获取指定日期N年后的时间
     *
     * @param targetDay 指定日期
     * @param years     年数
     * @return 返回结果
     */
    public static Date addYear(Date targetDay, int years) {
        return addDates(targetDay, years, 0, 0);
    }

    /**
     * 获取指定日期N月后的时间
     *
     * @param targetDay 指定日期
     * @param months    月数
     * @return 返回结果
     */
    public static Date addMonth(Date targetDay, int months) {
        return addDates(targetDay, 0, months, 0);
    }

    /**
     * 获取指定日期N天后的时间
     *
     * @param targetDay 指定日期
     * @param days      天数
     * @return 返回结果
     */
    public static Date addDay(Date targetDay, int days) {
        return addDates(targetDay, 0, 0, days);
    }

    /**
     * 获取指定日期后时间（指定加算年月日信息）
     *
     * @param targetDate 指定日期
     * @param addYear    增加年数
     * @param addMonth   增加月数
     * @param addDay     增加日数
     * @return 加算后日期
     */
    public static Date addDates(Date targetDate, int addYear, int addMonth, int addDay) {
        final int five = 5;
        final int one = 1;
        Calendar cal = GregorianCalendar.getInstance();
        cal.setTime(targetDate);
        cal.add(one, addYear);
        cal.add(TWO, addMonth);
        cal.add(five, addDay);
        return cal.getTime();
    }

    /**
     * 获取指定日期后时间（指定加算分钟信息）
     *
     * @param targetDate 指定日期
     * @param addMin     增加分钟
     * @return 加算后日期
     */
    public static Date addMin(Date targetDate, int addMin) {
        Calendar cal = GregorianCalendar.getInstance();
        cal.setTime(targetDate);
        cal.add(Calendar.MINUTE, addMin);
        return cal.getTime();
    }

    /**
     * 将毫秒（Long类型）转换成日期
     *
     * @param time 毫秒数
     * @return 返回的日期
     */
    public static Date timeToDate(Long time) {
        GregorianCalendar gc = new GregorianCalendar();
        gc.setTimeInMillis(time);
        return gc.getTime();
    }

    /**
     * 将日期字符串转成想要格式的日期字符串
     *
     * @param date     日期（String）
     * @param format   日期格式（String）
     * @param formatTo 转成日期的格式（String）
     * @return
     */
    public static String stringToString(String date, String format, String formatTo) throws ParseException {
        if (StringUtil.isNotEmpty(date)) {
            Date da = convertToDate(date, format);
            return convertToString(da, formatTo);
        }
        return "";
    }

    public static String stringToString(String date, String format, String formatTo, boolean adddateArea) throws ParseException {
        if (StringUtil.isNotEmpty(date)) {
            Date da = convertToDate(date, format, adddateArea);
            return convertToString(da, formatTo);
        }
        return "";
    }

    /**
     * 将UNIX时间戳转成YYYYMMDDSSSSS格式的日期字符串
     *
     * @param date 日期（String）
     * @return
     */
    public static String unixTimeStampToYYYYMMDDSSSSS(String date) {
        long timestamp = Long.parseLong(date);
        return new SimpleDateFormat(YYYYMMDDHHMMSSSSS).format(new Date(timestamp));
    }

    /**
     * 将UNIX时间戳转成指定格式的日期字符串
     *
     * @param timestamp 日期（long）
     * @param format    格式
     * @return
     */
    public static String unixTimeStampToFormatString(long timestamp, String format) {
        return new SimpleDateFormat(format).format(new Date(timestamp));
    }


    /**
     * 计算起始时间和结束时间相隔多少时间
     * 获取时间单位   y:年, M:月，w:周，d:天，h:小时，m:分钟，s:秒
     *
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @return 格式  数字 ：单位  中间用":"分割
     */
    public static String getCalculateBetweenTime(long startTime, long endTime) {
        if (endTime - startTime < 0) {
            return "";
        }
        Calendar startDate = Calendar.getInstance();
        Calendar endDate = Calendar.getInstance();
        //起始时间
        startDate.setTime(new Date(startTime));
        //结束时间
        endDate.setTime(new Date(endTime));
        //获取分钟数
        long min = (endTime - startTime) / MIN_UNIT / DATE_UNIT;
        //获取小时
        long hh = (endTime - startTime) / MIN_UNIT / DATE_UNIT / DATE_UNIT;
        //获取天
        long dd = (endTime - startTime) / MIN_UNIT / DATE_UNIT / DATE_UNIT / HOUR_UNIT;

        if (min <= HOUR_UNIT / TWO) {
            return min + ":" + "m";
        } else if (hh <= TWO * HOUR_UNIT) {
            return hh + ":" + "h";
        } else {
            return dd + ":" + "d";
        }
    }

    /**
     * 时间刻度判断
     *
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @return
     */
    public static String getTimeScale(long startTime, long endTime) {
        long t1 = endTime - startTime;
        return t1 <= DATE_UNIT * MIN_UNIT * DATE_UNIT * TWO ? "m" : t1 <= DATE_UNIT * MIN_UNIT * DATE_UNIT * TWO * HOUR_UNIT ? "h" : "d";
    }
}
