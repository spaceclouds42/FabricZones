package us.spaceclouds42.builders.data.spec

import kotlinx.serialization.Serializable

/**
 * Build zones, areas where builders
 * are allowed to use /gamemode builder
 */
@Serializable
data class Zone(
    /**
     * Name of the zone. Must be unique
     */
    override val id: String,

    /**
     * Start corner
     */
    var startPos: Pos,

    /**
     * End corner
     */
    var endPos: Pos,

    /**
     * Player that created the zone
     */
    val createdBy: String,

    /**
     * Determines which groups have access, and what level of access.
     * See [ZoneAccessMode]'s documentation for more information
     */
    var accessMode: ZoneAccessMode = ZoneAccessMode.EVERYONE,

    // Not to be implemented yet.. still very unsure of how I want this implemented
    // Tracks any changes made to a zone made by
    // a builder. Config does have settings for
    // how much is logged, and only the last 20
    // changes are saved in memory, the rest are
    // saved to file
    //
    // val buildLog: MutableMap<LocalDateTime, BlockChange>,
) : IdentifiableDataSpecBase()