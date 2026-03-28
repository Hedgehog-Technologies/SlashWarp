# Release Notes

## 2.2.0
- Compatibility for Minecraft 26.1
- Warp points shown in the `/warp list` command are now clickable
    - Configurable to automatically warp or to past the associated warp command into the chat box
- Add an optional and configurable, per-player cooldown for warping
    - This is disabled by default, but can be toggled on in the config file

## 2.1.0
- Add a config file to allow for customizing mod behavior
- `/warp back` can now be used to return to the location of your last death
    - This is disabled by default, but can be toggled on in the config file

## 2.0.0
- Compatibility for Minecraft 1.21.6+
- Add `/warp top` to teleport to the highest safe block position

## 1.1.0
- Vehicles and mounts currently being ridden will warp with the player
- Pets actively following the player will try to warp with the player
    - May fail if a valid location to teleport the pet to isn't found in time

## 1.0.0
- Warp locations are persistent across sessions
- Warp locations are independent per World file
- Warping between dimensions is supported
- Essentially the `teleport` or `tp` command in single player with some balance around having to have been someone before and setting the location as a warp point.
