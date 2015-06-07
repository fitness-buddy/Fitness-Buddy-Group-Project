package com.strengthcoach.strengthcoach.customui;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by varungupta on 6/6/15.
 */

public class RobotoTextView  extends TextView {
    public RobotoTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }
    public RobotoTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    public RobotoTextView(Context context) {
        super(context);
    }
    public void setTypeface(Typeface tf, int style) {
        if (style == Typeface.BOLD) {
            super.setTypeface(Typeface.createFromAsset(getContext().getAssets(),    "fonts/Roboto-Bold.ttf"));
        }
        else if(style == Typeface.ITALIC)
        {
            super.setTypeface(Typeface.createFromAsset(getContext().getAssets(), "fonts/Roboto-Italic.ttf"));
        }
        else
        {
            super.setTypeface(Typeface.createFromAsset(getContext().getAssets(), "fonts/Roboto-Regular.ttf"));
        }
    }
}