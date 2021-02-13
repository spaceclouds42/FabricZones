package us.spaceclouds42.zones.data.spec

import kotlinx.serialization.Serializable

/**
 * Position Double type, used to store
 * dimension specific positions with doubles
 */
@Serializable
data class PosD(
    /**
     * Dimension identifier as string. Ex: "minecraft:overworld"
     */
    val world: String,

    /**
     * x coordinate
     */
    val x: Double,

    /**
     * y coordinate
     */
    val y: Double,

    /**
     * z coordinate
     */
    val z: Double,
)
