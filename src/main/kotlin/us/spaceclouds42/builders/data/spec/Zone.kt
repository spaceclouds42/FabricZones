package us.spaceclouds42.builders.data.spec

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import net.minecraft.network.packet.s2c.play.ParticleS2CPacket
import net.minecraft.particle.DustParticleEffect
import net.minecraft.server.network.ServerPlayerEntity
import us.spaceclouds42.builders.ext.toRange
import us.spaceclouds42.builders.utils.Axis
import us.spaceclouds42.builders.utils.DoubleRange
import kotlin.math.max
import kotlin.math.min

/**
 * Zones, manageable areas where builders
 * can be allowed to use /gamemode builder,
 * and other protection settings like visibility
 */
@Serializable
data class Zone(
    /**
     * Name of the zone. Must be unique
     */
    override val id: String,

    /**
     * Start corner
     */
    val startPos: Pos,

    /**
     * End corner
     */
    val endPos: Pos,

    /**
     * Player that created the zone
     */
    val createdBy: String,

    /**
     * Determines which groups have access, and what level of access.
     *
     * @see ZoneAccessMode
     */
    var accessMode: ZoneAccessMode = ZoneAccessMode.EVERYONE,

    // Not to be implemented yet.. still very unsure of how I want this implemented
    //
    // Tracks any changes made to a zone made by
    // a builder. Config does have settings for
    // how much is logged, and only the last 20
    // changes are saved in memory, the rest are
    // saved to file
    //
    // val buildLog: MutableMap<LocalDateTime, BlockChange>,
) : IdentifiableDataSpecBase() {
    /**
     * A map of edges to their axis and position, calculated from the [startPos] and [endPos]
     */
    @Transient
    private val edges = mutableMapOf<Triple<Double, Double, Axis>, DoubleRange>()

    /**
     * Calculates the edges when the zone is initialized
     */
    init {
        val gX = max(startPos.x, endPos.x) + 0.5
        val gY = max(startPos.y, endPos.y) + 1.0
        val gZ = max(startPos.x, endPos.x) + 0.5
        val sX = min(startPos.x, endPos.x).toDouble()
        val sY = min(startPos.y, endPos.y).toDouble()
        val sZ = min(startPos.z, endPos.z).toDouble()

        // x edges
        val xRange = sX.toRange(gX)
        edges[Triple(gY, gZ, Axis.X)] = xRange
        edges[Triple(gY, sZ, Axis.X)] = xRange
        edges[Triple(sY, gZ, Axis.X)] = xRange
        edges[Triple(sY, sZ, Axis.X)] = xRange

        // y edges
        val yRange = sY.toRange(gY)
        edges[Triple(gX, gZ, Axis.Y)] = yRange
        edges[Triple(gX, sZ, Axis.Y)] = yRange
        edges[Triple(sX, gZ, Axis.Y)] = yRange
        edges[Triple(sX, sZ, Axis.Y)] = yRange

        // z edges
        val zRange = sZ.toRange(gZ)
        edges[Triple(gX, gY, Axis.Z)] = zRange
        edges[Triple(gX, sY, Axis.Z)] = zRange
        edges[Triple(sX, gY, Axis.Z)] = zRange
        edges[Triple(sX, sY, Axis.Z)] = zRange
    }

    /**
     * Detects players being in this zone
     * 
     * @param player the player it checks to be in the zone
     * @return true if player in zone, false if not
     */
    fun playerInZone(player: ServerPlayerEntity): Boolean {
        if (startPos.world != player.world.registryKey.value.toString()) return false

        if (
            min(startPos.x, endPos.x) < player.x && player.x < max(startPos.x, endPos.x) &&
            min(startPos.y, endPos.y) < player.y && player.y < max(startPos.y, endPos.y) &&
            min(startPos.z, endPos.z) < player.z && player.z < max(startPos.z, endPos.z)
        ) return true

        return false
    }

    /**
     * Displays particles along the edges of the zone border
     * 
     * @param player the player the receives the particle packets
     */
    fun renderBorders(player: ServerPlayerEntity) {
        for (e in edges) {
            when (e.key.third) {
                Axis.X -> {
                    val y = e.key.first
                    val z = e.key.second
                    for (x in e.value.getElements()) {
                        renderParticles(player, x, y, z)
                    }
                }
                Axis.Y -> {
                    val x = e.key.first
                    val z = e.key.second
                    for (y in e.value.getElements()) {
                        renderParticles(player, x, y, z)
                    }
                }
                Axis.Z -> {
                    val x = e.key.first
                    val y = e.key.second
                    for (z in e.value.getElements()) {
                        renderParticles(player, x, y, z)
                    }
                }
            }
        }
    }

    /**
     * Renders zone border particles at the specified coordinates
     *
     * @param player receiver of particle packet
     * @param x position of particle
     * @param y position of particle
     * @param z position of particle
     */
    private fun renderParticles(player: ServerPlayerEntity, x: Double, y: Double, z: Double) {
        player.networkHandler.sendPacket(ParticleS2CPacket(
            DustParticleEffect(DustParticleEffect.RED, 1.0F),
            true,
            x,
            y,
            z,
            0.2F,
            0.2F,
            0.2F,
            0.2F,
            1,
        ))
    }
}