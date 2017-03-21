package util;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.provider.MediaStore;
import android.support.v4.widget.SlidingPaneLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.Attributes;

import Music.Mp3Info;
import my_content_provider.PlayListContentProvider;
import OpeanSources.SystemBarTintManager;
import StaticValue.StaticValue;
import my_dialog.Play_List_Dialog;
import services.MusicServices;
import winter.zxb.smilesb101.winterMusic.MainActivity;
import winter.zxb.smilesb101.winterMusic.R;
import winter.zxb.smilesb101.winterMusic.localMusicActivity;

import static android.content.Context.ACTIVITY_SERVICE;
import static android.content.Context.BIND_AUTO_CREATE;
import static util.CheckPremission.context;
import static util.FileScan.UpdateMediaSQL;

/**
 * 帮助类
 * Created by SmileSB101 on 2016/10/12.
 */

public class Help{
	/**
	 * 检查应用程序是否在前台
	 */
	public static boolean isBackGround()
	{
		ActivityManager activity = (ActivityManager)StaticValue.MainActivity.getSystemService(ACTIVITY_SERVICE);
		List<ActivityManager.RunningAppProcessInfo> appProcesses = activity.getRunningAppProcesses();
		for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
			if (appProcess.processName.equals(context.getPackageName())) {
				if (appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_BACKGROUND) {
					Log.i("后台", appProcess.processName);
					return true;
				}else{
					Log.i("前台", appProcess.processName);
					return false;
				}
			}
		}
		return false;
	}
	public static void getScreenWindow()
	{
		WindowManager windowManager = MainActivity.activity.getWindowManager();
		DisplayMetrics metrics = new DisplayMetrics();
		windowManager.getDefaultDisplay().getMetrics(metrics);
		StaticValue.ScreenWidth = metrics.widthPixels;
		StaticValue.ScreenHeigtht = metrics.heightPixels;
	}
	/**
	 *创建播放栏，通过悬浮窗的方式，不过需要解决在桌面任然显示的问题
	 * @param Layout_Id 需要添加的视野的布局
	 */
	public static void ShowPlayBar_FloatWindow(int Layout_Id)
	{
		if(StaticValue.MainActivity != null) {
			View playbarView = LayoutInflater.from(StaticValue.MainActivity).inflate(Layout_Id,null);WindowManager.LayoutParams params = new WindowManager.LayoutParams(WindowManager.LayoutParams.TYPE_TOAST);//TYPE_TOAST 是关键  这样就不需要悬浮窗权限了
			params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;//禁止获得焦点，不然下面的界面无法接收到触摸点击事件  无法接收onBack
			params.width = WindowManager.LayoutParams.MATCH_PARENT;
			params.height = WindowManager.LayoutParams.WRAP_CONTENT;
			params.gravity = Gravity.BOTTOM;//对齐到底部
			WindowManager windowManager = (WindowManager)StaticValue.MainActivity.getApplication().getSystemService(StaticValue.MainActivity.getApplication().WINDOW_SERVICE);
			windowManager.addView(playbarView, params);
		}
		else
		{
			throw new Error("同步上下文没有得到赋值！");
		}
	}
	/**
	 * 更新系统状态栏颜色
	 * @param activity 需要同步颜色的活动
	 * @param status_color_ID 颜色的ID
	 */
	public static void initSystemBar(Activity activity,int status_color_ID) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			setTranslucentStatus(activity, true);
		}
		SystemBarTintManager tintManager = new SystemBarTintManager(activity);
		tintManager.setStatusBarTintEnabled(true);
        // 使用颜色资源
		tintManager.setStatusBarTintResource(status_color_ID);

	}

	/**
	 * 透明化系统状态栏
	 */
	public static void TransParentSystembar(Activity activity)
	{
		activity.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			Window window = activity.getWindow();
			window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
					| WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
			window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
					| View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
					| View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
			window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
			window.setStatusBarColor(Color.TRANSPARENT);
			window.setNavigationBarColor(Color.TRANSPARENT);
		}

	}


	/**
	 * 改变状态栏颜色
	 * @param activity
	 * @param boo
	 */
	@TargetApi(19)
	private static void setTranslucentStatus(Activity activity,boolean boo)
	{
		Window win = activity.getWindow();
		WindowManager.LayoutParams winParams = win.getAttributes();
		final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
		if (boo) {
			winParams.flags |= bits;
		} else {
			winParams.flags &= ~bits;
		}
		win.setAttributes(winParams);
	}

	/**
	 * 扫描音乐静态方法
	 * @return
	 */
	public static int Scan_Music(){
		ArrayList<Mp3Info> mp3InfoArrayList = new ArrayList<>();
		int OldMusicNum = StaticValue.mp3InfoArrayList.size();//申明上次音乐数量
		UpdateMediaSQL();//更新媒体数据库
		Cursor cursor = MainActivity.activity.getContentResolver().query(
				MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,null,null,null,
				MediaStore.Audio.Media.DEFAULT_SORT_ORDER);

		//遍历游标找到所有音乐，并且存入列表
		for(int i = 0; i < cursor.getCount(); i++) {
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

			if(isMusic != 0) { // 只把音乐添加到集合当中
				mp3Info.setId(id);
				mp3Info.setTitle(title);
				mp3Info.setAlbum(album);
				mp3Info.setAlbumId(albumId);
				mp3Info.setDisplayName(displayName);
				mp3Info.setArtist(artist);
				mp3Info.setDuration(duration);
				mp3Info.setSize(size);
				mp3Info.setUrl(url);
				mp3InfoArrayList.add(mp3Info);
			}
		}
		StaticValue.mp3InfoArrayList = mp3InfoArrayList;
		return mp3InfoArrayList.size()-OldMusicNum;
	}

	/**
	 * 插入数据到数据库
	 * @param TableUrl 表地址，
	 * @param mp3Infos 数据,
	 * @return Uri表
	 */
	public static ArrayList<Uri> INSERT_VALUES_TO_SQLITE(Uri TableUrl, Mp3Info...mp3Infos)
	{
        //准备要保存的数据
		ContentValues cv = new ContentValues();
		ArrayList<Uri> uris = new ArrayList<>();
		for(Mp3Info mp3Info:mp3Infos)
		{
			cv.put(PlayListContentProvider.ID,mp3Info.getId());
			cv.put(PlayListContentProvider.TITLE,mp3Info.getTitle());
			cv.put(PlayListContentProvider.ALBUM,mp3Info.getAlbum());
			cv.put(PlayListContentProvider.ALBUMID,mp3Info.getAlbumId());
			cv.put(PlayListContentProvider.DISPLAYNAME,mp3Info.getDisplayName());
			cv.put(PlayListContentProvider.ARTIST,mp3Info.getArtist());
			cv.put(PlayListContentProvider.DURATION,mp3Info.getDuration());
			cv.put(PlayListContentProvider.SIZE,mp3Info.getSize());
			cv.put(PlayListContentProvider.URL,mp3Info.getUrl());
			uris.add(MainActivity.activity.getContentResolver().insert(TableUrl,cv));
			cv = new ContentValues();
		}
		return uris;
	}

	/**
	 * 删除数据库中数据
	 * @param TableUrl 表地址
	 * @param where 什么位置
	 * @param keywords 什么键
	 * @return 删除数量
	 */
	public static int DELETE_SQLITEDATA(Uri TableUrl,String where,String[] keywords)
	{
		ContentResolver cr = MainActivity.activity.getContentResolver();
		return cr.delete(TableUrl,where,keywords);
	}

	/**
	 * 修改数据
	 * @param TableUrl 表路径
	 * @param TABLE_FEATURES_NAME 需要更新的字段名
	 * @param NEW_VALUE 更新的值
	 * @param where 什么语句，必须是一条SQL语句
	 * @param keywords 键
	 * @return
	 */
	public static int ALERT_SQLITEDATA(Uri TableUrl,String TABLE_FEATURES_NAME,String NEW_VALUE,String where,String[] keywords)
	{
		ContentValues cv = new ContentValues();
		ContentResolver cr = MainActivity.activity.getContentResolver();
		cv.put(TABLE_FEATURES_NAME,NEW_VALUE);
		return cr.update(TableUrl,cv,where,keywords);
	}

	/**
	 * 查询数据库数据
	 * @param TableUrl 表路径
	 * @param searchKey
	 * @param where
	 * @param keywords
	 * @param sortOrder
	 * @return 游标
	 */
	public static Cursor QUARY_SQLITEDATA(Uri TableUrl,String[] searchKey,String where,String[] keywords,String sortOrder)
	{
		ContentResolver cr = MainActivity.activity.getContentResolver();
		return cr.query(TableUrl,searchKey,where,keywords,sortOrder);
	}
}
