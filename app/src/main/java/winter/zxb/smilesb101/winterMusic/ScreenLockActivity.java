package winter.zxb.smilesb101.winterMusic;

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.sql.Time;
import java.util.Date;
import java.util.Timer;

import Music.Mp3Info;
import StaticValue.StaticValue;
import services.MusicServices;
import util.FastBlur;
import util.Help;
import util.TimeUtil;

/**
 * 锁屏活动类
 * Created by SmileSB101 on 2016/10/31 0031.
 */

public class ScreenLockActivity extends AppCompatActivity{
	private LinearLayout BG;
	private TextView Time;
	private TextView Date;
	private TextView MusicName;
	private TextView MusicArtist;
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
			ScreecLockActivityHandler.sendEmptyMessage(IsBinder);//发送绑定成功消息
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
		}
	};
	/**
	 * 绑定了
	 */
	private final static int IsBinder = 0;
	public final Handler ScreecLockActivityHandler = new Handler()
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

	private void setPlayWindowValue(){
		Time.setText(TimeUtil.getTime(TimeUtil.timeNoss));
		Date.setText(TimeUtil.getTime(TimeUtil.date));
		Mp3Info mp3 = musicIBind.getCurrentMusic();
		Bitmap bit = FastBlur.getBlackImage(mp3.getImage());
		BG.setBackground(new BitmapDrawable(StaticValue.NowActivity.getResources(),bit));
		MusicName.setText(mp3.getDisplayName());
		MusicArtist.setText(mp3.getArtist());
	}

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		Help.TransParentSystembar(this);
		this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
		Intent MusicServiceIntent = new Intent(this,MusicServices.class);
		bindService(MusicServiceIntent,mServiceConnection,BIND_AUTO_CREATE);
		setContentView(R.layout.screen_playwindow_layout);
		BG = (LinearLayout)findViewById(R.id.screen_playWindow_mainContent);
		Time = (TextView)findViewById(R.id.screen_playWindow_Time);
		Date = (TextView)findViewById(R.id.screen_playWindow_Date);
		MusicName = (TextView)findViewById(R.id.screen_playWindow_MusicName);
		MusicArtist = (TextView)findViewById(R.id.screen_playWindow_MusicArtist);
	}

	@Override
	public void onBackPressed(){

	}
}
