package us.spaceclouds42.zones.mixin.smuggling;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import us.spaceclouds42.zones.data.spec.Builder;
import us.spaceclouds42.zones.duck.BuilderAccessor;
import us.spaceclouds42.zones.data.BuilderManager;

/**
 * Ensures that builders cannot stay in builder mode by relogging or teleporting between dimensions
 */
@Mixin(ServerWorld.class)
abstract class ServerWorldMixin {
    /**
     * Deactivates builder mode when a player joins, teleports, changes dimension, or respawns
     *
     * @param player the player that is being added to the world
     * @param ci callback info
     */
    @Inject(
            method = "addPlayer",
            at = @At(
                    value = "HEAD"
            )
    )
    private void disableBuilderModeOnRespawn(ServerPlayerEntity player, CallbackInfo ci) {
        if (((BuilderAccessor) player).isInBuilderMode()) {
            Builder.Utils.deactivateBuilderMode(player);
        }
    }
}
