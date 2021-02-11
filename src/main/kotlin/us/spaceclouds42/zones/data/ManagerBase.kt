package us.spaceclouds42.zones.data

import kotlinx.serialization.SerializationException
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents
import net.minecraft.server.MinecraftServer
import net.minecraft.util.WorldSavePath
import us.spaceclouds42.zones.LOGGER
import us.spaceclouds42.zones.data.spec.IdentifiableDataSpecBase
import us.spaceclouds42.zones.log.LogMode
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import kotlin.reflect.KClass

/**
 * A way of loading, mutating, saving, and deleting data. Compatible
 * with data classes that extend [IdentifiableDataSpecBase]
 */
abstract class ManagerBase {
    /**
     * The data specification type that the manager manages.
     * All spec types must extend [IdentifiableDataSpecBase]
     */
    abstract val dataSpec: KClass<out IdentifiableDataSpecBase>

    /**
     * Name of the directory where data is stored
     */
    abstract val dirName: String

    /**
     * The file extension as a string (don't include dot)
     */
    abstract val fileExtension: String

    /**
     * Only managers that manage data that can
     * be altered by something other than the
     * data manager should have this enabled
     *
     * Some things that would require this:
     *  - entity inventory
     *  - entity position
     *  - entity age
     *
     * If anything that is being stored can
     * change without triggering something in
     * the data manager, be sure to enable this,
     * or on server shutdown, it will be lost
     */
    abstract val enableSaveOnShutDown: Boolean

    /**
     * Only managers that manage data that
     * needs to be loaded in memory at all
     * times need this enabled. If the data
     * is only needed when some entity is
     * loaded, use a mixin to that entity's
     * loading and call [managedEntityJoined]
     * and use another mixin to unload using
     * [managedEntityLeft]
     */
    abstract val enableLoadAllOnStart: Boolean

    /**
     * A map of all the manager's data in memory
     */
    protected val cache: MutableMap<String, IdentifiableDataSpecBase> = mutableMapOf()

    /**
     * Directory where the manager's data is saved to file
     */
    private lateinit var dataDir: Path

    /**
     * Whether or not the manager has been registered yet
     */
    private var registered: Boolean = false

    /**
     * Registers some actions to server start/stop events
     */
    fun register(server: MinecraftServer) {
        if (!registered) initialize(server)

        ServerLifecycleEvents.SERVER_STARTING.register {
            initialize(it)
        }

        ServerLifecycleEvents.SERVER_STOPPING.register {
            if (enableSaveOnShutDown) {
                saveAllData()
            }
        }

        registered = true
    }

    /**
     * Things required to start up the data manager
     *
     * @param server the minecraft server object running the mod
     */
    private fun initialize(server: MinecraftServer) {
        // Prevent any lingering data from previous
        // server run from causing any duplicate
        // element issues.
        cache.clear()

        // Sets data directory to the correct
        // folder within the Fabric Zones
        // directory
        dataDir = server
            .getSavePath(WorldSavePath.ROOT)
            .resolve("Fabric Zones")
            .resolve(dirName)
        Files.createDirectories(dataDir)
        LOGGER.info("${dataSpec.simpleName} data directory: $dataDir", LogMode.MINIMAL)

        // Loads up all data in the file system
        // for this manager's spec type
        if (enableLoadAllOnStart) {
            loadAllData()
        }
    }

    /**
     * Loads data for specified instance of `type`
     * from [file system][dataDir] to [cache]
     *
     * @param id identifier of the requested object as a string
     * @return the object that was loaded, or null if already
     * in [cache] or a deserialization error occurred
     */
    protected fun loadData(id: String): IdentifiableDataSpecBase? {
        // In the case that the data is
        // already in memory, shouldn't
        // add it to cache.
        if (cache.keys.contains(id)) {
            LOGGER.warn("Requested data '$id' of type '${dataSpec.simpleName!!}' is already in memory!", LogMode.DEBUG)
            return null
        }

        val dataFile = dataDir.resolve("$id.$fileExtension").toFile()

        // If there is saved data for the
        // requested object, load it
        if (dataFile.exists()) {
            return try {
                val data = readFromFile(dataFile.readText())
                cache[id] = data
                data
            } catch (unknownPropErr: SerializationException) {
                LOGGER.error("Could not load data at '$id.$fileExtension'", LogMode.MINIMAL)
                null
            }
        } else {
            LOGGER.warn("No file found at '$id.$fileExtension', creating new file", LogMode.WTF)

            // Here's some cursed reflection code
            // to create an empty object of the
            // data manager's spec type with only
            // the id. :tiny_potato:
            val constructor = dataSpec.constructors.first()
            val data = constructor.callBy(mapOf(constructor.parameters[0] to id))

            cache[id] = data

            // New data needs to be saved to file
            // because a file doesn't exist yet
            saveData(id)

            return data
        }
    }

