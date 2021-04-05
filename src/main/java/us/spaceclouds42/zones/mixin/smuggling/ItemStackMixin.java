package us.spaceclouds42.zones.mixin.smuggling;

import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.s2c.play.ScreenHandlerSlotUpdateS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.UseAction;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import us.spaceclouds42.zones.duck.BuilderAccessor;

/**
 * Adds protections against consuming items when in builder mode. Prevents gaining potion effects and resaturating.
 */
@Mixin(ItemStack.class)
public class ItemStackMixin {
    @Shadow @Final private Item item;

    /**
     * Blocks eating and drinnking when in builder mode
     *
     * @param w the dimension this occurs in
     * @param user the entity consuming an item
     * @param cir returnable callback info
     */
    @Inject(
            method = "finishUsing",
            at = @At(
                    "HEAD"
            ),
            cancellable = true
    )
    private void blockEatingAndDrinkingItemsInBuilderMode(World w, LivingEntity user, CallbackInfoReturnable<ItemStack> cir) {
        if (w.isClient) {
            return;
        }
        
        UseAction action = item.getUseAction((ItemStack)(Object) this);
        if (user instanceof ServerPlayerEntity && ((BuilderAccessor) user).isInBuilderMode() && (action == UseAction.EAT || action == UseAction.DRINK)) {
            cir.setReturnValue((ItemStack)(Object) this);
        }
    }
}
    
