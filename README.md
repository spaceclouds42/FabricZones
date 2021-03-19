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
*Things that prevent builders from abusing their building powers.*

| Blocked Action | Reason | How | Currently Implemented |
|----------------|--------|-----|-----------------------|
| Interacting with guis | Prevents the storage of items in BlockEntities or entity inventories to be smuggled out of zones | Cancels `ServerPlayerEntity#openScreenHandler` if player is in builder mode | yes |
| Placing blocks outside of build zones | Prevent users from placing diamonds etc. into the normal world | PlayerInteract packets are not handled if result is out of zone and player is in build mode | no |
| Breaking blocks outside of build zones | Shouldn't be able to mess with things outside of designated zone | TBD | no |
| Blocks dropping items | Could be used to duplicate signs, banners, doors, bamboo, etc. | TBD | no |
| Bone mealing | After bone meal is applied, could cause blocks to be created outside of zone | PlayerInteract packets are not handled if held item is bone meal | no |
| Throwing projectiles | Prevents introduction of potions and xp | TBD | no |
| Using spawn eggs (partial) | Prevents duping of drop loot | Spawned entities can never leave the zone, nor be killed by non builders. | no |
| Placing command blocks | Do I really need to explain that one? | PlayerInteract packets are not handled if held item is a type of command block (includes minecarts) | no |
| NBT items in inventory | Would allow for bypassing of some of these, e.g. using hotbars to place in chest full of netherite | TBD | no |
