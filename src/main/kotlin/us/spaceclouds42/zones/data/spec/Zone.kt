package us.spaceclouds42.zones.data.spec

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import net.minecraft.block.Blocks
import net.minecraft.network.packet.s2c.play.BlockUpdateS2CPacket
import net.minecraft.network.packet.s2c.play.ParticleS2CPacket
import net.minecraft.particle.DustParticleEffect
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.Identifier
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3i
import net.minecraft.util.registry.Registry
import net.minecraft.util.registry.RegistryKey
import net.minecraft.world.World
import us.spaceclouds42.zones.SERVER
import us.spaceclouds42.zones.ext.toRange
import us.spaceclouds42.zones.utils.Axis
import us.spaceclouds42.zones.utils.DoubleRange
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
    val startPos: PosI,

    /**
     * End corner
     */
    val endPos: PosI,

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

    /**
     * The color of the zone borders, uses rgb, defaults to red
     */
    var color: Triple<Int, Int, Int> = Triple(255, 0, 0),

    /**
     * The position to teleport to when the player runs `/zone goto <name>`
     */
    var gotoPos: PosD? = null

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
        val gX = max(startPos.x, endPos.x) + 1.0
        val gY = max(startPos.y, endPos.y) + 1.0
        val gZ = max(startPos.z, endPos.z) + 1.0
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
    fun positionInZone(world: World, x: Double, y: Double, z: Double): Boolean {
        if (startPos.world != world.registryKey.value.toString()) return false

        if (
            min(startPos.x, endPos.x) <= x && x <= (max(startPos.x, endPos.x) + 1.0) &&
            min(startPos.y, endPos.y) <= y && y < (max(startPos.y, endPos.y) + 1.0) &&
            min(startPos.z, endPos.z) <= z && z <= (max(startPos.z, endPos.z) + 1.0)
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
            DustParticleEffect(
                color.first/255.0F,
                color.second/255.0F,
                color.third/255.0F,
                1.0F
            ),
            true,
            x,
            y,
            z,
            0.1F,
            0.1F,
            0.1F,
            0.2F,
            1,
        ))
    }

    /**
     * Kicks a player out of a zone
     *
     * @param player the player that is kicked out
     */
    fun removePlayer(player: ServerPlayerEntity) {
        player.networkHandler.requestTeleport(min(startPos.x, endPos.x) - 1.0, player.y, min(startPos.z, endPos.z) - 1.0, player.yaw, player.pitch)
    }

    /**
     * Hides the zone from the player
     *
     * @param player the player the zone is hidden from
     */
    fun hideZone(player: ServerPlayerEntity) {
        if (startPos.world != player.world.registryKey.value.toString()) return

        for (xyz in getCloakedBlocks()) {
            player.networkHandler.sendPacket(
                BlockUpdateS2CPacket(
                    BlockPos(Vec3i(xyz.x, xyz.y, xyz.z)),
                    Blocks.AIR.defaultState
                )
            )
        }
    }

    /**
     * Re renders the zone to players
     *
     * @param player the player the zone is unhidden from
     */
    fun unHideZone(player: ServerPlayerEntity) {
        if (startPos.world != player.world.registryKey.value.toString()) return

        for (xyz in getCloakedBlocks()) {
            player.networkHandler.sendPacket(
                BlockUpdateS2CPacket(
                    BlockPos(Vec3i(xyz.x, xyz.y, xyz.z)),
                    player.world.getBlockState(BlockPos(Vec3i(xyz.x, xyz.y, xyz.z)))
                )
            )
        }
    }

    /**
     * Finds all the non air blocks in a zone
     *
     * @return a list of the block positions of all the non air blocks
     */
    fun getCloakedBlocks(): List<Vec3i> {
        val blocks = mutableListOf<Vec3i>()
        val world = SERVER.getWorld(RegistryKey.of(Registry.DIMENSION, Identifier.tryParse(startPos.world)))!!

        for (y in min(startPos.y, endPos.y)..max(startPos.y, endPos.y)) {
            for (x in min(startPos.x, endPos.x)..max(startPos.x, endPos.x)) {
                for (z in min(startPos.z, endPos.z)..max(startPos.z, endPos.z)) {
                    if (world.getBlockState(BlockPos(Vec3i(x, y, z))) != Blocks.AIR.defaultState) {
                        blocks.add(Vec3i(x, y, z))
                    }
                }
            }
        }

        return blocks;
    }
}