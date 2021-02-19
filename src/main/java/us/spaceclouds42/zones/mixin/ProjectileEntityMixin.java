package us.spaceclouds42.zones.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.*;
import net.minecraft.entity.projectile.thrown.*;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.hit.HitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import us.spaceclouds42.zones.ConstantsKt;
import us.spaceclouds42.zones.data.BuilderManager;
import us.spaceclouds42.zones.data.ZoneManager;
import us.spaceclouds42.zones.data.spec.PosD;
import us.spaceclouds42.zones.data.spec.Zone;
import us.spaceclouds42.zones.data.spec.ZoneAccessMode;
import us.spaceclouds42.zones.log.LogMode;

/**
 * Prevent projectiles from landing in zones under certain conditions
 */
@Mixin(
        {
                ProjectileEntity.class,
                DragonFireballEntity.class,
                EggEntity.class,
                EnderPearlEntity.class,
                ExperienceBottleEntity.class,
                FireballEntity.class,
                PotionEntity.class,
                ShulkerBulletEntity.class,
                SmallFireballEntity.class,
                SnowballEntity.class,
                WitherSkullEntity.class
        }
)
abstract class ProjectileEntityMixin {
    /**
     * Gets the proper owner because persistent projectile entities are funky
     *
     * @param entity the projectile that's owner is requested
     * @return the owner of the projectile entity
     */
    private Entity getOwner(ProjectileEntity entity) {
        if (entity instanceof PersistentProjectileEntity) {
            return ((PersistentProjectileEntity) entity).getOwner();
        }

        return entity.getOwner();
    }

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
        Zone zone = ZoneManager.INSTANCE.getZone(
                new PosD(
                        ((Entity) (Object) this).getEntityWorld().getRegistryKey().getValue().toString(),
                        hitResult.getPos().x,
                        hitResult.getPos().y,
                        hitResult.getPos().z
                )
        );

        if (
                notBuilder(getOwner((ProjectileEntity) (Object) this)) &&
                zone != null &&
                zone.getAccessMode() != ZoneAccessMode.EVERYONE
        ) {
            ci.cancel();
            ((Entity) (Object) this).discard();

            // Prevents players from permanently losing their tridents
            try {
                if (getOwner((ProjectileEntity) (Object) this) instanceof PlayerEntity && !((PlayerEntity) getOwner((ProjectileEntity) (Object) this)).isCreative()) {
                    ((PlayerEntity) getOwner((ProjectileEntity) (Object) this)).giveItemStack(((TridentEntityAccessor) this).invokeAsItemStack());
                }
            } catch (ClassCastException ignored) { }

            // Lets players know why they're projectile was removed by displaying zone borders
            if (getOwner((ProjectileEntity) (Object) this) instanceof ServerPlayerEntity) {
                zone.renderBorders((ServerPlayerEntity) getOwner((ProjectileEntity) (Object) this));
            }
        }
    }

    /**
     * Tells if an entity is a builder, used for determining if shooter of projectile is builder or not
     *
     * @param entity the checked entity
     * @return true if NOT a builder
     */
    private boolean notBuilder(Entity entity) {
        return !(entity != null && BuilderManager.INSTANCE.getOnlineBuilders().contains(entity.getUuid()));
    }
}
