package com.ondeline.InstaurantCliente;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class VerticalViewPager extends ViewPager {

    public VerticalViewPager(@NonNull Context context) {
        super(context);
        init();
    }

    public VerticalViewPager(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init(){
        setPageTransformer(true, new VerticalPage());
        setOverScrollMode(OVER_SCROLL_NEVER);
    }

    private MotionEvent getItercambioXY(MotionEvent ev){
        float width = getWidth();
        float height = getHeight();

        float newX = (ev.getY() / height) * width;
        float newY = (ev.getX() / width) * height;

        ev.setLocation(newX, newY);
        return ev;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        boolean intercepted = super.onInterceptTouchEvent(getItercambioXY(ev));
        getItercambioXY(ev);
        return intercepted;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        return super.onTouchEvent(getItercambioXY(ev));
    }

    private class VerticalPage implements ViewPager.PageTransformer{

        @Override
        public void transformPage(@NonNull View view, float position) {
            if(position < -1){
                view.setAlpha(0);
            }  else if(position <= 1){
                view.setAlpha(1);
                view.setTranslationX(view.getWidth() * (-position));
                float yPosition  = position * view.getHeight();
                view.setTranslationY(yPosition);
            } else{
                view.setAlpha(0);
            }
        }
    }
}
