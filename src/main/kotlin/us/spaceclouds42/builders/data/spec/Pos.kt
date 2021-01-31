package us.spaceclouds42.builders.data.spec

import net.minecraft.util.Identifier

/**
 * Position type, used to store
 * position of zone corners
 */
data class Pos(
    /**
     * Dimension identifier
     */
    val world: Identifier,

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
