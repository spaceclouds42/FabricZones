package us.spaceclouds42.builders.data.spec

import us.spaceclouds42.builders.data.ManagerBase

/**
 * A data specification intended to be extended by
 * a data class. Only requires an [id] property
 */
abstract class IdentifiableDataSpecBase {
    /**
     * Used as the file name of an instance of the data spec
     * if saved with a [data manager][ManagerBase]
     */
    abstract val id: String
}