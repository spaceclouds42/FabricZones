package us.spaceclouds42.zones.data

import kotlinx.serialization.json.Json
import net.minecraft.entity.Entity
import net.minecraft.util.Identifier
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3i
import net.minecraft.util.registry.Registry
import net.minecraft.util.registry.RegistryKey
import net.minecraft.world.World
import net.minecraft.world.chunk.WorldChunk
import us.spaceclouds42.zones.LOGGER
import us.spaceclouds42.zones.SERVER
import us.spaceclouds42.zones.data.spec.*
import us.spaceclouds42.zones.isServerInitialised
import us.spaceclouds42.zones.log.LogMode
import java.io.File
import java.lang.Integer.min
import kotlin.math.max

/**
 * Manages data concerning [zones][Zone]
 */
object ZoneManager : ManagerBase() {
    override val dataSpec = Zone::class

    override val dirName: String = "zones"

    override val fileExtension: String = "zone"

    override val enableSaveOnShutDown: Boolean = false

    override val enableLoadAllOnStart: Boolean = true

    override fun readFromFile(dataString: String): Zone {
        return Json.decodeFromString(Zone.serializer(), dataString)
    }

    override fun writeToFile(dataFile: File, data: IdentifiableDataSpecBase) {
        dataFile.writeText(Json { prettyPrint = true }.encodeToString(Zone.serializer(), data as Zone))
    }

    /**
     * Creates a file for the zone, and adds it to the [cache]
     */
    fun setZone(zone: Zone) {
        cache[zone.id] = zone

        saveData(zone.id)
    }

    /**
     * Request to get a zone object from the [cache], return if it exists
     *
     * @param name the name of the requested zone object
     * @return the zone with the corresponding name, or null if it does not exist
     */
    fun getZone(name: String): Zone? {
        return cache[name] as Zone?
    }

    /**
     * Request to get a zone object based on position
     *
     * @param pos position to find zone
     * @return the zone that contains that position, or null if no zone does
     */
    fun getZone(pos: PosD): Zone? {
        if (!isServerInitialised()) {
            return null
        }
        
        val world = SERVER.getWorld(RegistryKey.of(Registry.DIMENSION, Identifier.tryParse(pos.world)))

        if (world == null) {
            LOGGER.error("getZone in ZoneManager received invalid world when parsing ${pos.world}\nPlease report this error if you ever see this message", LogMode.MINIMAL)
            return null
        }

        for (zone in cache.values) {
            if ((zone as Zone).positionInZone(world, pos.x, pos.y, pos.z)) {
                return zone
            }
        }

        return null
    }

    /**
     * Request to get a zone object based on block position
     *
     * @param world dimension of position
     * @param pos block position
     * @return the zone that contains that position, or null if no zone does
     */
    fun getZone(world: World, pos: BlockPos): Zone? {
        return getZone(
            PosD(
                world.registryKey.value.toString(),
                pos.x + 0.5,
                pos.y + 0.5,
                pos.z + 0.5,
            )
        )
    }

    /**
     * Zone getter based on entity object
     * @param entity the entity to find zone at
     * @return the zone that the [entity] is in, or null if not in zone
     */
    fun getZone(entity: Entity): Zone? {
        return getZone(
            PosD(
                entity.world.registryKey.value.toString(),
                entity.x,
                entity.y,
                entity.z,
            )
        )
    }

    /**
     * Gets a map of all the zones
     *
     * @return the [cache]
     */
    fun getAllZones(): Map<String, Zone> {
        return cache as Map<String, Zone>
    }

    /**
     * Gets a list of all the restricted zones
     *
     * @return list of [cloaked][ZoneAccessMode.CLOAKED] and [builder][ZoneAccessMode.BUILDERS] zones
     */
    fun getRestrictedZones(): List<Zone> {
        val restricted = mutableListOf<Zone>()

        (cache.values as MutableCollection<Zone>).forEach {
            if (it.accessMode != ZoneAccessMode.EVERYONE) {
                restricted.add(it)
            }
        }

        return restricted
    }

    /**
     * Changes the boundary corners of a zone
     *
     * @param name the zone to be edited
     * @param startPos the new start position of the boundary
     * @param endPos the new end position of the boundary
     */
    fun editZonePos(name: String, startPos: PosI, endPos: PosI) {
        val old = cache[name] as Zone

        cache[name] = Zone(
            id = name,
            startPos = startPos,
            endPos = endPos,
            createdBy = old.createdBy,
            accessMode = old.accessMode,
            color = old.color,
            gotoPos = old.gotoPos,
            renderDistance = old.renderDistance,
        )

        saveData(name)
    }

    /**
     * Changes the access mode of a zone
     *
     * @param name the zone to be edited
     * @param mode the new access mode
     */
    fun editZoneAccess(name: String, mode: ZoneAccessMode) {
        val old = cache[name] as Zone

        cache[name] = Zone(
            id = name,
            startPos = old.startPos,
            endPos = old.endPos,
            createdBy = old.createdBy,
            accessMode = mode,
            color = old.color,
            gotoPos = old.gotoPos,
            renderDistance = old.renderDistance,
        )

        if (mode == ZoneAccessMode.CLOAKED) {
            SERVER.playerManager.playerList.forEach {
                if (it.uuid !in BuilderManager.getOnlineBuilders()) {
                    (cache[name] as Zone).hideZone(it)
                }
            }
        }

        if (mode != ZoneAccessMode.CLOAKED && old.accessMode == ZoneAccessMode.CLOAKED) {
            SERVER.playerManager.playerList.forEach {
                if (it.uuid !in BuilderManager.getOnlineBuilders()) {
                    (cache[name] as Zone).unHideZone(it)
                }
            }
        }

        saveData(name)
    }

