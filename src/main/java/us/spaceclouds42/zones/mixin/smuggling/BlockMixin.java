package us.spaceclouds42.zones.mixin.smuggling;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import us.spaceclouds42.zones.data.ZoneManager;
import us.spaceclouds42.zones.data.spec.PosD;
import us.spaceclouds42.zones.data.spec.Zone;

/**
 * Prevents any blocks from dropping as items inside of zones. This blocks builders from duping blocks that require a supporting block, e.g. signs
 * This also has the side effect of allowing for floating blocks, which I think is a neat feature for builders :>
 */
@Mixin(Block.class)
abstract class BlockMixin {
    /**
     * Cancel the dropping of the block as an item if the block is in a zone
     *
     * @param world the dimension that this block is in
     * @param pos location of this block
     * @param stack the item that this block would drop as
     * @param ci callback info
     */
    @Inject(
            method = "dropStack",
            at = @At(
                    value = "HEAD"
            ),
            cancellable = true
    )
    private static void preventDroppingInZone(World world, BlockPos pos, ItemStack stack, CallbackInfo ci) {
        if (!world.isClient) {
            Zone zone = ZoneManager.INSTANCE.getZone(world, pos);

            if(zone != null) {
                ci.cancel();
            }
        }
    }
}
