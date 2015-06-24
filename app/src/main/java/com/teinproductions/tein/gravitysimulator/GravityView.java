package com.teinproductions.tein.gravitysimulator;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;

public class GravityView extends View {

    private boolean running = false;

    private ArrayList<Luminary> luminaries = new ArrayList<>();
    private final Object lock = new Object();
    private Paint paint = new Paint();

    private int radius = 20;

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        paint.setColor(getResources().getColor(R.color.luminaryColor));

        for (Luminary luminary : luminaries) {
            if (luminary.getRadius() == 0) {
                canvas.drawPoint((float) luminary.getX(), (float) luminary.getY(), paint);
            } else {
                canvas.drawCircle((float) luminary.getX(), (float) luminary.getY(), luminary.getRadius(), paint);
            }
        }
    }

    public void begin() {
        running = true;
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (running) {
                    synchronized (lock) {
                        Luminary.move(luminaries, 1000);
                    }
                    postInvalidate();
                    try {
                        Thread.sleep(1);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    public void stop() {
        running = false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            float x = (int) event.getX();
            float y = (int) event.getY();

            synchronized (lock) {
                luminaries.add(new Luminary(radius, x, y));
                invalidate();
            }
            return true;
        }
        return super.onTouchEvent(event);
    }

    public boolean isRunning() {
        return running;
    }

    public int getRadius() {
        return radius;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }

    public GravityView(Context context) {
        super(context);
    }

    public GravityView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public GravityView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
}
