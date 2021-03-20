package us.spaceclouds42.zones.mixin.smuggling;

import net.minecraft.block.BlockState;
import net.minecraft.block.JukeboxBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import us.spaceclouds42.zones.data.ZoneManager;
import us.spaceclouds42.zones.duck.BuilderAccessor;

/**
 * Prevents builders from using creative mode and jukeboxes to dupe discs
 */
@Mixin(JukeboxBlock.class)
abstract class JukeboxBlockMixin {
    /**
     * Prevents jukeboxes from dropping the disc if located in a zone
     *
     * @param world the dimension this jukebox is in
     * @param pos the location of this jukebox
     * @param ci callback info
     */
    @Inject(
            method = "removeRecord",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/World;spawnEntity(Lnet/minecraft/entity/Entity;)Z"
            ),
            cancellable = true
    )
    private void preventDiscSpawning(World world, BlockPos pos, CallbackInfo ci) {
        if (ZoneManager.INSTANCE.getZone(world, pos) != null) {
            ci.cancel();
        }
    }

    /**
     * Stops players not in builder mode from stopping the music in a jukebox
     *
     * @param state the state of this jukebox block
     * @param world dimension this jukebox is in
     * @param pos location of this jukebox
     * @param player the player interacting with the jukebox
     * @param hand the hand of the player being used
     * @param hit the hit result of the player interacting with this block
     * @param cir callback info returnable
     */
    @Inject(
            method = "onUse",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/block/JukeboxBlock;removeRecord(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;)V"
            ),
            cancellable = true
    )
    private void preventStoppingMusic(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit, CallbackInfoReturnable<ActionResult> cir) {
        if (
                ZoneManager.INSTANCE.getZone(world, pos) != null &&
                player instanceof ServerPlayerEntity &&
                !((BuilderAccessor) player).isInBuilderMode()
        ) {
            cir.setReturnValue(ActionResult.PASS);
        }
    }
}
