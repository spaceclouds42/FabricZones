package us.spaceclouds42.builders.commands

import me.lucko.fabric.api.permissions.v0.Permissions
import net.minecraft.command.argument.EntityArgumentType
import net.minecraft.server.command.CommandManager
import net.minecraft.server.network.ServerPlayerEntity
import us.spaceclouds42.builders.data.BuilderManager
import us.spaceclouds42.builders.utils.*

class BuilderCommand : ICommand {
    override fun register(dispatcher: Dispatcher) {
        /**
         * Base command node
         */
        val builderNode: Node =
            CommandManager
                .literal("builder")
                .requires(Permissions.require("fabriczones.command.builder", 2))
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
                .then(
                    CommandManager
                        .argument("name", EntityArgumentType.player())
                        .executes { addPlayer(
                            it,
                            EntityArgumentType.getPlayer(it, "name")
                        ) }
                )
                .build()

        /**
         * Remove player node
         */
        val removeNode: Node =
            CommandManager
                .literal("remove")
                .then(
                    CommandManager
                        .argument("name", EntityArgumentType.player())
                        .executes { removePlayer(
                            it,
                            EntityArgumentType.getPlayer(it, "name")
                        ) }
                )
                .build()

        // builder (player|list)
        dispatcher.root.addChild(builderNode)
        // builder player (add|remove)
        builderNode.addChild(playerNode)
        // builder player add <name>
        playerNode.addChild(addNode)
        // builder player remove <name>
        playerNode.addChild(removeNode)
    }

    /**
     * Adds a player to builder list
     *
     * @param context command source
     * @param player the player being added
     * @return success = 1, fail = 0
     */
    private fun addPlayer(context: Context, player: ServerPlayerEntity): Int {
        BuilderManager.addPlayer(player)

        context.source.sendFeedback(
            green("Added ") +
                    yellow(player.entityName) +
                    green(" to builders"),
            true
        )

        return 1
    }

    /**
     * Removes a player from builder list
     *
     * @param context command source
     * @param player the player being removed
     * @return success = 1, fail = 0
     */
    private fun removePlayer(context: Context, player: ServerPlayerEntity): Int {
        BuilderManager.removePlayer(player.uuid)

        context.source.sendFeedback(
            red("Removed ") +
                    yellow(player.entityName) +
                    red(" from builders"),
            true
        )

        return 1
    }
}