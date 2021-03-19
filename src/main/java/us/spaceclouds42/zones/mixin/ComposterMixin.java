package us.spaceclouds42.zones.mixin;

import net.minecraft.block.BlockState;
import net.minecraft.block.ComposterBlock;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import us.spaceclouds42.zones.data.ZoneManager;
import us.spaceclouds42.zones.data.spec.PosD;
import us.spaceclouds42.zones.data.spec.Zone;

import static net.minecraft.block.ComposterBlock.LEVEL;

@Mixin(ComposterBlock.class)
public abstract class ComposterMixin {

    /**
     * Prevents a composter from dropping bonemeal when within a fabric zone region
     *
     * @param state blockstate for the composter
     * @param world world the composter is present in
     * @param pos position of the composter
     * @param cir Blockstate that should be returned for the composter
     */
    @Inject(
            method = "emptyFullComposter",
            at = @At(
                    "HEAD"
            ))
    private static void emptyFullComposter(BlockState state, World world, BlockPos pos, CallbackInfoReturnable<BlockState> cir) {
        if (!world.isClient) {
            String worldRegistryTag = world.getRegistryKey().getValue().toString();
            PosD posd = new PosD(worldRegistryTag, pos.getX(), pos.getY(), pos.getZ());
            Zone zone = ZoneManager.INSTANCE.getZone(posd);

            if(zone != null) { // If within a zone simply reset the composter state to 0 and don't drop any bonemeal
                BlockState blockState = state.with(LEVEL, 0);
                world.setBlockState(pos, blockState, 3);
                world.playSound(null, pos, SoundEvents.BLOCK_COMPOSTER_EMPTY, SoundCategory.BLOCKS, 1.0F, 1.0F);
                cir.setReturnValue(blockState);
            }
        }
    }
}
