package us.spaceclouds42.builders.data.spec

import kotlinx.serialization.Serializable

/**
 * Position type, used to store
 * position of zone corners
 */
@Serializable
data class Pos(
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
) {
    companion object {
        val ORIGIN = Pos("minecraft:overworld", 0, 0, 0)
    }
}
