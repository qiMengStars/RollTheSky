package com.xixi.rollthesky.roll;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RollStateBarrelRollTest {
    @Test
    void startsAndAdvancesPositiveBarrelRollWithoutTouchingBaseRoll() {
        RollState state = new RollState();

        state.startBarrelRoll(1);
        state.tickBarrelRoll(0.25f);

        assertTrue(state.isBarrelRolling());
        assertEquals(0.0f, state.getRawRoll(), 0.0001f);
        assertEquals(56.25f, state.getRawBarrelRoll(), 0.0001f);
        assertEquals((float) Math.sin(Math.PI * 0.25), state.getRawBarrelRollDodgeFactor(), 0.0001f);
    }

    @Test
    void completesBarrelRollAtFullRotation() {
        RollState state = new RollState();

        state.startBarrelRoll(1);
        for (int i = 0; i < 4; i++) {
            state.tickBarrelRoll(0.25f);
        }

        assertFalse(state.isBarrelRolling());
        assertEquals(360.0f, state.getRawBarrelRoll(), 0.0001f);
        assertEquals(0.0f, state.getRawBarrelRollDodgeFactor(), 0.0001f);
    }

    @Test
    void startsNegativeBarrelRollForLeftDirection() {
        RollState state = new RollState();

        state.startBarrelRoll(-1);
        state.tickBarrelRoll(1.0f / 3.0f);

        assertTrue(state.isBarrelRolling());
        assertEquals(-93.3333f, state.getRawBarrelRoll(), 0.0002f);
    }

    @Test
    void ignoresNewStartWhileAlreadyRolling() {
        RollState state = new RollState();

        state.startBarrelRoll(1);
        state.tickBarrelRoll(0.25f);
        state.startBarrelRoll(-1);
        state.tickBarrelRoll(0.25f);

        assertEquals(180.0f, state.getRawBarrelRoll(), 0.0001f);
    }
}
