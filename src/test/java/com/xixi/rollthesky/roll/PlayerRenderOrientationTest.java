package com.xixi.rollthesky.roll;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PlayerRenderOrientationTest {
    @AfterEach
    void tearDown() {
        ConfigHandler.invertVisualRoll = false;
    }

    @Test
    void bodyYawRotationFollowsActualViewYaw() {
        PlayerRenderOrientation.Orientation orientation = PlayerRenderOrientation.resolve(
                5.0f,
                15.0f,
                100.0f,
                140.0f,
                -20.0f,
                -20.0f,
                10,
                30.0f,
                0.25f
        );

        assertEquals(70.0f, orientation.getBodyYawRotation(), 0.0001f);
        assertEquals(110.0f, orientation.getInterpolatedViewYaw(), 0.0001f);
    }

    @Test
    void elytraPitchRotationUsesInterpolatedViewPitch() {
        PlayerRenderOrientation.Orientation orientation = PlayerRenderOrientation.resolve(
                0.0f,
                0.0f,
                0.0f,
                0.0f,
                -30.0f,
                10.0f,
                5,
                0.0f,
                0.5f
        );

        assertEquals(-10.0f, orientation.getInterpolatedViewPitch(), 0.0001f);
        assertEquals(-24.2f, orientation.getFlightPitchRotation(), 0.0001f);
    }

    @Test
    void visualRollHonorsConfigInversion() {
        ConfigHandler.invertVisualRoll = true;

        PlayerRenderOrientation.Orientation orientation = PlayerRenderOrientation.resolve(
                0.0f,
                0.0f,
                0.0f,
                0.0f,
                0.0f,
                0.0f,
                20,
                37.5f,
                1.0f
        );

        assertEquals(-37.5f, orientation.getVisualRollRotation(), 0.0001f);
    }

    @Test
    void thirdPersonCameraExcludesBarrelRoll() {
        float cameraRoll = PlayerRenderOrientation.resolveVisualRoll(15.0f, 120.0f, false);

        assertEquals(15.0f, cameraRoll, 0.0001f);
    }

    @Test
    void modelVisualRollIncludesBarrelRoll() {
        float modelRoll = PlayerRenderOrientation.resolveVisualRoll(15.0f, 120.0f, true);

        assertEquals(135.0f, modelRoll, 0.0001f);
    }

    @Test
    void visualRollResolverStillHonorsInversion() {
        ConfigHandler.invertVisualRoll = true;

        float modelRoll = PlayerRenderOrientation.resolveVisualRoll(15.0f, 120.0f, true);
        float cameraRoll = PlayerRenderOrientation.resolveVisualRoll(15.0f, 120.0f, false);

        assertEquals(-135.0f, modelRoll, 0.0001f);
        assertEquals(-15.0f, cameraRoll, 0.0001f);
    }

    @Test
    void modelRollUsesBodyAxisInsteadOfModelZAxis() {
        assertEquals(PlayerRenderOrientation.ModelRollAxis.BODY_Y,
                PlayerRenderOrientation.getModelRollAxis());
    }

    @Test
    void rollingRenderDisablesMotionYawCorrection() {
        PlayerRenderOrientation.Orientation orientation = PlayerRenderOrientation.resolve(
                20.0f,
                45.0f,
                70.0f,
                110.0f,
                0.0f,
                15.0f,
                15,
                5.0f,
                0.75f
        );

        assertEquals(0.0f, orientation.getMotionYawCorrection(), 0.0001f);
    }
}
