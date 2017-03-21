package winter.zxb.smilesb101.winterMusic;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.ViewGroup;

import StaticValue.StaticValue;
import TabFragments.friendMusic;
import TabFragments.localMusic;
import TabFragments.olineMusic;

/**
 * Created by Administrator on 2016/9/25.
 */
public class SimplePageAdapter extends FragmentPagerAdapter{
	private String[] titles;
	private Fragment[] fragments;
	/**
	 * Intent对象用于传递数据到下一个活动的Intent值
	 */
	private int intentINT;
	//构造方法
	public SimplePageAdapter(FragmentManager fm){
		super(fm);
		titles = new String[3];
		titles[0] = "网络音乐";
		titles[1] = "本地音乐";
		titles[2] = "好友社交";

		fragments = new Fragment[3];
		fragments[0] = olineMusic.getInstance(0);
		fragments[1] = localMusic.getInstance(1);
		fragments[2] = friendMusic.getInstance(2);
	}

	@Override
	public void startUpdate(ViewGroup container){
		super.startUpdate(container);
	}

	/**
	 * 获得frament
	 * @param position
	 * @return
	 */
	@Override
	public Fragment getItem(int position){
		return fragments[position];
	}

	@Override
	public int getCount(){
		return titles.length;
	}

	/**
	 * 获得抬头,这里是设置tabLayout标题部分的地方
	 * @param position
	 * @return
	 */
	@Override
	public CharSequence getPageTitle(int position){
		return "";
	}

	/**
	 * 实例化frament出来
	 * @param container 容器
	 * @param position 位置
	 * @return
	 */
	@Override
	public Object instantiateItem(ViewGroup container,int position){
		return super.instantiateItem(container,position);
	}
}
