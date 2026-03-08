package com.xixi.rollthesky.roll;

import net.minecraft.util.math.MathHelper;

public final class RollState {
    private float roll;
    private float prevRoll;
    private float rollBack;
    private float prevRollBack;
    private boolean rolling;

    public void setRolling(boolean rolling) {
        this.rolling = rolling;
    }

    public boolean isRolling() {
        return rolling;
    }

    public float getRoll(float partialTicks) {
        return prevRoll + (roll - prevRoll) * partialTicks;
    }

    public float getRollBack(float partialTicks) {
        return prevRollBack + (rollBack - prevRollBack) * partialTicks;
    }

    public float getRawRoll() {
        return roll;
    }

    public float getRawRollBack() {
        return rollBack;
    }

    public void setRoll(float newRoll) {
        if (!Float.isFinite(newRoll)) {
            return;
        }

        float lastRoll = this.roll;
        this.roll = newRoll;

        // Keep interpolation continuous when crossing -180/180.
        if (newRoll < -90.0f && lastRoll > 90.0f) {
            prevRoll -= 360.0f;
        } else if (newRoll > 90.0f && lastRoll < -90.0f) {
            prevRoll += 360.0f;
        }
    }

    public void addRollDelta(float deltaRoll) {
        if (!Float.isFinite(deltaRoll)) {
            return;
        }

        this.roll += deltaRoll;
        this.prevRoll += deltaRoll;
    }

    public void setRollBack(float newRollBack) {
        newRollBack = MathHelper.wrapDegrees(newRollBack);
        prevRollBack = rollBack;
        rollBack = newRollBack;
        normalizePrevBackForInterpolation();
    }

    public void updatePrev() {
        prevRoll = roll;
        prevRollBack = rollBack;
    }

    private void normalizePrevForInterpolation() {
        while (roll - prevRoll < -180.0f) {
            prevRoll -= 360.0f;
        }
        while (roll - prevRoll >= 180.0f) {
            prevRoll += 360.0f;
        }
    }

    private void normalizePrevBackForInterpolation() {
        while (rollBack - prevRollBack < -180.0f) {
            prevRollBack -= 360.0f;
        }
        while (rollBack - prevRollBack >= 180.0f) {
            prevRollBack += 360.0f;
        }
    }
}
