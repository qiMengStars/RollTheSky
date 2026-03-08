package com.xixi.rollthesky.roll;

import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public final class ElytraRollController {
    private ElytraRollController() {
    }

    public static void applyElytraLookDelta(EntityPlayerSP player, RollState state,
                                            double pitchInput, double yawInput, double rollInput) {
        if (player == null) {
            return;
        }

        float currentPitch = player.rotationPitch;
        float currentYaw = player.rotationYaw;
        float currentRoll = state.getRawRoll();

        Vec3d facing = player.getLookVec();
        Vec3d left = computeLeftVector(currentPitch, currentYaw, currentRoll);

        double pitchRad = Math.toRadians(-0.15 * pitchInput);
        double yawRad = Math.toRadians(0.15 * yawInput);
        double rollRad = Math.toRadians(0.15 * rollInput);

        facing = rotateAroundAxis(facing, left, pitchRad);

        Vec3d up = facing.crossProduct(left);
        facing = rotateAroundAxis(facing, up, yawRad);
        left = rotateAroundAxis(left, up, yawRad);

        left = rotateAroundAxis(left, facing, rollRad);

        facing = facing.normalize();
        left = left.normalize();

        double newPitch = -Math.asin(MathHelper.clamp(facing.y, -1.0, 1.0)) * 180.0 / Math.PI;
        double newYawBase = -Math.atan2(facing.x, facing.z) * 180.0 / Math.PI;
        double newYaw = currentYaw + MathHelper.wrapDegrees((float) (newYawBase - currentYaw));

        Vec3d normalLeft = rotateY(new Vec3d(1.0, 0.0, 0.0), Math.toRadians(-(newYawBase + 180.0)));
        double newRoll = -Math.atan2(left.crossProduct(normalLeft).dotProduct(facing), left.dotProduct(normalLeft)) * 180.0 / Math.PI;

        double deltaPitch = newPitch - currentPitch;
        double deltaYaw = newYaw - currentYaw;
        double deltaRoll = newRoll - currentRoll;

        // Apply vanilla yaw and pitch so clamping and prev-rotation updates stay consistent.
        player.turn((float) (deltaYaw / 0.15), (float) (-deltaPitch / 0.15));

        // Apply roll in the same way Entity#turn updates prev values (avoid partial tick jitter).
        state.addRollDelta((float) deltaRoll);
        state.setRollBack(state.getRawRoll());
    }

    private static Vec3d computeLeftVector(float pitchDeg, float yawDeg, float rollDeg) {
        Vec3d left = new Vec3d(1.0, 0.0, 0.0);
        left = rotateZ(left, Math.toRadians(-rollDeg));
        left = rotateX(left, Math.toRadians(-pitchDeg));
        left = rotateY(left, Math.toRadians(-(yawDeg + 180.0)));
        return left.normalize();
    }

    private static Vec3d rotateAroundAxis(Vec3d v, Vec3d axisUnit, double angleRad) {
        double cos = Math.cos(angleRad);
        double sin = Math.sin(angleRad);
        Vec3d axis = axisUnit.normalize();

        Vec3d term1 = v.scale(cos);
        Vec3d term2 = axis.crossProduct(v).scale(sin);
        Vec3d term3 = axis.scale(axis.dotProduct(v) * (1.0 - cos));
        return term1.add(term2).add(term3);
    }

    private static Vec3d rotateX(Vec3d v, double angleRad) {
        double cos = Math.cos(angleRad);
        double sin = Math.sin(angleRad);
        double y = v.y * cos - v.z * sin;
        double z = v.y * sin + v.z * cos;
        return new Vec3d(v.x, y, z);
    }

    private static Vec3d rotateY(Vec3d v, double angleRad) {
        double cos = Math.cos(angleRad);
        double sin = Math.sin(angleRad);
        double x = v.x * cos + v.z * sin;
        double z = -v.x * sin + v.z * cos;
        return new Vec3d(x, v.y, z);
    }

    private static Vec3d rotateZ(Vec3d v, double angleRad) {
        double cos = Math.cos(angleRad);
        double sin = Math.sin(angleRad);
        double x = v.x * cos - v.y * sin;
        double y = v.x * sin + v.y * cos;
        return new Vec3d(x, y, v.z);
    }
}
