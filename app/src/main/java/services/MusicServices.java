package services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RemoteViews;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import Music.Mp3Info;
import StaticValue.StaticValue;
import my_content_provider.PlayListContentProvider;
import my_dialog.Play_List_Dialog;
import util.ArtworkUtils;
import util.Help;
import util.NetWorkUtil.MusicNetWorkAPI;
import winter.zxb.smilesb101.winterMusic.MainActivity;
import winter.zxb.smilesb101.winterMusic.PlayMusicWindowActicity;
import winter.zxb.smilesb101.winterMusic.R;
import winter.zxb.smilesb101.winterMusic.ScreenLockActivity;

/**
 * 播放音乐服务类,绑定播放栏
 * Created by SmileSB101 on 2016/10/16 0016.
 */

public class MusicServices extends Service implements View.OnClickListener{
	/**
	 * 音乐服务广播动作字符串
	 */
	public final static String MusicServiceReceiverAction = "winter.zxb.smilesb101.winterMusic.service.receiver";//这里的动作可以自定义，只要区别不同的广播以及接收端和发送端相同的Action
	/**
	 * 广播接收器
	 */
	private MusicServicesBroadcastReceiver musicServicesBroadcastReceiver;
	/**
	 * 本地广播接收器
	 */
	private LocalBroadcastManager localBroadcastManager;
	/**
	 * 音乐播放实例
	 */
	private MediaPlayer mediaPlayer;
	/**
	 * 音乐播放进度控件
	 */
	private ProgressBar musicProcessBar;
	/**
	 * 音乐播放音乐专辑封面图片
	 */
	private ImageView musicImageView;
	/**
	 * 音乐播放音乐名称
	 */
	private TextView musicNameTextView;
	/**
	 * 音乐播放歌手名称
	 */
	private TextView musicArtistTextView;
	/**
	 * 音乐播放播放列表按钮
	 */
	private ImageView musicPlayListBtn;
	/**
	 * 音乐播放播放或者暂停按钮
	 */
	private ImageView musicPlayBtn;
	/**
	 * 音乐播放下一曲按钮
	 */
	private ImageView musicNextBtn;
	/**
	 * 浮动视野(音乐控制面板)
	 */
	public static View mFloatView;
	/**
	 * 通知管理者
	 */
	public static NotificationManager notificationManager;
	/**
	 * 根视野
	 */
	public static FrameLayout mContentContainer;
	/**
	 * 音乐是否在播放
	 */
	public static boolean isPlaying;

	/**
	 * 定义广播常量类
	 */
	public class BROADCAST{
		/**
		 * 创建音乐底部浮动栏
		 */
		public final static int TYPE_CREATE_MUSIC_VIEW = - 1;
		/**
		 * 播放
		 */
		public final static int TYPE_PLAY = 0;
		/**
		 * 暂停
		 */
		public final static int TYPE_PAUSE = - 1;
		/**
		 * 下一曲
		 */
		public final static int TYPE_NEXT = 2;
		/**
		 * 播放进度条最大值
		 */
		public final static int TYPE_MAXPROGRESS = 3;
		/**
		 * 播放进度条中的当前进度
		 */
		public final static int TYPE_CurrentProgress = 4;
		/**
		 * 停止播放
		 */
		public final static int TYPE_STOP = 5;
		/**
		 * 增加播放列表
		 */
		public final static int TYPE_ADDPLAYLIST = 6;
		/**
		 * 设置播放栏的值
		 */
		public final static int TYPE_SETPLAYBARVALUE = 7;
		/**
		 * 设置播放音乐列表的ITEM
		 */
		public final static int TYPE_SETLISTITEM = 8;
		/**
		 * 取消播放条目的播放图片
		 */
		public final static int TYPE_CANCELITEMIMAGE = 9;
		/**
		 * 专辑图片
		 */
		public final static String TYPE_IMAGENAME = "music_Image";
		/**
		 * 音乐名称
		 */
		public final static String TYPE_MUSICNAME = "music_Name";
		/**
		 * 专辑图片
		 */
		public final static String TYPE_ARITSTNAME = "music_Artist";
		/**
		 * 音乐进度最大值
		 */
		public final static String TYPE_PROGRESSMAX_NAME = "music_ProgressMax";
		/**
		 * 推送消息的时候键值对中的值名称
		 */
		public final static String TYPE_TYPENAME = "type";
		public final static String TYPE_VALUENAME = "value";
	}

