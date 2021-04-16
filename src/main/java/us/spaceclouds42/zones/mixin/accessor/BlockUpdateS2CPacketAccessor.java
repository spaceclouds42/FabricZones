package us.spaceclouds42.zones.mixin.accessor;

import net.minecraft.block.BlockState;
import net.minecraft.network.packet.s2c.play.BlockUpdateS2CPacket;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(BlockUpdateS2CPacket.class)
public interface BlockUpdateS2CPacketAccessor {
    @Accessor
    void setState(BlockState state);

    @Accessor("state")
    BlockState fabriczones$getState();

    @Accessor("pos")
    BlockPos fabriczones$getPos();
}
