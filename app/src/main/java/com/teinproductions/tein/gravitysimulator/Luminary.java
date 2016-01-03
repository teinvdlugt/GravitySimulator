package com.teinproductions.tein.gravitysimulator;

import java.util.ArrayList;

public class Luminary {

    public static final double G = 0.000000000066738480;

    private double radius;
    private double mass;
    private double x, y;
    private double velocityX = 0, velocityY = 0;
    private double aX = 0, aY = 0;

    public Luminary(double radius, double mass, double x, double y) {
        this.radius = radius;
        this.mass = mass;
        this.x = x;
        this.y = y;
    }

    public Luminary(double radius, double mass, double x, double y, double velocityX, double velocityY) {
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

    public double getRadius() {
        return radius;
    }

    public void setRadius(double radius) {
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

    public double getMass() {
        return mass;
    }

    public void setMass(double mass) {
        this.mass = mass;
    }

    public void addAcceleration(double aX, double aY) {
        this.aX += aX;
        this.aY += aY;
    }

    public void addMomentum(double px, double py) {
        addVelocity(px / mass, py / mass);
    }

    public boolean collidesWith(Luminary other) {
        final double diffX = x - other.x;
        final double diffY = y - other.y;
        final double distance = Math.sqrt(diffX * diffX + diffY * diffY);
        return distance <= radius + other.radius;
    }

    /**
     * Use the {@code acceleration} of the {@code Luminary} to change its
     * position and velocity during a given time. After that, reset the
     * {@code} acceleration to 0.
     *
     * @param time The time to use in the calculations.
     */
    public void commit(double time) {
        // Gain of velocity during the given time:
        final double dvx = .5 * aX * time;
        final double dvy = .5 * aY * time;

        // Gain of X and Y positions:
        final double dx = velocityX * time + dvx * time;
        final double dy = velocityY * time + dvy * time;

        addPos(dx, dy);
        addVelocity(dvx, dvy);

        aX = aY = 0;
    }

    public static void move(ArrayList<Luminary> luminaries, double time) {
        if (luminaries.size() == 1) {
            luminaries.get(0).addPos(luminaries.get(0).getVelocityX() * time, luminaries.get(0).getVelocityY() * time);
        }

        for (int i = 0; i < luminaries.size(); i++) {
            for (int j = i + 1; j < luminaries.size(); j++) {
                Luminary l1 = luminaries.get(i);
                Luminary l2 = luminaries.get(j);

                // Distance between the two objects:
                final double diffX = l2.x - l1.x;
                final double diffY = l2.y - l1.y;
                final double r = Math.sqrt(diffX * diffX + diffY * diffY);

                /*boolean nowTouching = l1.collidesWith(l2);*/

                // Mass of the two objects:
                double m1 = l1.mass;
                double m2 = l2.mass;

                // Force between the two objects:
                final double F = G * m1 * m2 / r / r;

                // Acceleration of the two objects:
                final double a1 = F / m1;
                final double a2 = F / m2;

                // Acceleration per axis:
                final double aX1 = a1 * diffX / r;
                final double aY1 = a1 * diffY / r;
                final double aX2 = a2 * -diffX / r;
                final double aY2 = a2 * -diffY / r;

                // Add the accelerations:
                l1.addAcceleration(aX1, aY1);
                l2.addAcceleration(aX2, aY2);

                /*// Check if collision happened
                if (!nowTouching && l1.collidesWith(l2)) {
                    //l1.velocityX = l1.velocityY = l2.velocityX = l2.velocityY = 0;
                    final double diffX2 = l2.getX() - l1.getX();
                    final double diffY2 = l2.getY() - l1.getY();
                    final double r2 = Math.sqrt(diffX * diffX + diffY * diffY);
                }*/
            }
        }

        for (Luminary luminary : luminaries) {
            luminary.commit(time);
        }
    }

    public static void collide(ArrayList<Luminary> luminaries) {
        for (int i = 0; i < luminaries.size(); i++) {
            for (int j = i + 1; j < luminaries.size(); j++) {
                Luminary l1 = luminaries.get(i);
                Luminary l2 = luminaries.get(j);

                // Distance between the two objects:
                final double diffX = l2.x - l1.x;
                final double diffY = l2.y - l1.y;
                final double r = Math.sqrt(diffX * diffX + diffY * diffY);

                // Check if the two Luminaries touch each other:
                final boolean colliding = r <= l1.radius + l2.radius;
                if (!colliding) continue;

                // Calculate the angle theta at which they collide:
                final double th = Math.atan2(diffY, diffX);

                // Backup velocities
                final double vX1 = l1.velocityX;
                final double vY1 = l1.velocityY;
                final double vX2 = l2.velocityX;
                final double vY2 = l2.velocityY;

                // Carry momentum from l1 to l2
                double px1 = vX1 * l1.mass;
                double py1 = vY1 * l1.mass;
                double dp2 = Math.cos(th) * px1 + Math.cos(th - Math.PI / 2) * py1;
                double dpx2 = Math.cos(th) * dp2;
                double dpy2 = Math.sin(th) * dp2;
                l1.addMomentum(-dpx2, -dpy2);
                l2.addMomentum(dpx2, dpy2);

                // Carry momentum from l2 to l1
                double px2 = vX2 * l2.mass;
                double dp1 = Math.cos(th) * px2;
                double py2 = vY2 * l2.mass;
                dp1 += Math.cos(th - Math.PI / 2) * py2;
                double dpx1 = Math.sin(th) * dp1;
                double dpy1 = Math.cos(th) * dp1;
                l1.addMomentum(dpx1, dpy1);
                l2.addMomentum(-dpx1, -dpy1);
            }
        }
    }

    public static void mergeColliding(ArrayList<Luminary> luminaries) {
        for (int i = 0; i < luminaries.size(); i++) {
            for (int j = i + 1; j < luminaries.size(); j++) {
                final double diffX = luminaries.get(j).x - luminaries.get(i).x;
                final double diffY = luminaries.get(j).y - luminaries.get(i).y;
                final double distance = Math.sqrt(diffX * diffX + diffY * diffY);

                final double radius1 = luminaries.get(i).radius;
                final double radius2 = luminaries.get(j).radius;

                if (distance < radius1 || distance < radius2) {
                    final int radius = (int) Math.sqrt(radius1 * radius1 + radius2 * radius2);
                    final double mass1 = luminaries.get(i).mass;
                    final double mass2 = luminaries.get(j).mass;
                    double X = (luminaries.get(i).x * mass1 + luminaries.get(j).x * mass2) / (mass1 + mass2);
                    double Y = (luminaries.get(i).y * mass1 + luminaries.get(j).y * mass2) / (mass1 + mass2);
                    double vX = (luminaries.get(i).velocityX * mass1 + luminaries.get(j).velocityX * mass2) / (mass1 + mass2);
                    double vY = (luminaries.get(i).velocityY * mass1 + luminaries.get(j).velocityY * mass2) / (mass1 + mass2);

                    luminaries.get(i).setRadius(radius);
                    luminaries.get(i).velocityX = vX;
                    luminaries.get(i).velocityY = vY;
                    luminaries.get(i).setX(X);
                    luminaries.get(i).setY(Y);
                    luminaries.get(i).setMass(mass1 + mass2);
                    luminaries.remove(j);
                }
            }
        }
    }

    /*public static void stopColliding(ArrayList<Luminary> luminaries) {
        for (int i = 0; i < luminaries.size(); i++) {
            for (int j = 0; j < luminaries.size(); j++) {
                final double diffX = luminaries.get(j).x - luminaries.get(i).x;
                final double diffY = luminaries.get(j).y - luminaries.get(i).y;
                final double distance = Math.sqrt(diffX * diffX + diffY * diffY);

                final double radius1 = luminaries.get(i).radius;
                final double radius2 = luminaries.get(j).radius;

                if (distance <= radius1 + radius2) {

                }
            }
        }
    }*/
}
