package com.bignerdranch.android.newprojectdemo.front_ui;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;

import com.bignerdranch.android.newprojectdemo.R;

/**
 * Created by nyanye on 7/23/16.
 */
public class LogoImage extends View {



        private final Drawable logo;

        public LogoImage(Context context) {
            super(context);
            logo = context.getResources().getDrawable(R.mipmap.image_sharing);
            setBackgroundDrawable(logo);
        }

//        public LogoImage(Context context, AttributeSet attrs) {
//            super(context, attrs);
//            logo = context.getResources().getDrawable(R.drawable.banner);
//            setBackgroundDrawable(logo);
//        }
//
//        public LogoImage(Context context, AttributeSet attrs, int defStyle) {
//            super(context, attrs, defStyle);
//            logo = context.getResources().getDrawable(R.drawable.banner);
//            setBackgroundDrawable(logo);
//        }

        @Override protected void onMeasure(int widthMeasureSpec,
                                           int heightMeasureSpec) {
            int width = MeasureSpec.getSize(widthMeasureSpec);
            int height = width * logo.getIntrinsicHeight() / logo.getIntrinsicWidth();
            setMeasuredDimension(width, height);
        }

}
