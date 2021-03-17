package us.spaceclouds42.zones.data.spec

import kotlinx.serialization.Serializable
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.world.GameMode
import us.spaceclouds42.zones.LOGGER
import us.spaceclouds42.zones.log.LogMode
import us.spaceclouds42.zones.access.BuilderAccessor

/**
 * Contains configurable properties of builders
 * that can help with using builder mode
 */
@Serializable
data class Builder(
    override val id: String,

    /**
     * Builder's name
     */
    val name: String = "null",

    /**
     * Whether or not the builder will
     * be in builder mode when they enter
     * a builder enabled zone
     */
    var builderModeEnabled: Boolean = true,

    /**
     * If enabled, the builder will not
     * activate a portal when it collides
     * with a nether or end portal
     */
    var antiPortalInteract: Boolean = false,
    // Is a full on no clip possible purely server side?

    /**
     * Whether or not a builder sees zone borders. (Only displayed when inside a zone)
     */
    var areZoneBordersVisible: Boolean = true,
) : IdentifiableDataSpecBase() {

    /**
     *
     */
    fun activateBuilderMode(player: ServerPlayerEntity) {
        val builderPlayer = player as BuilderAccessor

        if (builderPlayer.isInBuilderMode) {
            LOGGER.info("Tried to activate builder mode but ${player.entityName} is already in builder mode.", LogMode.DEBUG)
            return
        }

        builderPlayer.swapInventories()
        player.abilities.allowFlying = true
        player.abilities.creativeMode = true
        player.abilities.invulnerable = true
        player.changeGameMode(GameMode.CREATIVE)
    }

    /**
     *
     */
    fun deactivateBuilderMode(player: ServerPlayerEntity) {
        val builderPlayer = player as BuilderAccessor

        if (!builderPlayer.isInBuilderMode) {
            LOGGER.info("Tried to deactivate builder mode but ${player.entityName} is not in builder mode.", LogMode.DEBUG)
            return
        }

        builderPlayer.swapInventories()
        player.abilities.allowFlying = false
        player.abilities.creativeMode = false
        player.abilities.invulnerable = false
        player.changeGameMode(GameMode.DEFAULT)
    }
}