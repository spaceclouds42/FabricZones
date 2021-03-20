package us.spaceclouds42.zones.mixin.griefing;

import net.minecraft.block.BlockState;
import net.minecraft.block.FluidBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import us.spaceclouds42.zones.ConstantsKt;
import us.spaceclouds42.zones.data.ZoneManager;
import us.spaceclouds42.zones.data.spec.Zone;
import us.spaceclouds42.zones.log.LogMode;

/**
 * Prevents obsidian, cobblestone, and basalt formation when liquids/blocks are not in the same zone
 */
@Mixin(FluidBlock.class)
abstract class FluidBlockMixin {
    @Redirect(
            method = "receiveNeighborFluids",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/util/math/BlockPos;offset(Lnet/minecraft/util/math/Direction;)Lnet/minecraft/util/math/BlockPos;"
            )
    )
    private BlockPos cancelBlockFormation(BlockPos blockPos, Direction direction, World world, BlockPos pos, BlockState state) {
        BlockPos toPos = blockPos.offset(direction);

        Zone zoneFrom = ZoneManager.INSTANCE.getZone(world, pos);
        Zone zoneTo = ZoneManager.INSTANCE.getZone(world, toPos);

        if (zoneFrom != zoneTo) {
            ConstantsKt.LOGGER.info("Liquids not in same zone, cancelled block formation", LogMode.DEBUG);
            return pos;
        } else {
            ConstantsKt.LOGGER.info("Liquids in same zone, did not cancel block formation", LogMode.DEBUG);
            return toPos;
        }
    }
}
