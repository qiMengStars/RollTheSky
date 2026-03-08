package com.xixi.rollthesky.roll;

public final class InputSmoother {
    private double value;

    public double smooth(double input, double amount) {
        double clamped = clamp(amount, 0.0, 1.0);
        value += (input - value) * clamped;
        return value;
    }

    public void clear() {
        value = 0.0;
    }

    private static double clamp(double value, double min, double max) {
        if (value < min) {
            return min;
        }
        if (value > max) {
            return max;
        }
        return value;
    }
}
