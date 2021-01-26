package us.spaceclouds42.builders.data.spec

import net.minecraft.entity.player.PlayerInventory
import java.util.UUID

data class Builder(
    /**
     * UUID of the builder
     */
    val uuid: UUID,

    /**
     * Inventory of player when not in builder gamemode
     */
    var survivalInventory: PlayerInventory,

    /**
     * Inventory of player when in builder mode
     */
    var builderInventory: PlayerInventory,

    /**
     * If enabled, allows builder to phase
     * through blocks, just like spectator mode
     */
    var noClipEnabled: Boolean = false,
    // further testing required, client side mod might be required :mojank:
)