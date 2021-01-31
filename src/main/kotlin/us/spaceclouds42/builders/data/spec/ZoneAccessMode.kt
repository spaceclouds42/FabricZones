package us.spaceclouds42.builders.data.spec

/**
 * Access modes for zones
 */
enum class ZoneAccessMode {
    /**
     * Default access mode. All players can
     * enter the zone, and Builders are able
     * to use /gamemode builder in the zone
     */
    EVERYONE,

    /**
     * Everyone can see the zone, but
     * only Builders are able to enter
     */
    BUILDERS,

    /**
     * Only Builders can see the zone
     * and only they are able to enter
     */
    CLOAKED,
}