	/**
	 * 循环或者播放方式常量类
	 */
	public class LOOP_WAY{
		/**
		 * 列表循环方式
		 */
		public static final int LOOP_BYLIST = 0;
		/**
		 * 随机播放
		 */
		public static final int LOOP_RANDOM = 1;
		/**
		 * 单曲循环
		 */
		public static final int LOOP_SELF = 2;
	}

	/**
	 * 循环方式变量
	 */
	private static int How_TO_Loop;
	/**
	 * 内容容器
	 */
	private ContentResolver contentResolver;
	/**
	 * Mp3列表
	 */
	private ArrayList<Mp3Info> PlayList;
	/**
	 * 播放列表内容提供者
	 */
	public static PlayListContentProvider playListContentProvider;
	/**
	 * 当前播放位置
	 */
	private int CurrentPlayPosition;
	/**
	 * 上次播放位置
	 */
	private int LastPlayPosition;
	/**
	 * Ibinder实例
	 */
	public final MusicIBind mMusic_IBinder = new MusicIBind();
	public static NotificationBroadcastReceiver mNotificationReceiver;

	/**
	 * 返回Ibinder实例
	 *
	 * @param intent
	 *
	 * @return
	 */
	@Nullable
	@Override
	public IBinder onBind(Intent intent){
		//不需要调用者和Service有功能调用，返回空值
		Log.i("服务","onBind: ");
		return mMusic_IBinder;
	}

	@Override
	public int onStartCommand(Intent intent,int flags,int startId){
		Log.i("服务","onStartCommand: "+isPlaying);
		CreateFloatView();
		return super.onStartCommand(intent,flags,startId);
	}

	/**
	 * 创建的时候调用
	 */
	@Override
	public void onCreate(){
		super.onCreate();
		Log.i("服务","onCreate: ");
		isPlaying = false;
		PlayList = new ArrayList<>();
		How_TO_Loop = LOOP_WAY.LOOP_BYLIST;
		playListContentProvider = new PlayListContentProvider(MainActivity.activity);
		mediaPlayer = new MediaPlayer();
		CurrentPlayPosition = -1;
		CreateFloatView();
		if(MainActivity.activity != null) {
			//初始化广播
			musicServicesBroadcastReceiver = new MusicServicesBroadcastReceiver();
			IntentFilter intentFilter = new IntentFilter();
			intentFilter.addAction(MusicServiceReceiverAction);
			//本地广播注册
			localBroadcastManager = LocalBroadcastManager.getInstance(this);
			localBroadcastManager.registerReceiver(musicServicesBroadcastReceiver,intentFilter);

			IntentFilter intentFilter1 = new IntentFilter(PLAYNOTIFICATION_ACTION);
			mNotificationReceiver = new NotificationBroadcastReceiver();
			localBroadcastManager.registerReceiver(mNotificationReceiver,intentFilter1);

			//初始化
			PlayList = new ArrayList<>();
			contentResolver = MainActivity.activity.getContentResolver();
		} else {
			Log.i("错误","MainActivity.activity未被赋值！");
		}
	}

	@Override
	public void onDestroy(){
		mediaPlayer.release();//释放文件
		mediaPlayer = null;
		super.onDestroy();
		localBroadcastManager.unregisterReceiver(musicServicesBroadcastReceiver);
		localBroadcastManager.unregisterReceiver(mNotificationReceiver);
	}

	/**
	 * 按钮按下事件监听
	 * @param v
	 */
	@Override
	public void onClick(View v){
		switch(v.getId())
		{
			case R.id.float_Play_Btn://播放或者暂停按钮
				Log.i("播放按钮","按下");
				//检查播放状态来改变图标
				if(isPlaying)
				{
					//设置为暂停图片
					musicPlayBtn.setImageResource(R.drawable.playbar_btn_play);
					pauseInner();//暂停播放
				}
				else
				{
					musicPlayBtn.setImageResource(R.drawable.playbar_btn_pause);
					continuePlayInner();
				}
				break;
			case R.id.float_Music_Container://点击播放控制面板其他地方
				Log.i("播放面板其他对方","按下");
				//弹出播放界面
				//播放动画
				Intent intent = new Intent(StaticValue.MainActivity,PlayMusicWindowActicity.class);
				StaticValue.MainActivity.startActivity(intent);
				break;
			case R.id.float_Play_List://播放列表按钮
				Log.i("播放列表","按下");
				final Play_List_Dialog play_list_dialog = new Play_List_Dialog(StaticValue.NowActivity,PlayList);
				//检查是否有音乐在播放，有的话就要更新弹出列表的播放条目
				if(mediaPlayer.isPlaying())
				{
					play_list_dialog.setCurrentPlayPosition(CurrentPlayPosition);
				}
				play_list_dialog.showDialog();
				break;
			case R.id.float_Next_Music://下一首音乐按钮
				Log.i("播放下一首","按下");
				//播放下一首
				playNextInner();
				break;
		}
	}
	/**
	 * 自定义的Binder类
	 */
	public class MusicIBind extends Binder{

