package TabFragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;

/**
 *好友社交fragment
 * Created by SmileSB101 on 2016/9/25.
 */
public class friendMusic extends Fragment{
	public static friendMusic getInstance(int position)
	{
		friendMusic tabPageFragment = new friendMusic();
		Bundle bundle = new Bundle();
		bundle.putInt("好友社交", position);
		tabPageFragment.setArguments(bundle);
		return tabPageFragment;
	}
}
