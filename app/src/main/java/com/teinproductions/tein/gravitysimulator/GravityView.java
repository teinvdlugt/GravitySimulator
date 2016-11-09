package com.teinproductions.tein.gravitysimulator;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;

public class GravityView extends View {

    private boolean running = false;

    private ArrayList<Luminary> luminaries = new ArrayList<>();
    private final Object lock = new Object();
    /**
     * Paint used to draw Luminaries
     */
    private Paint luminaryPaint = new Paint();
    /**
     * Paint used to draw the arrow indicating the velocity of a luminary,
     * shown when a new luminary is being created by the user.
     */
    private Paint arrowPaint = new Paint();

    private int radius = 20;
    private double mass = 100000000;

    /**
     * Used to draw the velocity vector. It is recycled so that
     * there are no allocations in onDraw()
     */
    private Path arrowPath = new Path();
    /**
     * The width of the velocity vector.
     */
    private float arrowWidth;
    private final double _90deg = Math.PI / 4;

    /**
     * Every pixel is assumed to be one meter. If it would not be fast forwarded,
     * it would go very slow. This is the rate at which the animation is fast
     * forwarded.
     */
    private int fastForwardRate = 100000;
    /**
     * 'Second per frame', the inverse of the frame rate.
     */
    private double spf = 1. / 60;

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (drawing != null) {
            float x = (float) drawing.getX();
            float y = (float) drawing.getY();
            float velX = (float) drawing.getVelocityX();
            float velY = (float) drawing.getVelocityY();

            arrowPath.reset();
            arrowPath.moveTo(x, y);
            arrowPath.lineTo(x + velX, y + velY);
            /*// The angle that the arrow makes with the horizontal axis:
            double angle = Math.atan(velY / velX);
            float dx = (float) Math.cos(_90deg - angle) * arrowWidth;
            float dy = (float) Math.sin(_90deg - angle) * arrowWidth;
            arrowPath.rLineTo(dx, dy);
            arrowPath.lineTo(x + velX + dx, y + velY - dy);
            arrowPath.rLineTo(-2 * dx, -2 * dy);
            arrowPath.lineTo(x - dx, y - dy);
            arrowPath.close();*/ // TODO make arrow point
            canvas.drawPath(arrowPath, arrowPaint);

            canvas.drawCircle((float) drawing.getX(), (float) drawing.getY(), (float) drawing.getRadius(), luminaryPaint);
        }

        synchronized (lock) {
            for (Luminary luminary : luminaries) {
                if (luminary.getRadius() == 0) {
                    canvas.drawPoint((float) luminary.getX(), (float) luminary.getY(), luminaryPaint);
                } else {
                    canvas.drawCircle((float) luminary.getX(), (float) luminary.getY(), (float) luminary.getRadius(), luminaryPaint);
                }
            }
        }
    }

    public void begin() {
        running = true;
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (running) {
                    long before = System.currentTimeMillis();
                    synchronized (lock) {
                        Luminary.move(luminaries, fastForwardRate * spf);
                        removeLuminariesOutsideField();
                        Luminary.collide(luminaries);
                    }
                    postInvalidate();
                    long time = System.currentTimeMillis() - before;
                    try {
                        long sleep = (long) (1000 * spf - time);
                        Log.d("The Value Of Time", "Sleepy time: " + sleep);
                        Thread.sleep(Math.max((long) (1000 * spf - time), 0));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    private void removeLuminariesOutsideField() {
        for (int i = 0; i < luminaries.size(); i++) {
            Luminary luminary = luminaries.get(i);
            if (luminary.getX() + luminary.getRadius() < 0
                    || luminary.getX() - luminary.getRadius() > getWidth()
                    || luminary.getY() + luminary.getRadius() < 0
                    || luminary.getY() - luminary.getRadius() > getHeight()) {
                luminaries.remove(i);
            }
        }
    }

    public void stop() {
        running = false;
    }

    /**
     * The Luminary which is currently being drawn by the user.
     */
    private Luminary drawing;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = (int) event.getX();
        float y = (int) event.getY();
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            drawing = new Luminary(radius, mass, x, y);
            if (!running) invalidate();
            return true;
        } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
            double dx = x - drawing.getX();
            double dy = y - drawing.getY();
            drawing.setVelocityX(dx);
            drawing.setVelocityY(dy);
            if (!running) invalidate();
            return true;
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            if (drawing != null) {
                // I want the objects to move drawing.velocity() pixels per second,
                // but taking into account the fastForwardRate for the user to be
                // able to estimate how fast the object is going to move.
                drawing.setVelocityX(drawing.getVelocityX() / fastForwardRate);
                drawing.setVelocityY(drawing.getVelocityY() / fastForwardRate);
                synchronized (lock) {
                    luminaries.add(drawing);
                    drawing = null;
                    invalidate();
                }
                return true;
            }
        }
        return super.onTouchEvent(event);
    }

    public void restart() {
        synchronized (lock) {
            luminaries.clear();
            invalidate();
        }
    }

    private void init() {
        setBackgroundColor(getResources().getColor(R.color.backgroundColor));
        luminaryPaint.setColor(getResources().getColor(R.color.luminaryColor));
        luminaryPaint.setAntiAlias(true);
        arrowPaint.setColor(getResources().getColor(R.color.velocityArrowColor));
        arrowPaint.setAntiAlias(true);
        arrowWidth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, getResources().getDisplayMetrics());
        arrowPaint.setStrokeWidth(arrowWidth);
        arrowPaint.setStyle(Paint.Style.STROKE);
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

    public double getMass() {
        return mass;
    }

    public void setMass(double mass) {
        this.mass = mass;
    }

    public GravityView(Context context) {
        super(context);
        init();
    }

    public GravityView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public GravityView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }
}
