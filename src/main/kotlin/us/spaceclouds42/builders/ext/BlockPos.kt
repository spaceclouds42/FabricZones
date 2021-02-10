package us.spaceclouds42.builders.ext

import net.minecraft.util.Identifier
import net.minecraft.util.math.BlockPos
import us.spaceclouds42.builders.data.spec.PosI

fun BlockPos.toPos(world: Identifier): PosI {
    return PosI(
        world = world.toString(),

        x = this.x,
        y = this.y,
        z = this.z,
    )
}