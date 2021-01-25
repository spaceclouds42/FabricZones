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
- /builder zone create \<name\> \<x1\> \<z1\> \<x2\> \<z2\>
    - save corners
    - detect player entering/leaving zone
    - /builder zone goto \<name\>
    - /builder zone (pvp|protect|blacklist|whitelist) *(might become a separate mod with compatibility with this mod's zones.. 🤔)*
        - pvp and protect: toggleable in zones (maybe?)
        - whitelist and blacklist: block/allow select players from entering/interacting (maybe?)
    - /builder zone (delete|edit) \<name\>
- /gamemode builder
    - abilities: fly, invincibility, not able to pvp, toggleable noclip
    - block menu, maybe add some default build kits?