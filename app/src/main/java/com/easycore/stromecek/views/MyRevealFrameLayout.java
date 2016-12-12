package com.easycore.stromecek.views;


import android.content.Context;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.MotionEvent;
import io.codetail.widget.RevealFrameLayout;

public class MyRevealFrameLayout extends RevealFrameLayout {

    private Point lastTouch;

    public MyRevealFrameLayout(Context context) {
        super(context);
    }

    public MyRevealFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyRevealFrameLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {

        lastTouch = new Point((int) ev.getX(), (int) ev.getY());

        return super.onInterceptTouchEvent(ev);
    }

    public Point getLastTouchPoint() {
        return lastTouch;
    }
}
