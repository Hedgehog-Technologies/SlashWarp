# SlashWarp

A Minecraft mod that adds a simple `/warp` command set to singleplayer and multiplayer alike for quick traversal to previously visited landmarks and points of interest.

## Usage

```
/warp add <name> - Add your current location under <name>
/warp del <name> - Remove the warp location with <name>
/warp <name> - Warp to location with <name>
/warp back - Warp to previous location before your last warp or your last death (does not persist between sessions)
/warp list - List all available warps
/warp top - Warp to the highest safe block position in a 8 block radius
```

## Release Notes
See [release-notes](./docs/release-notes.md) for detailed changelogs.

## Dependencies
- Minecraft: `1.21.10`

### Mod Loader
- Fabric: `0.17.3`

OR
- Forge: `60.0.12`

OR
- NeoForge: `21.10.38-beta`
