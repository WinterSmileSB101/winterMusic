package util;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import StaticValue.StaticValue;

/**
 * 请求权限管理活动工具类
 * Created by SmileSB101 on 2016/10/8.
 */

public class PermissionActivity extends AppCompatActivity{
	public static final int PREMISSION_GRANTED = 0;//标示权限授权
	public static final int PREMISSION_DENIEG = 0;//权限不足，被拒绝授权的时候
	private static final int PREMISSION_REQUEST_CODE = 0;//系统权限授权的权限参数
	private static String EXTRA_PREMISSION;//权限参数（包名）
	private static final String PACKAGE_URL_SCHEME = "package:";//权限方案
	private CheckPremission checkPremission;//检测权限类的权限检测器
	private boolean isrequestCheck;//判断是否需要系统权限检测。防止和系统提示框重叠

	public PermissionActivity()
	{
		//进行初始化操作
		EXTRA_PREMISSION = StaticValue.MainActivity.getPackageName();//获取包名
	}

	/**
	 * 启动当前权限页面的公开接口
	 * @param activity 来源活动
	 * @param request_code 请求码
	 * @param premission 权限
	 */
	public static void StartActivityForPremission(Activity activity,int request_code,String...premission)
	{
		Intent intent = new Intent(activity,PermissionActivity.class);
		intent.putExtra(EXTRA_PREMISSION,premission);
		ActivityCompat.startActivityForResult(activity,intent,request_code,null);
	}

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		Log.i("权限管理活动创建",""+getIntent().hasExtra(EXTRA_PREMISSION));
		if (getIntent() == null || !getIntent().hasExtra(EXTRA_PREMISSION))//如果参数不等于配置的权限参数时
		{
			throw new RuntimeException("当前Activity需要使用静态的StartActivityForResult方法启动");//异常提示
		}
		checkPremission = new CheckPremission();
		isrequestCheck = true;//改变检测状态
	}
	//检测完之后请求用户授权
	@Override
	protected void onResume() {
		super.onResume();
		if (isrequestCheck) {
			String[] permission = getPermissions();
			if (checkPremission.permissionSet(permission)) {
				requestPermissions(permission);     //去请求权限
			} else {
				allPermissionGranted();//获取全部权限
			}
		} else {
			isrequestCheck = true;
		}
	}
	//获取全部权限
	private void allPermissionGranted() {
		setResult(PREMISSION_GRANTED);
		finish();
	}

	//请求权限去兼容版本
	private void requestPermissions(String... permission) {
		ActivityCompat.requestPermissions(this, permission, PREMISSION_REQUEST_CODE);
	}

	//返回传递过来的权限参数
	private String[] getPermissions() {
		return getIntent().getStringArrayExtra(EXTRA_PREMISSION);
	}
	/**
	 * 用于权限管理
	 * 如果全部授权的话，则直接通过进入
	 * 如果权限拒绝，缺失权限时，则使用dialog提示
	 *
	 * @param requestCode  请求代码
	 * @param permissions  权限参数
	 * @param grantResults 结果
	 */
	@Override
	public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		if (PREMISSION_REQUEST_CODE == requestCode && hasAllPermissionGranted(grantResults)) //判断请求码与请求结果是否一致
		{
			isrequestCheck = true;//需要检测权限，直接进入，否则提示对话框进行设置
			allPermissionGranted(); //进入
		} else {   //提示对话框设置
			isrequestCheck = false;
			showMissingPermissionDialog();//dialog
		}
	}
	//显示对话框提示用户缺少权限
	private void showMissingPermissionDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(PermissionActivity.this);
		builder.setTitle("警告");//提示帮助
		builder.setMessage("需要的权限没有获取到，相关功能不能执行。请进入设置手动授权。");

		//如果是拒绝授权，则退出应用
		//退出
		builder.setNegativeButton("退出程序", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				setResult(PREMISSION_DENIEG);//权限不足
				finish();
			}
		});
		//打开设置，让用户选择打开权限
		builder.setPositiveButton("设置权限", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				startAppSettings();//打开设置
			}
		});
		builder.setCancelable(false);
		builder.show();
	}

	//获取全部权限
	private boolean hasAllPermissionGranted(int[] grantResults) {
		for (int grantResult : grantResults) {
			if (grantResult == PackageManager.PERMISSION_DENIED) {
				return false;
			}
		}
		return true;
	}
	//打开系统应用设置(ACTION_APPLICATION_DETAILS_SETTINGS:系统设置权限)
	private void startAppSettings() {
		Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
		intent.setData(Uri.parse(PACKAGE_URL_SCHEME + getPackageName()));
		startActivity(intent);
	}
}
