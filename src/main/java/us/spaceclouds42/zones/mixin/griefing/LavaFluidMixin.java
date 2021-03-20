package us.spaceclouds42.zones.mixin.griefing;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FluidBlock;
import net.minecraft.fluid.FlowableFluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.LavaFluid;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import us.spaceclouds42.zones.data.ZoneManager;
import us.spaceclouds42.zones.data.spec.Zone;

/**
 * Prevents stone formation between lava/water when lava/water are not in same zone
 */
@Mixin(LavaFluid.class)
abstract class LavaFluidMixin extends FlowableFluid {
    @Shadow protected abstract void playExtinguishEvent(WorldAccess world, BlockPos pos);

    /**
     * @author fabriczones
     * @reason i again dont understand mixins well enough to know what i should actually be doing
     */
    public void flow(WorldAccess world, BlockPos pos, BlockState state, Direction direction, FluidState fluidState) {
        if (direction == Direction.DOWN) {
            FluidState fluidState2 = world.getFluidState(pos);

            Zone zoneFrom = ZoneManager.INSTANCE.getZone(
                    (World) world,
                    new BlockPos(
                            pos.getX(),
                            pos.getY() + 1,
                            pos.getZ()
                    )
            );
            Zone zoneTo = ZoneManager.INSTANCE.getZone(
                    (World) world,
                    pos
            );

            if (zoneFrom != zoneTo) {
                super.flow(world, pos, state, direction, fluidState);
                return;
            }

            if (this.isIn(FluidTags.LAVA) && fluidState2.isIn(FluidTags.WATER)) {
                if (state.getBlock() instanceof FluidBlock) {
                    world.setBlockState(pos, Blocks.STONE.getDefaultState(), 3);
                }

                this.playExtinguishEvent(world, pos);
                return;
            }
        }

        super.flow(world, pos, state, direction, fluidState);
    }
}
