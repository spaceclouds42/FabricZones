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

    /**
     * Whether or not the player is currently in a zone. If so,
     * the zone borders are rendered
     */
    @Unique private Boolean inZone = false;

    /**
     * The zone that the player is currently in, if they are in a zone
     */
    @Unique private Zone playerZone;

    /**
     * The number of ticks since the last time the zones borders were rendered
     */
    @Unique private int lastRenderTick = 0;

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
        // TODO: Figure out how to skip check when player pos is unchanged

        Collection<Zone> zones = ZoneManager.INSTANCE.getAllZones().values();

        for (Zone zone : zones) {
            ConstantsKt.LOGGER.info("Checking if " + player.getEntityName() + " is in " + zone.getId(), LogMode.WTF);
            if (zone.playerInZone(player)) {
                inZone = true;
                playerZone = zone;
                ConstantsKt.LOGGER.info("Player in zone '" + zone.getId() + "'", LogMode.WTF);
                break;
            } else {
                inZone = false;
                playerZone = null;
            }
        }
    }

    /**
     * Renders the borders of the zone that this player is in
     *
     * @param ci callback info
     */
    @Inject(
            method = "tick",
            at = @At(
                    value = "TAIL"
            )
    )
    private void renderBorders(CallbackInfo ci) {
        if (inZone && lastRenderTick++ > 4) {
            playerZone.renderBorders(player);
            lastRenderTick = 0;
        }
    }
}