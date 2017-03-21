package util;

import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.content.ContextCompat;

import StaticValue.StaticValue;
import winter.zxb.smilesb101.winterMusic.MainActivity;

/**
 * 检查权限管理工具类
 */
public class CheckPremission{
	public static Context context = MainActivity.activity.getApplicationContext();

	//检查权限时，判断系统的权限集合
	public static boolean permissionSet(String... permissions) {
		context = MainActivity.activity.getApplicationContext();
		for (String permission : permissions) {
			if (isLackPermission(permission)) {//是否添加完全部权限集合
				return true;
			}
		}
		return false;
	}

	//检查系统权限是，判断当前是否缺少权限(PERMISSION_DENIED:权限是否足够)
	private static boolean isLackPermission(String permission) {
		return ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_DENIED;
	}
}
