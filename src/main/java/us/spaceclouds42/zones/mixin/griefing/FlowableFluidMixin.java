package us.spaceclouds42.zones.mixin.griefing;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.fluid.FlowableFluid;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import us.spaceclouds42.zones.data.ZoneManager;
import us.spaceclouds42.zones.data.spec.Zone;

/**
 * Prevents fluids from flowing in to/out of zones
 */
@Mixin(FlowableFluid.class)
abstract class FlowableFluidMixin {
    /**
     * Blocks fluid flowing if result is in a different zone
     *
     * @param blockView world that fluid is in
     * @param fluidPos block pos of fluid before flowing
     * @param fluidBlockState block state of fluid
     * @param flowDirection direction that fluid will flow
     * @param flowTo block pos of fluid flowing result
     * @param flowToBlockState block state of fluid flowing result
     * @param fluidState the fluid state at the flowing result block position before fluid flows
     * @param fluid fluid type
     * @param cir callback info returnable
     */
    @Inject(
            method = "canFlow",
            at = @At(
                    value = "RETURN"
            ),
            cancellable = true
    )
    private void disallowFlowingBetweenZones(BlockView blockView, BlockPos fluidPos, BlockState fluidBlockState, Direction flowDirection, BlockPos flowTo, BlockState flowToBlockState, FluidState fluidState, Fluid fluid, CallbackInfoReturnable<Boolean> cir) {
        if (!cir.getReturnValue() || !(blockView instanceof World)) {
            return;
        }

        World world = (World) blockView;

        Zone zoneFrom = ZoneManager.INSTANCE.getZone(world, fluidPos);
        Zone zoneTo = ZoneManager.INSTANCE.getZone(world, flowTo);

        if (zoneFrom != zoneTo) {
            cir.setReturnValue(false);
        }
    }

    @Redirect(
            method = "getUpdatedState",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/WorldView;getBlockState(Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/block/BlockState;",
                    ordinal = 0
            )
    )
    private BlockState disallowMergingBetweenZonesHorizontal(WorldView view, BlockPos posTo, WorldView world2, BlockPos posFrom, BlockState state) {
        return disallowMergingBetweenZones(view, posTo, world2, posFrom, state);
    }

    @Redirect(
            method = "getUpdatedState",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/WorldView;getBlockState(Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/block/BlockState;",
                    ordinal = 2
            )
    )
    private BlockState disallowMergingBetweenZonesVertical(WorldView view, BlockPos posTo, WorldView world2, BlockPos posFrom, BlockState state) {
        return disallowMergingBetweenZones(view, posTo, world2, posFrom, state);
    }
    
    private BlockState disallowMergingBetweenZones(WorldView view, BlockPos posTo, WorldView world2, BlockPos posFrom, BlockState state) {
        if (!(view instanceof World)) {
            return view.getBlockState(posTo);
        }

        World world = (World) view;

        Zone zoneFrom = ZoneManager.INSTANCE.getZone(world, posFrom);
        Zone zoneTo = ZoneManager.INSTANCE.getZone(world, posTo);

        if (zoneFrom != zoneTo) {
            return Blocks.AIR.getDefaultState();
        } else {
            return world.getBlockState(posTo);
        }
    }
}
