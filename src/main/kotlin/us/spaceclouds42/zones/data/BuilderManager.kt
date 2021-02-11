package us.spaceclouds42.zones.data

import kotlinx.serialization.json.Json
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.server.network.ServerRecipeBook
import us.spaceclouds42.zones.data.spec.Builder
import us.spaceclouds42.zones.data.spec.IdentifiableDataSpecBase
import us.spaceclouds42.zones.data.spec.ZoneAccessMode
import java.io.File
import java.util.*

/**
 * Manages data concerning [builders][Builder]
 */
object BuilderManager : ManagerBase() {
    override val dataSpec = Builder::class

    override val dirName: String = "builders"

    override val fileExtension: String = "builder"

    override val enableSaveOnShutDown: Boolean = true

    override val enableLoadAllOnStart: Boolean = false

    override fun readFromFile(dataString: String): Builder {
        return Json.decodeFromString(Builder.serializer(), dataString)
    }

    override fun writeToFile(dataFile: File, data: IdentifiableDataSpecBase) {
        dataFile.writeText(Json { prettyPrint = true }.encodeToString(Builder.serializer(), data as Builder))
    }

    /**
     * Makes a player a builder
     *
     * @param player the player becoming a builder
     */
    fun addPlayer(player: ServerPlayerEntity) {
        val uuid = player.uuid.toString()

        cache[uuid] = Builder(
            id = uuid,
            name = player.entityName,
        )

        saveData(uuid)

        ZoneManager.getAllZones().values.forEach {
            if (it.accessMode == ZoneAccessMode.CLOAKED) {
                it.unHideZone(player)
            }
        }
    }

    /**
     * Removes a player from being a builder
     *
     * @param uuid the player's uuid
     */
    fun removePlayer(player: ServerPlayerEntity) {
        deleteData(player.uuid.toString())

        ZoneManager.getAllZones().values.forEach {
            if (it.accessMode == ZoneAccessMode.CLOAKED) {
                it.hideZone(player)
            }
        }
    }

    /**
     * Gets all the uuids of the builders that are currently online
     *
     * @return list of keys in the cache converted to uuids
     */
    fun getOnlineBuilders(): List<UUID> {
        val names = mutableListOf<UUID>()

        cache.keys.forEach {
            names.add(UUID.fromString(it))
        }

        return names
    }
}