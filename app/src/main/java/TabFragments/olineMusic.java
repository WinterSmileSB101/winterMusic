package TabFragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;

/**
 * 网络音乐fragment
 * Created by SmileSB101 on 2016/9/25.
 */
public class olineMusic extends Fragment{
	public static olineMusic getInstance(int position)
	{
		olineMusic tabPageFragment = new olineMusic();
		Bundle bundle = new Bundle();
		bundle.putInt("网络音乐", position);
		tabPageFragment.setArguments(bundle);
		return tabPageFragment;
	}
}
