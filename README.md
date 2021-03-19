# Fabric Zones
Adds a "gamemode" that allows players to fly and have access to unlimited blocks (just like creative),
but it prevents anything from leaving designated builder zones. The purpose is to prevent players from
using creative hotbars to cheat in illegal items, or just using the access to the creative inventory to
spawn in large amounts of items like diamonds or netherite.

## Using Fabric Zones
It is still in prealpha stage, so I do not recommend using it, but you can download *semi*stable builds 
the releases tab on GitHub

## Grief Protections

## Smuggling Protections
*Things that prevent builders from abusing their building powers*

| Blocked Action | Toggleable | Reason | How (optional) |
|----------------|------------|--------|----------------|
| Placing blocks outside of build zones | No | Prevent users from placing diamonds etc. into the normal world | PlayerInteract packets are not handled if result is out of zone and player is in build mode |
| Breaking blocks outside of build zones | No | Shouldn't be able to mess with |