package us.spaceclouds42.zones.mixin.griefing;

import net.minecraft.block.BlockState;
import net.minecraft.block.FallingBlock;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import us.spaceclouds42.zones.data.ZoneManager;
import us.spaceclouds42.zones.data.spec.PosD;
import us.spaceclouds42.zones.data.spec.Zone;

import java.util.Random;

/**
 * Prevent falling blocks from falling if placed in a zone
 */
@Mixin(FallingBlock.class)
abstract class FallingBlockMixin {
    /**
     * Prevents falling blocks from falling if located in a zone
     *
     * @param state state of this block
     * @param state2 state of block below this block
     * @param world the world this block is in
     * @param pos location of this block
     * @param random a random number
     * @return whether or not it should fall
     */
    @Redirect(
            method = "scheduledTick",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/block/FallingBlock;canFallThrough(Lnet/minecraft/block/BlockState;)Z"
            )
    )
    private boolean disallowFallingInZones(BlockState state, BlockState state2, ServerWorld world, BlockPos pos, Random random) {
        if (FallingBlock.canFallThrough(state)) {
            return ZoneManager.INSTANCE.getZone(world, pos) == null;
        } else {
            return false;
        }
    }
}
