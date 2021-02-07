package us.spaceclouds42.builders.commands

import us.spaceclouds42.builders.utils.Dispatcher

interface ICommand {
    /**
     * Registers all the command nodes to the command dispatcher
     *
     * @param dispatcher the server's command dispatcher
     */
    fun register(dispatcher: Dispatcher)
}