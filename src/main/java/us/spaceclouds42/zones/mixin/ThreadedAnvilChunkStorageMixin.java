package us.spaceclouds42.zones.mixin;

import net.minecraft.network.Packet;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ThreadedAnvilChunkStorage;
import net.minecraft.world.chunk.WorldChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import us.spaceclouds42.zones.data.ZoneManager;
import us.spaceclouds42.zones.data.spec.PosI;
import us.spaceclouds42.zones.data.spec.Zone;
import us.spaceclouds42.zones.duck.BuilderAccessor;

/**
 * Ensures cloaked blocks stay cloaked when update packets are sent
 */
@Mixin(ThreadedAnvilChunkStorage.class)
public class ThreadedAnvilChunkStorageMixin {
    /**
     * Hides cloaked blocks in chunk data packets
     * 
     * @param player receiver of packet
     * @param packets the packets being sent
     * @param chunk the chunk that the packets are sending data of
     * @param ci callback info
     */
    @Inject(
            method = "sendChunkDataPackets",
            at = @At(
                    "TAIL"
            )
    )
    private void hideBlocks(ServerPlayerEntity player, Packet<?>[] packets, WorldChunk chunk, CallbackInfo ci) {
        if (((BuilderAccessor) player).isBuilder()) {
            return;
        }
        
        PosI start = new PosI(
            chunk.getWorld().getRegistryKey().getValue().toString(),
            chunk.getPos().getStartX(),
            0,
            chunk.getPos().getStartZ()
        );
        PosI end = new PosI(
            chunk.getWorld().getRegistryKey().getValue().toString(),
            chunk.getPos().getEndX(),
            256,
            chunk.getPos().getEndZ()
        );
        
        for (Zone zone : ZoneManager.INSTANCE.getCloakedZonesIntersecting(start.toPosD(), end.toPosD())) {
            zone.hideArea(player, start, end);
        }
    }
}
