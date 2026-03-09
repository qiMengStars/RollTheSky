# Third-Person Orientation Design

## Problem

In third-person elytra flight, the local player model does not face the same direction as the actual camera/view direction.

## Root Cause

- `src/main/java/com/xixi/rollthesky/mixin/client/RenderPlayerMixin.java` only adds a visual Z-roll after vanilla player rotations.
- Vanilla `RenderLivingBase#doRender` still derives body/head pose from `renderYawOffset` and `rotationYawHead`, which are movement/body smoothing values rather than the current camera-facing yaw.
- Vanilla `RenderPlayer#applyRotations` also adds an elytra Y-rotation based on motion vs. look direction, which further diverges the rendered model from the real view direction used by roll flight controls.

## Desired Behavior

- While the local player is using roll flight, third-person rendering should use the same yaw/pitch/roll basis as the actual view.
- The rendered model should stop inheriting vanilla elytra motion-based yaw correction in roll-flight mode.
- The fix should remain local to the client player render path and not affect other players.

## Design

1. Add a small render-orientation helper that computes the render yaw/pitch/roll for the local rolling player from actual view rotations and roll state.
2. Add focused unit tests for that helper first.
3. Update `RenderPlayerMixin` so that, for the local rolling player only:
   - render-time body/head yaw fields are temporarily aligned to the actual view yaw,
   - vanilla elytra `applyRotations` is replaced with a camera-aligned version,
   - the existing visual roll is applied as part of that replacement instead of as an afterthought.

## Validation

- Unit tests cover yaw source selection, pitch interpolation, and removal of the extra motion yaw correction.
- Compile the mod.
- Run the focused test class.
