package com.teinproductions.tein.gravitysimulator;


import android.util.Log;

import java.util.ArrayList;

public class Luminary {

    public static final double G = 0.000000000066738480;

    private int radius;
    private double x, y;
    private double velocityX = 0, velocityY = 0;

    public Luminary(int radius, double x, double y) {
        this.radius = radius;
        this.x = x;
        this.y = y;
    }

    public Luminary(int radius, double x, double y, double velocityX, double velocityY) {
        this.radius = radius;
        this.x = x;
        this.y = y;
        this.velocityX = velocityX;
        this.velocityY = velocityY;
    }

    public void addVelocity(double addX, double addY) {
        velocityX += addX;
        velocityY += addY;
    }

    public double weight() {
        // m = r^3
        return 10000 * radius * radius;
    }

    public int getRadius() {
        return radius;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public void addPos(double addX, double addY) {
        x += addX;
        y += addY;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getVelocityX() {
        return velocityX;
    }

    public double getVelocityY() {
        return velocityY;
    }

    public static void move(ArrayList<Luminary> luminaries, double time) {
        for (int i = 0; i < luminaries.size(); i++) {
            for (int j = i + 1; j < luminaries.size(); j++) {
                // Distance between the two objects:
                final double diffX = luminaries.get(j).getX() - luminaries.get(i).getX();
                final double diffY = luminaries.get(j).getY() - luminaries.get(i).getY();
                final double r = Math.sqrt(diffX * diffX + diffY * diffY);

                // Mass of the two objects:
                double m1 = luminaries.get(i).weight();
                double m2 = luminaries.get(j).weight();

                // Force between the two objects:
                final double F = G * m1 * m2 / r / r;

                Log.d("SpaceX", "Force: " + F);

                // Acceleration of the two objects:
                // a = F / m
                final double a1 = F / m1;
                final double a2 = F / m2;

                // Acceleration per axis:
                final double aX1 = a1 * diffX / r;
                final double aY1 = a1 * diffY / r;
                final double aX2 = a2 * -diffX / r;
                final double aY2 = a2 * -diffY / r;

                // Gain of velocity during the given time:
                final double v1 = .5 * a1 * time;
                final double v2 = .5 * a2 * time;

                // Average gain of velocity per axis:
                final double vX1 = v1 * diffX / r;
                final double vY1 = v1 * diffY / r;
                final double vX2 = v2 * -diffX / r;
                final double vY2 = v2 * -diffY / r;

                // Gain of X and Y positions:
                final double X1 = luminaries.get(i).getVelocityX() * time + vX1 * time;
                final double Y1 = luminaries.get(i).getVelocityY() * time + vY1 * time;
                final double X2 = luminaries.get(j).getVelocityX() * time + vX2 * time;
                final double Y2 = luminaries.get(j).getVelocityY() * time + vY2 * time;

                // Add the positions:
                luminaries.get(i).addPos(X1, Y1);
                luminaries.get(j).addPos(X2, Y2);

                // Add the velocities:
                luminaries.get(i).addVelocity(vX1, vY1);
                luminaries.get(j).addVelocity(vX2, vY2);
            }
        }


        /*for (Luminary luminary : luminaries) {
            double moveX = luminary.getVelocityX() * time;
            double moveY = luminary.getVelocityY() * time;
            luminary.addPos((int) moveX, (int) moveY);
        }*/
    }
}
