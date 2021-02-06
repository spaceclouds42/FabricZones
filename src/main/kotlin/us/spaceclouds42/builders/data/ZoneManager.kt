package us.spaceclouds42.builders.data

import kotlinx.serialization.json.Json
import us.spaceclouds42.builders.data.spec.Builder
import us.spaceclouds42.builders.data.spec.DataSpecBase
import us.spaceclouds42.builders.data.spec.Zone
import java.io.File

object ZoneManager : ManagerBase() {
    override val dataSpec = Builder::class

    override val dirName: String = "zones"

    override val fileExtension: String = ".zone"

    override val enableSaveOnShutDown: Boolean = false

    override val enableLoadAllOnStart: Boolean = true

    override fun readFromFile(dataString: String): Zone {
        return Json.decodeFromString(Zone.serializer(), dataString)
    }

    override fun writeToFile(dataFile: File, data: DataSpecBase) {
        dataFile.writeText(Json.encodeToString(Zone.serializer(), data as Zone))
    }
}