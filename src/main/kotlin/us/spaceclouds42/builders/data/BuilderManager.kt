package us.spaceclouds42.builders.data

import kotlinx.serialization.json.Json
import us.spaceclouds42.builders.data.spec.Builder
import us.spaceclouds42.builders.data.spec.IdentifiableDataSpecBase
import java.io.File

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
}