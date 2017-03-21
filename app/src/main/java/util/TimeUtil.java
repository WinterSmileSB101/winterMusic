package util;

import android.provider.Settings;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 时间的工具类
 * Created by SmileSB101 on 2016/10/31 0031.
 */

public class TimeUtil{
	public final static String date_time = "yyyy年MM月dd日 HH:mm:ss";
	public final static String time = "HH:mm:ss";
	public final static String timeNoss = "HH:mm";
	public final static String date = "yyyy年MM月dd日";
	/**
	 * 获取时间（HH：MM：SS）
	 * @return
	 */
	public static String getTime(String FormatType)
	{
		SimpleDateFormat formatter = new SimpleDateFormat (FormatType);
		Date curDate = new Date(System.currentTimeMillis());//获取当前时间
		String str = formatter.format(curDate);
		return str;
	}
}
