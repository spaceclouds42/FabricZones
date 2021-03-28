package us.spaceclouds42.zones

import net.minecraft.server.MinecraftServer
import us.spaceclouds42.zones.log.Logger

/**
 * The [server object][MinecraftServer] that is running the mod
 */
lateinit var SERVER: MinecraftServer

fun isServerInitialised() = ::SERVER.isInitialized

/**
 * A global [logger][Logger]
 */
lateinit var LOGGER: Logger
