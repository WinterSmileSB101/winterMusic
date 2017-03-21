package winter.zxb.smilesb101.winterMusic;

import android.Manifest;
import android.animation.AnimatorSet;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;

import Animation.ValueAnimation.valueAmination;
import Music.Mp3Info;
import StaticValue.StaticValue;
import util.ArtworkUtils;
import util.CheckPremission;
import util.NetWorkUtil.MusicNetWorkAPI;
import util.PermissionActivity;

import static util.CheckPremission.context;
import static util.FileScan.UpdateMediaSQL;

/**
 * Created by SmileSB101 on 2016/10/7.
 */

public class MusicScanActivity extends AppCompatActivity{
	private static String MusicPath = "";
	/**
	 * 扫描图标
	 */
	private ImageView searchICON;
	/**
	 * 扫描进度
	 */
	private TextView Scan_process;
	/**
	 * 扫描路径
	 */
	private TextView Scan_path;
	/**
	 * mp3列表
	 */
	private static ArrayList<Mp3Info> mp3InfosList = new ArrayList<>();
	private static Activity MusicScanActivity;
	public static AnimatorSet animatorSet;
	private float SearchICON_X;
	private float SearchICON_Y;
	private Button Scan_Btn;
	private ImageView scan_scanDown;

	//配置需要取的权限
	static final String[] PERMISSION = new String[]{
			Manifest.permission.WRITE_EXTERNAL_STORAGE,// 写入权限
			Manifest.permission.READ_EXTERNAL_STORAGE,  //读取权限
	};
	private final int REQUEST_CODE = 0;//请求码

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.local_music_music_scan_doing);
		UpdateMediaSQL();//更新媒体数据库
		checkPermission();//检查权限
		Scan_Btn = (Button)findViewById(R.id.scan_imaditili_btn);
		Scan_Btn.setOnClickListener(ClickListener);
		searchICON = (ImageView)findViewById(R.id.scan_searchICON);
		scan_scanDown = (ImageView)findViewById(R.id.scan_scanDown);
		Scan_process = (TextView)findViewById(R.id.scan_imaditili_process);
		Scan_path = (TextView)findViewById(R.id.scan_imaditili_path);
		MusicScanActivity = this;
	}

	/**
	 * 按钮监听事件
	 * @param v
	 */
	public View.OnClickListener ClickListener = new View.OnClickListener(){
		@Override
		public void onClick(View v){
			switch(v.getId())
			{
				case R.id.ReturnMusic_btn://返回音乐按钮
				case R.id.close_btn://返回音乐
					setResult(RESULT_OK);
					finish();//结束活动
					break;
				case R.id.get_All_albm_lrc://一键获取专辑歌词
					break;
				case R.id.scan_imaditili_btn://立即扫描按钮按下
					switch(Scan_Btn.getText().toString())
					{
						case "立即扫描":
							//获得X,Y的位置
							SearchICON_X = v.getTranslationX();
							SearchICON_Y = v.getTranslationY();
							Log.i("扫描按钮点下",SearchICON_X+"  "+SearchICON_Y);
							//开启后台扫描程序
							mp3InfosList = new ArrayList<>();
							//赋值搜索动画
							animatorSet = valueAmination.circleMoveAni(searchICON,scan_scanDown);
							musicScan.execute(mp3InfosList);
							//改变文字为取消扫描
							Scan_Btn.setText("取消扫描");
							break;
						case "取消扫描":
							//改变文字为立即扫描
							//Scan_Btn.setText("立即扫描");
							//调用取消方法
							musicScan.cancel(true);
							break;
					}
					break;
			}
		}
	};

	/**
	 * 返回按钮按下时向上一个活动传递数据
	 */
	@Override
	public void onBackPressed(){
		super.onBackPressed();
		setResult(RESULT_OK);
		finish();
	}

	/**
	 * 异步任务扫描音乐静态方法
	 */
	AsyncTask<ArrayList<Mp3Info>,Integer,ArrayList<Mp3Info>> musicScan = new AsyncTask<ArrayList<Mp3Info>,Integer,ArrayList<Mp3Info>>(){
		@Override
		protected ArrayList<Mp3Info> doInBackground(ArrayList<Mp3Info>... params){
			ArrayList<Mp3Info> mp3InfoArrayList = new ArrayList<>();

			Cursor cursor = MainActivity.activity.getContentResolver().query(
					MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,null,null,null,
					MediaStore.Audio.Media.DEFAULT_SORT_ORDER);

			//遍历游标找到所有音乐，并且存入列表
			for(int i = 0; i < cursor.getCount(); i++) {
				//计算进度并且显示到进度上
				int progress = (int)(((float)i/cursor.getCount())*100);
				if(progress >= 90)
				{
					progress = 100;
				}
				Log.i("进度",progress+"");
				cursor.moveToNext();
				Mp3Info mp3Info = new Mp3Info();
				long id = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media._ID));
				String title = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
				String album = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM));
				long albumId = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID));
				String displayName = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME));
				displayName = displayName.substring(displayName.indexOf("-")+1);//去掉音乐家
				displayName = displayName.substring(0,displayName.indexOf("."));//去掉后缀名
				displayName = displayName.replace(" ","");//去掉空格
				String artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
				long duration = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION));
				long size = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.SIZE));
				String url = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
				int isMusic = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media.IS_MUSIC));
				Bitmap bitmap = ArtworkUtils.getArtwork(context,title,id,albumId,true);//获取专辑图片
				MusicPath = url;//赋值路径
				//存储歌词
				Log.i("MusicScan 180",displayName);
				Log.i("MusicScan 181",artist);
				MusicNetWorkAPI.Cloud_Muisc_getLrcAPI(context,"pc",displayName,artist);
				publishProgress(progress);//发布进度
				if(isMusic != 0) { // 只把音乐添加到集合当中
					mp3Info.setId(id);
					mp3Info.setTitle(title);
					mp3Info.setAlbum(album);
					mp3Info.setAlbumId(albumId);
					mp3Info.setDisplayName(displayName);
					mp3Info.setArtist(artist);
					mp3Info.setDuration(duration);
					mp3Info.setSize(size);
					mp3Info.setUrl(url);
					mp3Info.setImage(bitmap);
					//插入信息到数据库
					mp3InfoArrayList.add(mp3Info);
				}
			}

			return mp3InfoArrayList;
		}

		@Override
		protected void onPreExecute(){
			super.onPreExecute();
			Log.i("后台任务开始前","动画开始执行");
			animatorSet.start();
		}

		@Override
		protected void onProgressUpdate(Integer... values){
			super.onProgressUpdate(values);
			Log.d("更新进度条","进度"+values[0]);
			Scan_process.setText(values[0]+"%");
			Scan_path.setText(MusicPath);
		}

		@Override
		protected void onPostExecute(ArrayList<Mp3Info> mp3Infos){
			super.onPostExecute(mp3Infos);
			mp3InfosList = mp3Infos;
			StaticValue.mp3InfoArrayList = mp3Infos;
			Log.i("后台任务结束后","动画执行结束");
			animatorSet.end();
			Scan_Btn.setText("扫描完成");
			searchICON.setTranslationX(SearchICON_X);
			searchICON.setTranslationY(SearchICON_Y);
			//显示完成界面
			setContentView(R.layout.local_music_music_scan_down);
			//添加按钮键监听
			findViewById(R.id.ReturnMusic_btn).setOnClickListener(ClickListener);
			findViewById(R.id.get_All_albm_lrc).setOnClickListener(ClickListener);
			findViewById(R.id.close_btn).setOnClickListener(ClickListener);
			//更新扫描完成界面
			TextView AllMusic = (TextView)findViewById(R.id.MusicScan_AllMusic);//所有扫描到的音乐
			TextView NewMusic = (TextView)findViewById(R.id.MusicScan_NewMusic);//新增的音乐
			AllMusic.setText(" "+mp3InfosList.size()+"首");
			//计算新增的音乐数量，并更新

		}

		@Override
		protected void onCancelled(ArrayList<Mp3Info> mp3Infos){
			super.onCancelled(mp3Infos);
			//初始化这个活动
			//停止动画
			animatorSet.end();
			//把按钮改变为立即扫描
			Scan_Btn.setText("立即扫描");
		}
	};

	/**
	 * 异步任务通过递归遍历查询本地音乐
	 * 传入字符串组，0位置是文件的绝对路径,为空代表SD卡根目录，后面的位置是扫描音乐文件的类型
	 */
	AsyncTask<String,Integer,ArrayList<Mp3Info>> FindMusicFromPath = new AsyncTask<String,Integer,ArrayList<Mp3Info>>(){
		@Override
		protected ArrayList<Mp3Info> doInBackground(String... params){
			try{
				if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
					ArrayList<Mp3Info> mp3InfoArrayList = new ArrayList<>();
					if(params[0].equals("")) {
						//给出的遍历路径为空,把根目录赋值给Path
						params[0] = Environment.getExternalStorageDirectory().getAbsolutePath();
					}
					//递归加遍历寻找Mp3文件
					File file = new File(params[0]);
					if(file != null && file.exists() && file.isDirectory())
					{
						File[] files = file.listFiles();
						for(int i = 0;i < files.length;i++)
						{
							int progress = (int)(i/files.length)*100;//获取进度可以发布进度
							publishProgress(progress);
							if(files[i].listFiles()==null)
							{
								//如果这个文件的下属文件为空的话，则说明这是一个文件，不是文件夹
							}
							else
							{
								//使用递归
							}
						}
					}
					return mp3InfoArrayList;
				}
				else
				{
					Toast.makeText(StaticValue.MainActivity,"没有检测到SD卡，请检查后重试！",Toast.LENGTH_LONG);
					Log.i("错误","没有SD卡！");
					return null;
				}
			}
			catch(Exception e)
			{
				Log.i("错误",e.getMessage());
				throw e;
			}
		}

		@Override
		protected void onProgressUpdate(Integer... values){
			super.onProgressUpdate(values);
			Scan_process.setText(" "+values[0]+"%");
		}
		@Override
		protected void onPreExecute(){
			super.onPreExecute();
			Log.i("后台任务开始前","动画开始执行");
			animatorSet.start();
		}

		@Override
		protected void onPostExecute(ArrayList<Mp3Info> mp3Infos){
			super.onPostExecute(mp3Infos);
			mp3InfosList = mp3Infos;
			Log.i("后台任务结束后","动画执行结束");
			animatorSet.end();
			Scan_Btn.setText("扫描完成");
			searchICON.setTranslationX(SearchICON_X);
			searchICON.setTranslationY(SearchICON_Y);
			//显示完成界面
			setContentView(R.layout.local_music_music_scan_down);
			//添加按钮键监听
			findViewById(R.id.ReturnMusic_btn).setOnClickListener(ClickListener);
			findViewById(R.id.get_All_albm_lrc).setOnClickListener(ClickListener);
			findViewById(R.id.close_btn).setOnClickListener(ClickListener);
			//更新扫描完成界面
			TextView AllMusic = (TextView)findViewById(R.id.MusicScan_AllMusic);//所有扫描到的音乐
			TextView NewMusic = (TextView)findViewById(R.id.MusicScan_NewMusic);//新增的音乐
			AllMusic.setText(" "+mp3InfosList.size()+"首");
			//计算新增的音乐数量，并更新
		}

		@Override
		protected void onCancelled(ArrayList<Mp3Info> mp3Infos){
			super.onCancelled(mp3Infos);
			//初始化这个活动
			//停止动画
			animatorSet.end();
			//把按钮改变为立即扫描
			Scan_Btn.setText("立即扫描");
		}
	};

	/**
	 * 检查权限
	 */
	private void checkPermission()
	{
		CheckPremission.permissionSet(PERMISSION);
	}

	@Override
	protected void onResume() {
		super.onResume();
		//缺少权限时，进入权限设置页面
		if (CheckPremission.permissionSet(PERMISSION)) {
			startPermissionActivity();
		}
	}

	//进入请求权限设置页面
	private void startPermissionActivity() {
		PermissionActivity.StartActivityForPremission(this, REQUEST_CODE, PERMISSION);
	}

	/**
	 * 获取从下一个活动返回的数据
	 * @param requestCode
	 * @param resultCode
	 * @param data
	 */
	@Override
	protected void onActivityResult(int requestCode,int resultCode,Intent data){
		super.onActivityResult(requestCode,resultCode,data);
		//拒绝时，没有获取到主要权限，无法运行，关闭页面
		Log.i("回调","没有获取到权限无法运行！");
		if (requestCode == REQUEST_CODE && resultCode == PermissionActivity.PREMISSION_DENIEG) {
			Log.i("回调","没有获取到权限无法运行！");
			finish();
		}
	}
}
