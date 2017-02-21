package com.specisl.ResideMenu.Views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by Rahul on 8/25/2015.
 */
public class BariolRegularTextView extends TextView {

    public BariolRegularTextView(Context context) {
        super(context);
        Typeface face=Typeface.createFromAsset(context.getAssets(), "BariolRegular.ttf");
        this.setTypeface(face);
    }

    public BariolRegularTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        Typeface face=Typeface.createFromAsset(context.getAssets(), "BariolRegular.ttf");
        this.setTypeface(face);
    }

    public BariolRegularTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        Typeface face=Typeface.createFromAsset(context.getAssets(), "BariolRegular.ttf");
        this.setTypeface(face);
    }

    protected void onDraw (Canvas canvas) {
        super.onDraw(canvas);

    }

}
