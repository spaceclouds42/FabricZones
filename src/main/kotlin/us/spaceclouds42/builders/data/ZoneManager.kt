package us.spaceclouds42.builders.data

import kotlinx.serialization.json.Json
import us.spaceclouds42.builders.data.spec.IdentifiableDataSpecBase
import us.spaceclouds42.builders.data.spec.Pos
import us.spaceclouds42.builders.data.spec.Zone
import us.spaceclouds42.builders.data.spec.ZoneAccessMode
import java.io.File

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
     * Gets a map of all the zones
     *
     * @return the [cache]
     */
    fun getAllZones(): Map<String, Zone> {
        return cache as Map<String, Zone>
    }

    /**
     * Changes the boundary corners of a zone
     *
     * @param name the zone to be edited
     * @param startPos the new start position of the boundary
     * @param endPos the new end position of the boundary
     */
    fun editZonePos(name: String, startPos: Pos, endPos: Pos) {
        val old = cache[name] as Zone

        cache[name] = Zone(
            id = name,
            startPos = startPos,
            endPos = endPos,
            createdBy = old.createdBy,
            accessMode = old.accessMode,
            color = old.color,
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
        )

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
            color = Triple(r, g, b)
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
        return deleteData(name) as Zone?
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
     * Changes the access mode of a zone
     *
     * @param name the zone to be edited
     * @param accessMode the new access mode
     */
    fun setAccessMode(name: String, accessMode: ZoneAccessMode) {
        (cache[name] as Zone?)?.accessMode = accessMode
    }

    /**
     * Gets the access mode of a zone
     *
     * @param name the requested zone
     */
    fun getAccessMode(name: String): ZoneAccessMode? {
        return (cache[name] as Zone?)?.accessMode
    }
}