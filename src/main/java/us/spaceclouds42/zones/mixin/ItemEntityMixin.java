package us.spaceclouds42.zones.mixin;

import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import us.spaceclouds42.zones.access.BuilderAccessor;

@Mixin(ItemEntity.class)
public class ItemEntityMixin {
    /**
     * Prevents a player from picking up any items when in builder mode
     *
     * @param player the player that collides with the item stack
     * @param ci callback info
     */
    @Inject(
            method = "onPlayerCollision",
            at = @At(
                    value = "HEAD"
            ),
            cancellable = true
    )
    public void onPlayerCollision(PlayerEntity player, CallbackInfo ci) {
        if(!player.getEntityWorld().isClient()) {
            if(((BuilderAccessor) player).isInBuilderMode()) {
                ci.cancel(); // Cancel interaction and prevent picking up an item stack
            }
        }
    }
}
