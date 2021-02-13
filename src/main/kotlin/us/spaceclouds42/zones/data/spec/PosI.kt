package us.spaceclouds42.zones.data.spec

import kotlinx.serialization.Serializable

/**
 * Position Integer type, used to store
 * dimension specific positions with integers
 */
@Serializable
data class PosI(
    /**
     * Dimension identifier as string. Ex: "minecraft:overworld"
     */
    val world: String,

    /**
     * x coordinate
     */
    val x: Int,

    /**
     * y coordinate
     */
    val y: Int,

    /**
     * z coordinate
     */
    val z: Int,
)
