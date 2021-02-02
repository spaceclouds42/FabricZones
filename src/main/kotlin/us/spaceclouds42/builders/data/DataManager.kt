package us.spaceclouds42.builders.data

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents
import net.minecraft.util.WorldSavePath
import us.spaceclouds42.builders.data.spec.Builder
import us.spaceclouds42.builders.data.spec.Pos
import us.spaceclouds42.builders.data.spec.Zone
import java.nio.file.Path
import java.util.UUID

/**
 * Stores, saves, and mutates zone and builder objects
 */
object DataManager {
    /**
     * A map of all the zones. Key is name of zone, Value is the zone object
     */
    private val zones = mutableMapOf<String, Zone>()

    /**
     * A map of all the builders. Key is the
     * player's uuid, Value is the player's
     * corresponding builder object.
     */
    private val builders = mutableMapOf<UUID, Builder>()

    /**
     * Path to the directory of zone data
     */
    private lateinit var zoneDir: Path

    /**
     * Path to the directory of builder data
     */
    private lateinit var builderDir: Path

    /**
     * Registers a few things to server start/stop events
     */
    fun register() {
        ServerLifecycleEvents.SERVER_STARTING.register { server ->
            // Prevent any lingering data from previous
            // server run from causing any duplicate
            // element issues.
            zones.clear()
            builders.clear()

            val dataDir = server.getSavePath(WorldSavePath.ROOT).resolve("FabricBuilders")
            zoneDir = dataDir.resolve("zones")
            builderDir = dataDir.resolve("builders")

            zoneDir.toFile().mkdir()
            builderDir.toFile().mkdir()

            zoneDir.toFile().walk().forEach {
                if (it.name.endsWith(".zone")) {
                    loadData(it.name.replace(".zone", ""))
                }
            }
            builderDir.toFile().walk().forEach {
                if (it.name.endsWith(".builder")) {
                    loadData(UUID.fromString(it.name.replace(".builder", "")))
                }
            }
        }

        ServerLifecycleEvents.SERVER_STOPPING.register {
            // Saves builder's data before they players
            // are disconnected due to a shutdown
            for (uuid in builders.keys) {
                saveData(uuid)
            }
        }
    }

    /**
     * Loads data for requested object from file (if available)
     *
     * @param id the key of the object in it's corresponding map
     * @return returns zone or builder object if successful load,
     *         otherwise, returns false.
     */
    private fun loadData(id: Any): Any? {
        return if (id is String) loadZone(id) else if (id is UUID) loadBuilder(id) else false
    }

    private fun loadZone(name: String): Zone? {
        // TODO: Return loaded zone
        return Zone("blankZone", Pos.ORIGIN, Pos.ORIGIN, "a player")
    }

    private fun loadBuilder(uuid: UUID): Builder? {
        // TODO: Return loaded builder and add to builders if not present
        return Builder("SERVER.playerManager.getPlayer(uuid)!!.entityName")
    }

    /**
     * Saves data for specified object to file
     *
     * @param id the key of the object in it's corresponding map
     */
    private fun saveData(id: Any) {

    }

}