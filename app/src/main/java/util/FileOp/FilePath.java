package util.FileOp;

import android.os.Environment;
import android.provider.DocumentsContract;

import StaticValue.StaticValue;

/**
 *文件路径
 * Created by SmileSB101 on 2016/11/2 0002.
 */

public class FilePath{
	/**
	 * 软件根目录
	 */
	public static final String RootPath = Environment.getExternalStorageDirectory()+"";
	/**
	 * 文件下载路径
	 */
	public static final String DownLoadPath = "/馨乐/download/";
	/**
	 * 下载的歌词路径
	 */
	public static final String LrcPath = DownLoadPath+"lrc/";
	/**
	 * 创建歌词文件的路径
	 */
	public static final String CreateFileLrcPath = RootPath+LrcPath;
}
