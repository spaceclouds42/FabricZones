package us.spaceclouds42.zones.mixin.griefing;

import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LightningEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import us.spaceclouds42.zones.data.ZoneManager;

@Mixin(LightningEntity.class)
public class LightningEntityMixin {
    @Redirect(method = "spawnFire", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/BlockState;canPlaceAt(Lnet/minecraft/world/WorldView;Lnet/minecraft/util/math/BlockPos;)Z"))
    private boolean dontSpawnFireInZones(BlockState state, WorldView view, BlockPos pos) {
        if (!state.canPlaceAt(view, pos))
            return false;

        return ZoneManager.INSTANCE.getZone((World) view, pos) == null;
    }

    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;onStruckByLightning(Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/entity/LightningEntity;)V"))
    private void dontSetEntitiesOnFireInZones(Entity entity, ServerWorld world, LightningEntity lightning) {
        if (ZoneManager.INSTANCE.getZone(entity) == null)
            entity.onStruckByLightning(world, lightning);
    }
}
