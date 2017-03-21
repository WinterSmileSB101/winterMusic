package winter.zxb.smilesb101.winterMusic;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import StaticValue.StaticValue;
import myView.MyPopUpWindow;
import services.MusicServices;

/**
 * 自定义的adapter
 * Created by SmileSB101 on 2016/9/26.
 */
public class MylocalMusicSimpleAdapter extends BaseAdapter{

	private List<? extends Map<String, String>> Textlist;
	private List<? extends Map<String,Integer>> Piclist;
	private Context context;
	private static final ArrayList<View> views = new ArrayList<>();
	private ArrayList<String> text;
	/**
	 * 构造方法
	 * @param context 同步上下文
	 * @param textlist 文字表
	 * @param piclist 图片表
	 */
	public MylocalMusicSimpleAdapter(Context context,List<? extends Map<String, String>> textlist,List<? extends Map<String, Integer>> piclist)
	{
		Textlist = textlist;
		Piclist = piclist;
		this.context = context;
		text = new ArrayList<>();
		//text = new String[]{"本地音乐","最近播放","下载管理","我的歌手","我的电台","我的MV","创建的歌单"};
	}

	@Override
	public int getCount(){
		return Textlist.size();
	}

	@Override
	public Object getItem(int i){
		return Textlist.get(i);
	}

	@Override
	public long getItemId(int i){
		return i;
	}

	@Override
	public View getView(int position,View view,ViewGroup viewGroup){
		LayoutInflater mInflater = LayoutInflater.from(context);
		//产生一个View
		View tempview = null;
//根据type不同的数据类型构造不同的View
		ImageView image = null;
		TextView text = null;
		if(position < Textlist.size() - 2)
		{
			view = mInflater.inflate(R.layout.listlayout,null);
			views.add(view);//添加到表中
			view.setOnClickListener(localMusicListener);//添加按键监听
		    image = (ImageView)view.findViewById(R.id.list_item_pic);
		    image.setImageResource(Piclist.get(position).get("Pic"));
		    text = (TextView)view.findViewById(R.id.list_item_text);
		    text.setText(Textlist.get(position).get("Title"));
			this.text.add(Textlist.get(position).get("Title"));
	    }
		else
		{
			view = mInflater.inflate(R.layout.list_and_list_layout, null);
			view.setOnClickListener(musicListListener);
			image=(ImageView)view.findViewById(R.id.list_andlist_pic);
			image.setImageResource(Piclist.get(position).get("Pic"));
			text = (TextView)view.findViewById(R.id.list_andlist_text);
			text.setText(Textlist.get(position).get("Text"));
			this.text.add(Textlist.get(position).get("Text"));
			ImageView more = (ImageView)view.findViewById(R.id.list_andlist_tools);
			more.setTag(Textlist.get(position).get("Text"));//添加标签，用于以后分辨不同的列表条目
			more.setOnClickListener(MoreListener);
			more.setImageResource(Piclist.get(position).get("More"));
		}
		return view;
	}

	/**
	 * 除了歌单之外的监听事件
	 */
	View.OnClickListener localMusicListener = new View.OnClickListener(){
		@Override
		public void onClick(View view){
			Log.i("歌单之外点下","啊盛大盛大");
			TextView TempText = (TextView)view.findViewById(R.id.list_item_text);
			if(TempText.getText().equals(text.get(0)))//本地音乐
			{
				Log.i("歌单之外点下","本地音乐");
				//启动新活动
				Intent intent = new Intent();
				intent.setClass(MainActivity.activity,localMusicActivity.class);
				MainActivity.activity.startActivityForResult(intent,1);
			}
			else if(TempText.getText().equals(text.get(1)))//最近播放
			{
				Log.i("歌单之外点下","最近播放");
			}
			else if(TempText.getText().equals(text.get(2)))//下载管理
			{
				Log.i("歌单之外点下","下载管理");
			}
			else if(TempText.getText().equals(text.get(3)))//我的歌手
			{
				Log.i("歌单之外点下","我的歌手");
			}
			else if(TempText.getText().equals(text.get(4)))//我的电台
			{
				Log.i("歌单之外点下","我的电台");
			}
			else if(TempText.getText().equals(text.get(5)))//我的MV
			{
				Log.i("歌单之外点下","我的MV");
			}
			else
			{
				Log.i("歌单之外点下","asdasasdasdasd");
			}
		}
	};
	/**
	 * 歌单监听事件
	 */
	View.OnClickListener musicListListener = new View.OnClickListener(){
		@Override
		public void onClick(View view){
			TextView TempText = (TextView)view.findViewById(R.id.list_andlist_text);
			String t = TempText.getText().toString();
			if(t.contains(text.get(text.size()-2)))//创建的歌单
			{
				Log.i("歌单之外点下","创建的歌单");
			}
			else if(t.contains(text.get(text.size()-1)))//收藏的歌单
			{
				Log.i("歌单之外点下","收藏的歌单");
			}
		}
	};
	/**
	 * 歌单条目的最后一个更多选项的监听
	 * */
	View.OnClickListener MoreListener = new View.OnClickListener(){
		@Override
		public void onClick(View view){
			Log.i("更多按下","更多，更多");
			String string = view.getTag().toString();
			Log.d("字符串",string);
			MyPopUpWindow win = new MyPopUpWindow (StaticValue.MainActivity,400,300,string);
			win.showAsDropDown(view);//设置显示
		}
	};
}
