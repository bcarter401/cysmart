package com.cypress.cysmart.utils;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.widget.LinearLayout;
/**
 * Custom linear layout for carouselView
 */
public class CarouselLinearLayout extends LinearLayout {
	private float scale = 1.0f;

	public CarouselLinearLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public CarouselLinearLayout(Context context) {
		super(context);
	}

	public void setScaleBoth(float scale)
	{
		this.scale = scale;
		this.invalidate(); 	
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		int w = this.getWidth();
		int h = this.getHeight();
		canvas.scale(scale, scale, w/2, h/2);
		super.onDraw(canvas);
	}
}
