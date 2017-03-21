package util;


import java.sql.Time;

/**
 * 时间转换帮助类
 * Created by SmileSB101 on 2016/10/30 0030.
 */
public class ConvertTime{
	private final static String DD = "DD";
	private final static String HH = "HH";
	private final static String hh = "hh";
	private final static String mm = "mm";
	private final static String ss = "ss";
	/**
	 * 转换int毫秒为时间格式
	 * @param time 需要转换的时间（毫秒单位）
	 * @param FormatType 时间格式
	 * @return
	 */
	public static String ConvertIntToTime(int time,String FormatType)
	{
		time = time/1000;//换成秒
		String[] Type = FormatType.split(":");
		String ReString = "";
		int temp = 00;
		for(String string:Type)
		{
			switch(string)
			{
				case DD:
					temp = time/76400;
					time -= temp * 7600;
					break;
				case HH:
				case hh:
					temp = time/3600;
					time -= temp*3600;
					break;
				case mm:
					temp = time/60;
					time -= temp*60;
					break;
				case ss:
					ReString += time;
					return ReString;
			}
			ReString += temp+":";
		}
		return ReString;
	}
}