		public MusicIBind()
		{
			//
			// createPlayBarView();
		}
		/**
		 * 获取Service
		 *
		 * @return
		 */
		public MusicServices getService(){
			return MusicServices.this;
		}

		/**
		 * 播放音乐服务
		 *
		 * @return
		 */
		public boolean play(){
			return playInner(CurrentPlayPosition);
		}

		/**
		 * 移出音乐，多首
		 * @param mp3InfoArrayList
		 * @return
		 */
		public boolean removeMusic(ArrayList<Mp3Info> mp3InfoArrayList)
		{
			return removeMusicInner(mp3InfoArrayList);
		}

		/**
		 * 移出音乐，单首
		 * @param mp3Info
		 * @return
		 */
		public boolean removeMusic(Mp3Info mp3Info)
		{
			return removeMusic(mp3Info);
		}
		/**
		 * 获取正在播放的位置
		 * @return
		 */
		public int getPlayPosition()
		{
			return getPlayPositionInner();
		}

		/**
		 * 获取上次播放位置
		 * @return
		 */
		public int getlastPlayPosition()
		{
			return LastPlayPosition;
		}

		/**
		 * 获取最大进度
		 * @return
		 */
		public int getMaxProgress()
		{
			return mediaPlayer.getDuration();
		}
		/**
		 * 添加音乐到播放列表，并且播放所有音乐
		 */
		public void addPlayListAndPlayAll(ArrayList<Mp3Info> mp3s){
			addPlayListAndPlayAllInner(mp3s);
		}
		/**
		 * 添加音乐到播放列表，添加一些歌曲
		 *
		 * @param mp3s
		 */
		public void addPlayList(ArrayList<Mp3Info> mp3s){
			addPlayListInner(mp3s);
		}

		/**
		 * 添加音乐到播放列表，添加一首,并且播放这首歌
		 *
		 * @param mp3s
		 */
		public void addPlayList(Mp3Info mp3s){
			addPlayListInner(mp3s);
		}

		/**
		 * 上一曲
		 */
		public void PlayPREV()
		{
			PlayPREVInner();
		}
		/**
		 * 播放下一首
		 */
		public void playNext(){
			playNextInner();
		}

		/**
		 * 提前播放
		 */
		public void playPre(int position){
			playPreInner(position);
		}

		/**
		 * 暂停播放
		 */
		public void pause(){
			pauseInner();
		}

		/**
		 * 调选进度，设置进度
		 *
		 * @param pos
		 */
		public void seekTo(int pos){
			seekToInner(pos);
		}

		/**
		 * 获取当前音乐
		 *
		 * @return
		 */
		public Mp3Info getCurrentMusic(){
			return getCurrentMusicInner();
		}

		/**
		 * 获取当前播放位置
		 * @return
		 */
		public int getCurrentPlayPosition()
		{
			return CurrentPlayPosition;
		}
		/**
		 * 创建播放栏
		 */
		public void createPlayBarView()
		{
			CreateFloatView();
		}

		/**
		 * 是否在播放
		 *
		 * @return
		 */
		public boolean isPlaying(){
			return isPlayingInner();
		}

		/**
		 * 获取播放列表
		 *
		 * @return
		 */
		public ArrayList<Mp3Info> getPlayList(){
			return getPlayListInner();
		}

		/**
		 * 继续播放
		 */
		public void continuePlay(){
			continuePlayInner();
		}
	}

