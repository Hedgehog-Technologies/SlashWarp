# SlashWarp

A Minecraft mod that adds a simple `/warp` command set to singleplayer and multiplayer alike for quick traversal to previously visited landmarks and points of interest.

## Key Features
### 1.0.0
- Warp locations are persistent across sessions
- Warp locations are independent per World file
- Warping between dimensions is supported
- Essentially the `teleport` or `tp` command in single player with some balance around having to have been someone before and setting the location as a warp point.

### 1.1.0
- Vehicles and mounts currently being ridden will warp with the player
- Pets actively following the player will try to warp with the player
  - May fail if a valid location to teleport the pet to isn't found in time

## Usage

```
/warp add <name> - Add your current location under <name>
/warp del <name> - Remove the warp location with <name>
/warp <name> - Warp to location with <name>
/warp back - Warp to previous location before your last warp (does not persist between sessions)
/warp list - List all available warps
```

## Dependencies
- Minecraft: `1.21.5`

### Mod Loader
- Fabric: `0.16.13`  
OR
- Forge: `55.0.4`  
OR
- NeoForge: `21.5.32-beta`
