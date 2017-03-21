package my_content_provider;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import StaticValue.StaticValue;

/**
 * 自定义的数据库帮助类
 * Created by SmileSB101 on 2016/10/16 0016.
 */

public class DBHelper extends SQLiteOpenHelper{
	private final static int DB_VERSION = 1;
    private StaticValue.DB_LIST_CODE db_list;
	/**
	 * 表名数组
	 */
	public final String[] TABLE_NAME = new String[]{"PlayList_table","Single_Song_table","Singer_table","Album_table","Folder_table"};
	public DBHelper(Context context,StaticValue.DB_LIST_CODE db_list)
	{
		super(context,StaticValue.DB_NAME,null,DB_VERSION);
		this.db_list = db_list;
	}
	/**
	 * 创建数据库
	 * @param db
	 */
	@Override
	public void onCreate(SQLiteDatabase db){
		//建立表的CMD命令
		String TABLE_CMD = " CREATE TABLE ";
		//根据不同的表名建立不同表
		switch(db_list)
		{
			case PLAY_LIST://播放表
				TABLE_CMD += TABLE_NAME[0]+
						"("
						+"id"+" INTEGER PRIMARY KEY AUTOINCREMENT, "
						+"music_id"+" LONG, "
						+"music_title"+" VARCHAR(256), "
						+"music_album"+" VARCHAR(256), "
						+"music_albumId"+" LONG, "
						+"music_displayName"+" VARCHAR(256), "
						+"music_artist"+" VARCHAR(128), "
						+"music_duration"+" LONG, "
						+"music_size"+" LONG, "
						+"music_url"+" VARCHAR(256) "+
						");";
				break;
			case SINGER_LIST://歌手表
				TABLE_CMD += TABLE_NAME[2]+
						"("
						+"id"+" INTEGER PRIMARY KEY AUTOINCREMENT, "
						+"music_id"+" LONG, "
						+"music_title"+" VARCHAR(256), "
						+"music_album"+" VARCHAR(256), "
						+"music_albumId"+" LONG, "
						+"music_displayName"+" VARCHAR(256), "
						+"music_artist"+" VARCHAR(128), "
						+"music_duration"+" LONG, "
						+"music_size"+" LONG, "
						+"music_url"+" VARCHAR(256) "+
						");";
				break;
			case SINGLE_SONG_LIST://单曲表
				TABLE_CMD += TABLE_NAME[1]+
						"("
						+"id"+" INTEGER PRIMARY KEY AUTOINCREMENT, "
						+"music_id"+" LONG, "
						+"music_title"+" VARCHAR(256), "
						+"music_album"+" VARCHAR(256), "
						+"music_albumId"+" LONG, "
						+"music_displayName"+" VARCHAR(256), "
						+"music_artist"+" VARCHAR(128), "
						+"music_duration"+" LONG, "
						+"music_size"+" LONG, "
						+"music_url"+" VARCHAR(256) "+
						");";
				break;
			case ALBUM_LIST://专辑表
				TABLE_CMD += TABLE_NAME[3]+
						"("
						+"id"+" INTEGER PRIMARY KEY AUTOINCREMENT, "
						+"music_id"+" LONG, "
						+"music_title"+" VARCHAR(256), "
						+"music_album"+" VARCHAR(256), "
						+"music_albumId"+" LONG, "
						+"music_displayName"+" VARCHAR(256), "
						+"music_artist"+" VARCHAR(128), "
						+"music_duration"+" LONG, "
						+"music_size"+" LONG, "
						+"music_url"+" VARCHAR(256) "+
						");";
				break;
			case FOLDER_LIST://文件夹表
				TABLE_CMD += TABLE_NAME[4]+
						"("
						+"id"+" INTEGER PRIMARY KEY AUTOINCREMENT, "
						+"music_id"+" LONG, "
						+"music_title"+" VARCHAR(256), "
						+"music_album"+" VARCHAR(256), "
						+"music_albumId"+" LONG, "
						+"music_displayName"+" VARCHAR(256), "
						+"music_artist"+" VARCHAR(128), "
						+"music_duration"+" LONG, "
						+"music_size"+" LONG, "
						+"music_url"+" VARCHAR(256) "+
						");";
				break;
		}
		db.execSQL(TABLE_CMD);//建立表
	}

	/**
	 * 更新数据库
	 * @param db
	 * @param oldVersion
	 * @param newVersion
	 */
	@Override
	public void onUpgrade(SQLiteDatabase db,int oldVersion,int newVersion){
        //如果遇到数据库更新，我们简单的处理为删除以前的表，重新创建一张
		db.execSQL(" DROP TABLE IF EXISTS "+ TABLE_NAME);
		onCreate(db);
	}
}
