package us.spaceclouds42.zones

import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents
import net.minecraft.block.BlockState
import net.minecraft.block.entity.BlockEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import org.apache.logging.log4j.LogManager
import us.spaceclouds42.zones.commands.BuilderCommand
import us.spaceclouds42.zones.commands.BuilderModeCommand
import us.spaceclouds42.zones.commands.ZoneCommand
import us.spaceclouds42.zones.data.BuilderManager
import us.spaceclouds42.zones.data.ZoneManager
import us.spaceclouds42.zones.data.ZoneManager.getZone
import us.spaceclouds42.zones.data.spec.PosD
import us.spaceclouds42.zones.duck.BuilderAccessor
import us.spaceclouds42.zones.log.LogMode
import us.spaceclouds42.zones.log.Logger
import us.spaceclouds42.zones.utils.Dispatcher

/**
 * Does some initialization work, registers commands, starts up data managers, and sets global variables
 */
object Common : ModInitializer {
    override fun onInitialize() {
        LOGGER = Logger(LogManager.getLogger("Fabric Zones"), LogMode.WTF)
        LOGGER.info("Initializing", LogMode.MINIMAL)

        LOGGER.info("Registering to SERVER_STARTING event", LogMode.WTF)
        ServerLifecycleEvents.SERVER_STARTING.register {
            SERVER = it
            BuilderManager.register(it)
            ZoneManager.register(it)
        }

        LOGGER.info("Registering to CommandRegistrationCallback.EVENT event", LogMode.WTF)
        CommandRegistrationCallback.EVENT.register(::registerCommands)

        PlayerBlockBreakEvents.BEFORE.register(::beforeBlockBroken)
    }

    /**
     * Cancels block break event if block is in zone and player isn't a builder.
     */
    private fun beforeBlockBroken(world: World, player: PlayerEntity, pos: BlockPos, state: BlockState, blockEntity: BlockEntity?): Boolean {
        check(player is BuilderAccessor)

        val blockZone = getZone(
            PosD(
                world.registryKey.value.toString(),
                pos.x.toDouble(),
                pos.y.toDouble(),
                pos.z.toDouble()
            )
        )

        if (player.isInBuilderMode) {
            val playerZone = getZone(
                PosD(
                    world.registryKey.value.toString(),
                    player.x,
                    player.y,
                    player.z
                )
            )

            return blockZone == playerZone
        } else {
            return blockZone == null
        }
    }

    /**
     * Registers all the [commands][us.spaceclouds42.zones.commands] to the server
     */
    private fun registerCommands(dispatcher: Dispatcher, dedicated: Boolean) {
        LOGGER.info("Registering commands", LogMode.DEBUG)
        BuilderCommand().register(dispatcher)
        ZoneCommand().register(dispatcher)
        BuilderModeCommand().register(dispatcher)
    }
}