    /**
     * Loads all data from file to [cache]. By default, this is only
     * called when the server starts and if [enableLoadAllOnStart] is true
     */
    protected fun loadAllData() {
        dataDir.toFile().walk().forEach {
            if (it.name.endsWith(fileExtension)) {
                loadData(it.name.replace(".$fileExtension", ""))
            }
        }
    }

    /**
     * Removes the specified data from the cache, then
     * reloads it from file.
     *
     * @param id id of the requested object
     * @return the **previously** loaded data, which can
     * be null if the [cache] did not have that data stored
     */
    protected fun reloadData(id: String): IdentifiableDataSpecBase? {
        val oldData = cache.remove(id)
        loadData(id)
        return oldData
    }

    /**
     * Clears the [cache] and repopulates with data
     * from file. Only loads up data that was previously loaded
     *
     * @return the previous cache
     */
    protected fun reloadAllData(): Map<String, IdentifiableDataSpecBase> {
        val oldCache = cache
        val loadedIds = cache.keys
        cache.clear()

        for (id in loadedIds) {
            loadData(id)
        }

        return oldCache
    }

    /**
     * Saves data from [cache] to file. This does NOT
     * remove the data from the cache! Any time a change
     * to any data is made, this should be called for that
     * object
     *
     * @param id identifier of the object to be saved as a string
     */
    protected fun saveData(id: String) {
        val dataFile = dataDir.resolve("$id.$fileExtension").toFile()
        val data = cache[id] ?: throw NoSuchElementException("No cached data for '${dataSpec.simpleName}' object '$id'")

        if (!dataFile.exists()) {
            dataFile.createNewFile()
        }

        writeToFile(dataFile, data)
    }

    /**
     * Saves all data in the [cache] to file. By
     * default, this will only get called when
     * the server stops and if [enableSaveOnShutDown]
     * is set to true
     */
    protected fun saveAllData() {
        for (key in cache.keys) {
            saveData(key)
        }
    }

    /**
     * Deletes the save file for specified object,
     * and removes that object from the [cache] if present
     *
     * @param id the id of the object to be removed
     * @return the deleted data, null if it was not in the [cache]
     */
    protected fun deleteData(id: String): IdentifiableDataSpecBase? {
        val dataFile = dataDir.resolve("$id.$fileExtension").toFile()

        dataFile.delete()

        return cache.remove(id)
    }

    /**
     * Deletes the save file for all data that is currently loaded
     * and empties the [cache]. Be very careful with this
     *
     * @return the deleted cache
     */
    protected fun deleteAllLoadedData(): Map<String, IdentifiableDataSpecBase> {
        val oldCache = cache
        val loadedIds = cache.keys
        cache.clear()

        for (id in loadedIds) {
            deleteData(id)
        }

        return oldCache
    }

    /**
     * This is a hard reset on all saved data. It deletes
     * every save file and clears the [cache]
     */
    protected fun deleteAllData() {
        cache.clear()

        dataDir.toFile().walk().forEach {
            if (it.name.endsWith(fileExtension)) {
                it.delete()
            }
        }
    }

    /**
     * Loads data for an entity when they join
     *
     * @param id the identifier of the entity as a string
     */
    fun managedEntityJoined(id: String) {
        loadData(id)
    }

    /**
     * Unloads data for an entity when they leave
     *
     * @param id the identifier of the entity as a string
     */
    fun managedEntityLeft(id: String) {
        saveData(id)
        cache.remove(id)
    }

    /**
     * Reads data from text and converts it to an object that it represents.
     *
     * Example implementation:
     * ```
     *      return Json.decodeFromString(IdentifiableDataSpecBase.serializer(), dataString)
     * ```
     *
     * @param dataString Raw text from the file being read
     * @return an object created using the data from [dataString], should
     *         be of the same type as the type that the data manager manages
     */
    abstract fun readFromFile(dataString: String): IdentifiableDataSpecBase

    /**
     * Serializes [data] and writes it to [file][dataFile].
     *
     * Example implementation:
     * ```
     *      dataFile.writeText(Json { prettyPrint = true }.encodeToString(IdentifiableDataSpecBase.serializer(), data))
     * ```
     *
     * @param dataFile file to write to
     * @param data object being saved
     */
    abstract fun writeToFile(dataFile: File, data: IdentifiableDataSpecBase)
}