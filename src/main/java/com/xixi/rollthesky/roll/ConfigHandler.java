package com.xixi.rollthesky.roll;

import com.xixi.rollthesky.Tags;
import net.minecraftforge.common.config.Configuration;

import java.io.File;

public final class ConfigHandler {
    public static boolean modEnabled = true;
    public static boolean switchRollAndYaw = false;
    public static boolean momentumBasedMouse = false;
    public static boolean invertPitch = false;
    public static boolean invertYaw = false;
    public static boolean invertRoll = false;
    public static boolean invertVisualRoll = true;
    public static boolean disableWhenSubmerged = true;

    // Matches DABR defaults closer when combined with desktopYawSensitivity = 0.4.
    public static float yawRateDegPerTick = 13.5f;
    public static float rollReturnDamping = 0.85f;

    public static boolean smoothingEnabled = true;
    public static double smoothingPitch = 1.0;
    public static double smoothingYaw = 2.5;
    public static double smoothingRoll = 1.0;
    public static double smoothingStopBoost = 1.0;

    public static boolean enableBanking = true;
    public static double bankingStrength = 40.0;
    public static boolean automaticRighting = false;
    public static double rightingStrength = 50.0;

    public static double momentumMouseDeadzone = 0.2;

    public static double desktopPitchSensitivity = 1.0;
    public static double desktopYawSensitivity = 0.4;
    public static double desktopRollSensitivity = 0.5;

    private static Configuration config;

    private ConfigHandler() {
    }

    public static void init(File configDir) {
        File file = new File(configDir, Tags.MOD_ID + ".cfg");
        config = new Configuration(file);
        sync();
    }

    public static void sync() {
        String general = Configuration.CATEGORY_GENERAL;

        modEnabled = config.getBoolean("modEnabled", general, modEnabled,
                "Enable or disable the mod logic entirely.");
        switchRollAndYaw = config.getBoolean("switchRollAndYaw", general, switchRollAndYaw,
                "Swap roll and yaw axes for mouse input.");
        momentumBasedMouse = config.getBoolean("momentumBasedMouse", general, momentumBasedMouse,
                "Use a momentum-based mouse for continuous turning.");
        invertPitch = config.getBoolean("invertPitch", general, invertPitch,
                "Invert pitch input while rolling.");
        invertYaw = config.getBoolean("invertYaw", general, invertYaw,
                "Invert yaw input while rolling.");
        invertRoll = config.getBoolean("invertRoll", general, invertRoll,
                "Invert roll input while rolling.");
        invertVisualRoll = config.getBoolean("invertVisualRoll", general, invertVisualRoll,
                "Invert the rendered roll (camera + player model) without affecting physics or banking.");
        disableWhenSubmerged = config.getBoolean("disableWhenSubmerged", general, disableWhenSubmerged,
                "Disable roll control when the player is submerged.");

        yawRateDegPerTick = (float) config.getFloat("yawRateDegPerTick", general, yawRateDegPerTick,
                0.1f, 20.0f, "Yaw rate from keys in degrees per tick.");
        rollReturnDamping = (float) config.getFloat("rollReturnDamping", general, rollReturnDamping,
                0.5f, 0.99f, "Roll return damping per tick when not rolling.");

        smoothingEnabled = config.getBoolean("smoothingEnabled", general, smoothingEnabled,
                "Enable smoothing for pitch/yaw/roll inputs.");
        smoothingPitch = config.getFloat("smoothingPitch", general, (float) smoothingPitch,
                0.0f, 10.0f, "Smoothing strength for pitch.");
        smoothingYaw = config.getFloat("smoothingYaw", general, (float) smoothingYaw,
                0.0f, 10.0f, "Smoothing strength for yaw.");
        smoothingRoll = config.getFloat("smoothingRoll", general, (float) smoothingRoll,
                0.0f, 10.0f, "Smoothing strength for roll.");

        smoothingStopBoost = config.getFloat("smoothingStopBoost", general, (float) smoothingStopBoost,
                1.0f, 20.0f, "When raw input is zero, reduces smoothing tail by shrinking internal backlog (higher = stops sooner).");

        enableBanking = config.getBoolean("enableBanking", general, enableBanking,
                "Enable banking (adds subtle yaw/pitch based on roll).");
        bankingStrength = config.getFloat("bankingStrength", general, (float) bankingStrength,
                0.0f, 200.0f, "Banking strength.");
        automaticRighting = config.getBoolean("automaticRighting", general, automaticRighting,
                "Automatically roll back towards upright.");
        rightingStrength = config.getFloat("rightingStrength", general, (float) rightingStrength,
                0.0f, 200.0f, "Automatic righting strength.");

        momentumMouseDeadzone = config.getFloat("momentumMouseDeadzone", general, (float) momentumMouseDeadzone,
                0.0f, 1.0f, "Deadzone for the momentum-based mouse.");

        desktopPitchSensitivity = config.getFloat("desktopPitchSensitivity", general, (float) desktopPitchSensitivity,
                0.0f, 5.0f, "Additional multiplier for pitch input.");
        desktopYawSensitivity = config.getFloat("desktopYawSensitivity", general, (float) desktopYawSensitivity,
                0.0f, 5.0f, "Additional multiplier for yaw input.");
        desktopRollSensitivity = config.getFloat("desktopRollSensitivity", general, (float) desktopRollSensitivity,
                0.0f, 5.0f, "Additional multiplier for roll input.");

        if (config.hasChanged()) {
            config.save();
        }
    }
}
