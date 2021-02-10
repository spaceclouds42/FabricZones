package us.spaceclouds42.builders.mixin;

import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;

/**
 * Prevents players from doing things that they're not allowed to do in zones
 */
@Mixin(ServerPlayerEntity.class)
public class ServerPlayerEntityMixin {

}
