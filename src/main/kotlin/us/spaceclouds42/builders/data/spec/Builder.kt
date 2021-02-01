package us.spaceclouds42.builders.data.spec

import kotlinx.serialization.Serializable

/**
 * Contains configurable properties of builders
 * that can help with using builder mode
 */
@Serializable
data class Builder(
    /**
     * Name of the builder
     */
    val name: String,

    /**
     * If enabled, the builder will not
     * activate a portal when it collides
     * with a nether or end portal
     */
    var antiPortalInteract: Boolean = false,
    // Is a full on no clip possible purely server side?

    /**
     * Whether or not a player sees zone borders. (Only displayed when inside a zone)
     */
    var areZoneBordersVisible: Boolean = true,
) {
    /**
     * Set's the player's active inventory to their saved inventory, and
     * saves the previously active inventory. The inventory is saved with
     * the "BuilderInventory" tag on the player's data. For an extra
     * measure of "security," there is a boolean "builderInven" which
     * marks the saved inventory as either the inventory saved from builder
     * mode (true) or the inventory saved from normal play (false).
     *
     * @param inBuilderMode if this boolean matches the "builderInven"
     * boolean of the saved inventory, then it will successfully swap
     * inventories, otherwise it will cancel
     * @return Whether or not the inventory was successfully swapped
     */
    fun swapInventories(inBuilderMode: Boolean): Boolean {
        return false
    }
}