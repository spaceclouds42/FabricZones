package us.spaceclouds42.zones.mixin;

import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import us.spaceclouds42.zones.data.BuilderManager;
import us.spaceclouds42.zones.data.ZoneManager;
import us.spaceclouds42.zones.data.spec.Zone;

/**
 * Prevents entering a zone by use of other entities
 */
@Mixin(Entity.class)
abstract class EntityMixin {
    /**
     * Prevents entity from riding another entity if other entity
     * is in a zone and the rider attempter is not allowed to be in that zone
     *
     * @param entity the to be ridden entity
     * @param cir returnable callback info
     */
    @Inject(
            method = "canStartRiding",
            at = @At(
                    value = "RETURN"
            ),
            cancellable = true
    )
    private void preventIllegalZoneRiding(Entity entity, CallbackInfoReturnable<Boolean> cir) {
        if (!entityIsBuilder((Entity) (Object) (this)) && entityInIllegalZone(entity)) {
            cir.setReturnValue(false);
        }
    }

    /**
     * Finds if an entity is in a restricted zone
     *
     * @param entity the entity to check
     * @return whether or not the entity is in a restricted zone
     */
    private boolean entityInIllegalZone(Entity entity) {
        for (Zone zone : ZoneManager.INSTANCE.getRestrictedZones()) {
            if (zone.positionInZone(entity.world, entity.getX(), entity.getY(), entity.getZ())) {
                return true;
            }
        }

        return false;
    }

    /**
     * Checks if entity is a builder
     *
     * @param entity checked entity
     * @return whether or not the entity's uuid is in the builder list
     */
    private boolean entityIsBuilder(Entity entity) {
        return BuilderManager.INSTANCE.getOnlineBuilders().contains(entity.getUuid());
    }
}
