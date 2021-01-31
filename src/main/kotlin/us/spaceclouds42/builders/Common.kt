package us.spaceclouds42.builders

import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents
import org.apache.logging.log4j.LogManager
import us.spaceclouds42.builders.commands.BuilderCommand
import us.spaceclouds42.builders.data.ZoneManager
import us.spaceclouds42.builders.utils.Dispatcher

object Common : ModInitializer {
    private val logger = LogManager.getLogger("FabricBuilders")

    override fun onInitialize() {
        ServerLifecycleEvents.SERVER_STARTING.register {
            SERVER = it
        }

        CommandRegistrationCallback.EVENT.register(::registerCommands)
    }

    private fun registerCommands(dispatcher: Dispatcher, dedicated: Boolean) {
        logger.debug("Registering commands")

        BuilderCommand().register(dispatcher)
    }
}

