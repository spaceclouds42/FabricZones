package us.spaceclouds42.zones

import net.minecraft.server.MinecraftServer
import us.spaceclouds42.zones.log.Logger

/**
 * The [server object][MinecraftServer] that is running the mod
 */
lateinit var SERVER: MinecraftServer

/**
 * Tells whether or not the [server][SERVER] has been initialized. Prevents crash when trying to
 * access the server object when mod is on a client connecting to a dedicated server.
 */
fun isServerInitialised() = ::SERVER.isInitialized

/**
 * A global [logger][Logger]
 */
lateinit var LOGGER: Logger