	/**
	 * 实现方法
	 *
	 * @param position 播放位置
	 *
	 * @return
	 */
	private boolean playInner(int position){
		try {
			if(position >= 0) {
				playPreInner(position);
				mediaPlayer.start();
				CreatNotifition();//显示通知栏
				//发送消息
				Mp3Info m = PlayList.get(CurrentPlayPosition);
				for( int i = 0;i<StaticValue.mp3InfoArrayList.size();i++)
				{
					if(StaticValue.mp3InfoArrayList.get(i)==m)
					{
						Log.i("找到歌曲位置","removeFloatViewInner: "+i);
						//发送广播，通知UI界面改变
						Intent intent = new Intent(MusicServiceReceiverAction);
						intent.putExtra(BROADCAST.TYPE_TYPENAME,BROADCAST.TYPE_SETLISTITEM);
						intent.putExtra(BROADCAST.TYPE_VALUENAME,i);
						localBroadcastManager.sendBroadcast(intent);//发送消息
					}
				}
				isPlaying = true;
				if(mFloatView == null) {
					CreateFloatView();
				}
				/**
				 * 添加播放完成事件监听
				 */
				mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener(){
					@Override
					public void onCompletion(MediaPlayer mp){
						Log.i("播放完成事件","onCompletion: ");
						//playNextInner();
					}
				});
				/**
				 * 错误处理
				 */
				mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener(){
					@Override
					public boolean onError(MediaPlayer mp,int what,int extra){
						return false;
					}
				});
				setPlayBarValue(PlayList.get(CurrentPlayPosition),mediaPlayer);//设置播放栏
				//使用定时器提交播放进度
				Timer timer = new Timer();
				TimerTask task = new TimerTask(){
					@Override
					public void run(){
						musicProcessBar.setProgress(mediaPlayer.getCurrentPosition());
						Intent intent = new Intent(MusicServiceReceiverAction);
						intent.putExtra(BROADCAST.TYPE_TYPENAME,BROADCAST.TYPE_CurrentProgress);
						intent.putExtra(BROADCAST.TYPE_VALUENAME,mediaPlayer.getCurrentPosition());
						localBroadcastManager.sendBroadcast(intent);//发送消息
					}
				};
				timer.schedule(task,0,1000);
			}
			else
			{
				Toast.makeText(StaticValue.NowActivity,"播放位置错误！没有文件！",Toast.LENGTH_SHORT);
			}
		}
		catch(Exception e)
		{
			Log.i("PlayInner错误",e.getMessage());
		}

		return true;
	}

	/**
	 * 上一曲
	 */
	private void PlayPREVInner()
	{
		mediaPlayer.stop();
		int temp = 0;
		temp = LastPlayPosition;
		LastPlayPosition = CurrentPlayPosition;
		CurrentPlayPosition = temp;
		playInner(CurrentPlayPosition);
	}
	/**
	 * 移出音乐方法（一次移出多首）
	 * @param mp3Infos
	 * @return
	 */
	private boolean removeMusicInner(ArrayList<Mp3Info> mp3Infos)
	{
		if(PlayList!=null) {
			removeFloatViewInner();//去掉播放界面
			PlayList.remove(mp3Infos);
			return true;
		}
		return false;
	}

	/**
	 * 移出歌曲，单首
	 * @param mp3Info
	 * @return
	 */
	private boolean removeMusicInner(Mp3Info mp3Info)
	{
		if(PlayList != null)
		{
			removeFloatViewInner();//去掉播放界面
			PlayList.remove(mp3Info);
			return true;
		}
		return false;
	}
	/**
	 * 去掉播放栏的方法
	 */
	private void removeFloatViewInner()
	{
		mediaPlayer.stop();//停止播放
		//Log.i("去掉播放栏","removeFloatViewInner: ");
		Mp3Info m = PlayList.get(CurrentPlayPosition);
		for( int i = 0;i<StaticValue.mp3InfoArrayList.size();i++)
		{
			if(StaticValue.mp3InfoArrayList.get(i)==m)
			{
				//Log.i("找到歌曲位置","removeFloatViewInner: "+i);
				Intent intent = new Intent(MusicServiceReceiverAction);
				intent.putExtra(BROADCAST.TYPE_TYPENAME,BROADCAST.TYPE_CANCELITEMIMAGE);
				intent.putExtra(BROADCAST.TYPE_VALUENAME,i);
				localBroadcastManager.sendBroadcast(intent);//发送消息
			}
		}

		//更新UI，本地音乐的播放图标
		ViewGroup mDecorView = (ViewGroup)StaticValue.NowActivity.getWindow().getDecorView();
		mContentContainer = (FrameLayout)((ViewGroup)mDecorView.getChildAt(0)).getChildAt(1);
		mContentContainer.removeView(mFloatView);//移出播放栏
		mFloatView = null;//清空播放栏
	}
	/**
	 * 设置播放栏的值
	 * @param NowMp3 正在播放的mp3对象
	 * @param mediaPlayer1 当前的mediaPlayer对象
	 */
	private void setPlayBarValue(Mp3Info NowMp3,MediaPlayer mediaPlayer1)
	{
		mFloatView.invalidate();//重绘，解决布局图片变化或者文字变化引起的有上次内容残留的问题
		musicProcessBar.setMax(mediaPlayer1.getDuration());
		musicProcessBar.setProgress(mediaPlayer1.getCurrentPosition());
		musicNameTextView.setText(NowMp3.getDisplayName());
		musicImageView.setImageBitmap(ArtworkUtils.getArtwork(StaticValue.NowActivity,NowMp3.getTitle(),NowMp3.getId(),NowMp3.getAlbumId(),true));//设置专辑图片
		musicArtistTextView.setText(NowMp3.getArtist());
		//设置为暂停的图片
		musicPlayBtn.setImageResource(R.mipmap.playbar_btn_pause);
	}
	/**
	 * 添加音乐到播放列表
	 */
	private void addPlayListInner(ArrayList<Mp3Info> mp3s){
		for(Mp3Info mp3Info:mp3s)
		{
			addPlayListInner(mp3Info);
		}
	}

	/**
	 * 添加音乐到播放列表而且播放所有
	 * @param mp3Infos
	 */
	private void addPlayListAndPlayAllInner(ArrayList<Mp3Info> mp3Infos)
	{
		mediaPlayer.stop();//停止播放
		PlayList.clear();
		//清空数据库
		for(Mp3Info mp3Info:mp3Infos) {
			//插入数据库
			//Help.INSERT_VALUES_TO_SQLITE(PlayListContentProvider.CONTENT_SONGS_URI,mp3Info);
			PlayList.add(mp3Info);
		}
		CurrentPlayPosition = 0;
		Log.i("播放的音乐名称","名称："+PlayList.get(CurrentPlayPosition).getDisplayName());
		//播放音乐
		playInner(CurrentPlayPosition);
	}

	/**
	 * 获取当前播放位置的实现
	 * @return
	 */
	private int getPlayPositionInner()
	{
		return CurrentPlayPosition;
	}
	/**
	 * 添加音乐到播放列表,添加一首
	 */
	private void addPlayListInner(Mp3Info mp3s){
		if(!PlayList.contains(mp3s))
		{
			//不存在这首歌曲，直接存入
			Help.INSERT_VALUES_TO_SQLITE(PlayListContentProvider.CONTENT_SONGS_URI,mp3s);
			PlayList.add(mp3s);
			CurrentPlayPosition = PlayList.size()-1;//赋值当前播放位置为现在的位置
		}
		else
		{
			//存在这首音乐，找到音乐位置，播放
			for(int i = 0;i < PlayList.size();i++)
			{
				if(PlayList.get(i).equals(mp3s))
				{
					CurrentPlayPosition = i;
				}
			}
		}
		//播放当前音乐
		playInner(CurrentPlayPosition);
	}

	/**
	 * 播放下一首
	 * 这里要看调用时的列表循环状态，比如单曲循环还是列表循环或者随机循环。
	 */
	private void playNextInner()
	{
		try {
			LastPlayPosition = CurrentPlayPosition;//赋值上次播放位置
			Log.i("上次位置","playNextInner: "+LastPlayPosition);
			Log.i("下一首实现方法里","HowToLoop: "+How_TO_Loop);
			switch(How_TO_Loop)
			{
				case LOOP_WAY.LOOP_SELF://自身循环
					break;
				case LOOP_WAY.LOOP_BYLIST://列表循环
					Log.i("CurrentPosition",CurrentPlayPosition+"");
					if(CurrentPlayPosition - 1 < 0)//如果下一首小于0，那么说明到达了列表底部，于是回到头部开始播放
					{
						CurrentPlayPosition = PlayList.size() - 1;
					}
					else
					{
						CurrentPlayPosition = CurrentPlayPosition - 1;
					}
					break;
				case LOOP_WAY.LOOP_RANDOM://随机播放
					Random ran = new Random();
					int t = 1;
					while(t == 1) {
						CurrentPlayPosition = ran.nextInt(PlayList.size() - 1);
						if(CurrentPlayPosition!=LastPlayPosition)//不等于这次播放位置
						{
							t=2;//退出循环
						}
					}
					break;
			}
			playInner(CurrentPlayPosition);
		}
		catch(Exception e)
		{
			Log.i("ERROR","playNextInner: "+e.getMessage());
		}

	}
	/**
	 * 准备播放
	 */
	private void playPreInner(int position)
	{
		try {
			if(position != - 1) {
				CurrentPlayPosition = position;
				Log.i("现在位置",""+CurrentPlayPosition);
				Log.i("上次位置",""+LastPlayPosition);
			}
			String path = PlayList.get(CurrentPlayPosition).getUrl();
			File file = new File(path);
			if(file.exists()) {
				mediaPlayer.reset();
				mediaPlayer.setDataSource(path);
				mediaPlayer.prepare();
			}
			else
			{
				Toast.makeText(StaticValue.MainActivity,"啊噢，文件已经被不法分子删除了！",Toast.LENGTH_SHORT);
			}
		}
		catch(IOException e) {
			Log.i("PlayPreInner错误"," "+e.getMessage());
		}
	}
	/**
	 * 暂停播放
	 */
	private void pauseInner() {
		mediaPlayer.pause();//暂停播放
		isPlaying = false;//更改状态
	}

	/**
	 *继续播放，在暂停的条件下
	 */
	private void continuePlayInner()
	{
		mediaPlayer.start();//播放
		isPlaying = true;//更改状态
	}
	/**
	 * 停止播放音乐
	 */
	public void Stop()
	{
		StopInner();
	}
	/**
	 * 调选进度，设置进度
	 * @param pos
	 */
	public void seekToInner(int pos) {
	}
	/**
	 * 获取当前音乐
	 * @return
	 */
	public Mp3Info getCurrentMusicInner() {
		return PlayList.get(CurrentPlayPosition);
	}

	/**
	 * 是否在播放
	 * @return
	 */
	public boolean isPlayingInner() {
		return isPlaying;
	}

	/**
	 * 获取播放列表
	 * @return
	 */
	public ArrayList<Mp3Info> getPlayListInner() {
		return PlayList;
	}
	/**
	 * 停止播放音乐
	 */
	public void StopInner()
	{
		mediaPlayer.stop();//停止播放
		mediaPlayer.reset();//重置状态
	}

	public void GetLrcInner(Mp3Info mp3Info,Context context)
	{
		MusicNetWorkAPI.SearchMusic(context,mp3Info.getDisplayName(),10,1,0);//获取歌曲id，这里有10个
	}

	/**
	 * 建立底部浮动栏的方法
	 */
	public void CreateFloatView(){
		if(PlayList.size() > 0) {
			mFloatView = LayoutInflater.from(getBaseContext()).inflate(R.layout.float_music_control_layout,null);
			ViewGroup mDecorView = (ViewGroup)StaticValue.NowActivity.getWindow().getDecorView();
			mContentContainer = (FrameLayout)((ViewGroup)mDecorView.getChildAt(0)).getChildAt(1);
			FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
			musicProcessBar = (ProgressBar)mFloatView.findViewById(R.id.float_Music_ProgressBar);
			musicImageView = (ImageView)mFloatView.findViewById(R.id.float_MusicImage);
			musicNameTextView = (TextView)mFloatView.findViewById(R.id.float_Music_Name);
			musicArtistTextView = (TextView)mFloatView.findViewById(R.id.float_Music_Artist);
			musicPlayBtn = (ImageView)mFloatView.findViewById(R.id.float_Play_Btn);
			musicNextBtn = (ImageView)mFloatView.findViewById(R.id.float_Next_Music);
			musicPlayListBtn = (ImageView)mFloatView.findViewById(R.id.float_Play_List);
			mFloatView.findViewById(R.id.float_Music_Container).setOnClickListener(this);
			musicPlayBtn.setOnClickListener(this);
			musicPlayListBtn.setOnClickListener(this);
			musicNextBtn.setOnClickListener(this);
			//获取当前正在播放的音乐
			layoutParams.gravity = Gravity.BOTTOM;//设置对齐位置
			mContentContainer.addView(mFloatView,layoutParams);
			if(! mediaPlayer.isPlaying()) {
				//没有正在播放的，准备当前位置的音乐，设置值
				playPreInner(- 1);
			}
			setPlayBarValue(PlayList.get(CurrentPlayPosition),mediaPlayer);
			ShowlockServices();//显示锁屏活动
		}
	}

	private NotificationCompat.Builder builder;
	private RemoteViews remoteViews;
	private final static int NotiFition_TAG = 100;
	/**
	 * 显示通知栏
	 */
	private void CreatNotifition()
	{
		Log.i("MusicService: line928","进入");
		/**
		 * 使用NotificationManager来管理通知
		 */
		if(notificationManager==null||remoteViews==null) {
			notificationManager = (NotificationManager)StaticValue.NowActivity.getSystemService(NOTIFICATION_SERVICE);
			remoteViews = new RemoteViews(StaticValue.NowActivity.getPackageName(),R.layout.muisc_notifition);
		}
		ShowNotification();
		Log.i("出现","ShowNotifition: ");
	}

	/**
	 * 通知栏通知动作字符串
	 */
	public static final String PLAYNOTIFICATION_ACTION = "inter.zxb.smilesb101.winterMusic.PlayNotification";
	private void ShowNotification()
	{
		Mp3Info mp3 = PlayList.get(CurrentPlayPosition);
		remoteViews.setTextViewText(R.id.Notifition_MusicName,mp3.getDisplayName());
		remoteViews.setTextViewText(R.id.Notifition_MusicArtist,mp3.getArtist());
		remoteViews.setImageViewBitmap(R.id.Notifition_MusicImage,mp3.getImage());
		builder = new NotificationCompat.Builder(StaticValue.NowActivity);

		//定义广播
		Intent intent = new Intent(PLAYNOTIFICATION_ACTION);
		//播放按钮
		PendingIntent Intent_Play = PendingIntent.getBroadcast(this,1,intent,PendingIntent.FLAG_UPDATE_CURRENT);
		remoteViews.setOnClickPendingIntent(R.id.Notifition_MusicPlayBtn,Intent_Play);
		//下一曲按钮
		PendingIntent Intent_Next = PendingIntent.getBroadcast(this,2,intent,PendingIntent.FLAG_UPDATE_CURRENT);
		remoteViews.setOnClickPendingIntent(R.id.Notifition_MusicNextBtn,Intent_Next);
		//歌词按钮
		PendingIntent Intent_Lrc = PendingIntent.getBroadcast(this,3,intent,PendingIntent.FLAG_UPDATE_CURRENT);
		remoteViews.setOnClickPendingIntent(R.id.Notifition_MusicLrcBtn,Intent_Lrc);
		//取消通知栏按钮
		PendingIntent Intent_Cancle = PendingIntent.getBroadcast(this,4,intent,PendingIntent.FLAG_UPDATE_CURRENT);
		remoteViews.setOnClickPendingIntent(R.id.Notifition_MusicCancelBtn,Intent_Cancle);
		//取到播放界面
		PendingIntent Intent_GoToPlayWindow = PendingIntent.getBroadcast(this,5,intent,PendingIntent.FLAG_UPDATE_CURRENT);
		remoteViews.setOnClickPendingIntent(R.id.Notifition_Content,Intent_GoToPlayWindow);

		builder.setContent(remoteViews);
		builder.setSmallIcon(R.drawable.actionbar_share);
		Notification notify = builder.build();
		notify.flags = Notification.FLAG_NO_CLEAR ;//始终存在通知栏
		notificationManager.notify(NotiFition_TAG,notify);
	}

	/**
	 * 开启锁屏服务
	 */
	public void ShowlockServices()
	{
		MusicServicesBroadcastReceiver receiver = new MusicServicesBroadcastReceiver();
		registerReceiver(receiver,new IntentFilter(Intent.ACTION_SCREEN_OFF));
	}

	/**
	 *  播放音乐广播接收器
	 * Created by SmileSB101 on 2016/10/19 0019.
	 */
	public class MusicServicesBroadcastReceiver extends BroadcastReceiver{
		@Override
		public void onReceive(Context context,Intent intent){
			String action = intent.getAction();
			switch(action)
			{
				case Intent.ACTION_SCREEN_OFF://监听屏幕关闭
					Intent intent1 = new Intent(StaticValue.NowActivity,ScreenLockActivity.class);
					intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					startActivity(intent1);
					break;
			}
		}
	}

	/**
	 * 通知栏广播接收器
	 */
	public class NotificationBroadcastReceiver extends BroadcastReceiver
	{
		@Override
		public void onReceive(Context context,Intent intent){
			Log.i("通知栏发出广播被收到",""+intent.getAction());
		}
	}

}
