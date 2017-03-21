package winter.zxb.smilesb101.winterMusic;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatCallback;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NavUtils;
import android.support.v4.view.KeyEventCompat;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.VectorEnabledTintResources;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.Menu;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * 自定义的活动基类
 * Created by SmileSB101 on 2016/10/14.
 */

public class MyBaseActivity extends AppCompatActivity implements View.OnClickListener{
	/**
	 * 布局容器
	 */
	private FrameLayout mContentContainer;
	/**
	 * 活动底部播放栏，不需要可以在继承中重写OnPostCreate方法
	 */
	private View mFootView;
	/**
	 * 控制面板的按钮集合，0，播放列表，1，播放或者暂停，2，下一曲
	 */
	private ArrayList<ImageView> playbar_Control;
	private ImageView Music_Image;
	private TextView Music_Name;
	private TextView Music_Album;
	private boolean Music_isPlay;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		ViewGroup mDecorView = (ViewGroup) getWindow().getDecorView();
		mContentContainer = (FrameLayout) ((ViewGroup) mDecorView.getChildAt(0)).getChildAt(1);
		mFootView =  LayoutInflater.from(getBaseContext()).inflate(R.layout.float_music_control_layout, null);
	}

	@Override
	protected void onPostCreate(@Nullable Bundle savedInstanceState){
		super.onPostCreate(savedInstanceState);
		Log.d("实例化底部浮动控件","实例化实例化");
		Music_isPlay = false;//播放状态为没有播放
		FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		layoutParams.gravity = Gravity.BOTTOM;
		mContentContainer.addView(mFootView, layoutParams);
	}

	@Override
	protected void onResume(){
		super.onResume();
		playbar_Control.add((ImageView)mFootView.findViewById(R.id.float_Play_Btn));
		mFootView.findViewById(R.id.float_Play_Btn).setOnClickListener(this);
		playbar_Control.add((ImageView)mFootView.findViewById(R.id.float_Play_List));
		mFootView.findViewById(R.id.float_Play_List).setOnClickListener(this);
		playbar_Control.add((ImageView)mFootView.findViewById(R.id.float_Next_Music));
		mFootView.findViewById(R.id.float_Next_Music).setOnClickListener(this);
		Music_Image = (ImageView)mFootView.findViewById(R.id.float_MusicImage);
		Music_Name = (TextView)mFootView.findViewById(R.id.float_Music_Name);
		Music_Album = (TextView)mFootView.findViewById(R.id.float_Music_Artist);
	}

	@Override
	protected void onPause(){
		super.onPause();
		mFootView.findViewById(R.id.float_Play_Btn).setOnClickListener(null);
		mFootView.findViewById(R.id.float_Play_List).setOnClickListener(null);
		mFootView.findViewById(R.id.float_Next_Music).setOnClickListener(null);
	}

	/**
	 * 监听事件，监听底部浮动栏
	 * @param v
	 */
	@Override
	public void onClick(View v){
		switch(v.getId())
		{
			case R.id.float_Play_Btn://播放或者暂停按钮
				ImageView imageview = (ImageView)v;
				if(Music_isPlay)
				   imageview.setImageResource(R.mipmap.playbar_btn_pause);
				else
				{
					imageview.setImageResource(R.mipmap.playbar_btn_play);
				}
				break;
			case R.id.float_Play_List://播放列表按钮
				break;
			case R.id.float_Next_Music://下一曲按钮
				break;
			case R.id.float_Music_Container://播放控制容器
				break;
		}
	}
}
