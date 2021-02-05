package us.spaceclouds42.builders

import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents
import us.spaceclouds42.builders.commands.BuilderCommand
import us.spaceclouds42.builders.data.BuilderManager
import us.spaceclouds42.builders.log.LogInfo
import us.spaceclouds42.builders.log.LogMode
import us.spaceclouds42.builders.log.Logger
import us.spaceclouds42.builders.utils.Dispatcher

object Common : ModInitializer {
    override fun onInitialize() {
        LOGGER = Logger()
        LOGGER.info(LogInfo("Initializing"), LogMode.MINIMAL)

        LOGGER.info(LogInfo("Registering to SERVER_STARTING event"), LogMode.WTF)
        ServerLifecycleEvents.SERVER_STARTING.register {
            SERVER = it
            BuilderManager.register()
        }


        LOGGER.info(LogInfo("Registering to CommandRegistrationCallback.EVENT event"), LogMode.WTF)
        CommandRegistrationCallback.EVENT.register(::registerCommands)
    }

    private fun registerCommands(dispatcher: Dispatcher, dedicated: Boolean) {
        LOGGER.info(LogInfo("Registering commands"), LogMode.DEBUG)
        BuilderCommand().register(dispatcher)
    }
}

