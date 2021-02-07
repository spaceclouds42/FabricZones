package us.spaceclouds42.builders.ext

import net.minecraft.util.Identifier
import net.minecraft.util.math.BlockPos
import us.spaceclouds42.builders.data.spec.Pos

fun BlockPos.toPos(world: Identifier): Pos {
    return Pos(
        world = world.toString(),

        x = this.x,
        y = this.y,
        z = this.z,
    )
}