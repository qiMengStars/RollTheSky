# Third-Person Orientation Fix Implementation Plan
> **For Claude:** REQUIRED SUB-SKILL: Use superpowers:executing-plans to implement this plan task-by-task.

## Overview

Fix the third-person local-player render orientation so the model faces the same direction as the actual camera/view direction during roll-enabled elytra flight.

## Task 1: Add render-orientation helper

- Create `src/main/java/com/xixi/rollthesky/roll/PlayerRenderOrientation.java`.
- Add methods that:
  - interpolate camera/view yaw from `prevRotationYaw` and `rotationYaw`,
  - interpolate camera/view pitch from `prevRotationPitch` and `rotationPitch`,
  - compute elytra flight pitch rotation progress,
  - expose the visual roll angle with `invertVisualRoll` applied,
  - define that roll-flight rendering uses zero extra motion-based yaw correction.
- Keep the helper free of rendering side effects so it can be unit-tested directly.

## Task 2: Add regression tests first

- Create `src/test/java/com/xixi/rollthesky/roll/PlayerRenderOrientationTest.java`.
- Cover:
  - render yaw follows actual view yaw rather than smoothed body yaw,
  - render pitch uses interpolated view pitch and elytra progress,
  - render roll respects `invertVisualRoll`,
  - motion yaw correction is disabled for roll-flight rendering.
- Run the focused test class and confirm it fails for the expected missing-helper reason before implementation is finished.

## Task 3: Replace the local rolling player rotation path

- Update `src/main/java/com/xixi/rollthesky/mixin/client/RenderPlayerMixin.java`.
- Add render-time save/restore of the local player's `renderYawOffset`, `prevRenderYawOffset`, `rotationYawHead`, and `prevRotationYawHead` so the model/head calculations use the actual view yaw while rendering.
- Inject at the head of `applyRotations(...)` with cancellation.
- For the local rolling player only, replace vanilla elytra rotations with:
  - Y rotation from interpolated view yaw,
  - X rotation from interpolated view pitch plus vanilla elytra flight progress,
  - Z rotation from current visual roll,
  - no velocity/look-based extra yaw correction.
- Leave vanilla behavior untouched for all other players and states.

## Task 4: Verify

- Run `./gradlew.bat test --tests com.xixi.rollthesky.roll.PlayerRenderOrientationTest`.
- Run `./gradlew.bat compileJava`.
- If both pass, summarize the root cause and touched files.
