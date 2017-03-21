package StaticValue;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;

import java.util.ArrayList;

import Music.Mp3Info;
import services.MusicServices;
import winter.zxb.smilesb101.winterMusic.BaseActity;
import winter.zxb.smilesb101.winterMusic.MainActivity;
import winter.zxb.smilesb101.winterMusic.localMusicActivity;

/**
 * 静态数据类，用于保存全局静态变量
 * Created by SmileSB101 on 2016/9/25.
 */
public class StaticValue{
	/**
	 * 主活动
	 */
	public static BaseActity MainActivity;
	/**
	 * 当前活动
	 */
	public static BaseActity NowActivity;
	/**
	 * 主视野
	 */
	public static View MainView;
	/**
	 * 播放列表是否为空
	 */
	public static boolean IsPlayListNull;
	/**
	 * 屏幕宽
	 */
	public static int ScreenWidth;
	/**
	 * 屏幕高
	 */
	public static int ScreenHeigtht;
	/**
	 * 音乐List
	 */
	public static ArrayList<Mp3Info> mp3InfoArrayList;
	/**
	 * 主活动结果码
	 */
	public static final int MainActivityRequestCode = 0;
	/**
	 * 本地音乐列表活动结果码
	 */
	public static final int localMusicListRequestCode = 1;
	/**
	 * 扫描音乐活动结果码
	 */
	public static final int MusicScanActivityRequestCode = 2;
	/**
	 * 是否正在播放音乐
	 */
	public static boolean Music_IsPlay = false;

	/**
	 * 内容提供器模式字段
	 */
	public static final String SCHEME = "content://";
	/**
	 * 内容提供器的包名
	 */
	public static final String AUTHORITY = "winter.zxb.smilesb101.winterMusic//";

	/**
	 * 数据库表名枚举
	 */
	public enum DB_LIST_CODE{
		PLAY_LIST,SINGLE_SONG_LIST,SINGER_LIST,ALBUM_LIST,FOLDER_LIST
	};
	/**
	 * 数据库名称
	 */
	public static final String DB_NAME = "WinterMusic.db";
	public static Intent MusicServiceIntent;

	/**
	 * 活动名称
	 */
	public class ActivityName
	{
		/**
		 * 主活动名称
		 */
		public static final String MainActivityName = "MainActivity";
		/**
		 * 本地音乐活动名称
		 */
		public static final String LocalMusicActivityName = "LocalMusicActivity";
	}
}
