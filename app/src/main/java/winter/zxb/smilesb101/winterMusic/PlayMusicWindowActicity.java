package winter.zxb.smilesb101.winterMusic;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewDebug;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.LinearInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.ArrayList;

import Music.Mp3Info;
import StaticValue.StaticValue;
import my_dialog.Play_List_Dialog;
import services.MusicServices;
import util.ArtworkUtils;
import util.ConvertTime;
import util.FastBlur;

import static android.view.animation.Animation.RELATIVE_TO_SELF;
import static util.Help.TransParentSystembar;
import static util.Help.initSystemBar;
import static util.Help.isBackGround;
import static winter.zxb.smilesb101.winterMusic.localMusicActivity.localMusicListFragment.CancelListItemClick;
import static winter.zxb.smilesb101.winterMusic.localMusicActivity.localMusicListFragment.ListItemClick;

/**
 * 唱片机播放界面活动类
 * Created by SmileSB101 on 2016/10/26 0026.
 */

public class PlayMusicWindowActicity extends AppCompatActivity implements View.OnClickListener{
	private LinearLayout mView;
	/**
	 * 专辑的容器
	 */
	private RelativeLayout playmusic_window_MusicImage_zontainer;
	/**
	 * 返回按钮
	 */
	private ImageView playmusic_window_BackBtn;
	private TextView playmusic_windowF_MusicName;
	private TextView playmusic_windowF_ArtistName;
	/**
	 * 分享按钮
	 */
	private ImageView playmusic_windowF_ShareBtn;

