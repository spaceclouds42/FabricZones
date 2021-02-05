package us.spaceclouds42.builders.data

import us.spaceclouds42.builders.data.spec.Builder
import us.spaceclouds42.builders.data.spec.DataType
import java.io.File

object BuilderManager : ManagerBase() {
    override val dataType = Builder::class

    override val dirName: String = "builders"

    override val fileExtension: String = ".builder"

    override val enableSaveOnShutDown: Boolean = true

    override val enableLoadAllOnStart: Boolean = false

    /**
     * Reads data from text and converts it to an object that it represents.
     *
     * Example implementation:
     * ```
     *      return Json.decodeFromString(DataType.serializer(), dataString)
     * ```
     *
     * @param dataString Raw text from the file being read
     * @return an object created using the data from [dataString], should
     *         be of the same type as the type that the data manager manages
     */
    override fun readFromFile(dataString: String): DataType {
        TODO("Not yet implemented")
    }

    /**
     * Serializes [data] and writes it to [file][dataFile].
     *
     * Example implementation:
     * ```
     *      dataFile.writeText(Json.encodeToString(DataType.serializer(), data))
     * ```
     *
     * @param dataFile file to write to
     * @param data object being saved
     */
    override fun writeToFile(dataFile: File, data: DataType) {
        TODO("Not yet implemented")
    }
}