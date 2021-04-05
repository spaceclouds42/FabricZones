package us.spaceclouds42.zones.mixin.smuggling;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.network.packet.s2c.play.EntityStatusEffectS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import us.spaceclouds42.zones.duck.BuilderAccessor;

/**
 * Adds protections against builders abusing the safety of builder mode to wait out potion effects.
 */
@Mixin(StatusEffectInstance.class)
public abstract class StatusEffectInstanceMixin {
    @Shadow protected abstract int updateDuration();

    @Shadow private int duration;

    @Unique private int updateTime = 20;
    
    /**
     * Pauses potion effect timer when in builder mode
     * 
     * @param instance the potion effect
     * @param entity the entity with potion effect
     * @param overwriteCallback manages potion effect changes
     */
    @Redirect(
            method = "update",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/effect/StatusEffectInstance;updateDuration()I"
            )
    )
    private int dontUpdateEffectsInBuilderMode(StatusEffectInstance instance, LivingEntity entity, Runnable overwriteCallback) {
        if (entity instanceof ServerPlayerEntity && ((BuilderAccessor) entity).isInBuilderMode()) {
            if (--updateTime <= 0) {
                ((ServerPlayerEntity) entity).networkHandler.sendPacket(new EntityStatusEffectS2CPacket(entity.getEntityId(), instance));
                updateTime = 20;
            }
            return duration;
        }
        return updateDuration();
    }
}
