package us.spaceclouds42.zones.mixin.griefing;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FluidBlock;
import net.minecraft.fluid.FlowableFluid;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import us.spaceclouds42.zones.data.ZoneManager;
import us.spaceclouds42.zones.data.spec.Zone;

/**
 * Prevents obsidian, cobblestone, and basalt formation when liquids/blocks are not in the same zone
 */
@Mixin(FluidBlock.class)
abstract class FluidBlockMixin {
    @Shadow @Final protected FlowableFluid fluid;

    @Shadow protected abstract void playExtinguishSound(WorldAccess world, BlockPos pos);

    /**
     * @author fabriczones
     * @reason i dont know how to do this the right way
     */
    @Overwrite
    private boolean receiveNeighborFluids(World world, BlockPos pos, BlockState state) {
        if (this.fluid.isIn(FluidTags.LAVA)) {
            boolean bl = world.getBlockState(pos.down()).isOf(Blocks.SOUL_SOIL);
            Direction[] var5 = Direction.values();
            int var6 = var5.length;

            for (int var7 = 0; var7 < var6; ++var7) {
                Direction direction = var5[var7];
                if (direction != Direction.DOWN) {
                    BlockPos blockPos = pos.offset(direction);

                    Zone zoneFrom = ZoneManager.INSTANCE.getZone(world, pos);
                    Zone zoneTo = ZoneManager.INSTANCE.getZone(world, blockPos);

                    if (zoneFrom != zoneTo) {
                        continue;
                    }

                    if (world.getFluidState(blockPos).isIn(FluidTags.WATER)) {
                        Block block = world.getFluidState(pos).isStill() ? Blocks.OBSIDIAN : Blocks.COBBLESTONE;
                        world.setBlockState(pos, block.getDefaultState());
                        this.playExtinguishSound(world, pos);
                        return false;
                    }

                    if (bl && world.getBlockState(blockPos).isOf(Blocks.BLUE_ICE)) {
                        world.setBlockState(pos, Blocks.BASALT.getDefaultState());
                        this.playExtinguishSound(world, pos);
                        return false;
                    }
                }
            }
        }

        return true;
    }
}
