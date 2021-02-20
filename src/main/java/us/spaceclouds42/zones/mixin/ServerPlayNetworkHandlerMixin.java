package us.spaceclouds42.zones.mixin;

import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.network.packet.c2s.play.VehicleMoveC2SPacket;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import us.spaceclouds42.zones.ConstantsKt;
import us.spaceclouds42.zones.data.BuilderManager;
import us.spaceclouds42.zones.data.ZoneManager;
import us.spaceclouds42.zones.data.spec.Zone;
import us.spaceclouds42.zones.data.spec.ZoneAccessMode;
import us.spaceclouds42.zones.log.LogMode;

import java.util.Collection;

/**
 * Detects players in zones. Renders borders and kicks them out if not allowed.
 */
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
     * Prevent TOCTOU
     */
    @Unique private boolean detecting = false;

    /**
     * Checks if a player has moved into a zone, and prevents players from doing so if they do not have access
     *
     * @param packet the player's movement packet
     * @param ci callback info
     */
    @Inject(
            method = "onPlayerMove",
            at = @At(
                    value = "HEAD"
            ),
            cancellable = true
    )
    private void detectPlayerInZone(PlayerMoveC2SPacket packet, CallbackInfo ci) {
        if (detecting) { return; }
        detecting = true;

        if (packet instanceof PlayerMoveC2SPacket.LookOnly) {
            detecting = false;
            return;
        }

        Collection<Zone> zones = ZoneManager.INSTANCE.getAllZones().values();
        PlayerMoveC2SPacketAccessor packetAccessor = (PlayerMoveC2SPacketAccessor) packet;

        for (Zone zone : zones) {
            ConstantsKt.LOGGER.info("Checking if " + player.getEntityName() + " is in " + zone.getId(), LogMode.WTF);
            if (zone.positionInZone(player.world, packetAccessor.getX(), packetAccessor.getY(), packetAccessor.getZ())) {
                inZone = true;
                playerZone = zone;
                ConstantsKt.LOGGER.info("Player in zone '" + zone.getId() + "'", LogMode.WTF);
                ConstantsKt.LOGGER.warn("Value of playerZone.id: " + playerZone.getId(), LogMode.WTF);
                ConstantsKt.LOGGER.warn("Value of playerZone.accessMode: " + playerZone.getAccessMode(), LogMode.WTF);
                ConstantsKt.LOGGER.warn("Value of getOnlineBuilders(): " + BuilderManager.INSTANCE.getOnlineBuilders().toString(), LogMode.WTF);
                ConstantsKt.LOGGER.warn("Value of player: " + player, LogMode.WTF);
                ConstantsKt.LOGGER.warn("Value of player.uuid: " + player.getUuid(), LogMode.WTF);
                if (playerZone.getAccessMode() != ZoneAccessMode.EVERYONE && !BuilderManager.INSTANCE.getOnlineBuilders().contains(player.getUuid())) {
                    ConstantsKt.LOGGER.warn(zone.getId() + " is restricted and " + player.getEntityName() + " is not a builder", LogMode.WTF);
                    playerZone.renderBorders(player);
                    ci.cancel();
                    ConstantsKt.LOGGER.warn("Player move cancelled", LogMode.WTF);
                    if (playerZone.positionInZone(player.world, player.getX(), player.getY(), player.getZ())) {
                        playerZone.removePlayer(player);
                        ConstantsKt.LOGGER.warn("Player has been removed", LogMode.WTF);
                    } else {
                        player.requestTeleport(player.getX(), player.getY(), player.getZ());
                        ConstantsKt.LOGGER.warn("Player was set back", LogMode.WTF);
                    }
                    inZone = false;
                    playerZone = null;
                }
                break;
            } else {
                inZone = false;
                playerZone = null;
            }
        }

        detecting = false;
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
