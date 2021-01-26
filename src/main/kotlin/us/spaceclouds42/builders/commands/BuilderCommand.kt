package us.spaceclouds42.builders.commands

import com.mojang.brigadier.arguments.IntegerArgumentType
import com.mojang.brigadier.arguments.StringArgumentType
import net.minecraft.server.command.CommandManager
import us.spaceclouds42.builders.utils.Context
import us.spaceclouds42.builders.utils.Dispatcher
import us.spaceclouds42.builders.utils.Node

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
         * Base node of zone management
         */
        val zoneNode: Node =
            CommandManager
                .literal("zone")
                .build()

        /**
         * Add zone node
         */
        val createNode: Node =
            CommandManager
                .literal("create")
                .then(
                    CommandManager
                        .argument("name", StringArgumentType.word())
                        .then(
                            CommandManager
                                .argument("x1", IntegerArgumentType.integer())
                                .suggests { context, builder ->
                                    context.source.player.x
                                    builder.buildFuture()
                                }
                                .then(
                                    CommandManager
                                        .argument("z1", IntegerArgumentType.integer())
                                        .suggests { context, builder ->
                                            context.source.player.z
                                            builder.buildFuture()
                                        }
                                        .then(
                                            CommandManager
                                                .argument("x2", IntegerArgumentType.integer())
                                                .suggests { context, builder ->
                                                    context.source.player.x
                                                    builder.buildFuture()
                                                }
                                                .then(
                                                    CommandManager
                                                        .argument("z2", IntegerArgumentType.integer())
                                                        .suggests { context, builder ->
                                                            context.source.player.z
                                                            builder.buildFuture()
                                                        }
                                                        .executes { zoneCreateCommand(
                                                            it,
                                                            StringArgumentType.getString(it, "name"),
                                                            IntegerArgumentType.getInteger(it, "x1"),
                                                            IntegerArgumentType.getInteger(it, "z1"),
                                                            IntegerArgumentType.getInteger(it, "x2"),
                                                            IntegerArgumentType.getInteger(it, "z2")
                                                        ) }
                                                )
                                        )
                                )
                        )
                )
                .build()

        /**
         * Edit zone node
         */
        val editNode: Node =
            CommandManager
                .literal("edit")
                .build()

        /**
         * Delete zone node
         */
        val deleteNode: Node =
            CommandManager
                .literal("remove")
                .build()

        /**
         * GoTo zone node
         */
        val gotoNode: Node =
            CommandManager
                .literal("goto")
                .build()

        /**
         * List all zones node
         */
        val listNode: Node =
            CommandManager
                .literal("list")
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
        // Zone Nodes
        builderNode.addChild(zoneNode)
        zoneNode.addChild(createNode)
        zoneNode.addChild(editNode)
        zoneNode.addChild(deleteNode)
        zoneNode.addChild(gotoNode)
        zoneNode.addChild(listNode)
        // Player Nodes
        builderNode.addChild(playerNode)
        playerNode.addChild(addNode)
        playerNode.addChild(removeNode)
    }

    private fun zoneCreateCommand(context: Context, name: String, x1: Int, z1: Int, x2: Int, z2: Int): Int {
        println("Creating zone: \"$name\" from $x1 $z1 to $x2 $z2")
        return 1
    }
}