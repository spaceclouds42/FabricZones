package us.spaceclouds42.builders.commands

import com.mojang.brigadier.arguments.StringArgumentType
import net.minecraft.command.argument.BlockPosArgumentType
import net.minecraft.server.command.CommandManager
import net.minecraft.util.math.BlockPos
import us.spaceclouds42.builders.utils.Context
import us.spaceclouds42.builders.utils.Dispatcher
import us.spaceclouds42.builders.utils.Node
// TODO: rethink entire command structure :tiny_potato:
class BuilderCommand {
    fun register(dispatcher: Dispatcher) {
        /**
         * Base command node
         */
        val builderNode: Node =
            CommandManager
                .literal("builder")
                .requires { it.player.hasPermissionLevel(2) }
                .build()

        /**
         * Base node of player management
         */
        val playerNode: Node =
            CommandManager
                .literal("player")
                .build()

        /**
         * Add player node
         */
        val addNode: Node =
            CommandManager
                .literal("add")
                .build()

        /**
         * Remove player node
         */
        val removeNode: Node =
            CommandManager
                .literal("remove")
                .build()

        dispatcher.root.addChild(builderNode)
        // Player Nodes
        builderNode.addChild(playerNode)
        playerNode.addChild(addNode)
        playerNode.addChild(removeNode)
    }

}