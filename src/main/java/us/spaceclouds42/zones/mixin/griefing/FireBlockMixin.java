package us.spaceclouds42.zones.mixin.griefing;

import net.minecraft.block.BlockState;
import net.minecraft.block.FireBlock;
import net.minecraft.block.TntBlock;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import us.spaceclouds42.zones.data.ZoneManager;

import java.util.Random;

/**
 * Prevents fire spreading and burning of blocks inside of zones and from fires in/out of zones intereacting with each other
 */
@Mixin(FireBlock.class)
abstract class FireBlockMixin {
    // not doing these javadocs right now, way too many params here O_O

    @Redirect(
            method = "trySpreadingFire",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/World;setBlockState(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;I)Z"
            )
    )
    private boolean stopSpreadFromZonedFire(World world, BlockPos pos, BlockState state, int flags, World world2, BlockPos pos2, int spreadFactor, Random rand, int currentAge) {
        if (ZoneManager.INSTANCE.getZone(world, pos) == null) {
            return world.setBlockState(pos, state, flags);
        }
        return false;
    }

    @Redirect(
            method = "trySpreadingFire",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/World;removeBlock(Lnet/minecraft/util/math/BlockPos;Z)Z"
            )
    )
    private boolean preventFireDestroyZonedBlocks(World world, BlockPos pos, boolean move) {
        if (ZoneManager.INSTANCE.getZone(world, pos) == null) {
            return world.removeBlock(pos, move);
        }
        return false;
    }

    @Redirect(
            method = "trySpreadingFire",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/block/TntBlock;primeTnt(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;)V"
            )
    )
    private void preventFireLightingZonedTnt(World world, BlockPos pos) {
        if (ZoneManager.INSTANCE.getZone(world, pos) == null) {
            TntBlock.primeTnt(world, pos);
        }
    }

    @Redirect(
            method = "scheduledTick",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/GameRules;getBoolean(Lnet/minecraft/world/GameRules$Key;)Z"
            )
    )
    private boolean preventFireTickFromZone(GameRules gameRules, GameRules.Key<GameRules.BooleanRule> rule, BlockState state, ServerWorld world, BlockPos pos, Random random) {
        return world.getGameRules().getBoolean(GameRules.DO_FIRE_TICK) && ZoneManager.INSTANCE.getZone(world, pos) == null;
    }

    @Redirect(
            method = "scheduledTick",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/world/ServerWorld;setBlockState(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;I)Z",
                    ordinal = 1
            )
    )
    private boolean preventFireTickFromZone(ServerWorld serverWorld, BlockPos mutable, BlockState stateWithAge, int flags, BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if (ZoneManager.INSTANCE.getZone(world, mutable) == null) {
            return world.setBlockState(mutable, stateWithAge, flags);
        }
        return false;
    }
}
