package util.NetWorkUtil;

import android.content.Context;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.JsonArray;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import util.FileOp.FIleStore;
import util.FileOp.FilePath;
import util.InternetUtil;
import util.Result;

import static android.R.attr.id;


/**
 * 音乐网络类
 * Created by SmileSB101 on 2016/11/1 0001.
 */

public class MusicNetWorkAPI{
	/**
	 * 网易音乐搜索API
	 * http://s.music.163.com/search/get/
	 * 获取方式：GET
	 * 参数：
	 * src: lofter //可为空
	 * type: 1
	 * filterDj: true|false //可为空
	 * s: //关键词
	 * limit: 10 //限制返回结果数
	 * offset: 0 //偏移
	 * callback: //为空时返回json，反之返回jsonp callback
	 * @param s
	 * @param context
	 * @return
	 * 注意废数字才用‘’符号，要不不能用，否则出错！！
	 */
	public static void SearchMusic(Context context,String s,int limit,int type,int offset){
		String url = UrlConstants.CLOUD_MUSIC_API_SEARCH + "type="+type+"&s='" + s + "'&limit="+limit+"&offset="+offset;
		RequestQueue requestQueue = InternetUtil.getmRequestqueue(context);
		StringRequest straingRequest = new StringRequest(url,new Response.Listener<String>(){
			@Override
			public void onResponse(String s){
				try {
					JSONObject json = new JSONObject(s);
					Log.i("onResponse: ",json.toString());
				} catch(JSONException e) {
					e.printStackTrace();
				}
			}
		},new Response.ErrorListener(){
			@Override
			public void onErrorResponse(VolleyError volleyError){
				Log.i("onResponse: ",volleyError.toString());
			}
		});
		requestQueue.add(straingRequest);
	}

	/**
	 * 网易云音乐歌曲信息API
	 * @param context
	 * @param id 歌曲id
	 * @param ids 用[]包裹起来的歌曲id 写法%5B %5D
	 * @return
	 */
	public static void Cloud_Music_MusicInfoAPI(Context context,String id,String ids)
	{
		String url = UrlConstants.CLOUD_MUSIC_API_MUSICINGO + "id="+id+"&ids=%5B"+ids+"%5D";
		RequestQueue requestQueue = InternetUtil.getmRequestqueue(context);
		StringRequest straingRequest = new StringRequest(url,new Response.Listener<String>(){
			@Override
			public void onResponse(String s){
				try {
					JSONObject json = new JSONObject(s);
					Log.i("onResponse: ",json.toString());
				} catch(JSONException e) {
					e.printStackTrace();
				}
			}
		},new Response.ErrorListener(){
			@Override
			public void onErrorResponse(VolleyError volleyError){
				Log.i("onResponse: ",volleyError.toString());
			}
		});
		requestQueue.add(straingRequest);
	}

	/**
	 * 获取歌曲歌词的API
	 *URL：

	 GET http://music.163.com/api/song/lyric

	 必要参数：

	 id：歌曲ID

	 lv：值为-1，我猜测应该是判断是否搜索lyric格式

	 kv：值为-1，这个值貌似并不影响结果，意义不明

	 tv：值为-1，是否搜索tlyric格式
	 * @param context
	 * @param os
	 * @param MusicName 音乐名
	 * @param MusicArtist 音乐家
	 */
	public static void Cloud_Muisc_getLrcAPI(final Context context,final String os,final String MusicName,final String MusicArtist)
	{
		String url = UrlConstants.CLOUD_MUSIC_API_SEARCH + "type=1&s='" + MusicName + "'&limit="+10+"&offset="+0;
		RequestQueue requestQueue = InternetUtil.getmRequestqueue(context);
		StringRequest straingRequest = new StringRequest(url,new Response.Listener<String>(){
			@Override
			public void onResponse(String s){
				try {
					String MuiscId = "";
					JSONObject json = new JSONObject(s);
					Log.i("onResponse: 0",json.toString());
					JSONArray jsonElements = null;
					jsonElements = json.getJSONObject("result").getJSONArray("songs");
					Log.i("onResponse: 1",jsonElements.getJSONObject(0).toString());
					//解析json
					for(int i = 0;i<jsonElements.length();i++)
					{
						JSONObject jsonObject = jsonElements.getJSONObject(i);
						MuiscId = jsonObject.getString("id");
						Log.i("onResponse: MusicID",MuiscId);
						String result = jsonObject.get("artists").toString();
						result = result.replace("[","");
						result = result.replace("]","");
						result = result.replace("},{","}||{");
						if(result.contains("}||{"))
						{
							String[] results = result.split("[||]");//注意必须要加[]在安卓环境下，否者不能实现正常分割
							for(String str:results) {
								Log.i("onResponse: 3s",str);
								if(checkIScontain(str,MusicArtist))
								{
									FindLrc(context,MuiscId,MusicName+"-"+MusicArtist);//获取歌曲歌词
									break;
								}
								//Log.i("onResponse: 3s",musicArtist.get("name").toString());
							}
						}
						else
						{
							Log.i("onResponse: 3",result);
							if(checkIScontain(result,MusicArtist))
							{
								FindLrc(context,MuiscId,MusicName+"-"+MusicArtist);//获取歌曲歌词
								break;
							}
						}
					}

				} catch(JSONException e) {
					e.printStackTrace();
				}
			}
		},new Response.ErrorListener(){
			@Override
			public void onErrorResponse(VolleyError volleyError){
				Log.i("onResponse: ",volleyError.toString());
			}
		});
		requestQueue.add(straingRequest);
	}

