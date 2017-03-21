package my_popwindow;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.DrawFilter;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

import Music.Mp3Info;
import StaticValue.StaticValue;
import winter.zxb.smilesb101.winterMusic.MainActivity;
import winter.zxb.smilesb101.winterMusic.R;

import static util.CheckPremission.context;

/**
 * 播放列表的popwindow
 * Created by SmileSB101 on 2016/10/19 0019.
 */

public class PlayListPopWindow extends PopupWindow implements View.OnClickListener,View.OnLongClickListener{
	private Context context;
	private View view;
	private TextView leftTitle;
	private TextView centerTitle;
	private TextView rightTitle;

	private RecyclerView PlayList_RV;
	/**
	 * adapter
	 */
	private PlayList_RecycleViewAdapter playList_adapter;

	/**
	 * 构造方法
	 * @param context 同步上下文
	 * @param LayoutID 布局ID
	 */
	public PlayListPopWindow(Context context,int LayoutID,ArrayList<Mp3Info> PlayList)
	{
		this.context = context;
		view = LayoutInflater.from(context).inflate(LayoutID,null);
		Log.i("播放列表弹出界面","PlayListPopWindow: "+PlayList.size());
		this.setContentView(view);
		this.setOutsideTouchable(true);//在外部是否可点击
		this.setWidth(WindowManager.LayoutParams.MATCH_PARENT);
		this.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
		this.setFocusable(true);
		this.setAnimationStyle(R.style.playList_popupWiondwStyle);
		leftTitle = (TextView)view.findViewById(R.id.PlayList_LoveAllBtn);
		centerTitle = (TextView)view.findViewById(R.id.PlayList_Item_CenterTitle);
		rightTitle = (TextView)view.findViewById(R.id.PlayList_ClearBtn);
		PlayList_RV = (RecyclerView)view.findViewById(R.id.PlayList_PlayListView);
		RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context);
		PlayList_RV.setLayoutManager(layoutManager);//必须设置，否者不会显示
		leftTitle.setOnClickListener(PlayListPopWindowListener);
		centerTitle.setOnClickListener(PlayListPopWindowListener);
		centerTitle.setText("播放列表（"+PlayList.size()+"）");//设置歌曲数目
		rightTitle.setOnClickListener(PlayListPopWindowListener);
		playList_adapter = new PlayList_RecycleViewAdapter(context,PlayList);
		PlayList_RV.setAdapter(playList_adapter);
		/**
		 * 按键监听（实现监听触发的事件）
		 */
		playList_adapter.setOnRecyclerViewListener(new PlayList_RecycleViewAdapter.OnRecyclerViewListener(){
			@Override
			public void onItemClick(int position,View v){
				switch(v.getId()) {
					case R.id.PlayList_Item_Delete://删除按钮
						Log.i("按下删除按钮","onItemClick: ");
						if(playList_adapter.Mp3List!=null)
						{
							playList_adapter.Mp3List.remove(position);
							playList_adapter.notifyDataSetChanged();
						}
						break;
					case R.id.PlayList_Item_WhereCome://来源按钮
						Log.i("来源按钮按下","找到来源");
						break;
					case R.id.PlayList_Item_ContenLayout://其他布局
						//播放这首音乐
//						View view = v.getRootView().findViewById(R.id.PlayList_Item_WhereCome);//获取来源按钮
//						view.setVisibility(View.VISIBLE);
						MainActivity.musicIBind.addPlayList(playList_adapter.Mp3List.get(position));
						break;
				}
			}

			@Override
			public boolean onItemLongClick(int position,View v){
				return false;
			}
		});
	}


	@Override
	public void onClick(View v){

	}

	@Override
	public boolean onLongClick(View v){
		return false;
	}

	/**
	 *播放列表的Adapter
	 */
	static class PlayList_RecycleViewAdapter extends RecyclerView.Adapter{
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
			return new PlayList_RecyclerViewHolder(view);
		}

		@Override
		public void onBindViewHolder(RecyclerView.ViewHolder holder,int position){
			PlayList_RecyclerViewHolder recyclerViewHolder = (PlayList_RecyclerViewHolder)holder;
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
				rootView.findViewById(R.id.PlayList_Item_Delete).setOnClickListener(this);
			}

			@Override
			public void onClick(View v){

				if(null != onRecyclerViewListener) {
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
	 * 播放列表弹出菜单事件监听
	 */
	public View.OnClickListener PlayListPopWindowListener = new View.OnClickListener(){
		@Override
		public void onClick(View v){
			switch(v.getId())
			{
				case R.id.PlayList_ClearBtn://清除按钮按下
					Log.i("清除按钮按下","清空所有内容");
					if(playList_adapter.Mp3List != null)
					{
						playList_adapter.Mp3List.removeAll(playList_adapter.Mp3List);//移出所有歌曲
						playList_adapter.notifyDataSetChanged();//通知有改变发生
					}
					break;
				case R.id.PlayList_LoveAllBtn://收藏所有按钮
					Log.i("收藏按钮按下","弹出收藏面板");
					break;
			}
		}
	};
}
