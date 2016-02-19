package com.letv.framework.ui;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;

public class LeImageView extends ImageView {
	
	public LeImageView(Context context){
		super(context);
	}
	
	public LeImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public LeImageView(Context context, AttributeSet attrs, int style) {
		super(context, attrs, style);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	protected void onDetachedFromWindow(){
		super.onDetachedFromWindow();
		Drawable drawable = this.getBackground();
		if(drawable != null)
			drawable.setCallback(null);
		drawable = this.getDrawable();
		if(drawable != null)
			drawable.setCallback(null);
	}
}