    /**
     * Changes the color of a zone's border, uses rgb
     *
     * @param name the zone to be edited
     * @param r the new red value
     * @param g the new green value
     * @param b the new blue value
     */
    fun editZoneBorderColor(name: String, r: Int, g: Int, b: Int) {
        val old = cache[name] as Zone

        cache[name] = Zone(
            id = name,
            startPos = old.startPos,
            endPos = old.endPos,
            createdBy = old.createdBy,
            accessMode = old.accessMode,
            color = Triple(r, g, b),
            gotoPos = old.gotoPos,
            renderDistance = old.renderDistance,
        )

        saveData(name)
    }

    /**
     * Changes the goto position of a zone
     * @param name the zone to be edited
     * @param gotoPos the new position
     */
    fun editZoneGotoPos(name: String, gotoPos: PosD?) {
        val old = cache[name] as Zone

        cache[name] = Zone(
            id = name,
            startPos = old.startPos,
            endPos = old.endPos,
            createdBy = old.createdBy,
            accessMode = old.accessMode,
            color = old.color,
            gotoPos = gotoPos,
            renderDistance = old.renderDistance,
        )

        saveData(name)
    }

    /**
     * Changes the render distance of a zone
     * @param name the zone to be edited
     * @param distance the new render distance
     */
    fun editZoneRenderDistance(name: String, distance: Int) {
        val old = cache[name] as Zone

        cache[name] = Zone(
            id = name,
            startPos = old.startPos,
            endPos = old.endPos,
            createdBy = old.createdBy,
            accessMode = old.accessMode,
            color = old.color,
            gotoPos = old.gotoPos,
            renderDistance = distance,
        )

        saveData(name)
    }

    /**
     * Permanently removes a zone
     *
     * @param name the zone to be deleted
     * @return the zone object that was deleted
     */
    fun deleteZone(name: String): Zone? {
        val zone = deleteData(name) as Zone?

        if (zone?.accessMode == ZoneAccessMode.CLOAKED) {
            SERVER.playerManager.playerList.forEach {
                if (it.uuid !in BuilderManager.getOnlineBuilders()) {
                    zone.unHideZone(it)
                }
            }
        }

        return zone
    }

    /**
     * Reloads the data for a zone from file
     *
     * @param name the zone to be reloaded
     * @return the previously loaded zone
     */
    fun reloadZone(name: String): Zone? {
        return reloadData(name) as Zone?
    }

    /**
     * Reloads data for all zones
     *
     * @return the previous map of names to zones, before the reload
     */
    fun reloadAllZones(): Map<String, Zone> {
        return reloadAllData() as Map<String, Zone>
    }

    /**
     * Get the name of the player that created the zone
     *
     * @param name the requested zone
     * @return the player that created the zone
     */
    fun getCreator(name: String): String? {
        return (cache[name] as Zone?)?.createdBy
    }

    /**
     * Gets the access mode of a zone
     *
     * @param name the requested zone
     */
    fun getAccessMode(name: String): ZoneAccessMode? {
        return (cache[name] as Zone?)?.accessMode
    }

    /**
     * Gets all the cloaked blocks within a chunk
     *
     * @param chunk the chunk to check
     * @return list of vec3i containing x y z of cloaked blocks
     */
    fun getCloakedBlocks(chunk: WorldChunk): List<Vec3i> {
        val cloakedPositions = mutableListOf<Vec3i>()

        getAllZones().values.forEach { zone ->
            if (zone.accessMode == ZoneAccessMode.CLOAKED) {
                zone.getCloakedBlocks().forEach { block ->
                    val xMin = chunk.pos.x * 16
                    val zMin = chunk.pos.z * 16
                    val xMax = xMin + 15
                    val zMax = zMin + 15

                    if (block.x in xMin..xMax && block.z in zMin..zMax) {
                        cloakedPositions.add(block)
                    }
                }
            }
        }

        return cloakedPositions
    }

    /**
     * Determines whether a specific position is cloaked
     * 
     * @param world dimension of position
     * @param x x coordinate
     * @param y y coordinate
     * @param z z coordinate
     * @return if block at pos is cloaked
     */
    fun isCloaked(world: World, x: Int, y: Int, z: Int): Boolean {
        getAllZones().values.forEach { zone ->
            if (zone.accessMode == ZoneAccessMode.CLOAKED && zone.positionInZone(world, x + 0.5, y + 0.5, z + 0.5)) {
                return true
            }
        }
        return false
    }

    fun getCloakedZonesIntersecting(start: PosD, end: PosD): List<Zone> {
        return getAllZones().values.filter { zone ->
            zone.accessMode == ZoneAccessMode.CLOAKED && zone.intersectsWith(start, end)
        }
    }

    fun getCloakedBlocks(): List<Vec3i> {
        val cloakedBlocks = mutableListOf<Vec3i>()

        getAllZones().values.forEach { zone ->
            if (zone.accessMode == ZoneAccessMode.CLOAKED) {
                zone.getCloakedBlocks().forEach { block ->
                    cloakedBlocks.add(block)
                }
            }
        }

        return cloakedBlocks
    }

    fun hasOverlap(startPos: PosI, endPos: PosI): Pair<Boolean, String?> {
        val minPos = PosI(
            startPos.world,
            min(startPos.x, endPos.x),
            min(startPos.y, endPos.y),
            min(startPos.z, endPos.z),
        )

        val maxPos = PosI(
            startPos.world,
            max(startPos.x, endPos.x),
            max(startPos.y, endPos.y),
            max(startPos.z, endPos.z),
        )

        getAllZones().values.forEach { zone ->
            if (zone.detectOverlap(minPos, maxPos)) {
                return Pair(true, zone.id)
            }
        }
        return Pair(false, null)
    }
}
