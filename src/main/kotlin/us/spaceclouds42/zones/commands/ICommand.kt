package us.spaceclouds42.zones.commands

import us.spaceclouds42.zones.utils.Dispatcher

interface ICommand {
    /**
     * Registers all the command nodes to the command dispatcher
     *
     * @param dispatcher the server's command dispatcher
     */
    fun register(dispatcher: Dispatcher)
}