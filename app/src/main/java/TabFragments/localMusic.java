package TabFragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

import StaticValue.StaticValue;
import winter.zxb.smilesb101.winterMusic.MainActivity;
import winter.zxb.smilesb101.winterMusic.MylocalMusicSimpleAdapter;
import winter.zxb.smilesb101.winterMusic.R;

/**
 * 本地音乐fragment，包括本地音乐，最近播放等
 * Created by SmileSB101 on 2016/9/25.
 */
public class localMusic extends Fragment{
	private ListView localMusicList;
	/**
	 * 歌单视野
	 */
	private ListView musicListView;
	private static ArrayList<HashMap<String,String>> picList = new ArrayList<HashMap<String,String>>();
	/**
	 * 歌单的表
	 */
	private static ArrayList<HashMap<String,Integer>> textList = new ArrayList<>();
	/**
	 * 初始化
	 */
	public static localMusic getInstance(int position) {
		localMusic tabPageFragment = new localMusic();
		Bundle bundle = new Bundle();
		bundle.putInt("本地音乐", position);
		tabPageFragment.setArguments(bundle);

		/**
		 * 初始化mainList中的数据项
		 */
		HashMap map = new HashMap();
		map.put("Pic",R.mipmap.music_icn_local);
		map.put("Title","本地音乐");
		picList.add(map);
		textList.add(map);
		map = new HashMap();
		map.put("Pic",R.mipmap.music_icn_recent);
		map.put("Title","最近播放");
		picList.add(map);
		textList.add(map);
		map = new HashMap();
		map.put("Pic",R.mipmap.music_icn_dld);
		map.put("Title","下载管理");
		picList.add(map);
		textList.add(map);
		map = new HashMap();
		map.put("Pic",R.mipmap.music_icn_artist);
		map.put("Title","我的歌手");
		picList.add(map);
		textList.add(map);
		map = new HashMap();
		map.put("Pic",R.mipmap.music_icn_dj);
		map.put("Title","我的电台");
		picList.add(map);
		textList.add(map);
		map = new HashMap();
		map.put("Pic",R.mipmap.music_icn_mv);
		map.put("Title","我的MV");
		picList.add(map);
		textList.add(map);

		//初始化歌单的表
		map = new HashMap();
		map.put("Pic",R.drawable.ic_menu_share);
		map.put("Text","创建的歌单（29）");
		map.put("More",R.mipmap.music_icn_more);
		picList.add(map);
		textList.add(map);
		map = new HashMap();
		map.put("Pic",R.drawable.ic_menu_share);
		map.put("Text","收藏的歌单（209）");
		map.put("More",R.mipmap.music_icn_more);
		picList.add(map);
		textList.add(map);
		return tabPageFragment;
	}

	/**
	 * 实例化这个布局
	 * @param inflater
	 * @param container
	 * @param savedInstanceState
	 * @return
	 */
	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater,@Nullable ViewGroup container,@Nullable Bundle savedInstanceState){
		View view = inflater.inflate(R.layout.tab_localmusic, container, false);

		localMusicList =(ListView)view.findViewById(R.id.tabLocalList);
		/**
		 * 设置表中内容
		 */
		MylocalMusicSimpleAdapter mainListAdapter = new MylocalMusicSimpleAdapter(MainActivity.activity,picList,textList);
		localMusicList.setAdapter(mainListAdapter);
		return view;
	}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
	}
}
