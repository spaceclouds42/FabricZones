package us.spaceclouds42.zones.mixin;

import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.WorldChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.function.Consumer;

@Mixin(WorldChunk.class)
public interface WorldChunkAccessor {
    @Accessor("sections")
    ChunkSection[] getSections();

    @Accessor("sections")
    void setSections(ChunkSection[] sections);

    @Accessor("loadToWorldConsumer")
    Consumer<WorldChunk> getLoadToWorldConsumer();
}
