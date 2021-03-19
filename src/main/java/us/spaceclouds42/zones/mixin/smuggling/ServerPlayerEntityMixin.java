package us.spaceclouds42.zones.mixin.smuggling;

import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Formatting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import us.spaceclouds42.zones.duck.BuilderAccessor;

import java.util.OptionalInt;

/**
 *  Prevents builders from dropping items from builder mode.
 *  Prevents builders from using containers to store items.
 */
@Mixin(ServerPlayerEntity.class)
abstract class ServerPlayerEntityMixin {
    /**
     * The player that the mixin is mixed-in to
     */
    @Unique private final ServerPlayerEntity thisPlayer = (ServerPlayerEntity) (Object) this;

    /**
     * Prevents builders from dropping items, which also extends to items not dropping on death
     *
     * @param stack the item attempted to be dropped
     * @param throwRandomly whether or not throw direction of item should be random
     * @param retainOwnership if item can only be picked up by thrower
     * @param cir callback info (returnable)
     */
    @Inject(
            method = "dropItem",
            at = @At(
                    value = "HEAD"
            ),
            cancellable = true
    )
    private void preventDropItem(ItemStack stack, boolean throwRandomly, boolean retainOwnership, CallbackInfoReturnable<ItemEntity> cir) {
        if (((BuilderAccessor) thisPlayer).isInBuilderMode()) {
            cir.setReturnValue(null);
        }
    }

    /**
     * Prevents builder from using containers and other handled guis
     *
     * @param factory the handled screen
     * @param cir callback info (returnable)
     */
    @Inject(
            method = "openHandledScreen",
            at = @At(
                    value = "HEAD"
            ),
            cancellable = true
    )
    private void preventOpeningScreenWhenBuilder(NamedScreenHandlerFactory factory, CallbackInfoReturnable<OptionalInt> cir) {
        if (((BuilderAccessor) thisPlayer).isInBuilderMode()) {
            thisPlayer.sendMessage((new LiteralText("Cannot open in builder mode")).formatted(Formatting.RED), true);
            cir.setReturnValue(OptionalInt.empty());
        }
    }
}
