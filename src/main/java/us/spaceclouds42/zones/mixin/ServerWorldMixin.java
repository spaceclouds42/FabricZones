package us.spaceclouds42.zones.mixin;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import us.spaceclouds42.zones.access.BuilderAccessor;
import us.spaceclouds42.zones.data.BuilderManager;

@Mixin(ServerWorld.class)
abstract class ServerWorldMixin {
    @Inject(
            method = "onPlayerConnected",
            at = @At(
                    value = "HEAD"
            )
    )
    private void addBuilderOnConnect(ServerPlayerEntity player, CallbackInfo ci) {
        if (((BuilderAccessor) player).isInBuilderMode()) {
            BuilderManager.INSTANCE.addPlayer(player);
        }
    }

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
