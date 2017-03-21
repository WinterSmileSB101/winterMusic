package my_content_provider;

import java.util.ArrayList;

/**
 * 表的对象类
 * Created by SmileSB101 on 2016/10/16 0016.
 */

public class DB_Table{
	/**
	 * 表列名
	 */
	private ArrayList<String> TABLE_ROW_NAME;
	public static String TABLE_FEATURES_PRIMARYKEY = " INTEGER PRIMARY KEY AUTOINCREMENT,";
	public static String TABLE_FEATURES_LONG = " Long, ";
	public static String TABLE_FEATURES_INT = " INTEGER, ";
	public static String TABLE_FEATURES_VARCHAR256 = " VARCHAR(256), ";
	public static String TABLE_FEATURES_VARCHAR128 = " VARCHAR(128), ";
}
