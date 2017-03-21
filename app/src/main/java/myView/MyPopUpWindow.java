package myView;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

import StaticValue.StaticValue;
import winter.zxb.smilesb101.winterMusic.R;
/**
 * 自定义的PopWindow
 * Created by SmileSB101 on 2016/9/27.
 */

public class MyPopUpWindow extends PopupWindow implements View.OnClickListener{
	private Context context;
	private View view;
	private TextView text = null;
	private ListView listView = null;
	private ArrayList<HashMap<String,String>> listItem;
	private SimpleAdapter ListViewAdapter;

	public MyPopUpWindow(Context context,String musicListName)
	{
		this(context,ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT,musicListName);
	}
	public MyPopUpWindow(Context context, int width, int height,String MusicListName)
	{
		this.context = context;
		setWidth(width);
		setHeight(height);
		setFocusable(true);
		setOutsideTouchable(true);
		setTouchable(true);
		setBackgroundDrawable(new BitmapDrawable());
		//LayoutInflater第一个参数是布局文件名称
		view = LayoutInflater.from(this.context).inflate(R.layout.float_popuo_window,null);
		setContentView(view);
		InitArray(MusicListName);

	}

	/**
	 * 初始化数组
	 */
	private void InitArray(String Musiclist_Num)
	{
		listItem = new ArrayList<>();
		HashMap map = new HashMap();
		Log.i("初始化传输过来的字符串",Musiclist_Num);
		Musiclist_Num = Musiclist_Num.substring(0,5);
		switch(Musiclist_Num)
		{
			case "创建的歌单":
				Log.d("初始化数组",Musiclist_Num);
				map.put("Pic",R.drawable.ic_menu_gallery);
				map.put("Text","创建新歌单");
				listItem.add(map);
				break;
			case "收藏的歌单":
				Log.d("初始化数组",Musiclist_Num);
				break;
		}
		map = new HashMap();
		map.put("Pic",R.drawable.ic_menu_manage);
		map.put("Text","歌单管理");
		listItem.add(map);

		listView = (ListView)view.findViewById(R.id.popup_listView);
		ListViewAdapter = new SimpleAdapter(StaticValue.MainActivity,listItem,R.layout.pop_window_item,new String[]{"Pic","Text"},new int[]{R.id.popup_item_pic,R.id.popup_item_text});
		listView.setAdapter(ListViewAdapter);
	}
	/**
	 * 事件监听
	 * @param v
	 */
	@Override
	public void onClick(View v){
		switch(v.getId())
		{
			case R.id.action_settings:
				break;
		}
	}
}
