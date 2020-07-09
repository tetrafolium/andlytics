
package com.github.andlyticsproject.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.widget.Gallery;

@SuppressWarnings("deprecation")
public class ChartGallery extends Gallery {

//  private static String LOG_TAG=ChartGallery.class.toString();
private static final float SWIPE_MIN_DISTANCE = 100;

private boolean interceptTouchEvents;

private boolean useMultiImageFling;

private boolean ignoreLayoutCalls;

private boolean allowChangePageSliding = true;

public ChartGallery(final Context context, final AttributeSet attrs,
                    final int defStyle) {
	super(context, attrs, defStyle);
}

public ChartGallery(final Context context, final AttributeSet attrs) {
	super(context, attrs);
}

public ChartGallery(final Context context) {
	super(context);
}

@Override
protected void onLayout(final boolean changed, final int l, final int t,
                        final int r, final int b) {
	if (!isIgnoreLayoutCalls())
		super.onLayout(changed, l, t, r, b);
}

@Override
public boolean onInterceptTouchEvent(final MotionEvent ev) {

	if (interceptTouchEvents) {
		return true;
	}
	return false;
}

public boolean onFling(final MotionEvent e1, final MotionEvent e2,
                       final float velocityX, final float velocityY) {

	if (useMultiImageFling) {
		return super.onFling(e1, e2, velocityX, velocityY);

	} else {
		boolean result = false;

		if (Math.abs(velocityX) > 900) {

			if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE && velocityX <= 0) {

				// hack - send event to simulate right key press
				KeyEvent rightKey =
					new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DPAD_RIGHT);
				onKeyDown(KeyEvent.KEYCODE_DPAD_RIGHT, rightKey);

				rightKey =
					new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_DPAD_RIGHT);
				onKeyUp(KeyEvent.KEYCODE_DPAD_RIGHT, rightKey);

				result = true;

			} else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE) {

				// hack - send event to simulate left key press
				KeyEvent leftKey =
					new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DPAD_LEFT);
				onKeyDown(KeyEvent.KEYCODE_DPAD_LEFT, leftKey);

				leftKey =
					new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_DPAD_LEFT);
				onKeyUp(KeyEvent.KEYCODE_DPAD_LEFT, leftKey);

				result = true;
			}
		}

		return result;
	}
}

@Override
public boolean onScroll(final MotionEvent e1, final MotionEvent e2,
                        final float distanceX, final float distanceY) {
	if ((!allowChangePageSliding) && (getSelectedView() != null && getSelectedView().getTag() != null)) {
		int[] tag = (int[])getSelectedView().getTag();
		if (distanceX < 0 && tag[1] <= 1)
			return true;
		if (distanceX > 0 && tag[1] >= (tag[2] - 1))
			return true;
	}
	return super.onScroll(e1, e2, distanceX, distanceY);
}

public void setInterceptTouchEvents(final boolean interceptTouchEvents) {
	this.interceptTouchEvents = interceptTouchEvents;
}

public boolean isInterceptTouchEvents() {
	return interceptTouchEvents;
}

public void setUseMultiImageFling(final boolean useMultiImageFling) {
	this.useMultiImageFling = useMultiImageFling;
}

public boolean isUseMultiImageFling() {
	return useMultiImageFling;
}

public void setIgnoreLayoutCalls(final boolean ignoreLayoutCalls) {
	this.ignoreLayoutCalls = ignoreLayoutCalls;
}

public boolean isIgnoreLayoutCalls() {
	return ignoreLayoutCalls;
}

public void setAllowChangePageSliding(final boolean allowChangePageSliding) {
	this.allowChangePageSliding = allowChangePageSliding;
}
}
