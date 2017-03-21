package util;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

/**
 * 网络通信工具类
 * Created by SmileSB101 on 2016/11/1 0001.
 */

public class InternetUtil{
	/**
	 * 网络请求队列
	 */
	private static RequestQueue mRequestqueue;
	public static RequestQueue getmRequestqueue(Context context)
	{
		if(mRequestqueue == null)
		{
			mRequestqueue = Volley.newRequestQueue(context);
			return mRequestqueue;
		}
		else{
			return mRequestqueue;
		}
	}
	public static <T> void addToRequestQueue(Request<T> request,Context context){
		getmRequestqueue(context).add(request);
	}
	/**
	 * 网易音乐搜索API
	 * http://s.music.163.com/search/get/
	 * 获取方式：GET
	 * 参数：
	 * src: lofter //可为空
	 * type: 1
	 *
	 * s：搜索的内容

	 offset：偏移量（分页用）

	 limit：获取的数量

	 type：搜索的类型

	 歌曲 1

	 专辑 10

	 歌手 100

	 歌单 1000

	 用户 1002

	 mv 1004

	 歌词 1006

	 主播电台 1009
	 * filterDj: true|false //可为空
	 * s: //关键词
	 * limit: 10 //限制返回结果数
	 * offset: 0 //偏移
	 * callback: //为空时返回json，反之返回jsonp callback
	 */
	public static void keywordSearch(String s, Response.Listener<JSONObject> listener,
							  Response.ErrorListener errorListener,Context context) {
		String url = UrlConstants.CLOUD_MUSIC_API_PREFIX + "type=1&s='" + s + "'&limit=10&offset=0";
		request(Request.Method.GET, url, null, listener, errorListener,context);
	}

	/**
	 * 请求方法
	 * @param method
	 * @param url
	 * @param jsonObject
	 * @param listener
	 * @param errorListener
	 * @param context
	 */
	public static void request(int method, String url, JSONObject jsonObject, Response.Listener<JSONObject> listener,
						Response.ErrorListener errorListener,Context context) {
		JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(method, url, jsonObject, listener, errorListener);
		addToRequestQueue(jsonObjectRequest,context);
	}

	public class UrlConstants {
		public static final String CLOUD_MUSIC_API_PREFIX = "http://s.music.163.com/search/get/?";
	}
}
