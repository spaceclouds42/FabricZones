package us.spaceclouds42.zones.mixin;

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

@Mixin(Block.class)
public class BlockMixin {

    @Inject(method = "dropStack", at = @At("HEAD"))
    private static void dropStack(World world, BlockPos pos, ItemStack stack, CallbackInfo ci) {
        if (!world.isClient) {
            String worldRegistryTag = world.getRegistryKey().getValue().toString();
            PosD posd = new PosD(worldRegistryTag, pos.getX(), pos.getY(), pos.getZ());
            Zone zone = ZoneManager.INSTANCE.getZone(posd);

            if(zone != null) {
                ci.cancel();
            }
        }
    }
}
