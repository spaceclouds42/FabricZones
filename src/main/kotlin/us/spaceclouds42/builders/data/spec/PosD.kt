package us.spaceclouds42.builders.data.spec

import kotlinx.serialization.Serializable

/**
 * PositionBase type, used to store
 * position of zone corners
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
