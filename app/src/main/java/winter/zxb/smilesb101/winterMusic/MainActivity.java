package winter.zxb.smilesb101.winterMusic;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.IBinder;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.view.MenuItem;
import java.util.ArrayList;
import StaticValue.StaticValue;
import services.MusicServices;
import static util.Help.getScreenWindow;
import static util.Help.initSystemBar;

public class MainActivity extends BaseActity
		implements NavigationView.OnNavigationItemSelectedListener{

	/**
	 *
	 */
	private ViewPager viewPager;
	private int viewPagerNum;
	private TabLayout tb_layout;
	/**
	 * 分栏的tab引用
	 */
	private ArrayList<TabLayout.Tab> tbInstances = new ArrayList<TabLayout.Tab>();
	private SimplePageAdapter simplePageAdapter;

	/**
	 * 初始化变量常量
	 */
	private void initValue()
	{
		initSystemBar(this,R.color.colorDarkRed);//同步状态栏颜色
		getScreenWindow();
		simplePageAdapter = new SimplePageAdapter(getSupportFragmentManager());
		viewPager = (ViewPager)findViewById(R.id.viewpager);
		tb_layout = (TabLayout)findViewById(R.id.tabbar);
		viewPager.setAdapter(simplePageAdapter);
		viewPagerNum = simplePageAdapter.getCount();
		tb_layout.setupWithViewPager(viewPager);
		//添加tab
		for(int i = 0;i<viewPagerNum;i++)
		{
			tbInstances.add(tb_layout.getTabAt(i));
			Drawable drawable = null;
			TabLayout.Tab tab = tb_layout.getTabAt(i);
			switch(tab.getPosition())
			{
				case 0:
					drawable = ContextCompat.getDrawable(MainActivity.activity,R.mipmap.actionbar_discover_selected);
					break;
				case 1:
					drawable = ContextCompat.getDrawable(MainActivity.activity,R.mipmap.actionbar_music_normal);
					break;
				case 2:
					drawable = ContextCompat.getDrawable(MainActivity.activity,R.mipmap.actionbar_friends_normal);
					break;
				default:
					tab.setText("敬请期待");
					break;
			}
			drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
			ImageSpan imageSpan = new ImageSpan(drawable, ImageSpan.ALIGN_BOTTOM);//设置图片
			SpannableString spannableString = new SpannableString(" ");//设置文字

			spannableString.setSpan(imageSpan, 0,1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			tab.setText(spannableString);
		}
		/**
		 * 设置监听器,在这里可以设置图片
		 */
		tb_layout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener(){
			//被选中
			@Override
			public void onTabSelected(TabLayout.Tab tab){
				Drawable drawable = null;
				switch(tab.getPosition())
				{
					case 0:
						drawable = ContextCompat.getDrawable(MainActivity.activity,R.mipmap.actionbar_discover_selected);
						break;
					case 1:
						drawable = ContextCompat.getDrawable(MainActivity.activity,R.mipmap.actionbar_music_selected);
						break;
					case 2:
						drawable = ContextCompat.getDrawable(MainActivity.activity,R.mipmap.actionbar_friends_selected);
						break;
					default:
						tab.setText("敬请期待");
						break;
				}
				drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
				ImageSpan imageSpan = new ImageSpan(drawable, ImageSpan.ALIGN_BOTTOM);//设置图片
				SpannableString spannableString = new SpannableString(" ");//设置文字

				spannableString.setSpan(imageSpan, 0,1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
				tab.setText(spannableString);
			}
			//没有被选中
			@Override
			public void onTabUnselected(TabLayout.Tab tab){
				Drawable drawable = null;
				switch(tab.getPosition())
				{
					case 0:
						drawable = ContextCompat.getDrawable(MainActivity.activity,R.mipmap.actionbar_discover_normal);
						break;
					case 1:
						drawable = ContextCompat.getDrawable(MainActivity.activity,R.mipmap.actionbar_music_normal);
						break;
					case 2:
						drawable = ContextCompat.getDrawable(MainActivity.activity,R.mipmap.actionbar_friends_normal);
						break;
					default:
						tab.setText("敬请期待");
						break;
				}
				drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
				ImageSpan imageSpan = new ImageSpan(drawable, ImageSpan.ALIGN_BOTTOM);//设置图片
				SpannableString spannableString = new SpannableString(" ");//设置文字

				spannableString.setSpan(imageSpan, 0,1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
				tab.setText(spannableString);
			}
			//重新被选中
			@Override
			public void onTabReselected(TabLayout.Tab tab){
			}
		});
		tb_layout.setTabMode(TabLayout.MODE_SCROLLABLE);

	}

	@Override
	protected void onPause(){
		super.onPause();
	}

	@Override
	protected void onStop(){
		super.onStop();
		Log.i("程序后台调用","");
		//Help.isBackGround();
	}

	/**
	 * 销毁时调用
	 */
	@Override
	protected void onDestroy(){
		super.onDestroy();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		ActivityName = StaticValue.ActivityName.MainActivityName;
		setContentView(R.layout.activity_main);
		initValue();
		StaticValue.MainActivity = this;
		StaticValue.MainView = findViewById(R.id.container);
		FloatingActionButton fab = (FloatingActionButton)findViewById(R.id.fab);
		fab.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View view){
				Snackbar.make(view,"Replace with your own action",Snackbar.LENGTH_LONG)
						.setAction("Action",null).show();
			}
		});

		DrawerLayout drawer = (DrawerLayout)findViewById(R.id.drawer_layout);

		NavigationView navigationView = (NavigationView)findViewById(R.id.nav_view);
		navigationView.setNavigationItemSelectedListener(this);
	}

	@Override
	protected void onActivityResult(int requestCode,int resultCode,Intent data){
		super.onActivityResult(requestCode,resultCode,data);
		StaticValue.NowActivity = MainActivity.activity;
		Log.i("主活动中结果",""+StaticValue.NowActivity.ActivityName);
		switch(requestCode)
		{
			case 1:
				break;
		}
	}

	@Override
	public void onBackPressed(){
		DrawerLayout drawer = (DrawerLayout)findViewById(R.id.drawer_layout);
		if(drawer.isDrawerOpen(GravityCompat.START)) {
			drawer.closeDrawer(GravityCompat.START);
		} else {
			super.onBackPressed();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu){
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main,menu);
		return true;
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item){
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		//noinspection SimplifiableIfStatement
		if(id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@SuppressWarnings("StatementWithEmptyBody")
	@Override
	public boolean onNavigationItemSelected(MenuItem item){
		// Handle navigation view item clicks here.
		int id = item.getItemId();

		if(id == R.id.nav_camera) {
			// Handle the camera action
		} else if(id == R.id.nav_gallery) {

		} else if(id == R.id.nav_slideshow) {

		} else if(id == R.id.nav_manage) {

		} else if(id == R.id.nav_share) {

		} else if(id == R.id.nav_send) {

		}

		DrawerLayout drawer = (DrawerLayout)findViewById(R.id.drawer_layout);
		drawer.closeDrawer(GravityCompat.START);
		return true;
	}
}
