package us.spaceclouds42.builders.data

import kotlinx.serialization.SerializationException
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents
import net.minecraft.util.WorldSavePath
import us.spaceclouds42.builders.LOGGER
import us.spaceclouds42.builders.data.spec.DataSpecBase
import us.spaceclouds42.builders.log.LogInfo
import us.spaceclouds42.builders.log.LogMode
import java.io.File
import java.nio.file.Path
import kotlin.reflect.KClass

/**
 * An interface for all data managers. All methods include
 * an example implementation using [DataSpecBase] as the data type
 * that the manager manages
 */
abstract class ManagerBase {
    /**
     * The data type that the manager manages.
     * All data types must be of type [DataSpecBase]
     */
    abstract val dataSpec: KClass<out DataSpecBase>

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
    protected val cache: MutableMap<String, DataSpecBase> = mutableMapOf()

    /**
     * Directory where the manager's data is saved to file
     */
    private lateinit var dataDir: Path

    /**
     * Registers some actions to server start/stop events
     */
    fun register() {
        ServerLifecycleEvents.SERVER_STARTING.register { server ->
            // Prevent any lingering data from previous
            // server run from causing any duplicate
            // element issues.
            cache.clear()

            // Sets data directory to the correct
            // folder within the FabricBuilders
            // directory
            dataDir = server
                .getSavePath(WorldSavePath.ROOT)
                .resolve("FabricBuilders")
                .resolve(dirName)

            // Loads up all data in the file system
            // for this manager's spec type
            if (enableLoadAllOnStart) {
                loadAllData()
            }
        }

        ServerLifecycleEvents.SERVER_STOPPING.register {
            if (enableSaveOnShutDown) {
                saveAllData()
            }
        }
    }

    /**
     * Loads data for specified instance of `type`
     * from [file system][dataDir] to [cache]
     *
     * @param id identifier of the requested object as a string
     * @return whether or not it successfully loaded the data to memory
     */
    private fun loadData(id: String): Boolean {
        // In the case that the data is
        // already in memory, shouldn't
        // add it to cache.
        if (cache.keys.contains(id)) {
            LOGGER.warn(LogInfo("Requested data '#1' of type '#2' is already in memory!", arrayOf(id, dataSpec.simpleName!!)), LogMode.DEBUG)
            return false
        }

        val dataFile = dataDir.resolve("$id.$fileExtension").toFile()

        // If there is saved data for the
        // requested object, load it
        if (dataFile.exists()) {
            return try {
                val data = readFromFile(dataFile.readText())
                cache[id] = data
                true
            } catch (unknownPropErr: SerializationException) {
                LOGGER.error(LogInfo("Could not load data at '#1.#2'", arrayOf(id, fileExtension)), LogMode.MINIMAL)
                false
            }
        } else {
            LOGGER.warn(LogInfo("No file found at '#1.type', creating new file", arrayOf(id)), LogMode.WTF)

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

            return true
        }
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
     * Deletes the save file for specified object,
     * and removes that object from the cache
     *
     * @param id the id of the object to be removed
     */
    protected fun deleteData(id: String) {
        val dataFile = dataDir.resolve("$id.$fileExtension").toFile()

        dataFile.delete()

        cache.remove(id)
    }

    /**
     * Loads all data from file to [cache]. By default, this is only
     * called when the server starts and if [enableLoadAllOnStart] is true
     */
    private fun loadAllData() {
        dataDir.toFile().walk().forEach {
            if (it.name.endsWith(fileExtension)) {
                loadData(it.name.replace(fileExtension, ""))
            }
        }
    }

    /**
     * Saves all data in the [cache] to file. By
     * default, this will only get called when
     * the server stops and if [enableSaveOnShutDown]
     * is set to true
     */
    private fun saveAllData() {
        for (key in cache.keys) {
            saveData(key)
        }
    }

    /**
     * Loads data for an entity when they join
     *
     * @param id the identifier of the entity as a string
     */
    protected fun managedEntityJoined(id: String) {
        loadData(id)
    }

    /**
     * Unloads data for an entity when they leave
     *
     * @param id the identifier of the entity as a string
     */
    protected fun managedEntityLeft(id: String) {
        saveData(id)
        cache.remove(id)
    }

    /**
     * Reads data from text and converts it to an object that it represents.
     *
     * Example implementation:
     * ```
     *      return Json.decodeFromString(DataSpecBase.serializer(), dataString)
     * ```
     *
     * @param dataString Raw text from the file being read
     * @return an object created using the data from [dataString], should
     *         be of the same type as the type that the data manager manages
     */
    abstract fun readFromFile(dataString: String): DataSpecBase

    /**
     * Serializes [data] and writes it to [file][dataFile].
     *
     * Example implementation:
     * ```
     *      dataFile.writeText(Json.encodeToString(DataSpecBase.serializer(), data))
     * ```
     *
     * @param dataFile file to write to
     * @param data object being saved
     */
    abstract fun writeToFile(dataFile: File, data: DataSpecBase)
}