	/**
	 * 旋转手臂
	 */
	private ImageView playmusic_window_Needle;
	/**
	 * 音乐专辑图片
	 */
	private ImageView playmusic_window_MusicImage;
	/**
	 * 喜欢按钮
	 */
	private ImageView playmusic_window_LoveBtn;
	/**
	 * 下载按钮
	 */
	private ImageView playmusic_window_dldBtn;
	/**
	 * 更多信息按钮
	 */
	private ImageView playmusic_window_moreBtn;
	/**
	 * 播放进度当前进度时间
	 */
	private TextView playmusic_window_ProgressCurrcent;
	/**
	 * 播放进度进度条控件
	 */
	private SeekBar playmusic_window_SeekBar;
	/**
	 * 播放进度条最大时间
	 */
	private TextView playmusic_window_ProgressMax;
	/**
	 * 播放循环方式按钮
	 */
	private ImageView playmusic_window_LoopWayBtn;
	/**
	 * 上一曲按钮
	 */
	private ImageView playmusic_window_PlayPreMusicBtn;
	/**
	 * 播放或者暂停按钮
	 */
	private ImageView playmusic_window_PlayMusicBtn;
	/**
	 * 下一曲按钮
	 */
	private ImageView playmusic_window_PlayNextMusicBtn;
	/**
	 * 播放列表按钮
	 */
	private ImageView playmusic_window_PlayMusicListBtn;
	/**
	 * 播放的时候的动画
	 */
	private ObjectAnimator discAnimation,needleAnimation;
	/**
	 * 旋转的中心点
	 */
	private final int RotatePivotX = 45,RotatePivotY = 45;
	/**
	 * 是否在播放音乐
	 */
	private boolean IsPlaying = false,IsPause = false,IsFirst = true;
	/**
	 * 播放列表
	 */
	private ArrayList<Mp3Info> PlayList;
	/**
	 * 音乐绑定对象
	 */
	private static MusicServices.MusicIBind musicIBind;
	/**
	 * 服务连接对象
	 */
	public ServiceConnection mServiceConnection = new ServiceConnection() {

		@Override
		public void onServiceConnected(ComponentName name,IBinder service) {
			//绑定成功后，取得MusicSercice提供的接口
			Log.i("绑定服务成功","啊盛大盛大");
			musicIBind = (MusicServices.MusicIBind) service;
			PlayMusicActivityHandler.sendEmptyMessage(IsBinder);//发送绑定成功消息
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
		}
	};
	/**
	 * 绑定了
	 */
	private final static int IsBinder = 0;
	public final Handler PlayMusicActivityHandler = new Handler()
	{
		@Override
		public void handleMessage(Message msg){
			super.handleMessage(msg);
			switch(msg.what)
			{
				case IsBinder:
					setPlayWindowValue();
					break;
			}
		}
	};
	/**
	 * 本地广播管理器
	 */
	private LocalBroadcastManager localBroadcastManager;
	/**
	 * 广播接收器
	 */
	private UI_Receiver ui_receiver;
	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState){
		Intent MusicServiceIntent = new Intent(this,MusicServices.class);
		//startService(MusicServiceIntent);
		bindService(MusicServiceIntent,mServiceConnection,BIND_AUTO_CREATE);
		IntentFilter intentF = new IntentFilter(MusicServices.MusicServiceReceiverAction);
		ui_receiver = new UI_Receiver();
		localBroadcastManager = LocalBroadcastManager.getInstance(this);
		localBroadcastManager.registerReceiver(ui_receiver,intentF);
		super.onCreate(savedInstanceState);
		initValue();
	}
	/**
	 * 数据更新
	 */
	private void initValue()
	{
		//initSystemBar(this,R.color.colorAccent);//同步状态栏颜色
		TransParentSystembar(this);//透明状态栏
		setContentView(R.layout.playmusic_window_layout);
		mView = (LinearLayout)findViewById(R.id.playmusic_window_Content);
		playmusic_window_MusicImage_zontainer = (RelativeLayout)findViewById(R.id.playmusic_window_MusicImage_zontainer);
		playmusic_window_Needle = (ImageView)findViewById(R.id.playmusic_window_Needle);
		playmusic_window_MusicImage = (ImageView)findViewById(R.id.playmusic_window_MusicImage);
		playmusic_window_LoveBtn = (ImageView)findViewById(R.id.playmusic_window_LoveBtn);
		playmusic_window_dldBtn = (ImageView)findViewById(R.id.playmusic_window_dldBtn);
		playmusic_window_moreBtn = (ImageView)findViewById(R.id.playmusic_window_moreBtn);
		playmusic_window_ProgressCurrcent = (TextView)findViewById(R.id.playmusic_window_ProgressCurrcent);
		playmusic_window_SeekBar = (SeekBar)findViewById(R.id.playmusic_window_SeekBar);
		playmusic_window_ProgressMax = (TextView)findViewById(R.id.playmusic_window_ProgressMax);
		playmusic_window_LoopWayBtn = (ImageView)findViewById(R.id.playmusic_window_LoopWayBtn);
		playmusic_window_PlayPreMusicBtn = (ImageView)findViewById(R.id.playmusic_window_PlayPreMusicBtn);
		playmusic_window_PlayMusicBtn = (ImageView)findViewById(R.id.playmusic_window_PlayMusicBtn);
		playmusic_window_PlayNextMusicBtn = (ImageView)findViewById(R.id.playmusic_window_PlayNextMusicBtn);
		playmusic_window_PlayMusicListBtn = (ImageView)findViewById(R.id.playmusic_window_PlayMusicListBtn);
		playmusic_window_BackBtn = (ImageView)findViewById(R.id.playmusic_window_BackBtn);
		playmusic_windowF_MusicName = (TextView)findViewById(R.id.playmusic_windowF_MusicName);
		playmusic_windowF_ArtistName = (TextView)findViewById(R.id.playmusic_windowF_ArtistName);
		playmusic_windowF_ShareBtn = (ImageView)findViewById(R.id.playmusic_windowF_ShareBtn);

		IsPlaying = false;
		IsPause = false;
		IsFirst = true;
		PlayList = new ArrayList<>();
		playmusic_window_PlayMusicBtn.setOnClickListener(this);
		playmusic_window_PlayNextMusicBtn.setOnClickListener(this);
		playmusic_window_PlayPreMusicBtn.setOnClickListener(this);
		playmusic_window_BackBtn.setOnClickListener(this);
		playmusic_windowF_ShareBtn.setOnClickListener(this);
	}
	/**
	 * 按钮事件监听与实现
	 * @param v
	 */
	@Override
	public void onClick(View v){
		AnimatorSet as = new AnimatorSet();
		switch(v.getId())
		{
			case R.id.playmusic_window_PlayPreMusicBtn://上一曲
				as = ChangeDisc("PrevMusic");
				as.start();
				break;
			case R.id.playmusic_window_PlayMusicBtn://播放按钮
				if(!IsPlaying) {
					//播放音乐
					setPlayWindowValue();//设置播放的值
					playmusic_window_PlayMusicBtn.setImageResource(R.drawable.play_btn_pause_xml);
					IsPlaying = true;
					if(IsPause)
					{
						//是从暂停状态进入
						IsPause = false;
						//继续动画播放
						MusicDiscContinuePlay();//设置动画
						discAnimation.start();
						needleAnimation.start();
						musicIBind.continuePlay();//播放音乐
					}
					else
					{
						MusicPlayAni();
						needleAnimation.start();
						discAnimation.start();
						musicIBind.play();//继续播放
					}
				}
				else
				{
					//暂停播放
					playmusic_window_PlayMusicBtn.setImageResource(R.drawable.play_btn_play_xml);
					needleAnimation = MusicStopAni();
					needleAnimation.start();
					IsPlaying = false;
					IsPause = true;
					musicIBind.pause();
				}

				break;
			case R.id.playmusic_window_PlayNextMusicBtn://下一曲按钮
				as = ChangeDisc("NextMusic");
				as.start();
				break;
			case R.id.playmusic_window_PlayMusicListBtn://播放列表按钮
				//弹出播放列表
				final Play_List_Dialog play_list_dialog = new Play_List_Dialog(StaticValue.NowActivity,PlayList);
				//检查是否有音乐在播放，有的话就要更新弹出列表的播放条目
				if(musicIBind.isPlaying())
				{
					play_list_dialog.setCurrentPlayPosition(musicIBind.getCurrentPlayPosition());
				}
				play_list_dialog.showDialog();
				break;
		}
	}
	@Override
	protected void onDestroy(){
		super.onDestroy();
		unbindService(mServiceConnection);
		localBroadcastManager.unregisterReceiver(ui_receiver);
	}

	/**
	 * 换碟的动画
	 */
	private AnimatorSet ChangeDisc(final String WhichBtn)
	{
		ObjectAnimator temp = MusicStopAni();
		//重置旋转角度
		ObjectAnimator rotate = ObjectAnimator.ofFloat(playmusic_window_MusicImage_zontainer,"rotation",playmusic_window_MusicImage_zontainer.getRotation(),0f);
		rotate.setDuration(100);
		//播放换碟动画
		ObjectAnimator scaleX = ObjectAnimator.ofFloat(playmusic_window_MusicImage,"scaleX",1f,0.5f);
		scaleX.setDuration(300);
		ObjectAnimator scaleY = ObjectAnimator.ofFloat(playmusic_window_MusicImage,"scaleY",1f,0.5f);
		scaleY.setDuration(300);
		ObjectAnimator Translation = ObjectAnimator.ofFloat(playmusic_window_MusicImage,"translationY",playmusic_window_MusicImage.getTranslationY(),playmusic_window_MusicImage.getTranslationX()-400);
		Translation.setDuration(300);
		ObjectAnimator Alpha = ObjectAnimator.ofFloat(playmusic_window_MusicImage,"alpha",1f,0f);
		Alpha.setDuration(300);
		final AnimatorSet animatorSet = new AnimatorSet();
		animatorSet.play(rotate).with(temp);
		AnimatorSet animatorSet1 = new AnimatorSet();
		animatorSet1.play(scaleX).with(scaleY);
		AnimatorSet animatorSet2 = new AnimatorSet();
		animatorSet2.play(Translation).with(Alpha);
		AnimatorSet animatorSet3 = new AnimatorSet();
		animatorSet3.play(animatorSet).with(animatorSet1).before(animatorSet2);
		//检测动画播放完毕
		animatorSet3.addListener(new Animator.AnimatorListener(){
			@Override
			public void onAnimationStart(Animator animation){

			}

			@Override
			public void onAnimationEnd(Animator animation){
				//重置位置
				playmusic_window_MusicImage.setAlpha(1f);
				playmusic_window_MusicImage.setTranslationX(0f);
				playmusic_window_MusicImage.setTranslationY(0f);
				playmusic_window_MusicImage.setScaleX(1f);
				playmusic_window_MusicImage.setScaleY(1f);
				IsPause = false;
				IsPlaying = true;
				playmusic_window_PlayMusicBtn.setImageResource(R.drawable.play_btn_pause_xml);
				switch(WhichBtn)
				{
					case "NextMusic":
						musicIBind.playNext();//下一首
						break;
					case "PrevMusic":
						musicIBind.PlayPREV();//上一首
						break;
				}
				setPlayWindowValue();
				MusicPlayAni();
				AnimatorSet animationSet = new AnimatorSet();
				animationSet.play(discAnimation).with(needleAnimation);
				animationSet.start();
			}

			@Override
			public void onAnimationCancel(Animator animation){

			}

			@Override
			public void onAnimationRepeat(Animator animation){

			}
		});
		return animatorSet3;
	}
	/**
	 * 播放音乐时的动画
	 */
	private void MusicPlayAni()
	{
		discAnimation = ObjectAnimator.ofFloat(playmusic_window_MusicImage_zontainer,"rotation",0,360);
		discAnimation.setDuration(20000);
		discAnimation.setInterpolator(new LinearInterpolator());
		discAnimation.setRepeatCount(ValueAnimator.INFINITE);
		//旋转到转盘角度
		needleAnimation =  ObjectAnimator.ofFloat(playmusic_window_Needle,"rotation",-35,0);
		playmusic_window_Needle.setPivotX(RotatePivotX);
		playmusic_window_Needle.setPivotY(RotatePivotY);
		needleAnimation.setDuration(800);
		needleAnimation.setInterpolator(new LinearInterpolator());
	}

	/**
	 * 继续播放音乐
	 */
	public void MusicDiscContinuePlay()
	{
		if(discAnimation != null)
		{
			float startROtation = (float)discAnimation.getAnimatedValue();
			discAnimation.setFloatValues(startROtation,360f+startROtation);
		}
		needleAnimation =  ObjectAnimator.ofFloat(playmusic_window_Needle,"rotation",-35,0);
		playmusic_window_Needle.setPivotX(RotatePivotX);
		playmusic_window_Needle.setPivotY(RotatePivotY);
		needleAnimation.setDuration(800);
		needleAnimation.setInterpolator(new LinearInterpolator());
	}

	/**
	 * 停止播放时的动画
	 * return 动画500毫秒
	 */
	private ObjectAnimator MusicStopAni()
	{
		if(discAnimation != null && discAnimation.isRunning()) {
			discAnimation.cancel();
		}
		if(needleAnimation != null)
		{
			ObjectAnimator ob =  ObjectAnimator.ofFloat(playmusic_window_Needle,"rotation",0,-35);
			playmusic_window_Needle.setPivotX(RotatePivotX);
			playmusic_window_Needle.setPivotY(RotatePivotY);
			ob.setDuration(300);
			ob.setInterpolator(new LinearInterpolator());
			return ob;
		}
		return null;
	}

	/**
	 * 载入的时候抬起手臂的动画
	 */
	private void MusicFirstAni()
	{
		ObjectAnimator animator = ObjectAnimator.ofFloat(playmusic_window_Needle,"rotation",0,-35);
		playmusic_window_Needle.setPivotX(RotatePivotX);
		playmusic_window_Needle.setPivotY(RotatePivotY);
		animator.setDuration(10);
		animator.setInterpolator(new LinearInterpolator());
		animator.start();//开始动画
	}
	private void setPlayWindowValue()
	{
		if(musicIBind != null) {
			Log.i("MusicIBind不为空","initValue: ");
			PlayList = musicIBind.getPlayList();
		}
		Mp3Info mp3Info = PlayList.get(musicIBind.getPlayPosition());//获取当前正在播放音乐
		playmusic_window_MusicImage.setImageResource(R.mipmap.default_play_window_image);//专辑封面，这里是设置默认的
		playmusic_windowF_MusicName.setText(mp3Info.getDisplayName());//设置音乐名称
		playmusic_windowF_ArtistName.setText(mp3Info.getArtist());
		playmusic_window_MusicImage.setImageBitmap(mp3Info.getImage());
		playmusic_window_ProgressMax.setText(ConvertTime.ConvertIntToTime(musicIBind.getMaxProgress(),"mm:ss"));
		//Log.i("最大进度",""+ ConvertTime.ConvertIntToTime(musicIBind.getMaxProgress(),"mm:ss"));
		playmusic_window_SeekBar.setMax(musicIBind.getMaxProgress());
		drawBlurBitmap(mp3Info.getImage(),mView);
		if(musicIBind.isPlaying() && IsFirst)//正在播放音乐而且是第一次进入
		{
			playmusic_window_PlayMusicBtn.setImageResource(R.drawable.play_btn_pause_xml);
			IsPlaying = true;
			IsPause = false;
			IsFirst = false;
			MusicPlayAni();
			needleAnimation.start();
			discAnimation.start();
		}
	}

	/**
	 *获取高斯模糊图片
	 * @param bitmap 需要处理的图片
	 * @param view 视野
	 */
	private void drawBlurBitmap(Bitmap bitmap,View view)
	{

		if(view==null) {
			view = getWindow().getDecorView();
		}
		long startMs = System.currentTimeMillis();
		float scaleFactor = 2;//图片缩放比例；
		float radius = 50;//模糊程度

		Bitmap overlay = Bitmap.createBitmap(
				(int) (view.getMeasuredWidth() / scaleFactor),
				(int) (view.getMeasuredHeight() / scaleFactor),
				Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(overlay);
		canvas.translate(-view.getLeft(), -view.getTop());
		canvas.scale(scaleFactor,scaleFactor);//设置图片为全屏
		Paint paint = new Paint();
		paint.setFlags(Paint.FILTER_BITMAP_FLAG);
		canvas.drawBitmap(bitmap, 0, 0, paint);


		overlay = FastBlur.doBlur(overlay, (int) radius, true);
		overlay = FastBlur.getBlackImage(overlay);
		view.setBackground(new BitmapDrawable(getResources(), overlay));
	}
	/**
	 *  广播接收器
	 * Created by SmileSB101 on 2016/10/19 0019.
	 */
	public class UI_Receiver extends BroadcastReceiver{
		@Override
		public void onReceive(Context context,Intent intent){
			try {
				int type = intent.getIntExtra(MusicServices.BROADCAST.TYPE_TYPENAME,-2);
				Log.i("收到消息",type + "");
				switch(type) {
					case MusicServices.BROADCAST.TYPE_CurrentProgress:
						playmusic_window_SeekBar.setProgress(intent.getIntExtra(MusicServices.BROADCAST.TYPE_VALUENAME,0));//设置进度条
						playmusic_window_ProgressCurrcent.setText(ConvertTime.ConvertIntToTime(intent.getIntExtra(MusicServices.BROADCAST.TYPE_VALUENAME,0),"mm:ss"));
						break;
				}
			}
			catch(Exception e)
			{
				Log.i(this.getClass().getSimpleName()+"错误",""+e.getMessage());
			}
		}
	}
}
