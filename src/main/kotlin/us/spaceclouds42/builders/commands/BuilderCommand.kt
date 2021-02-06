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
                                .argument("start", BlockPosArgumentType.blockPos())
                                .then(
                                    CommandManager
                                        .argument("end", BlockPosArgumentType.blockPos())
                                        .executes { zoneCreateCommand(
                                            it,
                                            StringArgumentType.getString(it, "name"),
                                            BlockPosArgumentType.getBlockPos(it, "start"),
                                            BlockPosArgumentType.getBlockPos(it, "end")
                                        ) }
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
                .then(
                    CommandManager
                        .argument("name", StringArgumentType.word())
                        //TODO: suggest zone names for edit
                        .then(
                            CommandManager
                                .argument("start", BlockPosArgumentType.blockPos())
                                .then(
                                    CommandManager
                                        .argument("end", BlockPosArgumentType.blockPos())
                                        .executes { zoneEditCommand(
                                            it,
                                            StringArgumentType.getString(it, "name"),
                                            BlockPosArgumentType.getBlockPos(it, "start"),
                                            BlockPosArgumentType.getBlockPos(it, "end")
                                        ) }
                                )
                        )
                )
                .build()

        /**
         * Delete zone node
         */
        val deleteNode: Node =
            CommandManager
                .literal("remove")
                .then(
                    CommandManager
                        .argument("name", StringArgumentType.word())
                        //TODO: suggest zone names for delete
                        .executes { zoneDeleteCommand(
                            it,
                            StringArgumentType.getString(it, "name")
                        ) }
                )
                .build()

        /**
         * GoTo zone node
         */
        val gotoNode: Node =
            CommandManager
                .literal("goto")
                .then(
                    CommandManager
                        .argument("name", StringArgumentType.word())
                        //TODO: suggest zone names for goto
                        .executes { zoneGotoCommand(
                            it,
                            StringArgumentType.getString(it, "name")
                        ) }
                )
                .build()

        /**
         * List all zones node
         */
        val listNode: Node =
            CommandManager
                .literal("list")
                .executes { zoneListCommand(it) }
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

    /**
     * Creates a new zone if name
     * is not already taken by
     * another zone
     *
     * @param context command source
     * @param name zone name
     * @param startPos first corner of zone
     * @param endPos second corner of zone
     * @return 1 if successful creation, 0 if not
     */
    private fun zoneCreateCommand(context: Context, name: String, startPos: BlockPos, endPos: BlockPos): Int {
        println("Creating zone: \"$name\" from ${startPos.x} ${startPos.z} to ${endPos.x} ${endPos.z}")
        return 0
    }

    /**
     * Edits an existing zone's corners
     *
     * @param context command source
     * @param name zone name
     * @param startPos new first corner of zone
     * @param endPos new second corner of zone
     * @return 1 if successful edit, 0 if not
     */
    private fun zoneEditCommand(context: Context, name: String, startPos: BlockPos, endPos: BlockPos): Int {
        println("Editing zone: \"$name\", now located at ${startPos.x} ${startPos.z} to ${endPos.x} ${endPos.z}")
        return 0
    }

    /**
     * Deletes an existing zone
     *
     * @param context command source
     * @param name zone name
     * @return 1 if successful deletion, 0 if not
     */
    private fun zoneDeleteCommand(context: Context, name: String): Int {
        println("Deleting zone: \"$name\"")
        return 0
    }

    /**
     * Teleports to a zone
     *
     * @param context command source
     * @param name zone name
     * @return 1 if successful teleport, 0 if not
     */
    private fun zoneGotoCommand(context: Context, name: String): Int {
        println("Going to \"$name\"")
        return 0
    }

    /**
     * Lists all existing zones with
     * hover text with additional info
     * including who created it, location,
     * last edited by
     *
     * @param context command source
     * @return 1 if successful listing, 0 if not
     */
    private fun zoneListCommand(context: Context): Int {
        println("Listing all builder zones..")
        return 0
    }
}