	/**
	 * 检查是否包含后面字符串（name后字段是否等于contains）
	 * @param str 需要检查的字符串
	 * @param Contains 对比的字符串
	 * @return
	 */
	private static boolean checkIScontain(String str,String Contains)
	{
		String[] str1 = str.split("[,]");
		for(String temp:str1)
		{
			if(temp.contains("name"))
			{
				String MusicArt = temp.substring(temp.indexOf(":")+1);
				MusicArt = MusicArt.replace("\"","");
				Log.i("MUSICAPI LINE188",MusicArt);
				Log.i("MUSICAPI LINE190",Contains);
				if(MusicArt.equals(Contains))
				{
					Log.i("MUSICAPI LINE188","Success");
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * 获取歌词的方法
	 * @param context 同步上下文，用来存储文件
	 * @param MusicID 音乐ID
	 * @param FileName 文件名称
	 */
	private static void FindLrc(final Context context,String MusicID,final String FileName)
	{

		String url = UrlConstants.CLOUD_MUSIC_API_MUSICLRC + "os=pc&id="+MusicID+"&lv=-1&kv=-1&tv=-1";
		RequestQueue requestQueue = InternetUtil.getmRequestqueue(context);
		StringRequest straingRequest = new StringRequest(url,new Response.Listener<String>(){
			@Override
			public void onResponse(String s){
				try {
					JSONObject json = new JSONObject(s);
					FIleStore.storeStringLrc(context,FileName,json.toString(),FilePath.LrcPath);
					Log.i("onResponse: ",json.toString());
				} catch(JSONException e) {
					e.printStackTrace();
				}
			}
		},new Response.ErrorListener(){
			@Override
			public void onErrorResponse(VolleyError volleyError){
				Log.i("onResponse: ",volleyError.toString());
			}
		});
		requestQueue.add(straingRequest);
	}
	/**
	 * 获取歌单的API
	 * @param context
	 * @param id 歌单ID
	 */
	public static void Cloud_Muisc_MusicListSearch(Context context,String id)
	{
		String url = UrlConstants.CLOUD_MUSIC_API_MUSICLIST + "id="+id+"&updateTime=-1";
		RequestQueue requestQueue = InternetUtil.getmRequestqueue(context);
		StringRequest straingRequest = new StringRequest(url,new Response.Listener<String>(){
			@Override
			public void onResponse(String s){
				try {
					JSONObject json = new JSONObject(s);
					Log.i("onResponse: ",json.toString());
				} catch(JSONException e) {
					e.printStackTrace();
				}
			}
		},new Response.ErrorListener(){
			@Override
			public void onErrorResponse(VolleyError volleyError){
				Log.i("onResponse: ",volleyError.toString());
			}
		});
		requestQueue.add(straingRequest);
	}
	public static JSONObject json = null;
	public static JSONObject getInfoFromUrl_Volley(String url,Context context)
	{
		json = null;
		RequestQueue requestQueue = InternetUtil.getmRequestqueue(context);
		StringRequest straingRequest = new StringRequest(url,new Response.Listener<String>(){
			@Override
			public void onResponse(String s){
				try {
					json = new JSONObject(s);
					Log.i("onResponse: ",json.toString());
				} catch(JSONException e) {
					e.printStackTrace();
				}
			}
		},new Response.ErrorListener(){
			@Override
			public void onErrorResponse(VolleyError volleyError){
				Log.i("onResponse: ",volleyError.toString());
			}
		});
		requestQueue.add(straingRequest);
		return json;
	}
	public class UrlConstants {
		/**
		 * 云音乐搜索API网址
		 */
		public static final String CLOUD_MUSIC_API_SEARCH = "http://s.music.163.com/search/get/?";
		/**
		 * 歌曲信息API网址
		 */
		public static final String CLOUD_MUSIC_API_MUSICINGO = "http://music.163.com/api/song/detail/?";
		/**
		 * 获取歌曲的歌词
		 */
		public static final String CLOUD_MUSIC_API_MUSICLRC = "http://music.163.com/api/song/lyric?";
		/**
		 * 获取歌单
		 */
		public static final String CLOUD_MUSIC_API_MUSICLIST = "http://music.163.com/api/playlist/detail?";
	}
}
