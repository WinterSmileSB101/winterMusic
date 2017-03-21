package winter.zxb.smilesb101.winterMusic;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.icu.text.LocaleDisplayNames;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import StaticValue.StaticValue;
import my_popwindow.PlayListPopWindow;
import services.MusicServices;

/**
 * 自定义基类，实现无缝切换底部播放栏
 * Created by SmileSB101 on 2016/10/19 0019.
 */

public class BaseActity extends AppCompatActivity implements View.OnClickListener{
	/**
	 * 活动名称
	 */
	public static String ActivityName;
	/**
	 * 广播接收器对象
	 */
	//public static UI_Receiver ui_receiver;
	/**
	 * 应用内广播接收器
	 */
	public static LocalBroadcastManager localBroadcastManager;
	/**
	 * 活动对象
	 */
	public static BaseActity activity;
	/**
	 * windowManager对象
	 */
	private WindowManager windowManager;
	/**
	 * 根视野
	 */
	public static FrameLayout mContentContainer;
	/**
	 * 浮动视野
	 */
	public static View mFloatView;
	/**
	 * 音乐服务的Intent
	 */
	public static Intent MusicServiceIntent;
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
	 * 音乐服务Bind对象
	 */
	public static MusicServices.MusicIBind musicIBind;
	/**
	 * 服务连接对象
	 */
	public ServiceConnection mServiceConnection = new ServiceConnection() {

		@Override
		public void onServiceConnected(ComponentName name,IBinder service) {
			//绑定成功后，取得MusicSercice提供的接口
			musicIBind = (MusicServices.MusicIBind) service;
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
		}
	};
	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		activity = this;
		//StaticValue.NowActivity = this;
		/*ViewGroup mDecorView = (ViewGroup) getWindow().getDecorView();
		mContentContainer = (FrameLayout) ((ViewGroup) mDecorView.getChildAt(0)).getChildAt(1);
		ui_receiver = new UI_Receiver();
		MusicServiceIntent = new Intent(activity,MusicServices.class);
		if(StaticValue.MusicServiceIntent == null) {
			//MusicServiceIntent = new Intent(MainActivity.activity,MusicServices.class);
			StaticValue.MusicServiceIntent = MusicServiceIntent;
			//启动MusicService
		}
		//实现绑定操作
		bindService(MusicServiceIntent, mServiceConnection, BIND_AUTO_CREATE);*/
	}

	/**
	 * 在这里注册广播
	 */
	@Override
	protected void onResume(){
		Log.i("注册广播接收器","");
		overridePendingTransition(0, 0);//设置返回没有动画
		StaticValue.NowActivity = this;
		MusicServiceIntent = new Intent(this,MusicServices.class);
		startService(MusicServiceIntent);
		bindService(MusicServiceIntent,mServiceConnection,BIND_AUTO_CREATE);
		/*IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(MusicServices.MusicServiceReceiverAction);
		//注册应用内广播接收器
		localBroadcastManager = LocalBroadcastManager.getInstance(this);
		localBroadcastManager.registerReceiver(ui_receiver, intentFilter);*/
		super.onPostResume();
	}

	/**
	 * 在这里取消广播注册
	 */
	@Override
	protected void onDestroy(){
		overridePendingTransition(0, 0);//设置返回没有动画
		//取消服务绑定
		unbindService(mServiceConnection);
		Log.i("取消服务和广播注册","");
		//取消注册应用内广播接收器
		//localBroadcastManager.unregisterReceiver(ui_receiver);
		super.onDestroy();
	}

	@Override
	protected void onPostCreate(@Nullable Bundle savedInstanceState){
		super.onPostCreate(savedInstanceState);
		//CreateFloatView();
	}

	/**
	 * 在这里去掉动画
	 * @param intent
	 * @param requestCode
	 */
	@Override
	public void startActivityForResult(Intent intent,int requestCode){
		intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);//设置前进没有动画
		super.startActivityForResult(intent,requestCode);
	}

	@Override
	protected void onActivityResult(int requestCode,int resultCode,Intent data){
		super.onActivityResult(requestCode,resultCode,data);
	}

	/**
	 * 建立底部浮动栏的方法
	 */
	/*public void CreateFloatView(){
		mFloatView =  LayoutInflater.from(getBaseContext()).inflate(R.layout.float_music_control_layout, null);
		FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
		musicProcessBar = (ProgressBar)mFloatView.findViewById(R.id.float_Music_ProgressBar);
		musicImageView = (ImageView)mFloatView.findViewById(R.id.float_MusicImage);
		musicNameTextView = (TextView)mFloatView.findViewById(R.id.float_Music_Name);
		musicArtistTextView = (TextView)mFloatView.findViewById(R.id.float_Music_Artist);
		musicPlayBtn = (ImageView)mFloatView.findViewById(R.id.float_Play_Btn);
		musicNextBtn = (ImageView)mFloatView.findViewById(R.id.float_Next_Music);
		musicPlayListBtn  = (ImageView)mFloatView.findViewById(R.id.float_Play_List);
		mFloatView.findViewById(R.id.float_Music_Container).setOnClickListener(this);
		musicPlayBtn.setOnClickListener(this);
		musicPlayListBtn.setOnClickListener(this);
		musicNextBtn.setOnClickListener(this);
		//获取当前正在播放的音乐
		layoutParams.gravity = Gravity.BOTTOM;//设置对齐位置
		mContentContainer.addView(mFloatView,layoutParams);
	}*/
	@Override
	protected void onStart(){
		super.onStart();
	}

	/***
	 * 重点，设置这个可以实现前进Activity时候的无动画切换
	 * @param intent
	 */
	@Override
	public void startActivity(Intent intent){
		intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);//设置切换没有动画，用来实现活动之间的无缝切换
		super.startActivity(intent);
	}

	/**
	 *  重点，在这里设置按下返回键，或者返回button的时候无动画
	 */
	@Override
	public void finish(){
		overridePendingTransition(0, 0);//设置返回没有动画
		Intent intent = new Intent();
		if(mFloatView==null)
		{
			intent.putExtra("type",-2);
		}
		else
		{
			intent.putExtra("type",MusicServices.BROADCAST.TYPE_CREATE_MUSIC_VIEW);
		}
		setResult(RESULT_OK,intent);
		super.finish();
	}

	@Override
	public void onBackPressed(){
		overridePendingTransition(0, 0);//设置返回没有动画
		super.onBackPressed();
		Intent intent = new Intent();
		if(mFloatView==null)
		{
			intent.putExtra("type",-2);
		}
		else
		{
			intent.putExtra("type",MusicServices.BROADCAST.TYPE_CREATE_MUSIC_VIEW);
		}
		setResult(RESULT_OK,intent);
	}

	/**
	 * 点击事件
	 * @param v
	 */
	@Override
	public void onClick(View v){
		switch(v.getId())
		{
			case R.id.float_Play_Btn://播放或者暂停按钮
				ImageView imageview = (ImageView)v;
				if(StaticValue.Music_IsPlay) {
					imageview.setImageResource(R.mipmap.playbar_btn_pause);
					Toast.makeText(StaticValue.MainActivity,"暂停",Toast.LENGTH_SHORT);
					StaticValue.Music_IsPlay = false;
				}
				else
				{
					StaticValue.Music_IsPlay = true;
					imageview.setImageResource(R.mipmap.playbar_btn_play);
					Toast.makeText(StaticValue.MainActivity,"播放",Toast.LENGTH_SHORT);
				}
				break;
			case R.id.float_Play_List://播放列表按钮
				Log.i("按下播放列表","啊盛大盛大");
				break;
			case R.id.float_Next_Music://下一曲按钮
				break;
			case R.id.float_Music_Container://播放控制容器
				break;
		}
	}
	/**
	 *  广播接收器
	 * Created by SmileSB101 on 2016/10/19 0019.
	 */
	/*public class UI_Receiver extends BroadcastReceiver{
		@Override
		public void onReceive(Context context,Intent intent){
			int type = intent.getIntExtra(MusicServices.BROADCAST.TYPE_TYPENAME,-2);
			int value = 0;
			//Log.i("收到消息",type+"");
			switch(type)
			{
				case MusicServices.BROADCAST.TYPE_MAXPROGRESS:
					value = intent.getIntExtra(MusicServices.BROADCAST.TYPE_VALUENAME,0);
					//更新进度条
					Log.i("进度条最大值",""+value);
					musicProcessBar.setMax(value);
					break;
				case MusicServices.BROADCAST.TYPE_CurrentProgress:
					value = intent.getIntExtra(MusicServices.BROADCAST.TYPE_VALUENAME,0);
					//更新进度条
					Log.d("更新进度条",""+value+"ActivityName: "+ActivityName);
					musicProcessBar.setProgress(value);
					break;
				case MusicServices.BROADCAST.TYPE_PLAY:
					//设置为暂停的图片
					musicPlayBtn.setImageResource(R.mipmap.playbar_btn_pause);
					break;
				case MusicServices.BROADCAST.TYPE_PAUSE:
					//设置为播放的图片
					musicPlayBtn.setImageResource(R.mipmap.playbar_btn_play);
					break;
				case MusicServices.BROADCAST.TYPE_SETPLAYBARVALUE://设置播放栏的值
					int  maxProgress = intent.getIntExtra(MusicServices.BROADCAST.TYPE_PROGRESSMAX_NAME,0);
					int image = intent.getIntExtra(MusicServices.BROADCAST.TYPE_IMAGENAME,0);
					String name = intent.getStringExtra(MusicServices.BROADCAST.TYPE_MUSICNAME);
					String artist = intent.getStringExtra(MusicServices.BROADCAST.TYPE_ARITSTNAME);
					musicProcessBar.setMax(maxProgress);
					musicNameTextView.setText(name);
					musicImageView.setImageResource(image);
					musicArtistTextView.setText(artist);
					//设置为暂停的图片
					musicPlayBtn.setImageResource(R.mipmap.playbar_btn_pause);
					break;
			}
		}
	}*/
}
