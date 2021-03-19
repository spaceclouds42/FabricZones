package us.spaceclouds42.zones.mixin;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import us.spaceclouds42.zones.access.BuilderAccessor;
import us.spaceclouds42.zones.data.BuilderManager;

/**
 * Ensures that builders cannot stay in builder mode by relogging or teleporting between dimensions
 */
@Mixin(ServerWorld.class)
abstract class ServerWorldMixin {
    /**
     * Prevents a crash when joining in singleplayer with a new uuid
     *
     * @param player the player that joined
     * @param ci callback info
     */
    @Inject(
            method = "onPlayerConnected",
            at = @At(
                    value = "HEAD"
            )
    )
    private void addBuilderOnConnect(ServerPlayerEntity player, CallbackInfo ci) {
        if (((BuilderAccessor) player).isInBuilderMode() && !BuilderManager.INSTANCE.isBuilder(player.getUuid())) {
            BuilderManager.INSTANCE.addPlayer(player);
        }
    }

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
            BuilderManager.INSTANCE.getBuilder(player.getUuid()).deactivateBuilderMode(player);
        }
    }
}
