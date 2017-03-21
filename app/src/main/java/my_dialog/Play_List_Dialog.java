package my_dialog;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Binder;
import android.os.Build;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.text.TextUtilsCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;

import Music.Mp3Info;
import StaticValue.StaticValue;
import my_popwindow.PlayListPopWindow;
import services.MusicServices;
import util.ArtworkUtils;
import winter.zxb.smilesb101.winterMusic.MainActivity;
import winter.zxb.smilesb101.winterMusic.R;
/**
 * 音乐列表弹出框类
 * Created by SmileSB101 on 2016/10/17 0017.
 */

public class Play_List_Dialog extends AlertDialog{

	private static Context context;
	/**
	 * 主框口
	 */
	private Dialog ad;

	/**
	 * 左边标题
	 */
	private TextView leftTitle;
	/**
	 * 中间标题
	 */
	private static TextView centerTitle;
	/**
	 * 右边标题
	 */
	private TextView rightTitle;

	/**
	 * 播放列表recycleView
	 */
	private RecyclerView PlayList_RV;
	/**
	 * adapter
	 */
	private static PlayList_RecycleViewAdapter playList_recycleViewAdapter;
	/**
	 * 音乐列表
	 */
	private ArrayList<Mp3Info> PlayList;
	/**
	 * 上次播放位置
	 */
	private int LastPlayPosition;
	/**
	 * 当前播放位置
	 */
	private static int CurrentPlayPosition;
	/**
	 * 广播管理器
	 */
	private LocalBroadcastManager localBroadcastManager;



	public Play_List_Dialog(Context context,ArrayList<Mp3Info> playList){
		super(context);
		LastPlayPosition = -1;
		CurrentPlayPosition = -1;
		localBroadcastManager = LocalBroadcastManager.getInstance(context);
		this.PlayList = playList;
		if(PlayList==null)
		{
			PlayList = new ArrayList<>();
		}
		this.context = context;
		final Dialog ad = new Dialog(context, R.style.MyDialog);
		//ad = new AlertDialog.Builder(context).create();
		View view = LayoutInflater.from(StaticValue.NowActivity).inflate(R.layout.play_list_popupwindow_layout,null);
		Window window = ad.getWindow();
		window.setGravity(Gravity.BOTTOM);
		window.setWindowAnimations(R.style.dialogStyle);
		window.getDecorView().setPadding(0, 0, 0, 0);//设置窗口全屏化，重点！！
		//获得window窗口的属性
		WindowManager.LayoutParams lp = window.getAttributes();
		//设置窗口宽度为充满全屏
		lp.width = WindowManager.LayoutParams.MATCH_PARENT;
		//设置窗口高度为包裹内容
		//lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
		//将设置好的属性set回去
		window.setAttributes(lp);
		//将自定义布局加载到dialog上
		ad.setContentView(view);
		ad.setCanceledOnTouchOutside(true);//可以在外部点击弹回
		this.ad = ad;
		initValue(view);//设置值
	}

