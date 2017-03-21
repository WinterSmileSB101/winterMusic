package util.NetWorkUtil;

import android.content.Context;

/**
 * 音乐网络工具类
 * Created by SmileSB101 on 2016/11/2 0002.
 */

public class NetMuiscUtil{
	/**
	 * 获取指定歌曲歌词
	 * @param context
	 * @param MusicName
	 * @return
	 */
	public static boolean getLrc(Context context,String MusicName)
	{
		MusicNetWorkAPI.Cloud_Muisc_getLrcAPI(context,"pc",MusicName,"");
		return true;
	}
}
