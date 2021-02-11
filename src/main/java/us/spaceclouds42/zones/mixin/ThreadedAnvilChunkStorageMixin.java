package us.spaceclouds42.zones.mixin;

import net.minecraft.block.Blocks;
import net.minecraft.network.Packet;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ThreadedAnvilChunkStorage;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.WorldChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;
import us.spaceclouds42.zones.data.BuilderManager;
import us.spaceclouds42.zones.data.ZoneManager;

import java.util.List;

@Mixin(ThreadedAnvilChunkStorage.class)
abstract class ThreadedAnvilChunkStorageMixin {
    @ModifyArgs(
            method = "sendChunkDataPackets",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/network/packet/s2c/play/ChunkDataS2CPacket;<init>(Lnet/minecraft/world/chunk/WorldChunk;)V"
            )
    )
    private void editChunkDataPacket(Args args, ServerPlayerEntity player, Packet<?>[] packets, WorldChunk chunk) {
        if (BuilderManager.INSTANCE.getOnlineBuilders().contains(player.getUuid())) {
            return;
        }

        WorldChunk fakeChunk = new WorldChunk(
                chunk.getWorld(),
                chunk.getPos(),
                chunk.getBiomeArray(),
                chunk.getUpgradeData(),
                chunk.getBlockTickScheduler(),
                chunk.getFluidTickScheduler(),
                chunk.getInhabitedTime(),
                ((WorldChunkAccessor) chunk).getSections(),
                ((WorldChunkAccessor) chunk).getLoadToWorldConsumer()
        );

        List<Vec3i> cloakedBlocks = ZoneManager.INSTANCE.getCloakedBlocks(fakeChunk);
        if (cloakedBlocks.isEmpty()) { return; }

        ChunkSection[] sections = ((WorldChunkAccessor) chunk).getSections();

        for (Vec3i xyz : cloakedBlocks) {
            int sectionIndex = fakeChunk.getSectionIndex(xyz.getY());

            sections[sectionIndex] = removeCloakedBlock(
                    new BlockPos(xyz.getX(), xyz.getY(), xyz.getZ()),
                    ((WorldChunkAccessor) fakeChunk).getSections()[sectionIndex]
            );
        }

        ((WorldChunkAccessor) fakeChunk).setSections(sections);

        args.set(0, fakeChunk);
    }

    private ChunkSection removeCloakedBlock(BlockPos pos, ChunkSection section) {
        section.setBlockState(pos.getX() & 15, pos.getY() & 15, pos.getZ() & 15, Blocks.AIR.getDefaultState(), false);
        return section;
    }
}
