package us.spaceclouds42.zones.mixin.griefing;

import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.LavaFluid;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import us.spaceclouds42.zones.data.ZoneManager;

import java.util.Random;

/**
 * Prevents fire spawning from lava in zones and lava from outside affecting flammable blocks inside a zone
 */
@Mixin(LavaFluid.class)
abstract class LavaFluidMixin {
    /**
     * Prevents zoned lava from causing fire
     *
     * @param gameRules the current gamerules of the server
     * @param rule the current DO_FIRE_TICK rule
     * @param world dimension of this lava
     * @param pos location of this lava
     * @param state fluid state of this lava
     * @param random rng
     * @return whether or not the fire should spread
     */
    @Redirect(
            method = "onRandomTick",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/GameRules;getBoolean(Lnet/minecraft/world/GameRules$Key;)Z"
            )
    )
    private boolean stopFireSpread(GameRules gameRules, GameRules.Key<GameRules.BooleanRule> rule, World world, BlockPos pos, FluidState state, Random random) {
        if (!world.getGameRules().getBoolean(GameRules.DO_FIRE_TICK)) {
            return false;
        } else {
            return ZoneManager.INSTANCE.getZone(world, pos) == null;
        }
    }

    /**
     * Prevents burning of blocks inside a zone from lava outside a zone
     *
     * @param world dimension of the block
     * @param pos location of the block
     * @param cir callback info returnable
     */
    @Inject(
            method = "hasBurnableBlock",
            at = @At(
                    value = "HEAD"
            ),
            cancellable = true
    )
    private void preventBurningZonedBlocks(WorldView world, BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        if (world instanceof World && ZoneManager.INSTANCE.getZone((World) world, pos) != null) {
            cir.setReturnValue(false);
        }
    }
}
