# Fabric Builders
Adds a "gamemode" that allows players to fly and have access to unlimited blocks (just like creative),
but it prevents anything from leaving designated builder zones. The purpose is to prevent players from
using creative hotbars to cheat in illegal items, or just using the access to the creative inventory to
spawn in large amounts of items like diamonds or netherite.

## Using Fabric Builders
As it is still under development, there are no releases. I will create a prerelease when I think that
it is reasonably ready for use, but before then, I suggest you don't build it yourself and use it, as
it is not anywhere near production ready yet, and will likely have major bugs or features missing.

## Road Map
- inventory saving (both survival inventory and builder mode inven)
- ~~/zone create \<name\> \<x1\> \<z1\> \<x2\> \<z2\>~~
    - ~~display zone edges for builders in build mode and in zone~~
    - ~~save corners~~
    - ~~detect player entering/leaving zone~~
    - ~~/zone goto \<name\>~~
    - /zone pvp (true|false)
        - pvp and maybe other things? (damage, fire, tnt, mobs, etc): toggleable in zones
    - ~~/zone (delete|edit) \<name\>~~
- /gamemode builder
    - abilities: fly, invincibility, not able to pvp, toggleable noclip
- /builder (add|remove) \<name\>
    - gives `/gamemode builder` perms to player
- log any changes to any zone to file *going to be a very future feature*
