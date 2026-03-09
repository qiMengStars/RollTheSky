package com.xixi.rollthesky.roll;

import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import org.lwjgl.input.Keyboard;

public final class RollKeyBindings {
    private static final String CATEGORY = "key.categories.rollthesky";

    private static final KeyBinding ROLL_LEFT = new KeyBinding("key.rollthesky.roll_left", Keyboard.KEY_Z, CATEGORY);
    private static final KeyBinding ROLL_RIGHT = new KeyBinding("key.rollthesky.roll_right", Keyboard.KEY_C, CATEGORY);

    private static boolean registered;

    private RollKeyBindings() {
    }

    public static void register() {
        if (registered) {
            return;
        }

        ClientRegistry.registerKeyBinding(ROLL_LEFT);
        ClientRegistry.registerKeyBinding(ROLL_RIGHT);
        registered = true;
    }

    public static int consumeBarrelRollDirection() {
        int direction = 0;

        while (ROLL_LEFT.isPressed()) {
            direction--;
        }
        while (ROLL_RIGHT.isPressed()) {
            direction++;
        }

        if (direction < 0) {
            return -1;
        }
        if (direction > 0) {
            return 1;
        }
        return 0;
    }
}