	/**
	 * 显示dialog面板
	 */
	public void showDialog()
	{
		ad.show();
	}
	/**
	 * 设置值
	 */
	public void initValue(View view){
		leftTitle = (TextView)view.findViewById(R.id.PlayList_LoveAllBtn);
		centerTitle = (TextView)view.findViewById(R.id.PlayList_Item_CenterTitle);
		rightTitle = (TextView)view.findViewById(R.id.PlayList_ClearBtn);
		PlayList_RV = (RecyclerView)view.findViewById(R.id.PlayList_PlayListView);
		centerTitle.setText("播放列表（"+PlayList.size()+"）");
		RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context);
		PlayList_RV.setLayoutManager(layoutManager);//必须设置，否者不会显示
		leftTitle.setOnClickListener(PlayListPopWindowListener);
		//centerTitle.setOnClickListener(PlayListPopWindowListener);
		rightTitle.setOnClickListener(PlayListPopWindowListener);
		playList_recycleViewAdapter = new PlayList_RecycleViewAdapter(context,PlayList);
		PlayList_RV.setAdapter(playList_recycleViewAdapter);
		/**
		 * 按键监听（在这里添加事件处理）
		 */
		playList_recycleViewAdapter.setOnRecyclerViewListener(new PlayList_RecycleViewAdapter.OnRecyclerViewListener(){
			@Override
			public void onItemClick(int position,View v){
				switch(v.getId()) {
					case R.id.PlayList_Item_Delete://删除按钮
						Log.i("按下删除按钮","onItemClick: ");
						if(playList_recycleViewAdapter.Mp3List != null) {
							Mp3Info mp3Info = playList_recycleViewAdapter.Mp3List.get(position);
							playList_recycleViewAdapter.Mp3List.remove(mp3Info);
							playList_recycleViewAdapter.notifyDataSetChanged();
							//更新UI界面
							centerTitle.setText("播放列表（"+playList_recycleViewAdapter.Mp3List.size()+"）");
							//通知服务更改播放列表
							MainActivity.musicIBind.removeMusic(mp3Info);
							mp3Info = null;
						}
						break;
					case R.id.PlayList_Item_WhereCome://来源按钮
						Log.i("来源按钮按下","找到来源");
						break;
					case R.id.PlayList_Item_ContenLayout://其他布局
						//播放这首音乐
						getMusicPosition(playList_recycleViewAdapter.Mp3List.get(position),v,position);//发送播放广播到主界面,设置UI
						break;
				}
			}

			@Override
			public boolean onItemLongClick(int position,View v){
				return false;
			}
		});
	}

	/**
	 * 设置上次播放位置
	 * @param currentPlayPosition
	 */
	public void setCurrentPlayPosition(int currentPlayPosition)
	{
		LastPlayPosition = CurrentPlayPosition;
		CurrentPlayPosition = currentPlayPosition;
		playList_recycleViewAdapter.notifyDataSetChanged();
	}
	/**
	 * 显示是否正在播放图标
	 * @param Position 位置
	 */
	public void showIsPlayImage(int Position)
	{
		if(Position != -1)
		{
			View view = playList_recycleViewAdapter.views.get(Position);
			ImageView isPlay = (ImageView)view.findViewById(R.id.PlayList_Item_IsPlay);
			ImageView whereCome = (ImageView)view.findViewById(R.id.PlayList_Item_WhereCome);
			isPlay.setVisibility(View.VISIBLE);
			whereCome.setVisibility(View.VISIBLE);
		}
	}
	/**
	 * 获得Mp3对应的位置,并发送广播
	 * @param mp3Info 点击的mp3信息
	 * @param v 点击的视野
	 * @param CurrentPosition  这次播放的位置
	 * @return
	 */
	public void getMusicPosition(Mp3Info mp3Info,View v,int CurrentPosition)
	{
		int position = -1;//本地音乐类中的位置
		for(int i = 0;i<StaticValue.mp3InfoArrayList.size();i++)
		{
			if(StaticValue.mp3InfoArrayList.get(i) == mp3Info)
			{
				position = i;
			}
		}
		if(LastPlayPosition > -1) {
			//Log.i("获得音乐位置",""+LastPlayPosition);
			View viewLast = playList_recycleViewAdapter.views.get(LastPlayPosition);
			ImageView isPlayLast = (ImageView)viewLast.findViewById(R.id.PlayList_Item_IsPlay);
			ImageView whereComeLast = (ImageView)viewLast.findViewById(R.id.PlayList_Item_WhereCome);
			TextView textView_ = (TextView)viewLast.findViewById(R.id.PlayList_Item__);
			TextView textViewMusicName = (TextView)viewLast.findViewById(R.id.PlayList_Item_MusicName);
			TextView textViewArtist = (TextView)viewLast.findViewById(R.id.PlayList_Item_Artist);
			textView_.setTextColor(Color.BLACK);
			textViewMusicName.setTextColor(Color.BLACK);
			textViewArtist.setTextColor(Color.BLACK);
			isPlayLast.setVisibility(View.GONE);
			whereComeLast.setVisibility(View.GONE);
		}
		LastPlayPosition = CurrentPosition;//更新上次位置
		if(position != -1)
		{
			ImageView isPlay = (ImageView)v.findViewById(R.id.PlayList_Item_IsPlay);
			ImageView whereCome = (ImageView)v.findViewById(R.id.PlayList_Item_WhereCome);
			TextView textView_ = (TextView)v.findViewById(R.id.PlayList_Item__);
			TextView textViewMusicName = (TextView)v.findViewById(R.id.PlayList_Item_MusicName);
			TextView textViewArtist = (TextView)v.findViewById(R.id.PlayList_Item_Artist);
			textView_.setTextColor(Color.RED);
			textViewMusicName.setTextColor(Color.RED);
			textViewArtist.setTextColor(Color.RED);
			isPlay.setVisibility(View.VISIBLE);
			whereCome.setVisibility(View.VISIBLE);
			Log.i("通知改变","位置 "+position);
			//发送广播，通知UI界面改变
			Intent intent = new Intent(MusicServices.MusicServiceReceiverAction);
			intent.putExtra(MusicServices.BROADCAST.TYPE_TYPENAME,MusicServices.BROADCAST.TYPE_SETLISTITEM);
			intent.putExtra(MusicServices.BROADCAST.TYPE_VALUENAME,position);
			localBroadcastManager.sendBroadcast(intent);//发送广播
		}
		MainActivity.musicIBind.addPlayList(playList_recycleViewAdapter.Mp3List.get(CurrentPosition));
	}
	public Play_List_Dialog(Context context,int themeResId){
		super(context,themeResId);
	}

	/**
	 * 定义外部接口
	 */
	public interface Play_List_dialog_Callback{
	void letfTitleClick(View view);
	void centerTitleClick(View view);
	void rightTitleClick(View view);
	void onItemClick(int position);
	void onItemLongClick(int position);
};

	/**
	 * 设置标题
	 * @param left
	 * @param center
	 * @param right
	 */
	public void setTitle(String left,String center,String right)
	{
		if(!left.equals(""))
		{
			leftTitle.setText(left);
		}
		if(!center.equals(""))
		{
			centerTitle.setText(center+" (");
		}
		if(!right.equals(""))
		{
			rightTitle.setText(right);
		}
	}
	@Override
	public void create(){
		super.create();
		LayoutInflater inflater = LayoutInflater.from(StaticValue.MainActivity);
		View view = inflater.inflate(R.layout.play_list_popupwindow_layout,null);
	}

	@Override
	public void show(){
		super.show();
	}


	@Override
	public void setTitle(CharSequence title){
		super.setTitle(title);
	}

	/**
	 *播放列表的Adapter
	 */
	public static class PlayList_RecycleViewAdapter extends RecyclerView.Adapter{
		public Context context;
		public ArrayList<Mp3Info> Mp3List;
		public ArrayList<HashMap<String,Integer>> ImageList;
		public ArrayList<View> views;

		public PlayList_RecycleViewAdapter(Context context,ArrayList<Mp3Info> musicList)
		{
			Mp3List = new ArrayList<>();
			ImageList = new ArrayList<>();
			this.context = context;
			Mp3List = musicList;
			HashMap map = new HashMap();
			map.put("WhereCome",R.mipmap.play_playlist_icn_link);
			map.put("Delete",R.mipmap.lay_protype_btn_close_prs);
			this.ImageList.add(map);
			views = new ArrayList<>();
		}

		/**
		 * 定义回调接口
		 */
		public interface OnRecyclerViewListener {
			void onItemClick(int position,View v);
			boolean onItemLongClick(int position,View v);
		}

		/**
		 * 事件
		 */
		private static PlayList_RecycleViewAdapter.OnRecyclerViewListener onRecyclerViewListener;

		/**
		 * 设置事件监听
		 * @param onRecyclerViewListener
		 */
		public void setOnRecyclerViewListener(PlayList_RecycleViewAdapter.OnRecyclerViewListener onRecyclerViewListener) {
			this.onRecyclerViewListener = onRecyclerViewListener;
		}
		@Override
		public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent,int viewType){
			View view = null;
			view = LayoutInflater.from(context).inflate(R.layout.playlist_popup_item,null);
			LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
			view.setLayoutParams(layoutParams);
			return new PlayList_RecycleViewAdapter.PlayList_RecyclerViewHolder(view);
		}

		@Override
		public void onBindViewHolder(RecyclerView.ViewHolder holder,int position){
			PlayList_RecycleViewAdapter.PlayList_RecyclerViewHolder recyclerViewHolder = (PlayList_RecycleViewAdapter.PlayList_RecyclerViewHolder)holder;
			recyclerViewHolder.Position = position;
			View view = recyclerViewHolder.rootView;
			views.add(view);
			TextView Music_Name = (TextView)view.findViewById(R.id.PlayList_Item_MusicName);
			TextView Music__ = (TextView)view.findViewById(R.id.PlayList_Item__);
			TextView Music_Artist = (TextView)view.findViewById(R.id.PlayList_Item_Artist);
			ImageView Music_WhereCome = (ImageView)view.findViewById(R.id.PlayList_Item_WhereCome);
			ImageView Music_Delete = (ImageView)view.findViewById(R.id.PlayList_Item_Delete);
			Music_Name.setText(Mp3List.get(position).getDisplayName());
			Music_Artist.setText(Mp3List.get(position).getArtist());
			Music_WhereCome.setImageResource(ImageList.get(0).get("WhereCome"));
			Music_Delete.setImageResource(ImageList.get(0).get("Delete"));
			if(CurrentPlayPosition == position)
			{
				//设置正在播放的图标
				ImageView isPlay = (ImageView)view.findViewById(R.id.PlayList_Item_IsPlay);
				Music_Name.setTextColor(Color.RED);
				Music__.setTextColor(Color.RED);
				Music_Artist.setTextColor(Color.RED);
				Music_WhereCome.setVisibility(View.VISIBLE);
				isPlay.setVisibility(View.VISIBLE);
			}
			else
			{
				//设置正在播放的图标
				ImageView isPlay = (ImageView)view.findViewById(R.id.PlayList_Item_IsPlay);
				Music_Name.setTextColor(Color.BLACK);
				Music__.setTextColor(Color.BLACK);
				Music_Artist.setTextColor(Color.BLACK);
				Music_WhereCome.setVisibility(View.GONE);
				isPlay.setVisibility(View.GONE);
			}
		}

		@Override
		public int getItemCount(){
			return Mp3List.size();
		}

		class PlayList_RecyclerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener{
			public View rootView;
			public int Position;
			/**
			 * 构造方法，在这里进行列表项的实例化
			 * @param itemView
			 */
			public PlayList_RecyclerViewHolder(View itemView){
				super(itemView);
				rootView = itemView.findViewById(R.id.PlayList_Item_ContenLayout);
				rootView.setOnClickListener(this);
				rootView.setOnLongClickListener(this);
				rootView.findViewById(R.id.PlayList_Item_WhereCome).setOnClickListener(this);
				rootView.findViewById(R.id.PlayList_Item_WhereCome).setOnLongClickListener(this);
				rootView.findViewById(R.id.PlayList_Item_Delete).setOnClickListener(this);
				rootView.findViewById(R.id.PlayList_Item_Delete).setOnLongClickListener(this);
			}

			@Override
			public void onClick(View v){
				if(null!=onRecyclerViewListener)
				{
					onRecyclerViewListener.onItemClick(Position,v);
				}
			}

			@Override
			public boolean onLongClick(View v){
				if(null != onRecyclerViewListener)
				{
					return onRecyclerViewListener.onItemLongClick(Position,v);
				}
				return false;
			}
		}
	}

	/**
	 * 播放列表弹出菜单顶端事件监听
	 */
	public static View.OnClickListener PlayListPopWindowListener = new View.OnClickListener(){
		@Override
		public void onClick(View v){
			switch(v.getId())
			{
				case R.id.PlayList_ClearBtn://清除按钮按下
					Log.i("清除按钮按下","");
					//弹出确定按钮
					android.support.v7.app.AlertDialog.Builder alertDialog = new android.support.v7.app.AlertDialog.Builder(context);
					TextView textView = new TextView(context);
					textView.setTextColor(Color.BLACK);
					textView.setText("确定清空播放列表？");
					alertDialog.setCustomTitle(textView);
					alertDialog.setPositiveButton("清空",new OnClickListener(){
						@Override
						public void onClick(DialogInterface dialog,int which){
							//删除列表
							if(playList_recycleViewAdapter.Mp3List != null) {
								ArrayList<Mp3Info> mp3Infos = playList_recycleViewAdapter.Mp3List;
								//通知服务更改播放列表
								MainActivity.musicIBind.removeMusic(mp3Infos);
								playList_recycleViewAdapter.Mp3List.removeAll(mp3Infos);
								playList_recycleViewAdapter.notifyDataSetChanged();
								//更新UI界面
								centerTitle.setText("播放列表（"+0+"）");
								mp3Infos = null;
							}
						}
					});
					alertDialog.setNegativeButton("取消",null);
					alertDialog.create().show();
					break;
				case R.id.PlayList_LoveAllBtn://收藏所有按钮
					Log.i("收藏按钮按下","弹出收藏所有界面");
					break;
			}
		}
	};
}
