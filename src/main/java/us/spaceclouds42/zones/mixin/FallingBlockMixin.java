package us.spaceclouds42.zones.mixin;

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

@Mixin(FallingBlock.class)
public abstract class FallingBlockMixin {
    @Shadow
    public static boolean canFallThrough(BlockState state) {
        return false;
    }

    @Redirect(
        method = "scheduledTick",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/block/FallingBlock;canFallThrough(Lnet/minecraft/block/BlockState;)Z"
        )
    )
    private boolean disallowFallingInZones(BlockState state, BlockState state2, ServerWorld world, BlockPos pos, Random random) {
        if (canFallThrough(state)) {
            return ZoneManager.INSTANCE.getZone(world, pos) == null;
        } else {
            return false;
        }
    }
}
