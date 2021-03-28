package us.spaceclouds42.zones.mixin.smuggling;

import net.minecraft.entity.LivingEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import us.spaceclouds42.zones.duck.BuilderAccessor;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {
    @Inject(method = "addStatusEffect", at = @At("HEAD"), cancellable = true)
    private void disallowEffectsWhileInBuilderMode(CallbackInfoReturnable<Boolean> ci) {
        if (((Object) this) instanceof ServerPlayerEntity && ((BuilderAccessor) this).isInBuilderMode()) {
            ci.setReturnValue(false);
        }
    }
}
