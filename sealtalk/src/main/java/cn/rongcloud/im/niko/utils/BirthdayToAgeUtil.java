package cn.rongcloud.im.niko.utils;

import android.text.TextUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class BirthdayToAgeUtil {
    private static String birthday;
    private static String ageStr;
    private static int age;
    //出生年、月、日
    private static int year;
    private static int month;
    private static int day;
    public static String birthdayToAge(String birthday) {
        if(TextUtils.isEmpty(birthday)){return "";}
        BirthdayToAgeUtil.birthday = birthday;
        stringToInt(BirthdayToAgeUtil.birthday, "yyyy-MM-dd");
        // 得到当前时间的年、月、日
        Calendar cal = Calendar.getInstance();
        int yearNow = cal.get(Calendar.YEAR);
        int monthNow = cal.get(Calendar.MONTH) + 1;
        int dayNow = cal.get(Calendar.DATE);
        // 用当前年月日减去出生年月日
        int yearMinus = yearNow - year;
        int monthMinus = monthNow - month;
        int dayMinus = dayNow - day;
        age = yearMinus;// 先大致赋值
        if (yearMinus <= 0) {
            age = 0;
            ageStr = String.valueOf(age) + "周岁";
            return ageStr;
        }
        if (monthMinus < 0) {
            age = age - 1;
        } else if (monthMinus == 0) {
            if (dayMinus < 0) {
                age = age - 1;
            }
        }
        ageStr = String.valueOf(age) + "周岁";
        return ageStr;
    }


    public static String longToString(long strTime){
        strTime = strTime* 1000;
        Date date = new Date(strTime);
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);
    }

    /**
     * String类型转换成date类型
     * strTime: 要转换的string类型的时间，
     * formatType: 要转换的格式yyyy-MM-dd HH:mm:ss
     * //yyyy年MM月dd日 HH时mm分ss秒，
     * strTime的时间格式必须要与formatType的时间格式相同
     */
    private static Date longToString(String strTime, String formatType) {
        try {
            SimpleDateFormat formatter = new SimpleDateFormat(formatType);
            Date date;
            date = formatter.parse(strTime);
            return date;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * String类型转换为long类型
     * .............................
     * strTime为要转换的String类型时间
     * formatType时间格式
     * formatType格式为yyyy-MM-dd HH:mm:ss//yyyy年MM月dd日 HH时mm分ss秒
     * strTime的时间格式和formatType的时间格式必须相同
     */
    private static void stringToInt(String strTime, String formatType) {
        try {
            //String类型转换为date类型
            Calendar calendar = Calendar.getInstance();
            Date date = longToString(strTime, formatType);
            calendar.setTime(date);
            if (date == null) {
            } else {
                //date类型转成long类型
                year = calendar.get(Calendar.YEAR);
                month = calendar.get(Calendar.MONTH) + 1;
                day = calendar.get(Calendar.DAY_OF_MONTH);
            }
        } catch (Exception e) {
        }
    }

    public final static String RIDE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'Z'";
    public final static String FORMAT = "yyyy-MM-dd HH:mm:ss";
    public final static String FORMAT_Y_M = "MM-dd";

    public static String scFormatYearMonth(String dateString) {
        try {
            Date date = parseDate(dateString, RIDE_FORMAT);
            SimpleDateFormat formatter = new SimpleDateFormat(FORMAT_Y_M);
            return formatter.format(date);
        } catch (Exception e) {
            return "";
        }
    }


    public static String formartTimeRide(String dateString) {
        try {
            Date date = parseDate(dateString, RIDE_FORMAT);
            date = new Date(date.getTime()+ TimeZone.getDefault().getRawOffset());
            SimpleDateFormat formatter = new SimpleDateFormat(FORMAT);
            formatter.setTimeZone(TimeZone.getDefault());
            return formatter.format(date);
        } catch (Exception e) {
            return "";
        }
    }
    public static Date parseDate(String date, String format) {
        //Ride中使用的format为 "yyyy-MM-dd'T'HH:mm:ss'Z'"
        Date dt = null;
        SimpleDateFormat dateFormat = new SimpleDateFormat(format);
        try {
            dt = dateFormat.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return dt;
    }

    public static String formatTimeMillis(long millis){
        Date date = new Date(millis);
        return new SimpleDateFormat(FORMAT_Y_M).format(date);
    }



}
