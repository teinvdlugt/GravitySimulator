package com.teinproductions.tein.gravitysimulator;

import java.util.ArrayList;

public class Luminary {

    public static final double G = 0.000000000066738480;

    private double radius;
    private double mass;
    private double x, y;
    private double velocityX = 0, velocityY = 0;

    public Luminary(double radius, double mass, double x, double y) {
        this.radius = radius;
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

    public static void move(ArrayList<Luminary> luminaries, double time) {
        if (luminaries.size() == 1) {
            luminaries.get(0).addPos(luminaries.get(0).getVelocityX() * time, luminaries.get(0).getVelocityY() * time);
        }

        for (int i = 0; i < luminaries.size(); i++) {
            for (int j = i + 1; j < luminaries.size(); j++) {
                // Distance between the two objects:
                final double diffX = luminaries.get(j).getX() - luminaries.get(i).getX();
                final double diffY = luminaries.get(j).getY() - luminaries.get(i).getY();
                final double r = Math.sqrt(diffX * diffX + diffY * diffY);

                // Mass of the two objects:
                double m1 = luminaries.get(i).mass;
                double m2 = luminaries.get(j).mass;

                // Force between the two objects:
                final double F = G * m1 * m2 / r / r;

                // Acceleration of the two objects:
                // a = F / m
                final double a1 = F / m1;
                final double a2 = F / m2;

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
}
