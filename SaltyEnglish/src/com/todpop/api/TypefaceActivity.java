package com.todpop.api;

import android.app.Activity;
import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;

public class TypefaceActivity extends Activity {
    private static Typeface mTypeface;

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);

        if (TypefaceActivity.mTypeface == null)
            TypefaceActivity.mTypeface = Typeface.createFromAsset(getAssets(), "fonts/roboto_regular_nanum_bold.ttf.mp3");

        ViewGroup root = (ViewGroup) findViewById(android.R.id.content);
        setGlobalFont(root);
    }
    public static void setGlobalFont(ViewGroup root) {
        for (int i = 0; i < root.getChildCount(); i++) {
            View child = root.getChildAt(i);
            setFont(child);
        }
    }
    public static void setFont(View v){
        if (v instanceof TextView){
            ((TextView)v).setTypeface(mTypeface);
        }
        else if(v instanceof RadioButton){
        	((RadioButton)v).setTypeface(mTypeface);
        }
        else if (v instanceof ViewGroup)
            setGlobalFont((ViewGroup)v);
    }
}