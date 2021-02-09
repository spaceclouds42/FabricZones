package us.spaceclouds42.builders.mixin;

import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import us.spaceclouds42.builders.ConstantsKt;
import us.spaceclouds42.builders.data.ZoneManager;
import us.spaceclouds42.builders.data.spec.Zone;
import us.spaceclouds42.builders.log.LogMode;

import java.util.Collection;

@Mixin(ServerPlayNetworkHandler.class)
abstract class ServerPlayNetworkHandlerMixin {
    /**
     * The handled player
     */
    @Shadow public ServerPlayerEntity player;

    @Shadow @Final private static Logger LOGGER;
    /**
     * Whether or not the player is currently in a zone. If so,
     * the zone borders are rendered
     */
    @Unique public Boolean inZone = false;

    /**
     * The zone that the player is currently in, if they are in a zone
     */
    @Unique private Zone playerZone;

    /**
     * Checks if a player has moved into or out of a zone
     *
     * @param packet the player's movement packet
     * @param ci callback info
     */
    @Inject(
            method = "onPlayerMove",
            at = @At(
                    value = "TAIL"
            )
    )
    private void detectPlayerInZone(PlayerMoveC2SPacket packet, CallbackInfo ci) {
        // TODO: Skip if pos is same

        Collection<Zone> zones = ZoneManager.INSTANCE.getAllZones().values();

        for (Zone zone : zones) {
            ConstantsKt.LOGGER.info("Checking if " + player.getEntityName() + " is in " + zone.getId(), LogMode.WTF);
            if (zone.playerInZone(player)) {
                inZone = true;
                playerZone = zone;
                ConstantsKt.LOGGER.info("Player in zone '" + zone.getId() + "'", LogMode.WTF);
                playerZone.renderBorders(player);
                break;
            } else {
                inZone = false;
                playerZone = null;
            }
        }
    }
}
