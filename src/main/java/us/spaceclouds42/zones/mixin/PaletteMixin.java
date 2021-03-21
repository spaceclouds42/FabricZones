/*
package us.spaceclouds42.zones.mixin;

import me.jellysquid.mods.lithium.common.world.chunk.LithiumHashPalette;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.chunk.ArrayPalette;
import net.minecraft.world.chunk.BiMapPalette;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import us.spaceclouds42.zones.data.ZoneManager;

@Mixin(
        value = {
                ArrayPalette.class,
                BiMapPalette.class,
                LithiumHashPalette.class
        }
        )
abstract class PaletteMixin<T> {
    @Shadow public abstract int getIndex(T object);

    @ModifyArg(
            method = {
                    "toPacket",
                    "getPacketSize"
            },
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/util/collection/IdList;getRawId(Ljava/lang/Object;)I"
            )
    )
    public T GetIdRedirect(T object) {
        if (object instanceof BlockState) {
            for (Vec3i block : ZoneManager.INSTANCE.getCloakedBlocks()) {
                int x = block.getX() & 15;
                int y = block.getY() & 15;
                int z = block.getZ() & 15;
                int index = y << 8 | z << 4 | x;

                if (index == this.getIndex(object)) {
                    return (T) Blocks.AIR.getDefaultState();
                }
            }
        }
        return object;
    }
}
*/