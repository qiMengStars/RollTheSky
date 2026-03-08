# Roll The Sky

Roll The Sky is a Minecraft 1.12.2 Forge client mod focused on elytra flight control.
It adds configurable roll-based camera behavior, banking, smoothing, righting, and mouse/key turning logic inspired by modern free-flight camera control.

## Features

- Elytra roll and camera tilt while flying
- Configurable yaw / roll axis swapping
- Optional pitch, yaw, roll, and visual inversion
- Input smoothing and momentum-style mouse turning
- Banking and automatic righting controls
- Forge 1.12.2 + MixinBooter based implementation

## Development

- Build toolchain: Gradle 9.2.1 with RetroFuturaGradle
- Build JDK: Java 25
- Runtime target: Minecraft Forge 1.12.2
- Main mod id: `rollthesky`

Useful tasks:

- `./gradlew build`
- `./gradlew runClient`
- `./gradlew runObfClient`

## Release checklist

- Verify `build/libs/rollthesky-<version>.jar`
- Ensure bundled mixin config is `mixins.rollthesky.json`
- Ensure generated refmap is `mixins.rollthesky.refmap.json`
- Test in a production modpack with MixinBooter installed