package us.spaceclouds42.builders

import net.minecraft.server.MinecraftServer
import us.spaceclouds42.builders.log.Logger

/**
 * The [server object][MinecraftServer] that is running the mod
 */
lateinit var SERVER: MinecraftServer

/**
 * A global [logger][Logger]
 */
lateinit var LOGGER: Logger
