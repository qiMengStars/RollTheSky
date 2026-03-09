package com.xixi.rollthesky.roll;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.util.math.Vec3d;

public final class RollRuntime {
    public static final RollState STATE = new RollState();
    private static final DabrSmoother PITCH_SMOOTHER = new DabrSmoother();
    private static final DabrSmoother YAW_SMOOTHER = new DabrSmoother();
    private static final DabrSmoother ROLL_SMOOTHER = new DabrSmoother();
    private static boolean loggedMouseHook;

    private static long lastNanoTime = -1L;
    private static double mouseTurnX;
    private static double mouseTurnY;

    private RollRuntime() {
    }

    public static boolean shouldRoll(EntityPlayerSP player) {
        if (!ConfigHandler.modEnabled) {
            return false;
        }
        if (player == null || !player.isElytraFlying()) {
            return false;
        }
        if (ConfigHandler.disableWhenSubmerged && player.isInWater()) {
            return false;
        }
        return true;
    }

    public static void markMouseHookActive() {
        if (!loggedMouseHook) {
            loggedMouseHook = true;
            System.out.println("[RollTheSky] Mouse hook active (EntityRendererMixin applied)");
        }
    }

    public static void clearSmoothers() {
        PITCH_SMOOTHER.clear();
        YAW_SMOOTHER.clear();
        ROLL_SMOOTHER.clear();
    }

    public static void resetMouseState() {
        lastNanoTime = -1L;
        mouseTurnX = 0.0;
        mouseTurnY = 0.0;
    }

    public static float getVisualRoll(float partialTicks) {
        return STATE.getRoll(partialTicks) + STATE.getBarrelRoll(partialTicks);
    }

    public static float getVisualRollBack(float partialTicks) {
        return STATE.getRollBack(partialTicks) + STATE.getBarrelRoll(partialTicks);
    }

    public static void tickBarrelRoll(EntityPlayerSP player) {
        if (player == null || !shouldRoll(player)) {
            STATE.resetBarrelRoll();
            return;
        }

        int direction = RollKeyBindings.consumeBarrelRollDirection();
        if (direction != 0) {
            STATE.startBarrelRoll(direction);
        }

        float duration = ConfigHandler.barrelRollDurationTicks;
        if (duration < 1.0f) {
            duration = 1.0f;
        }
        STATE.tickBarrelRoll(1.0f / duration);
        applyBarrelRollDodge(player);
    }

    public static void handleMouseTurn(EntityPlayerSP player, float yawInput, float pitchInput) {
        if (player == null) {
            return;
        }

        // If this prints, the turn redirect is active.
        markMouseHookActive();

        if (!shouldRoll(player)) {
            resetMouseState();
            player.turn(yawInput, pitchInput);
            return;
        }

        double dt = getDeltaSeconds(System.nanoTime());

        // Convert raw mouse deltas to our input space. In 1.12 these are already sensitivity-scaled.
        double mouseX = yawInput;
        // In 1.12, mouse Y input handed to Entity#turn has the opposite sign of modern cursor deltas.
        // Flip here so our DABR-style math matches the expected pitch direction.
        double mouseY = -pitchInput;

        if (ConfigHandler.momentumBasedMouse) {
            // Accumulate a "virtual stick" vector, like DABR's momentum mouse.
            mouseTurnX += mouseX / 300.0;
            mouseTurnY += mouseY / 300.0;

            double lenSq = mouseTurnX * mouseTurnX + mouseTurnY * mouseTurnY;
            if (lenSq > 1.0) {
                double invLen = 1.0 / Math.sqrt(lenSq);
                mouseTurnX *= invLen;
                mouseTurnY *= invLen;
            }

            double readyX = mouseTurnX;
            double readyY = mouseTurnY;

            double deadzone = ConfigHandler.momentumMouseDeadzone;
            if (readyX * readyX + readyY * readyY < deadzone * deadzone) {
                readyX = 0.0;
                readyY = 0.0;
            }

            double scale = 1200.0 * dt;
            mouseX = readyX * scale;
            mouseY = readyY * scale;
        } else {
            mouseTurnX = 0.0;
            mouseTurnY = 0.0;
        }

        double keyAxis = getKeyAxisInput(dt);

        FlightControlInput.Axes axes = FlightControlInput.resolve(
                mouseY,
                mouseX,
                keyAxis,
                ConfigHandler.switchRollAndYaw,
                ConfigHandler.invertPitch,
                ConfigHandler.invertYaw,
                ConfigHandler.invertRoll
        );

        double pitch = axes.getPitch();
        double yaw = axes.getYaw();
        double roll = axes.getRoll();

        // Apply additional sensitivity multipliers (DABR defaults: yaw=0.4).
        pitch *= ConfigHandler.desktopPitchSensitivity;
        yaw *= ConfigHandler.desktopYawSensitivity;
        roll *= ConfigHandler.desktopRollSensitivity;

        if (ConfigHandler.smoothingEnabled) {
            pitch = smoothInputDabr(PITCH_SMOOTHER, pitch, ConfigHandler.smoothingPitch, dt);
            yaw = smoothInputDabr(YAW_SMOOTHER, yaw, ConfigHandler.smoothingYaw, dt);
            roll = smoothInputDabr(ROLL_SMOOTHER, roll, ConfigHandler.smoothingRoll, dt);
        }

        // Late modifiers (not smoothed), mirroring DABR's ordering.
        if (ConfigHandler.enableBanking) {
            double[] bank = computeBankingInputs(player, dt);
            pitch += bank[0];
            yaw += bank[1];
        }
        if (ConfigHandler.automaticRighting) {
            roll += computeRightingInput(dt);
        }

        ElytraRollController.applyElytraLookDelta(player, STATE, pitch, yaw, roll);
    }

