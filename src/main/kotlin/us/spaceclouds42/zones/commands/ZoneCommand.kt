package us.spaceclouds42.zones.commands

import com.mojang.brigadier.arguments.IntegerArgumentType
import com.mojang.brigadier.arguments.StringArgumentType
import me.lucko.fabric.api.permissions.v0.Permissions
import net.minecraft.command.argument.BlockPosArgumentType
import net.minecraft.server.command.CommandManager
import net.minecraft.text.ClickEvent
import net.minecraft.text.HoverEvent
import net.minecraft.util.Identifier
import net.minecraft.util.math.BlockPos
import net.minecraft.util.registry.Registry
import net.minecraft.util.registry.RegistryKey
import us.spaceclouds42.zones.data.ZoneManager
import us.spaceclouds42.zones.data.spec.PosD
import us.spaceclouds42.zones.data.spec.Zone
import us.spaceclouds42.zones.data.spec.ZoneAccessMode
import us.spaceclouds42.zones.ext.toPos
import us.spaceclouds42.zones.utils.*

class ZoneCommand : ICommand {
    override fun register(dispatcher: Dispatcher) {
        /**
         * Base node of zone management
         */
        val zoneNode: Node =
            CommandManager
                .literal("zone")
                .requires(Permissions.require("fabriczones.command.zone", 2))
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
                                .literal("access")
                                .then(
                                    CommandManager
                                        .argument("mode", StringArgumentType.word())
                                        .suggests { _, builder ->
                                            ZoneAccessMode.values().forEach {
                                                builder.suggest(it.toString().toLowerCase())
                                            }

                                            builder.buildFuture()
                                        }
                                        .executes { zoneEditAccessCommand(
                                            it,
                                            StringArgumentType.getString(it, "name"),
                                            ZoneAccessMode.parse(StringArgumentType.getString(it, "mode"))
                                        ) }

                                )
                        )
                        .then(
                            CommandManager
                                .literal("corners")
                                .then(
                                    CommandManager
                                        .argument("start", BlockPosArgumentType.blockPos())
                                        .then(
                                            CommandManager
                                                .argument("end", BlockPosArgumentType.blockPos())
                                                .executes { zoneEditPosCommand(
                                                    it,
                                                    StringArgumentType.getString(it, "name"),
                                                    BlockPosArgumentType.getBlockPos(it, "start"),
                                                    BlockPosArgumentType.getBlockPos(it, "end")
                                                ) }
                                        )
                                )
                        )
                        .then(
                            CommandManager
                                .literal("color")
                                .then(
                                    CommandManager
                                        .argument("red", IntegerArgumentType.integer(0, 255))
                                        .then(
                                            CommandManager
                                                .argument("green", IntegerArgumentType.integer(0, 255))
                                                .then(
                                                    CommandManager
                                                        .argument("blue", IntegerArgumentType.integer(0, 255))
                                                        .executes { zoneEditColorFromRGBCommand(
                                                            it,
                                                            StringArgumentType.getString(it, "name"),
                                                            IntegerArgumentType.getInteger(it, "red"),
                                                            IntegerArgumentType.getInteger(it, "green"),
                                                            IntegerArgumentType.getInteger(it, "blue")
                                                        ) }
                                                )
                                        )
                                ).then(
                                    CommandManager
                                        .argument("hex", StringArgumentType.word())
                                        .suggests { _, builder ->
                                            builder.suggest("rainbow")
                                            builder.suggest("00ff00")
                                            builder.suggest("0 255 0")

                                            builder.buildFuture()
                                        }
                                        .executes { zoneEditColorFromHEXCommand(
                                            it,
                                            StringArgumentType.getString(it, "name"),
                                            StringArgumentType.getString(it, "hex")
                                        ) }
                                )
                        )
                        .then(
                            CommandManager
                                .literal("gotoPos")
                                .executes { zoneEditGotoPos(
                                    it,
                                    StringArgumentType.getString(it, "name")
                                ) }
                                .then(
                                    CommandManager
                                        .literal("remove")
                                        .executes { zoneEditGotoPos(
                                            it,
                                            StringArgumentType.getString(it, "name"),
                                            true
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

        // zone (create|edit|delete|goto|list)
        dispatcher.root.addChild(zoneNode)
        // zone create <name> <start> <end>
        zoneNode.addChild(createNode)
        // zone edit <name> (corners|access|color|goto)
        zoneNode.addChild(editNode)
        // zone delete <name>
        zoneNode.addChild(deleteNode)
        // zone goto <name>
        zoneNode.addChild(gotoNode)
        // zone list
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
            green("Created zone: \"$name\", from ") +
                    yellow("${startPos.x} ${startPos.y} ${startPos.z}") +
                    green(" to ") +
                    yellow("${endPos.x} ${endPos.y} ${endPos.z}"),
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
    private fun zoneEditPosCommand(context: Context, name: String, startPos: BlockPos, endPos: BlockPos): Int {
        val source = context.source
        val world = source.world.registryKey.value

        ZoneManager.editZonePos(
            name = name,
            startPos = startPos.toPos(world),
            endPos = endPos.toPos(world),
        )

        source.sendFeedback(
            green("Edited zone: \"$name\", now at ") +
            yellow("${startPos.x} ${startPos.y} ${startPos.z}") +
            green(" to ") +
            yellow("${endPos.x} ${endPos.y} ${endPos.z}"),
            true
        )

        return 1
    }

    /**
     * Edits an existing zone's access mode
     *
     * @param context command source
     * @param name zone name
     * @param mode new access mode
     * @return 1 if successful edit, 0 if not
     */
    private fun zoneEditAccessCommand(context: Context, name: String, mode: ZoneAccessMode): Int {
        ZoneManager.editZoneAccess(
            name = name,
            mode = mode,
        )

        context.source.sendFeedback(
            green("Edited zone: \"$name\", access mode is now ") + yellow(mode.toString()),
            true
        )

        return 1
    }

    /**
     * Edits an existing zone's border color using rgb
     *
     * @param context command source
     * @param name zone name
     * @param r red value
     * @param g green value
     * @param b blue value
     * @return 1 if successful edit, 0 if not
     */
    private fun zoneEditColorFromRGBCommand(context: Context, name: String, r: Int, g: Int, b: Int): Int {
        ZoneManager.editZoneBorderColor(name, r, g, b)

        if (r == g && g == b && r == b && r == 65025) {
            context.source.sendFeedback(
                green("Edited zone: \"$name\", border color is now ") +
                    red("R") +
                    gold("A") +
                    yellow("I") +
                    green("N") +
                    darkGreen("B") +
                    darkAqua("O") +
                    darkPurple("W"),
                false
            )

            return 1;
        }

        context.source.sendFeedback(
            green("Edited zone: \"$name\", border color is now ") +
                    yellow("$r, $g, $b"),
            false
        )

        return 1
    }

    /**
     * Edits an existing zone's border color using hex
     *
     * @param context command source
     * @param name zone name
     * @param hex color as hex code e.g. #00FF00
     * @return 1 if successful edit, 0 if not
     */
    private fun zoneEditColorFromHEXCommand(context: Context, name: String, hex: String): Int {
        if (hex.toLowerCase() == "rainbow") {
            return zoneEditColorFromRGBCommand(context, name, 65025, 65025, 65025)
        }

        if (hex.length != 6 || hex.replace("[A-Fa-f0-9]{6}".toRegex(), "") != "") {
            context.source.sendError(
                red("Incorrect HEX color code format")
            )
            return 0
        }

        val r = Integer.valueOf(hex.substring(0, 2), 16)
        val g = Integer.valueOf(hex.substring(2, 4), 16)
        val b = Integer.valueOf(hex.substring(4, 6), 16)

        return zoneEditColorFromRGBCommand(context, name, r, g, b)
    }

    /**
     * Sets the goto position of a zone
     *
     * @param context command source
     * @param name zone to be edited
     * @return 1 if successful edit, 0 if not
     */
    private fun zoneEditGotoPos(context: Context, name: String, remove: Boolean = false): Int {
        if (remove) {
            ZoneManager.editZoneGotoPos(name, null)

            context.source.sendFeedback(
                red("Removed goto position for zone \"$name\""),
                true
            )

            return 1
        }

        val player = context.source.player
        val x = player.x
        val y = player.y
        val z = player.z

        if (ZoneManager.getZone(name)?.positionInZone(player.world, x, y, z) == false) {
            context.source.sendError(
                red("You are not in the zone!")
            )

            return 0
        }

        ZoneManager.editZoneGotoPos(
            name,
            PosD(
                player.world.registryKey.value.toString(),
                x,
                y,
                z,
            ),
        )

        context.source.sendFeedback(
            green("Edited zone: \"$name\", now at ") +
                yellow("${x.toInt()}, ${y.toInt()}, ${z.toInt()}"),
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
            red("Deleted zone: ${ZoneManager.deleteZone(name)?.id}"),
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
        val gotoZone = ZoneManager.getZone(name)

        if (gotoZone?.gotoPos == null) {
            context.source.sendError(
                red("No goto position has been set for this zone")
            )

            return 0
        }

        val player = context.source.player
        val gotoPos = gotoZone.gotoPos!!

        player.teleport(
            player.server.getWorld(RegistryKey.of(Registry.DIMENSION, Identifier.tryParse(gotoPos.world))),
            gotoPos.x,
            gotoPos.y,
            gotoPos.z,
            player.yaw,
            player.pitch
        )

        context.source.sendFeedback(
            green("Going to: ") + yellow(name),
            false
        )

        return 1
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
                                green("Click to teleport!\n") +
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