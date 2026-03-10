# Design: Default-Disabled Barrel Roll Keys

## Summary
Change the default barrel roll key bindings (Z/C) to be unbound by default for new configurations, while keeping the feature available for users to bind manually.

## Context
The mod currently registers barrel roll key bindings with defaults of Z and C. The request is to make the barrel roll keys disabled by default for new configurations only, without affecting existing user bindings or adding new feature toggles.

## Goals
- New installs show barrel roll keys as unbound by default.
- Users can still bind the keys in the Controls menu.
- No behavior changes outside the default binding values.

## Non-Goals
- Do not add a new config option or UI toggle.
- Do not migrate or override existing key bindings.
- Do not change roll runtime logic.

## Proposed Change
- Update the two `KeyBinding` defaults in `RollKeyBindings` to use `Keyboard.KEY_NONE` instead of `Keyboard.KEY_Z`/`Keyboard.KEY_C`.

## Affected Components
- `src/main/java/com/xixi/rollthesky/roll/RollKeyBindings.java`

## Behavior
- On first run (new config), the barrel roll keys appear as unbound.
- Users can manually bind the keys in Controls.
- Existing configs are not altered.

## Testing
- Manual: launch client, open Controls, verify barrel roll keys are unbound by default.
- Optional: unit tests not required because runtime logic is unchanged.

## Risks and Mitigations
- Risk: users may assume the feature is removed. Mitigation: rely on Controls menu for discoverability; no code changes needed.