    private static double[] computeBankingInputs(EntityPlayerSP player, double dt) {
        double currentRollRad = Math.toRadians(STATE.getRawRoll());
        double currentPitchRad = Math.toRadians(player.rotationPitch);

        double strength = ConfigHandler.bankingStrength;

        // Default DABR formulas (no expression parser yet).
        double x = Math.sin(currentRollRad) * Math.cos(currentPitchRad) * 10.0 * strength;
        double y = (-1.0 + Math.cos(currentRollRad)) * Math.cos(currentPitchRad) * 10.0 * strength;
        if (Double.isNaN(x) || Double.isNaN(y)) {
            return new double[] { 0.0, 0.0 };
        }

        x *= dt;
        y *= dt;

        // Convert absolute (upright) x/y into local pitch/yaw deltas for the current roll.
        double cos = Math.cos(currentRollRad);
        double sin = Math.sin(currentRollRad);
        double pitch = -y * cos - x * sin;
        double yaw = -y * sin + x * cos;
        return new double[] { pitch, yaw };
    }

    private static double computeRightingInput(double dt) {
        double currentRollRad = Math.toRadians(STATE.getRawRoll());
        double cutoff = Math.sqrt(10.0 / 3.0);
        double rollDelta = 0.0;

        if (-cutoff < currentRollRad && currentRollRad < cutoff) {
            rollDelta = -Math.pow(currentRollRad, 3) / 3.0 + currentRollRad;
        }

        double strength = 10.0 * ConfigHandler.rightingStrength;
        return -rollDelta * strength * dt;
    }

    private static double getDeltaSeconds(long nanoTime) {
        if (lastNanoTime < 0L) {
            lastNanoTime = nanoTime;
            return 1.0 / 60.0;
        }

        long deltaNanos = nanoTime - lastNanoTime;
        lastNanoTime = nanoTime;
        double dt = deltaNanos / 1.0e9;

        // Clamp to avoid wild jumps when the game hitches or loses focus.
        if (dt < 0.0) {
            dt = 0.0;
        } else if (dt > 0.1) {
            dt = 0.1;
        }
        return dt;
    }

    private static double getKeyAxisInput(double dt) {
        Minecraft mc = Minecraft.getMinecraft();
        GameSettings settings = mc.gameSettings;
        if (settings == null) {
            return 0.0;
        }

        int dir = 0;
        if (settings.keyBindLeft.isKeyDown()) {
            dir -= 1;
        }
        if (settings.keyBindRight.isKeyDown()) {
            dir += 1;
        }
        if (dir == 0) {
            return 0.0;
        }

        double degPerSecond = ConfigHandler.yawRateDegPerTick * 20.0;
        double degThisFrame = degPerSecond * dt * dir;
        return degThisFrame / 0.15;
    }

    private static void applyBarrelRollDodge(EntityPlayerSP player) {
        float factor = STATE.getRawBarrelRollDodgeFactor();
        int direction = STATE.getBarrelRollDirection();
        if (factor <= 0.0f || direction == 0) {
            return;
        }

        double yawRad = Math.toRadians(player.rotationYaw);
        Vec3d right = new Vec3d(-Math.cos(yawRad), 0.0, -Math.sin(yawRad));
        double strength = ConfigHandler.barrelRollDodgeStrength * factor * direction;

        player.motionX += right.x * strength;
        player.motionZ += right.z * strength;
    }

    private static double smoothInputDabr(DabrSmoother smoother, double input, double smoothness, double dt) {
        // Match DABR: amount = (1 / smoothness) * dt
        if (smoothness <= 0.0) {
            return input;
        }

        double amount = (1.0 / smoothness) * dt;
        if (amount < 0.0) {
            amount = 0.0;
        } else if (amount > 1.0) {
            amount = 1.0;
        }

        return smoother.smooth(input, amount, ConfigHandler.smoothingStopBoost);
    }
}
