package us.spaceclouds42.zones.mixin.griefing;

import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;
import net.minecraft.world.explosion.ExplosionBehavior;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import us.spaceclouds42.zones.data.ZoneManager;
import us.spaceclouds42.zones.data.spec.PosD;

@Mixin(Explosion.class)
public class ExplosionMixin {
    @Shadow @Mutable @Final private Explosion.DestructionType destructionType;
    @Shadow @Mutable @Final private float power;

    @Inject(method = "<init>(Lnet/minecraft/world/World;Lnet/minecraft/entity/Entity;Lnet/minecraft/entity/damage/DamageSource;Lnet/minecraft/world/explosion/ExplosionBehavior;DDDFZLnet/minecraft/world/explosion/Explosion$DestructionType;)V", at = @At("RETURN"))
    private void init(World world, Entity entity, DamageSource damageSource, ExplosionBehavior explosionBehavior, double x, double y, double z, float power, boolean createFire, Explosion.DestructionType destructionType, CallbackInfo ci) {
        if (ZoneManager.INSTANCE.getZone(
            new PosD(
                world.getRegistryKey().getValue().toString(),
                x,
                y,
                z
            )
        ) != null) {
            // Explosion is in zone
            this.destructionType = Explosion.DestructionType.NONE;
            this.power = 0;
        }
    }

    @Redirect(method = "collectBlocksAndDamageEntities", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/explosion/ExplosionBehavior;canDestroyBlock(Lnet/minecraft/world/explosion/Explosion;Lnet/minecraft/world/BlockView;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;F)Z"))
    private boolean dontDestroyZonedBlocks(ExplosionBehavior explosionBehavior, Explosion explosion, BlockView world, BlockPos pos, BlockState state, float power) {
        if (!explosionBehavior.canDestroyBlock(explosion, world, pos, state, power)) return false;

        return ZoneManager.INSTANCE.getZone((World) world, pos) == null;
    }

    @Redirect(method = "collectBlocksAndDamageEntities", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;isImmuneToExplosion()Z"))
    private boolean dontDamageZonedEntities(Entity entity) {
        if (entity.isImmuneToExplosion()) return true;

        return ZoneManager.INSTANCE.getZone(
            new PosD(
                entity.world.getRegistryKey().getValue().toString(),
                entity.getX(),
                entity.getY(),
                entity.getZ()
            )
        ) != null;
    }
}
