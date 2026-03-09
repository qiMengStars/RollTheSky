package com.xixi.rollthesky.roll;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FlightControlInputTest {
    @Test
    void defaultModeMapsMouseToRollAndKeysToYaw() {
        FlightControlInput.Axes axes = FlightControlInput.resolve(
                3.0,
                6.0,
                2.0,
                false,
                false,
                false,
                false
        );

        assertEquals(3.0, axes.getPitch(), 0.0001);
        assertEquals(2.0, axes.getYaw(), 0.0001);
        assertEquals(6.0, axes.getRoll(), 0.0001);
    }

    @Test
    void switchedModeMapsMouseToYawAndKeysToRoll() {
        FlightControlInput.Axes axes = FlightControlInput.resolve(
                1.5,
                5.0,
                -3.0,
                true,
                false,
                false,
                false
        );

        assertEquals(1.5, axes.getPitch(), 0.0001);
        assertEquals(5.0, axes.getYaw(), 0.0001);
        assertEquals(-3.0, axes.getRoll(), 0.0001);
    }

    @Test
    void invertFlagsApplyAfterAxisMapping() {
        FlightControlInput.Axes axes = FlightControlInput.resolve(
                2.0,
                4.0,
                3.0,
                true,
                true,
                true,
                true
        );

        assertEquals(-2.0, axes.getPitch(), 0.0001);
        assertEquals(-4.0, axes.getYaw(), 0.0001);
        assertEquals(-3.0, axes.getRoll(), 0.0001);
    }
}
