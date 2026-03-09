# Flight Roll Keys Design

## Goal

Add dedicated left/right roll controls for elytra flight so the player can quickly dodge and bank in a way closer to action-flight games.

## Current State

- Roll flight already exists and ultimately consumes `pitch`, `yaw`, and `roll` axes in `RollRuntime`.
- Roll can currently come from mouse X or A/D depending on `switchRollAndYaw`.
- There are no dedicated roll keybindings yet.

## Desired Behavior

- Add two dedicated client keybindings: roll left and roll right.
- These keys should only affect the `roll` axis during roll-enabled elytra flight.
- Dedicated roll keys should always map to roll, regardless of `switchRollAndYaw`.
- Existing mouse/A-D behavior should continue to work as before.

## Key Choice

- Default to `Z` for roll left and `C` for roll right.
- Rationale: close to `WASD`, uncommon default conflicts in vanilla 1.12.2, and does not steal `Q`, `E`, or `F`.

## Design

1. Add a client-only keybinding holder and register the two keys during mod client setup.
2. Extract input-axis mapping into a small pure helper so dedicated roll keys can be tested without Minecraft runtime state.
3. Add a new config value for dedicated roll-key turn rate.
4. Wire the new key axis into `RollRuntime` before sensitivity/smoothing so existing roll inversion and smoothing continue to apply naturally.

## Validation

- Unit tests cover key-axis mapping and ensure dedicated roll keys remain on the roll axis for both `switchRollAndYaw` modes.
- Build compiles successfully.
