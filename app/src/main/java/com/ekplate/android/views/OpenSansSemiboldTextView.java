package com.ekplate.android.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by Rahul on 7/18/2015.
 */
public class OpenSansSemiboldTextView extends TextView {
    public OpenSansSemiboldTextView(Context context) {
        super(context);
        Typeface face=Typeface.createFromAsset(context.getAssets(), "OpenSans-Semibold.ttf");
        this.setTypeface(face);
    }

    public OpenSansSemiboldTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        Typeface face=Typeface.createFromAsset(context.getAssets(), "OpenSans-Semibold.ttf");
        this.setTypeface(face);
    }

    public OpenSansSemiboldTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        Typeface face=Typeface.createFromAsset(context.getAssets(), "OpenSans-Semibold.ttf");
        this.setTypeface(face);
    }

    protected void onDraw (Canvas canvas) {
        super.onDraw(canvas);


    }
}
