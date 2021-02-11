package us.spaceclouds42.zones.utils

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.tree.LiteralCommandNode
import net.minecraft.server.command.ServerCommandSource

typealias Dispatcher = CommandDispatcher<ServerCommandSource>
typealias Context = CommandContext<ServerCommandSource>
typealias Node = LiteralCommandNode<ServerCommandSource>