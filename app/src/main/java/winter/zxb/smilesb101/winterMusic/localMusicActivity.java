package winter.zxb.smilesb101.winterMusic;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.net.http.SslCertificate;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.PagerAdapter;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.util.MutableFloat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Music.Mp3Info;
import services.MusicServices;
import StaticValue.StaticValue;

import static util.CheckPremission.context;
import static util.Help.Scan_Music;
import static util.Help.initSystemBar;
import static winter.zxb.smilesb101.winterMusic.localMusicActivity.localMusicListFragment.CancelListItemClick;
import static winter.zxb.smilesb101.winterMusic.localMusicActivity.localMusicListFragment.ListItemClick;

/**
 * 本地音乐列表活动
 */
public class localMusicActivity extends BaseActity{

	/**
	 * The {@link android.support.v4.view.PagerAdapter} that will provide
	 * fragments for each of the sections. We use a
	 * {@link FragmentPagerAdapter} derivative, which will keep every
	 * loaded fragment in memory. If this becomes too memory intensive, it
	 * may be best to switch to a
	 * {@link android.support.v4.app.FragmentStatePagerAdapter}.
	 */
	private MySectionsPagerAdapter mSectionsPagerAdapter;

	/**
	 * The {@link ViewPager} that will host the section contents.
	 */
	private ViewPager mViewPager;
	private static BaseActity localMusicActivity;
	/**
	 * 本地音乐List
	 */
	private static ArrayList<HashMap<String,String>> LocalMusicList;
	/**
	 * 本地音乐ListView
	 */
	private static RecyclerView localMusicListViews;
	/**
	 * 本地音乐广播管理器
	 */
	private static LocalBroadcastManager localBroadcastManager;
	/**
	 * 广播接收器对象
	 */
	private static UI_Receiver ui_receiver;
	/**
	 * 下拉刷新控件
	 */
	private static SwipeRefreshLayout DropDownlayout;
	/**
	 * 刷新完成
	 */
	public static final int REQUEST_REFESH_OK = 1;
	/**
	 * Handler
	 */
	public static Handler handler = new Handler(){
		@Override
		public void handleMessage(Message msg){
			super.handleMessage(msg);
			switch(msg.what)
			{
				case REQUEST_REFESH_OK:
					DropDownlayout.setRefreshing(false);
					break;
			}
		}
	};
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		ActivityName = StaticValue.ActivityName.LocalMusicActivityName;
		initSystemBar(this,R.color.colorDarkRed);//同步状态栏颜色
		LocalMusicList = new ArrayList<>();
		setContentView(R.layout.activity_local_music_list);
		Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		toolbar.setNavigationIcon(R.mipmap.abc_ic_ab_back_mtrl_am_alpha);//必须放在setSupportActionBar后才有用，否则没有，设置返回图标
		toolbar.setNavigationOnClickListener(back_btn);//添加按键监听
		// Create the adapter that will return a fragment for each of the three
		// primary sections of the activity.
		mSectionsPagerAdapter = new MySectionsPagerAdapter(getSupportFragmentManager());

		// Set up the ViewPager with the sections adapter.
		mViewPager = (ViewPager)findViewById(R.id.container);
		mViewPager.setAdapter(mSectionsPagerAdapter);

		TabLayout tabLayout = (TabLayout)findViewById(R.id.tabs);
		tabLayout.setupWithViewPager(mViewPager);

