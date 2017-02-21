package com.ekplate.android.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by Rahul on 9/1/2015.
 */
public class BariolLightItalicTextView extends TextView {
    public BariolLightItalicTextView(Context context) {
        super(context);
        Typeface face=Typeface.createFromAsset(context.getAssets(), "Bariol_Light_Italic.otf");
        this.setTypeface(face);
    }

    public BariolLightItalicTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        Typeface face=Typeface.createFromAsset(context.getAssets(), "Bariol_Light_Italic.otf");
        this.setTypeface(face);
    }

    public BariolLightItalicTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        Typeface face=Typeface.createFromAsset(context.getAssets(), "Bariol_Light_Italic.otf");
        this.setTypeface(face);
    }

    protected void onDraw (Canvas canvas) {
        super.onDraw(canvas);

    }
}
