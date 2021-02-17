package us.spaceclouds42.zones.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.util.hit.HitResult;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import us.spaceclouds42.zones.data.BuilderManager;
import us.spaceclouds42.zones.data.ZoneManager;
import us.spaceclouds42.zones.data.spec.PosD;
import us.spaceclouds42.zones.data.spec.Zone;
import us.spaceclouds42.zones.data.spec.ZoneAccessMode;

/**
 * Prevent projectiles from landing in zones under certain conditions
 */
@Mixin(ProjectileEntity.class)
abstract class ProjectileEntityMixin {
    @Shadow @Nullable public abstract Entity getOwner();

    /**
     * Checks certain conditions, and if true, cancels the collision
     *
     * @param hitResult what the projectile will collide with
     * @param ci callback info
     */
    @Inject(
            method = "onCollision",
            at = @At(
                    value = "HEAD"
            ),
            cancellable = true
    )
    private void preventCollision(HitResult hitResult, CallbackInfo ci) {
        if (getOwner() != null && BuilderManager.INSTANCE.getOnlineBuilders().contains(getOwner().getUuid())) { return; }

        Zone zone = ZoneManager.INSTANCE.getZone(
                new PosD(
                        ((Entity) (Object) this).getEntityWorld().getRegistryKey().getValue().toString(),
                        hitResult.getPos().x,
                        hitResult.getPos().y,
                        hitResult.getPos().z
                )
        );

        if (zone == null) { return; }

        if (zone.getAccessMode() != ZoneAccessMode.EVERYONE) {
            ci.cancel();
            ((Entity) (Object) this).kill();
        }
    }
}
