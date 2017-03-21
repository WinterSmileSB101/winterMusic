package util.FileOp;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.security.SecureRandom;

/**
 * 文件存储类
 * Created by SmileSB101 on 2016/11/2 0002.
 */

public class FIleStore{
	/**
	 * 保存歌词文件
	 * @param context 同步上下文
	 * @param MuiscName 音乐名称
	 * @param lrc 歌词
	 * @param path 路径
	 * @return 是否成功存入
	 */
	public static boolean storeStringLrc(Context context,String MuiscName,String lrc,String path)
	{
		boolean flag = false;
		createSDCardDir(path);
		path = MuiscName + ".lrc";
		File file = new File(FilePath.CreateFileLrcPath,path);
		Log.i("存入时的Path",path);
		try {
			file.createNewFile();
			FileWriter fw = new FileWriter(file);
			fw.write(lrc);
			fw.flush();
			fw.close();
			flag = true;
			return flag;
		} catch(IOException e) {
			Log.i("storeStringLrc: ",e.toString());
			return false;
		}
	}
	public static void createSDCardDir(String Mpath){
		if(Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
			// 创建一个文件夹对象，赋值为外部存储器的目录
			File sdcardDir = Environment.getExternalStorageDirectory();
			//得到一个路径，内容是sdcard的文件夹路径和名字
			String path = sdcardDir.getPath() + Mpath;
			File path1 = new File(path);
			if(! path1.exists()) {
				//若不存在，创建目录，可以在应用启动的时候创建
				path1.mkdirs();
			}
		} else {
			return;

		}
	}

	/**
	 * 创建零时文件
	 * @param fileType 文件类型
	 * @return
	 */
	public static File createTempFile(String fileType)
	{
		switch(fileType)
		{
			case FileType.TXT:
				break;
			case FileType.JPG:
				break;
		}
		return null;
	}
	public class FileType{
		/**
		 * 文本文件
		 */
		public static final String TXT = "txt";
		/**
		 * jpg文件
		 */
		public static final String JPG = "jpg";
	}
}
