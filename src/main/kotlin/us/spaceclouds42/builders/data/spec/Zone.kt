package us.spaceclouds42.builders.data.spec

import net.minecraft.server.network.ServerPlayerEntity

/**
 * Build zones, areas where builders
 * are allowed to use /gamemode builder
 */
data class Zone(
    /**
     * Boundaries corners of the zone
     */
    var startPos: Pos,
    var endPos: Pos,

    /**
     * Player that created the zone
     */
    val createdBy: ServerPlayerEntity,

    var accessMode: Access = Access.EVERYONE,

    // Not to be implemented yet.. still very unsure of how I want this implemented
    /* (not a javadoc because ^)
     * Tracks any changes made to a zone made by
     * a builder. Config does have settings for
     * how much is logged, and only the last 20
     * changes are saved in memory, the rest are
     * saved to file
     */
    // val buildLog: MutableMap<LocalDateTime, BlockChange>,
)
{
    /**
     * Access modes
     */
    enum class Access {
        /**
         * Default access mode. All players can
         * enter the zone, and Builders are able
         * to use /gamemode builder in the zone
         */
        EVERYONE,

        /**
         * Everyone can see the zone, but
         * only Builders are able to enter
         */
        BUILDERS,

        /**
         * Only Builders can see the zone
         * and only they are able to enter
         */
        CLOAKED,
    }
}
