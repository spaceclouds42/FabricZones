package us.spaceclouds42.zones.mixin.griefing;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.FireBlock;
import net.minecraft.block.TntBlock;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.*;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import us.spaceclouds42.zones.data.ZoneManager;

import java.util.Random;

@Mixin(FireBlock.class)
abstract class FireBlockMixin {
    @Shadow protected abstract int getSpreadChance(BlockState state);

    @Shadow protected abstract BlockState getStateWithAge(WorldAccess world, BlockPos pos, int age);

    @Shadow
    protected static int getFireTickDelay(Random random) {
        return 0;
    }

    @Shadow @Final public static IntProperty AGE;

    @Shadow protected abstract boolean isRainingAround(World world, BlockPos pos);

    @Shadow protected abstract boolean areBlocksAroundFlammable(BlockView world, BlockPos pos);

    @Shadow protected abstract boolean isFlammable(BlockState state);

    @Shadow protected abstract int getBurnChance(WorldView world, BlockPos pos);

    /**
     * @author me
     * @reason temp
     */
    @Overwrite
    private void trySpreadingFire(World world, BlockPos pos, int spreadFactor, Random rand, int currentAge) {
        int i = this.getSpreadChance(world.getBlockState(pos));
        if (rand.nextInt(spreadFactor) < i) {
            BlockState blockState = world.getBlockState(pos);
            if (rand.nextInt(currentAge + 10) < 5 && !world.hasRain(pos)) {
                int j = Math.min(currentAge + rand.nextInt(5) / 4, 15);

                // redirect setBlockState
                if (ZoneManager.INSTANCE.getZone(world, pos) == null) {
                    world.setBlockState(pos, this.getStateWithAge(world, pos, j), 3);
                }
            } else {
                // redirect removeBlock
                if (ZoneManager.INSTANCE.getZone(world, pos) == null) {
                    world.removeBlock(pos, false);
                }
            }

            Block block = blockState.getBlock();
            if (block instanceof TntBlock) {
                TntBlock var10000 = (TntBlock)block;
                // redirect primeTnt
                if (ZoneManager.INSTANCE.getZone(world, pos) == null) {
                    TntBlock.primeTnt(world, pos);
                }
            }
        }
    }

    /**
     * @author me
     * @reason temp
     */
    @Overwrite
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        world.getBlockTickScheduler().schedule(pos, (FireBlock)(Object)this, getFireTickDelay(world.random));
        // redirect getBoolean to return this instead
        if (world.getGameRules().getBoolean(GameRules.DO_FIRE_TICK) && ZoneManager.INSTANCE.getZone(world, pos) == null) {
            if (!state.canPlaceAt(world, pos)) {
                world.removeBlock(pos, false);
            }

            BlockState blockState = world.getBlockState(pos.down());
            boolean bl = blockState.isIn(world.getDimension().getInfiniburnBlocks());
            int i = (Integer)state.get(AGE);
            if (!bl && world.isRaining() && this.isRainingAround(world, pos) && random.nextFloat() < 0.2F + (float)i * 0.03F) {
                world.removeBlock(pos, false);
            } else {
                int j = Math.min(15, i + random.nextInt(3) / 2);
                if (i != j) {
                    state = (BlockState)state.with(AGE, j);
                    world.setBlockState(pos, state, 4);
                }

                if (!bl) {
                    if (!this.areBlocksAroundFlammable(world, pos)) {
                        BlockPos blockPos = pos.down();
                        if (!world.getBlockState(blockPos).isSideSolidFullSquare(world, blockPos, Direction.UP) || i > 3) {
                            world.removeBlock(pos, false);
                        }

                        return;
                    }

                    if (i == 15 && random.nextInt(4) == 0 && !this.isFlammable(world.getBlockState(pos.down()))) {
                        world.removeBlock(pos, false);
                        return;
                    }
                }

                boolean bl2 = world.hasHighHumidity(pos);
                int k = bl2 ? -50 : 0;
                this.trySpreadingFire(world, pos.east(), 300 + k, random, i);
                this.trySpreadingFire(world, pos.west(), 300 + k, random, i);
                this.trySpreadingFire(world, pos.down(), 250 + k, random, i);
                this.trySpreadingFire(world, pos.up(), 250 + k, random, i);
                this.trySpreadingFire(world, pos.north(), 300 + k, random, i);
                this.trySpreadingFire(world, pos.south(), 300 + k, random, i);
                BlockPos.Mutable mutable = new BlockPos.Mutable();

                for(int l = -1; l <= 1; ++l) {
                    for(int m = -1; m <= 1; ++m) {
                        for(int n = -1; n <= 4; ++n) {
                            if (l != 0 || n != 0 || m != 0) {
                                int o = 100;
                                if (n > 1) {
                                    o += (n - 1) * 100;
                                }

                                mutable.set((Vec3i)pos, l, n, m);
                                int p = this.getBurnChance(world, mutable);
                                if (p > 0) {
                                    int q = (p + 40 + world.getDifficulty().getId() * 7) / (i + 30);
                                    if (bl2) {
                                        q /= 2;
                                    }

                                    if (q > 0 && random.nextInt(o) <= q && (!world.isRaining() || !this.isRainingAround(world, mutable))) {
                                        int r = Math.min(15, i + random.nextInt(5) / 4);
                                        // redirect setBlockState
                                        if (ZoneManager.INSTANCE.getZone(world, mutable) == null) {
                                            world.setBlockState(mutable, this.getStateWithAge(world, mutable, r), 3);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

            }
        }
    }
}
