package my_content_provider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import StaticValue.StaticValue;
import winter.zxb.smilesb101.winterMusic.MainActivity;

/**
 * 自定义的播放列表ContentProvider
 */
	public class PlayListContentProvider extends ContentProvider{
	public PlayListContentProvider()
	{

	}
    /**
      * 内容提供器的路径名，可以是一个文件的，一可以是文件夹的
      */
	private static final String PATH = "songs";
	private DBHelper mDbHelper;//数据库建立帮助器
	private Context context;
	public static final Uri CONTENT_SONGS_URI = Uri.parse(StaticValue.SCHEME + StaticValue.AUTHORITY +PATH);
	public static final String ID = "music_id";
	public static final String TITLE = "music_title";
	public static final String ALBUM = "music_album";
	public static final String ALBUMID = "music_albumId";
	public static final String DISPLAYNAME = "music_displayName";
	public static final String ARTIST = "music_artist";
	public static final String DURATION = "music_duration";
	public static final String SIZE = "music_size";
	public static final String URL = "music_url";
	public PlayListContentProvider(Context context){
		this.context = context;
		mDbHelper = new DBHelper(context,StaticValue.DB_LIST_CODE.PLAY_LIST);
	}

	/**
	 * 删除条目
	 * @param uri
	 * @param selection
	 * @param selectionArgs
	 * @return
	 */
	@Override
	public int delete(Uri uri,String selection,String[] selectionArgs){
		// Implement this to handle requests to delete one or more rows.
		SQLiteDatabase db = mDbHelper.getWritableDatabase();//获取数据库
		//清空playlist_table表，并将删除的数据条数返回
		int count =  db.delete(mDbHelper.TABLE_NAME[0],selection,selectionArgs);
		return count;
	}

	/**
	 * 获取类型
	 * @param uri
	 * @return
	 */
	@Override
	public String getType(Uri uri){
		// TODO: Implement this to handle requests for the MIME type of the data
		// at the given URI.
		throw new UnsupportedOperationException("Not yet implemented");
	}

	/**
	 * 插入内容,添加播放列表的操作，每次向数据库插入一条信息，
	 * @param uri
	 * @param values
	 * @return
	 */
	@Override
	public Uri insert(Uri uri,ContentValues values){
		// TODO: Implement this to handle requests to insert a new row.
		Uri result = null;
		mDbHelper = new DBHelper(MainActivity.activity,StaticValue.DB_LIST_CODE.PLAY_LIST);
		SQLiteDatabase db = mDbHelper.getWritableDatabase();//通过DBHelper获取写数据库的方法
		long id = db.insert(mDbHelper.TABLE_NAME[0], null, values);
		if(id > 0)
		{
			//根据返回到id值组合成该数据项对应的Uri地址,
			//假设id为8，那么这个Uri地址类似于content://com.anddle.PlayListContentProvider/songs/8
			result = ContentUris.withAppendedId(CONTENT_SONGS_URI,id);
		}
		return result;
	}

	/**
	 * 创建的时候调用
	 * @return
	 */
	@Override
	public boolean onCreate(){
		// TODO: Implement this to initialize your content provider on startup.
		mDbHelper = new DBHelper(context,StaticValue.DB_LIST_CODE.PLAY_LIST);//建立数据库帮助实例
		return true;
	}

	/**
	 * 查询方法
	 * @param uri
	 * @param projection
	 * @param selection
	 * @param selectionArgs
	 * @param sortOrder
	 * @return
	 */
	@Override
	public Cursor query(Uri uri,String[] projection,String selection,
						String[] selectionArgs,String sortOrder){
		// TODO: Implement this to handle query requests from clients.
		SQLiteDatabase db = mDbHelper.getWritableDatabase();//获取数据库
		Cursor cursor = db.query(mDbHelper.TABLE_NAME[0],projection,selection,selectionArgs,sortOrder,null,null);
		return cursor;//返回所查询到的游标
	}

	/**
	 * 更新内容提供器
	 * @param uri
	 * @param values
	 * @param selection
	 * @param selectionArgs
	 * @return
	 */
	@Override
	public int update(Uri uri,ContentValues values,String selection,
					  String[] selectionArgs){
		// TODO: Implement this to handle requests to update one or more rows.
		SQLiteDatabase db = mDbHelper.getWritableDatabase();//获取数据库
		int count = db.update(mDbHelper.TABLE_NAME[0],values,selection,selectionArgs);
		return count;//返回更新数量
	}
}