		FloatingActionButton fab = (FloatingActionButton)findViewById(R.id.fab);
		fab.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View view){
				Snackbar.make(view,"敬请期待",Snackbar.LENGTH_LONG)
						.setAction("Action",null).show();
			}
		});

		ui_receiver = new UI_Receiver();
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(MusicServices.MusicServiceReceiverAction);
		//注册应用内广播接收器
		localBroadcastManager = LocalBroadcastManager.getInstance(this);
		localBroadcastManager.registerReceiver(ui_receiver, intentFilter);

	}


	/**
	 * 返回按钮的监听事件
	 */
	View.OnClickListener back_btn = new View.OnClickListener(){
	@Override
	public void onClick(View v){
		//localBroadcastManager.unregisterReceiver(ui_receiver);
		finish();//结束这个活动
	}
};
	/**
	 * 这里是toolbar的按钮选项菜单
	 * @param menu
	 * @return
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu){
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_local_music_list,menu);//实例化菜单选项
		return true;
	}

	/**
	 * 菜单项被选中时调用
	 * @param item
	 * @return
	 */
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
		else
		{
			switch(id)
			{
				case R.id.menu_search_btn://搜索按钮
					break;
				case R.id.menu_more_scanMusic://扫描音乐按钮
					break;
				case R.id.menu_more_orderStyle://选择排序方式按钮
					break;
				case R.id.menu_more_getLrc://一键获取封面歌词按钮
					break;
				case R.id.menu_more_updateMusic://升级音质按钮
					break;
			}
		}

		return super.onOptionsItemSelected(item);
	}
		/**
		 * 这个方法用来解决超出的menuItem不显示图标的问题，这个方法在AppCompatActivity中不被调用
		 * @param featureId
		 * @param menu
		 * @return
		 */
	@Override
	public boolean onMenuOpened(int featureId, Menu menu) {
		if (featureId == Window.FEATURE_ACTION_BAR && menu != null) {
			if (menu.getClass().getSimpleName().equals("MenuBuilder")) {
				try {
					Method m = menu.getClass().getDeclaredMethod("setOptionalIconsVisible", Boolean.TYPE);
					m.setAccessible(true);
					m.invoke(menu, true);
				} catch (Exception e) {
				}
			}
		}
		return super.onMenuOpened(featureId, menu);
	}

	@Override
	public void onBackPressed(){
		//localBroadcastManager.unregisterReceiver(ui_receiver);
		super.onBackPressed();
	}

	/**
	 * 这个方法用来解决超出的menuItem不显示图标的问题,这个方法在AppCompatActivity中被调用
	 * @param view
	 * @param menu
	 * @return
	 */
	@Override
	protected boolean onPrepareOptionsPanel(View view, Menu menu) {
		if (menu != null) {
			if (menu.getClass().getSimpleName().equals("MenuBuilder")) {
				try{
					Method m = menu.getClass().getDeclaredMethod("setOptionalIconsVisible", Boolean.TYPE);
					m.setAccessible(true);
					m.invoke(menu, true);
				} catch (Exception e) {
					Log.e(getClass().getSimpleName(), "onMenuOpened...unable to set icons for overflow menu", e);
				}
			}
		}
		return super.onPrepareOptionsPanel(view, menu);
	}

	/**
	 * ViewPager中的Fragment
	 */
	public static class localMusicListFragment extends Fragment{
		/**
		 * The fragment argument representing the section number for this
		 * fragment.
		 */
		private static final String ARG_SECTION_NUMBER = "section_number";
		private int sectionNumber;
		/**
		 * 下拉刷新控件
		 */
		public static SwipeRefreshLayout DropDownRefesh;
		/**
		 * Item上次按下位置
		 */
		private static int TouchPositionlast = -1;
		/**
		 *本地音乐列表的单曲表的adapter
		 */
		public static localMusicActivity.localMusicListAdapter_RecycleView recycleView_Adapter;

		public localMusicListFragment(){
			sectionNumber = 0;
		}

		public localMusicListFragment(int sectionNumber)
		{
			this.sectionNumber = sectionNumber;
		}

		/**
		 * Returns a new instance of this fragment for the given section
		 * number.
		 */
		public static localMusicListFragment newInstance(int sectionNumber){
			localMusicListFragment fragment = new localMusicListFragment(sectionNumber);
			Bundle args = new Bundle();
			args.putInt(ARG_SECTION_NUMBER,sectionNumber);
			fragment.setArguments(args);
			return fragment;
		}

		@Override
		public View onCreateView(final LayoutInflater inflater,ViewGroup container,
								 Bundle savedInstanceState){
			//在这里需要判断本地的歌曲列表是否有被获取过，没有则提示空R.layout.local_music_empty
			//有的话就实例化不是空的R.layout.fragment_local_music_list
			View rootView = null;
			if(StaticValue.mp3InfoArrayList != null) {
				HashMap map = new HashMap();
				localMusicListAdapter_RecycleView recycleViewAdapter = null;
				rootView = inflater.inflate(R.layout.fragment_local_music_list,container,false);
				localMusicListViews = (RecyclerView)rootView.findViewById(R.id.local_music_list);
				localMusicListViews.setHasFixedSize(true);
				RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context);
				localMusicListViews.setLayoutManager(layoutManager);
				localMusicListViews.setOnScrollChangeListener(new RecyclerView.OnScrollChangeListener(){
					@Override
					public void onScrollChange(View v,int scrollX,int scrollY,int oldScrollX,int oldScrollY){

					}
				});
				Log.i("更新音乐表","更新更新"+sectionNumber);
				//获取下拉刷新控件
				DropDownlayout = (SwipeRefreshLayout)rootView.findViewById(R.id.mSwipeRefreshLayout);
				DropDownRefesh = DropDownlayout;
				DropDownlayout.setColorSchemeResources(android.R.color.holo_blue_light, android.R.color.holo_red_light, android.R.color.holo_orange_light, android.R.color.holo_green_light);
				/**
				 * 添加刷新监听事件
				 */
				DropDownlayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener(){
					@Override
					public void onRefresh(){
						new AsyncTask<SwipeRefreshLayout,Void,Integer>(){
							@Override
							protected void onPreExecute(){
								//开启后台线程刷新本地列表
								Toast.makeText(StaticValue.MainActivity,"刷新中..请稍候",Toast.LENGTH_SHORT).show();
							}

							@Override
							protected void onPostExecute(Integer result){
								recycleView_Adapter.notifyDataSetChanged();//通知列表Adapter有改变发生
								DropDownRefesh.setRefreshing(false);
								//handler.sendEmptyMessage(REQUEST_REFESH_OK);
								if(result >= 0)
								{
									Toast.makeText(StaticValue.MainActivity,"刷新成功！新增歌曲"+result+"首。",Toast.LENGTH_SHORT).show();
								}
								else
								{
									Toast.makeText(StaticValue.MainActivity,"刷新成功！删除歌曲"+result+"首。",Toast.LENGTH_SHORT).show();
								}
							}

							@Override
							protected Integer doInBackground(SwipeRefreshLayout... params){
								return Scan_Music();
							}
						}.execute(DropDownRefesh);
					}
				});
				switch(sectionNumber)
				{
					case 0://"单曲",排序方式按照单曲，内容也是单曲，后面同理
						//遍历增加mp3信息，可以考虑异步任务
						for(Mp3Info mp3:StaticValue.mp3InfoArrayList) {
							map.put("MusicName",mp3.getDisplayName());
							map.put("MusicAlbum",mp3.getAlbum());
							map.put("MusicQuality","HighQuality");
							map.put("IsDownLoad","true");
							map.put("More","More");
							LocalMusicList.add(map);
							map = new HashMap();
						}
						recycleViewAdapter = new localMusicListAdapter_RecycleView(StaticValue.MainActivity,LocalMusicList,sectionNumber);
						recycleView_Adapter = recycleViewAdapter;
						localMusicListViews.setAdapter(recycleViewAdapter);
						break;
					case 1://"歌手";
						for(Mp3Info mp3:StaticValue.mp3InfoArrayList) {
							map.put("MusicName",mp3.getDisplayName());
							map.put("MusicAlbum",mp3.getAlbum());
							map.put("MusicQuality","HighQuality");
							map.put("IsDownLoad","true");
							map.put("More","More");
							LocalMusicList.add(map);
							map = new HashMap();
						}
						recycleViewAdapter = new localMusicListAdapter_RecycleView(MainActivity.activity,LocalMusicList,sectionNumber);
						recycleView_Adapter = recycleViewAdapter;
						localMusicListViews.setAdapter(recycleViewAdapter);
						break;
					case 2://"专辑";
						break;
					case 3://"文件夹";
						break;
				}
			}
			else {
				rootView = inflater.inflate(R.layout.local_music_empty,container,false);
				rootView.findViewById(R.id.MusicFileScan_Btn).setOnClickListener(MusicScan);//添加事件监听
			}
			if(recycleView_Adapter!=null)
			{
				recycleView_Adapter.setOnRecyclerViewListener(new localMusicListAdapter_RecycleView.OnRecyclerViewListener(){
					@Override
					public void onItemClick(int position,View v){
						switch(v.getId())
						{
							case R.id._include_goToChose://多选按钮按下
								break;
							case R.id._include_content://顶端布局其他位置按下
								Log.i("播放所有音乐按下","onItemClick: ");
								//播放列表所有音乐
								musicIBind.addPlayListAndPlayAll(StaticValue.mp3InfoArrayList);
								break;
							case R.id.local_music_list_item_More://音乐项目更多按钮按下
								Log.i("更多按钮按下","onItemClick: ");
								break;
							case R.id.local_music_list_layout://音乐项其他位置按下,-1是为了除去顶部布局的影响
								musicIBind.addPlayList(StaticValue.mp3InfoArrayList.get(position-1));
								break;
						}
					}

					@Override
					public boolean onItemLongClick(int position,View v){
						return false;
					}
				});
			}
			return rootView;
		}

		/**
		 * 列表中项目按下的处理方法
		 * @param position
		 */
		public static void ListItemClick(int position)
		{
			Log.i("设置ListItem图标","位置："+position);
			recycleView_Adapter.setCurrcentPlayPosition(position);
		}
		/**
		 * 去掉播放图标
		 * @param position
		 */
		public static void CancelListItemClick(int position)
		{
			//播放音乐，添加音乐到播放列表
			Log.i("取消播放图标","位置："+position);
			View view = recycleView_Adapter.views.get(position);//获得当前按下的条目
			ImageView IsPlay = (ImageView)view.findViewById(R.id.local_music_list_item_IsPaly);
			IsPlay.setVisibility(View.GONE);
		}
		/**
		 * 立即扫描音乐的事件监听
		 */
		public View.OnClickListener MusicScan = new View.OnClickListener(){
			@Override
			public void onClick(View v){
				Log.i("扫描音乐按下","按下，跳转到活动扫描");
				Intent intent = new Intent(activity,MusicScanActivity.class);
				startActivityForResult(intent,2);
			}
		};
	}

	/**
	 * 接受下一个活动回馈的数据
	 * @param requestCode
	 * @param resultCode
	 * @param data
	 */
	@Override
	protected void onActivityResult(int requestCode,int resultCode,Intent data){
		mViewPager.setCurrentItem(0);
		mSectionsPagerAdapter.notifyDataSetChanged();//更新Fragment-重新载入
		Log.i("结果","onActivityResult: "+requestCode);
		switch(requestCode) {
			case StaticValue.localMusicListRequestCode:
				if(resultCode == RESULT_OK) {
					if(StaticValue.mp3InfoArrayList != null) {
						//更新音乐列表
						if(mViewPager != null) {
							Log.i("结果","onActivityResult: "+resultCode);
							mViewPager.invalidate();//重绘？
						}
					}
				}
				break;
		}

	}

	/**
	 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
	 * one of the sections/tabs/pages.
	 */
	public class MySectionsPagerAdapter extends FragmentPagerAdapter{

		public MySectionsPagerAdapter(FragmentManager fm){
			super(fm);
		}

		@Override
		public Fragment getItem(int position){
			// getItem is called to instantiate the fragment for the given page.
			// Return a PlaceholderFragment (defined as a static inner class below).
			return localMusicListFragment.newInstance(position+1);
		}

		@Override
		public Object instantiateItem(ViewGroup container,int position){
			return super.instantiateItem(container,position);
		}

		@Override
		public int getItemPosition(Object object){
			Log.i("getItemPosition","获取页面");
			return PagerAdapter.POSITION_NONE;
		}

		@Override
		public int getCount(){
			// Show 3 total pages.
			return 4;
		}

		@Override
		public CharSequence getPageTitle(int position){
			switch(position) {
				case 0:
					return "单曲";
				case 1:
					return "歌手";
				case 2:
					return "专辑";
				case 3:
					return "文件夹";
			}
			return null;
		}
	}

	/**
	 * 本地音乐列表的Adapter，用于RecycleView
	 */
	public static class localMusicListAdapter_RecycleView extends RecyclerView.Adapter{
		private Context context;
		public List<? extends Map<String,String>> Mp3List;
		public ArrayList<View> views;
		public int selectPosition;
		/**
		 * 顶端元素类型
		 */
		private static final int TYPE_TOP = - 1;
		/**
		 * 中间元素类型
		 */
		private static final int TYPE_ITEM = 0;
		/**
		 * 底端元素类型
		 */
		private static final int TYPE_FOOT = 1;
		/**
		 * 是否是顶端元素
		 */
		private boolean isFirst;
		/**
		 * 是否是底端元素
		 */
		private boolean isFoot;
		/**
		 * 现在播放位置
		 */
		private int CurrcentPlayPosition;
		/**
		 * 上次播放位置
		 */
		private int lastPlayPosition;

		/**
		 * 定义回调接口
		 */
		public interface OnRecyclerViewListener{
			void onItemClick(int position,View v);

			boolean onItemLongClick(int position,View v);
		}

		private static OnRecyclerViewListener onRecyclerViewListener;

		public void setOnRecyclerViewListener(OnRecyclerViewListener onRecyclerViewListener){
			this.onRecyclerViewListener = onRecyclerViewListener;
		}

		/**
		 * 构造方法
		 *
		 * @param context
		 * @param textlist
		 * @param selectPosition 选择的项目，0，单曲，1，歌手，2，专辑，3，文件夹
		 */
		public localMusicListAdapter_RecycleView(Context context,List<? extends Map<String,String>> textlist,int selectPosition){
			CurrcentPlayPosition = - 1;
			lastPlayPosition = - 1;
			this.context = context;
			Mp3List = textlist;
			this.selectPosition = selectPosition;
			isFirst = true;
			isFoot = false;
			views = new ArrayList<>();
		}

		/**
		 * 设置当前播放位置
		 * @param position
		 */
		public void setCurrcentPlayPosition(int position)
		{
			lastPlayPosition = CurrcentPlayPosition;
			CurrcentPlayPosition = position;
			this.notifyDataSetChanged();//通知有数据变化
		}
		@Override
		public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent,int viewType){
			View view = null;
			switch(viewType)
			{
				case TYPE_TOP://顶端
					isFirst = true;
					view = LayoutInflater.from(context).inflate(R.layout.include_local_music_single_top_layout,null);
					break;
				case TYPE_ITEM://中间项
					//根据不同的选择位置初始化不同的布局
					switch(selectPosition) {
						case 0:
							break;
						case 1://音乐表，单曲
							view = LayoutInflater.from(context).inflate(R.layout.local_music_list_item,null);
							break;
						case 2:
							break;
						case 3:
							break;
						case 4:
							break;
					}
					break;
				case TYPE_FOOT://底部
					view = LayoutInflater.from(StaticValue.MainActivity).inflate(R.layout.local_music_list_item,null);
					isFoot = true;
					break;
			}

			LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
			view.setLayoutParams(layoutParams);
			return new localMusic_RecycleViewHolder(view);
		}

		@Override
		public void onBindViewHolder(RecyclerView.ViewHolder holder,int position){
			localMusic_RecycleViewHolder localMusic_recycleViewHolder = (localMusic_RecycleViewHolder)holder;
			localMusic_recycleViewHolder.Position = position;
			View rootView = localMusic_recycleViewHolder.rootView;
			views.add(rootView);//添加到view表中
			if(position!=0)//不是头元素
			{
				if(position+1 != getItemCount()) {//不是尾部元素
					//不同位置设置不同的事件对应布局操作
					switch(selectPosition) {
						case 0:
							break;
						case 1:
							//views.add(rootView);
							TextView MusicName = (TextView)rootView.findViewById(R.id.local_music_list_item_MusicName);
							ImageView More = (ImageView)rootView.findViewById(R.id.local_music_list_item_More);
							ImageView IsDownLoad = (ImageView)rootView.findViewById(R.id.local_music_list_item_isDownload);
							ImageView MusicQuality = (ImageView)rootView.findViewById(R.id.local_music_list_item_MusicQuality);
							TextView MusicAlbum = (TextView)rootView.findViewById(R.id.local_music_list_item_MusicAlbum);
							//-1是为了除去顶部布局带来的影响
							MusicName.setText(Mp3List.get(position-1).get("MusicName"));
							MusicAlbum.setText(Mp3List.get(position-1).get("MusicAlbum"));
							More.setImageResource(R.mipmap.list_icn_more);
							switch(Mp3List.get(position-1).get("MusicQuality")) {
								case "HighQuality":
									MusicQuality.setImageResource(R.mipmap.list_icn_hq_sml);
									break;
								case "SuperQuality":
									MusicQuality.setImageResource(R.mipmap.list_icn_sq_sml);
									break;
							}
							switch(Mp3List.get(position-1).get("IsDownLoad")) {
								case "true":
									IsDownLoad.setImageResource(R.mipmap.local_fb_icn_ok);
									break;
								case "fail":
									IsDownLoad.setImageResource(R.mipmap.local_fb_icn_fail);
									break;
								case "downloading":
									IsDownLoad.setImageResource(R.mipmap.list_icn_dld_gray);
									break;
							}
							//隐藏所有其他没播放的图片
							ImageView IsPlayLast = (ImageView)rootView.findViewById(R.id.local_music_list_item_IsPaly);
							IsPlayLast.setVisibility(View.GONE);//设置此控件为不可见
							if(position-1 == CurrcentPlayPosition) {
								//这个位置是播放位置
								Log.i("本地音乐显示播放图标","onBindViewHolder: line：724 pos:"+position);
								ImageView IsPlay = (ImageView)rootView.findViewById(R.id.local_music_list_item_IsPaly);
								IsPlay.setVisibility(View.VISIBLE);
							}
							break;
						case 2:
							break;
						case 3:
							break;
						case 4:
							break;
					}
				}
				else
				{
					//是尾部元素
					Toast.makeText(StaticValue.MainActivity,"加载更多",Toast.LENGTH_SHORT);
				}
			}
			else if(position==0)
			{
				//是头部元素
				TextView PlayText = (TextView)rootView.findViewById(R.id._include_play_music_Num);
				PlayText.setText("（共 "+StaticValue.mp3InfoArrayList.size()+" 首）");
			}
		}

		@Override
		public int getItemViewType(int position){
			if(position == 0)
			{
				return TYPE_TOP;
			}
			if(position + 1 == getItemCount())
			{
				return TYPE_FOOT;
			}
			else
			{
				return TYPE_ITEM;
			}
		}

		@Override
		public int getItemCount(){
			return Mp3List.size()+1;//+1是为了增加底部加载按钮
		}
		/**
		 * 本地音乐的RecycleViewHolder,管理Item
		 */
		class localMusic_RecycleViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,View.OnLongClickListener{
			public View rootView;
			public int Position;
			/**
			 * 构造方法
			 * @param itemView
			 */
			public localMusic_RecycleViewHolder(View itemView){
				super(itemView);
				if(isFirst)//是第一个，头部元素
				{
					rootView = itemView.findViewById(R.id._include_content);
					ImageView GotoChose_Btn = (ImageView)rootView.findViewById(R.id._include_goToChose);
					rootView.setOnLongClickListener(this);
					rootView.setOnClickListener(this);
					GotoChose_Btn.setOnLongClickListener(this);
					GotoChose_Btn.setOnClickListener(this);
					//防止重复调用
					isFirst = false;
				}
				else if(isFoot)//底部元素
				{
				}
				else {
					//不同位置设置不同的事件对应布局操作
					switch(selectPosition) {
						case 0:
							break;
						case 1:
							//views.add(rootView);
							rootView = itemView.findViewById(R.id.local_music_list_layout);
							ImageView More = (ImageView)rootView.findViewById(R.id.local_music_list_item_More);
							rootView.setOnClickListener(this);
							rootView.setOnLongClickListener(this);
							More.setOnClickListener(this);
							More.setOnLongClickListener(this);
							break;
					}
				}
		}

			@Override
			public void onClick(View v){
				Log.d("RECYCLEVIEW","按下");
				if(null != onRecyclerViewListener)
				{
					onRecyclerViewListener.onItemClick(Position,v);
				}
			}

			@Override
			public boolean onLongClick(View v){
				Log.d("RECYCLEVIEW","长按下");
				if(null != onRecyclerViewListener)
				{
					 return onRecyclerViewListener.onItemLongClick(Position,v);
				}
				return false;
			}
		}
		}

	/**
	 * 本地音乐列表的Adapter,用于ListView
	 */
	public static class localMusicListAdapter_ListView extends BaseAdapter{
		private Context context;
		private List<? extends Map<String,String>> Mp3List;
		private ArrayList<View> views;
		private int selectPosition;

		/**
		 * 构造方法
		 * @param context
		 * @param textlist
		 * @param selectPosition 选择的项目，0，单曲，1，歌手，2，专辑，3，文件夹
		 */
		public localMusicListAdapter_ListView(Context context,List<? extends Map<String, String>> textlist,int selectPosition)
		{
			this.context = context;
			Mp3List = textlist;
			this.selectPosition = selectPosition;
			views = new ArrayList<>();
		}

		@Override
		public int getCount(){
			return Mp3List.size();
		}

		@Override
		public Object getItem(int position){
			return Mp3List.get(position);
		}

		@Override
		public long getItemId(int position){
			return position;
		}

		/**
		 * 获取视野
		 * @param position
		 * @param convertView
		 * @param parent
		 * @return
		 */
		@Override
		public View getView(int position,View convertView,ViewGroup parent){
			LayoutInflater mInflater = LayoutInflater.from(context);
			//产生一个view
			View view = null;
			switch(selectPosition)
			{
				case 0://单曲
					/*Log.i("更新音乐表","更新更新");
					view = mInflater.inflate(R.layout.local_music_list_item,null);//获取布局文件，建立基本布局
					views.add(view);
					TextView MusicName = (TextView)view.findViewById(R.id.local_music_list_item_MusicName);
					ImageView More = (ImageView)view.findViewById(R.id.local_music_list_item_More);
					ImageView IsDownLoad = (ImageView)view.findViewById(R.id.local_music_list_item_isDownload);
					ImageView MusicQuality = (ImageView)view.findViewById(R.id.local_music_list_item_MusicQuality);
					TextView MusicAlbum = (TextView)view.findViewById(R.id.local_music_list_item_MusicAlbum);
					MusicName.setText(Mp3List.get(position).get("MusicName"));
					MusicAlbum.setText(Mp3List.get(position).get("MusicAlbum"));
					switch(Mp3List.get(position).get("MusicQuality"))
					{
						case "HighQuality":
							MusicQuality.setImageResource(R.mipmap.list_icn_hq_sml);
							break;
						case "SuperQuality":
							MusicQuality.setImageResource(R.mipmap.list_icn_sq_sml);
							break;
					}
					switch(Mp3List.get(position).get("IsDownLoad"))
					{
						case "true":
							IsDownLoad.setImageResource(R.mipmap.local_fb_icn_ok);
							break;
						case "fail":
							IsDownLoad.setImageResource(R.mipmap.local_fb_icn_fail);
							break;
						case "downloading":
							IsDownLoad.setImageResource(R.mipmap.list_icn_dld_gray);
							break;
					}*/
					break;
				case 1://歌手
					Log.i("更新音乐表","更新更新");
					view = mInflater.inflate(R.layout.local_music_list_item,null);//获取布局文件，建立基本布局
					views.add(view);
					TextView MusicName = (TextView)view.findViewById(R.id.local_music_list_item_MusicName);
					ImageView More = (ImageView)view.findViewById(R.id.local_music_list_item_More);
					ImageView IsDownLoad = (ImageView)view.findViewById(R.id.local_music_list_item_isDownload);
					ImageView MusicQuality = (ImageView)view.findViewById(R.id.local_music_list_item_MusicQuality);
					TextView MusicAlbum = (TextView)view.findViewById(R.id.local_music_list_item_MusicAlbum);
					MusicName.setText(Mp3List.get(position).get("MusicName"));
					MusicAlbum.setText(Mp3List.get(position).get("MusicAlbum"));
					More.setImageResource(R.mipmap.list_icn_more);
					ImageView IsPlay = (ImageView)view.findViewById(R.id.local_music_list_item_IsPaly);
					IsPlay.setImageResource(R.mipmap.song_play_icon);
					switch(Mp3List.get(position).get("MusicQuality"))
					{
						case "HighQuality":
							MusicQuality.setImageResource(R.mipmap.list_icn_hq_sml);
							break;
						case "SuperQuality":
							MusicQuality.setImageResource(R.mipmap.list_icn_sq_sml);
							break;
					}
					switch(Mp3List.get(position).get("IsDownLoad"))
					{
						case "true":
							IsDownLoad.setImageResource(R.mipmap.local_fb_icn_ok);
							break;
						case "fail":
							IsDownLoad.setImageResource(R.mipmap.local_fb_icn_fail);
							break;
						case "downloading":
							IsDownLoad.setImageResource(R.mipmap.list_icn_dld_gray);
							break;
					}
					break;
				case 2://专辑
					break;
				case 3://文件夹
					break;
			}
			return view;
		}
	}

	@Override
	protected void onDestroy(){
		super.onDestroy();
		localBroadcastManager.unregisterReceiver(ui_receiver);
	}

	/**
	 *  广播接收器
	 * Created by SmileSB101 on 2016/10/19 0019.
	 */
	public class UI_Receiver extends BroadcastReceiver{
		@Override
		public void onReceive(Context context,Intent intent){
			try {
				int type = intent.getIntExtra(MusicServices.BROADCAST.TYPE_TYPENAME,- 2);
				switch(type) {
					case MusicServices.BROADCAST.TYPE_SETLISTITEM:
						ListItemClick(intent.getIntExtra(MusicServices.BROADCAST.TYPE_VALUENAME,- 1));
						break;
					case MusicServices.BROADCAST.TYPE_CANCELITEMIMAGE://取消播放图标
						CancelListItemClick(intent.getIntExtra(MusicServices.BROADCAST.TYPE_VALUENAME,- 1));
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
