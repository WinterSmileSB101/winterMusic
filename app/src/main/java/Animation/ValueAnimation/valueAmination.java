package Animation.ValueAnimation;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.view.View;
import android.view.animation.AnimationSet;
import android.view.animation.LinearInterpolator;

/**
 * 属性动画静态类
 * Created by SmileSB101 on 2016/9/26.
 */
public class valueAmination{
	/**
	 * 圆形移动动画
	 * @param v 移动的目标
	 */
	public static AnimatorSet circleMoveAni(View v,View scanDown)
	{
		int[] location = new int[2];
		v.getLocationOnScreen(location);
		int[] circlePoint = new int[2];
		circlePoint[0] = location[0]+v.getWidth()/2;
		circlePoint[1] = location[1]+20;
		ObjectAnimator horX = ObjectAnimator.ofFloat(v,"scaleX",1f,0.5f,1f);
		horX.setRepeatCount(ValueAnimator.INFINITE);
		ObjectAnimator horY = ObjectAnimator.ofFloat(v,"scaleY",1f,0.5f,1f);
		horY.setRepeatCount(ValueAnimator.INFINITE);
		scanDown.setVisibility(View.VISIBLE);
		ObjectAnimator scan_down = ObjectAnimator.ofFloat(scanDown,"translationY",scanDown.getTranslationX(),scanDown.getTranslationX()+200);
		scan_down.setRepeatCount(ValueAnimator.INFINITE);

		AnimatorSet animatorSet = new AnimatorSet();
		animatorSet.play(horX).with(horY).with(scan_down);
		//animatorSet.start();
		return animatorSet;
	}
}
