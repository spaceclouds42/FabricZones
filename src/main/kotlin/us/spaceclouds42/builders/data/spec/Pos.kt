package us.spaceclouds42.builders.data.spec

import net.minecraft.util.Identifier

/**
 * Position type, used to store
 * position of zone corners
 */
data class Pos(
    val world: Identifier,

    val x: Double,
    val y: Double,
    val z: Double,
)
