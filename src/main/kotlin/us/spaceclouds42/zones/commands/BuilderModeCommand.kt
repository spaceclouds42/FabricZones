package us.spaceclouds42.zones.commands

import me.lucko.fabric.api.permissions.v0.Permissions
import net.minecraft.server.command.CommandManager
import us.spaceclouds42.zones.data.BuilderManager
import us.spaceclouds42.zones.utils.*

class BuilderModeCommand : ICommand {
    override fun register(dispatcher: Dispatcher) {
        /**
         * Base command node
         */
        val builderModeNode: Node =
            CommandManager
                .literal("buildermode")
                .requires(Permissions.require("fabriczones.command.buildermode", 2))
                .build()

        /**
         * Enable builder mode node
         */
        val enableNode: Node =
            CommandManager
                .literal("enable")
                .executes {
                    enableBuilderMode(it)
                }
                .build()

        /**
         * Disable builder mode node
         */
        val disableNode: Node =
            CommandManager
                .literal("disable")
                .executes {
                    disableBuilderMode(it)
                }
                .build()

        // buildermode (enable|disable)
        dispatcher.root.addChild(builderModeNode)
        // buildermode enable
        builderModeNode.addChild(enableNode)
        // buildermode disable
        builderModeNode.addChild(disableNode)
    }

    private fun enableBuilderMode(context: Context): Int {
        val source = context.source
        val builder = BuilderManager.getBuilder(source.player.uuid)

        if (builder == null) {
            source.sendError(
                red("Are you sure you are a builder? Your builder data was not found")
            )
            return 0
        }

        return if (builder.builderModeEnabled) {
            source.sendError(
                red("You are already in builder mode")
            )
            0
        } else {
            BuilderManager.setBuilderModeEnabled(source.player, true)
            source.sendFeedback(
                green("Builder mode enabled"),
                false
            )
            1
        }
    }

    private fun disableBuilderMode(context: Context): Int {
        val source = context.source
        val builder = BuilderManager.getBuilder(source.player.uuid)

        if (builder == null) {
            source.sendError(
                red("Are you sure you are a builder? Your builder data was not found")
            )
            return 0
        }

        return if (!builder.builderModeEnabled) {
            source.sendError(
                red("You already are not in builder mode")
            )
            0
        } else {
            BuilderManager.setBuilderModeEnabled(source.player, false)
            source.sendFeedback(
                red("Builder mode disabled"),
                false
            )
            1
        }
    }
}