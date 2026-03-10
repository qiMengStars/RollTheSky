package com.xixi.rollthesky.roll;

import net.minecraft.client.settings.KeyBinding;
import org.junit.jupiter.api.Test;
import org.lwjgl.input.Keyboard;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RollKeyBindingsTest {
    @Test
    void defaultBarrelRollKeysAreUnbound() throws Exception {
        Field leftField = RollKeyBindings.class.getDeclaredField("ROLL_LEFT");
        leftField.setAccessible(true);
        KeyBinding left = (KeyBinding) leftField.get(null);

        Field rightField = RollKeyBindings.class.getDeclaredField("ROLL_RIGHT");
        rightField.setAccessible(true);
        KeyBinding right = (KeyBinding) rightField.get(null);

        assertEquals(Keyboard.KEY_NONE, left.getKeyCode());
        assertEquals(Keyboard.KEY_NONE, right.getKeyCode());
    }
}
