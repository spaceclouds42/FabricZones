# Fabric Zones
Adds a "gamemode" that allows players to fly and have access to unlimited blocks (just like creative),
but it prevents anything from leaving designated builder zones. The purpose is to prevent players from
using creative hotbars to cheat in illegal items, or just using the access to the creative inventory to
spawn in large amounts of items like diamonds or netherite.

## Using Fabric Zones
It is still in prealpha stage, so I do not recommend using it, but you can download *semi*stable builds 
the releases tab on GitHub

## Grief Protections
*Things that prevent anyone from griefing a zone*

| Blocked Action | How | Currently Implemented |
|----------------|-----|-----------------------|
| Projectiles from non builders | Any projectile entering a zone will be deleted, tridents will be given back to thrower | yes | 
| Falling block entity | Any falling block entity that does not originate from inside a zone will turn into an item, and any falling blocks inside zones will not fall | yes ([@BasiqueEvangelist](https://github.com/BasiqueEvangelist/)) |
| Liquids | Any liquid flowing into the zone is stopped from entering, any liquid flowing out is stopped from exiting. Liquids in different zones do not interact or connect in any way. | yes ([@BasiqueEvangelist](https://github.com/BasiqueEvangelist/)) |
| Lava/Water block generation | Stone/Cobble/Obsidian/Basalt generation does not occur if liquids/blocks are not all in the same zone | yes |
| Fire spreading | Disables fire spreading in same way as the gamerule | yes |
| Mob griefing | Fully disables any mob griefing that the gamerule disables | no |
| Rain putting out fire | Rain will not put out the fire if it is located in a zone | no |
| Lightning causing fire | Lightning bolts will not cause create fire in a zone | no |
| Explosions | If explodes at a position not in a zone, it will not cause any damage inside the zone, but still will cause damage outside. If position is inside of zone, griefing is cancelled entirely | no |
| Placing blocks | Place action is cancelled if result is in a zone (non builders only) | no |
| Breaking blocks | Break action is cancelled if result is in a zone (non builders only) | no |
| Interacting with blocks (settings) | By default, only pressure plate and button interactions are processed, can disable all or enable all (non builders only) | no |
| Pistons/slimestone | TBD | no |
| Projectiles from zoned entities | Any projectile from a zoned entity will be deleted | no |

## Smuggling Protections
*Things that prevent builders from abusing their building powers.*

| Blocked Action | Reason | How | Currently Implemented |
|----------------|--------|-----|-----------------------|
| Interacting with guis | Prevents the storage of items in BlockEntities or entity inventories to be smuggled out of zones | Cancels `ServerPlayerEntity#openScreenHandler` if player is in builder mode | yes |
| Dropping items (normal and on death) | Seriously? *You could dupe anything if this was allowed* | Drop item method is cancelled, works with death too | yes |
| Picking up items | As they cannot drop items, shouldn't be able to pick up other player's items | Item pick up is cancelled if player is a builder | yes ([@profjb](https://github.com/profjb58)) |
| Blocks dropping items | Could be used to duplicate signs, banners, doors, bamboo, etc. | Block dropping is completely cancelled, allowing for floating blocks | yes ([@profjb](https://github.com/profjb58)) |
| Using composters (partial) | Bone meal duping | Composters will not drop bone meal when at maximum capacity, simply set back to 0 | yes ([@profjb](https://github.com/profjb58)) |
| Using jukeboxes (partial) | Disc duping | Jukeboxes will not drop discs, non builders cannot use zoned jukeboxes | yes |
| Placing blocks outside of build zones | Prevent users from placing diamonds etc. into the normal world | PlayerInteract packets are not handled if result is out of zone and player is in build mode | no |
| Breaking blocks outside of build zones | Shouldn't be able to mess with things outside of designated zone | TBD | no |
| Bone mealing | After bone meal is applied, could cause blocks to be created outside of zone | PlayerInteract packets are not handled if held item is bone meal | no |
| Throwing projectiles | Prevents introduction of potions and xp | TBD | no |
| Using spawn eggs (partial) | Prevents duping of drop loot | Spawned entities can never leave the zone, nor be killed by non builders, nor drop loot | no |
| Placing command blocks | Do I really need to explain that one? | PlayerInteract packets are not handled if held item is a type of command block (includes minecarts) | no |
| NBT items in inventory | Would allow for bypassing of some of these, e.g. using hotbars to place in chest full of netherite | TBD | no |
| Baby turtles dropping scutes (partial) | Scute duping | If baby turtle spawned from a spawn egg or egg inside zone, they will not drop scute | no |
| Attack mobs/players | Builders are in creative mode, do I really need to- | Cancels attack if mob does not originate from zone or if attack result is player | no |
| Pillager raids | Duping totems, emeralds, etc. | Bad omen does not trigger a raid if the village is located inside a protected zone, raid parties cannot spawn in zones | no |
| Plant growth (partial) | Prevents spawning in of wood and other plants | Saplings do not grow. Cactus, sugarcane, and bamboo will not grow past the height of the zone. | no |

## Zone Settings
*Wait until the default is functional, then we can talk about fancy settings*
