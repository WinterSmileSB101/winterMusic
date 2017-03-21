package util;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.StaticLayout;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.concurrent.Exchanger;

import Music.Mp3Info;
import StaticValue.StaticValue;
import winter.zxb.smilesb101.winterMusic.MainActivity;

/**
 * 文件扫描类
 * Created by SmileSB101 on 2016/9/26.
 */
public class FileScan{

	/**
	 * 更新媒体数据库，以便开机后新增文件没有得到更新
	 */
	public static void UpdateMediaSQL()
	{
		//android4.4之后不能通过广播的方式更新，于是通过下面的方法
		MediaScannerConnection.scanFile(MainActivity.activity,new String[]{Environment.getExternalStorageDirectory().getAbsolutePath()},null,null);
	}

	/**
	 * 获取本地mp3的列表信息
	 * @param context 同步上下文
	 * @return Mp3表
	 */
	public static ArrayList<Mp3Info> getMp3Info(Context context)
	{
		ArrayList<Mp3Info> mp3InfoArrayList = new ArrayList<>();

		Cursor cursor = context.getContentResolver().query(
				MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null, null,
				MediaStore.Audio.Media.DEFAULT_SORT_ORDER);

		//遍历游标找到所有音乐，并且存入列表
		for(int i = 0;i<cursor.getCount();i++)
		{
			cursor.moveToNext();
			Mp3Info mp3Info = new Mp3Info();
			long id = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media._ID));
			String title = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
			String album = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM));
			long albumId = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID));
			String displayName = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME));
			String artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
			long duration = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION));
			long size = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.SIZE));
			String url = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
			int isMusic = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media.IS_MUSIC));
			Bitmap bitmap = ArtworkUtils.getArtwork(context,title,id,albumId,true);//获取专辑图片
			if (isMusic != 0) { // 只把音乐添加到集合当中
				mp3Info.setId(id);
				mp3Info.setTitle(title);
				mp3Info.setAlbum(album);
				mp3Info.setAlbumId(albumId);
				mp3Info.setDisplayName(displayName);
				mp3Info.setArtist(artist);
				mp3Info.setDuration(duration);
				mp3Info.setSize(size);
				mp3Info.setUrl(url);
				mp3Info.setImage(bitmap);
				mp3InfoArrayList.add(mp3Info);
			}
		}

		return mp3InfoArrayList;
	}

	/**
	 * 获取bitmap
	 * @return bitmap
	 */
	public Bitmap getMusicArtistbitmap(Context context,String title,long song_id,long album_id,
									   boolean allowdefault)
	{
		return ArtworkUtils.getArtwork(context,title,song_id,album_id,allowdefault);
	}
	public static ArrayList<Mp3Info> FindMusicFromPath(File file,String...MusicTypes)
	{
		try{
			if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
				ArrayList<Mp3Info> mp3InfoArrayList = new ArrayList<>();
				if(file == null) {
					//给出的遍历路径为空,把根目录赋值给Path
					file = Environment.getExternalStorageDirectory();
				}
				//递归加遍历寻找Mp3文件
				if(file != null && file.exists() && file.isDirectory())
				{
					File[] files = file.listFiles();
					for(int i = 0;i < files.length;i++)
					{
					}
				}
				return mp3InfoArrayList;
			}
			else
			{
				Toast.makeText(StaticValue.MainActivity,"没有检测到SD卡，请检查后重试！",Toast.LENGTH_LONG);
				Log.i("错误","没有SD卡！");
				return null;
			}
		}
		catch(Exception e)
		{
			Log.i("错误",e.getMessage());
			throw e;
		}
	}
}
