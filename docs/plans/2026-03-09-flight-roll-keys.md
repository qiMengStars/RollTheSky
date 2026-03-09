# Flight Roll Keys Implementation Plan
> **For Claude:** REQUIRED SUB-SKILL: Use superpowers:executing-plans to implement this plan task-by-task.

## Overview

Add dedicated `roll left` and `roll right` controls for client-side elytra roll flight, with defaults on `Z` and `C`, while preserving current mouse and A/D behavior.

### Task 1: Add pure input mapping helper

- Create `src/main/java/com/xixi/rollthesky/roll/FlightControlInput.java`.
- Move the axis-mapping rules out of `RollRuntime` into this helper.
- The helper should:
  - accept mouse pitch/yaw inputs,
  - accept A/D axis input,
  - accept dedicated roll-key axis input,
  - honor `switchRollAndYaw`, `invertPitch`, `invertYaw`, and `invertRoll`,
  - always apply dedicated roll keys to the roll axis.

### Task 2: Add tests first

- Create `src/test/java/com/xixi/rollthesky/roll/FlightControlInputTest.java`.
- Cover:
  - default mode keeps mouse X on roll and A/D on yaw,
  - switched mode keeps mouse X on yaw and A/D on roll,
  - dedicated roll keys always add to roll in both modes,
  - invert flags affect the expected axes.
- Run the focused test class and confirm failure before implementation exists.

### Task 3: Add keybindings and config

- Create `src/main/java/com/xixi/rollthesky/roll/RollKeyBindings.java`.
- Register `roll left` and `roll right` keybindings on the client.
- Default keys: `Z` and `C`.
- Add `rollKeyRateDegPerTick` to `src/main/java/com/xixi/rollthesky/roll/ConfigHandler.java`.

### Task 4: Wire runtime behavior

- Update `src/main/java/com/xixi/rollthesky/RollTheSkyMod.java` to register keybindings on the client.
- Update `src/main/java/com/xixi/rollthesky/roll/RollRuntime.java` to:
  - query the dedicated roll-key axis,
  - pass all raw inputs through `FlightControlInput`,
  - keep sensitivity, smoothing, banking, and righting behavior unchanged after mapping.

### Task 5: Verify

- Run `./gradlew.bat test --tests com.xixi.rollthesky.roll.FlightControlInputTest --tests com.xixi.rollthesky.roll.PlayerRenderOrientationTest`.
- Run `./gradlew.bat compileJava`.
- Summarize the new controls and touched files.
