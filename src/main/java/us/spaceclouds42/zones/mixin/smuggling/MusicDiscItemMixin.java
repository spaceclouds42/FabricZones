package us.spaceclouds42.zones.mixin.smuggling;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.MusicDiscItem;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import us.spaceclouds42.zones.data.ZoneManager;
import us.spaceclouds42.zones.duck.BuilderAccessor;

/**
 * Stops players from using discs on zoned jukeboxes so that the player will not lose their disc
 */
@Mixin(MusicDiscItem.class)
abstract class MusicDiscItemMixin {
    /**
     * Prevents players not in builder mode from using disc on jukeboxes
     *
     * @param context the item usage context, including where and what player
     * @param cir callback info returnable
     */
    @Inject(
            method = "useOnBlock",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/block/JukeboxBlock;setRecord(Lnet/minecraft/world/WorldAccess;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;Lnet/minecraft/item/ItemStack;)V"
            ),
            cancellable = true
    )
    private void preventUsingDisc(ItemUsageContext context, CallbackInfoReturnable<ActionResult> cir) {
        PlayerEntity player = context.getPlayer();

        if (player instanceof ServerPlayerEntity) {
            World world = context.getWorld();
            BlockPos pos = context.getBlockPos();

            if (!((BuilderAccessor) player).isInBuilderMode() && ZoneManager.INSTANCE.getZone(world, pos) != null) {
                cir.setReturnValue(ActionResult.PASS);
            }
        }
    }
}
