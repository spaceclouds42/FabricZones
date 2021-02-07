package us.spaceclouds42.builders.commands

import com.mojang.brigadier.arguments.StringArgumentType
import net.minecraft.command.argument.BlockPosArgumentType
import net.minecraft.server.command.CommandManager
import net.minecraft.text.ClickEvent
import net.minecraft.text.HoverEvent
import net.minecraft.util.math.BlockPos
import us.spaceclouds42.builders.data.ZoneManager
import us.spaceclouds42.builders.data.spec.Zone
import us.spaceclouds42.builders.ext.toPos
import us.spaceclouds42.builders.utils.*

class ZoneCommand : ICommand {
    override fun register(dispatcher: Dispatcher) {
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
                        .suggests { _, builder ->
                            ZoneManager.getAllZones().keys.forEach {
                                builder.suggest(it)
                            }

                            builder.buildFuture()
                        }
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
                        .suggests { _, builder ->
                            ZoneManager.getAllZones().keys.forEach {
                                builder.suggest(it)
                            }

                            builder.buildFuture()
                        }
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
                        .suggests { _, builder ->
                            ZoneManager.getAllZones().keys.forEach {
                                builder.suggest(it)
                            }

                            builder.buildFuture()
                        }
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

        dispatcher.root.addChild(zoneNode)
        zoneNode.addChild(createNode)
        zoneNode.addChild(editNode)
        zoneNode.addChild(deleteNode)
        zoneNode.addChild(gotoNode)
        zoneNode.addChild(listNode)
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
        val source = context.source
        val world = source.world.registryKey.value

        ZoneManager.setZone(
            Zone(
                id = name,
                startPos = startPos.toPos(world),
                endPos = endPos.toPos(world),
                createdBy = source.player.entityName
            )
        )

        source.sendFeedback(
            green("Created zone: \"$name\" from ${startPos.x} ${startPos.y} ${startPos.z} to ${endPos.x} ${endPos.y} ${endPos.z}"),
            true
        )

        return 1
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
        val source = context.source
        val world = source.world.registryKey.value

        ZoneManager.editZonePos(
            name = name,
            startPos = startPos.toPos(world),
            endPos = endPos.toPos(world),
        )

        source.sendFeedback(
            green("Edited zone: \"$name\", now at ${startPos.x} ${startPos.y} ${startPos.z} to ${endPos.x} ${endPos.y} ${endPos.z}"),
            true
        )

        return 1
    }

    /**
     * Deletes an existing zone
     *
     * @param context command source
     * @param name zone name
     * @return 1 if successful deletion, 0 if not
     */
    private fun zoneDeleteCommand(context: Context, name: String): Int {
        context.source.sendFeedback(
            red("Deleted zone: ${ZoneManager.deleteZone(name)}"),
            true
        )

        return 1
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
        val zones = ZoneManager.getAllZones().values
        val msg = green("=======< Zones >=======\n")

        for (zone in zones.withIndex()) {
            msg + click(
                hover(
                    yellow(zone.value.id),
                    HoverEvent(
                        HoverEvent.Action.SHOW_TEXT,
                                green("Click to teleport!") +
                                        gray("Zone created by: ${zone.value.createdBy}")
                    )
                ),
                ClickEvent(ClickEvent.Action.RUN_COMMAND, "/zone goto ${zone.value.id}")
            )
            if (zone.index < zones.size - 1) msg + gray(", ")
        }

        context.source.sendFeedback(
            msg,
            false
        )

        return 1
    }
}