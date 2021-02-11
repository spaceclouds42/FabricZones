package us.spaceclouds42.zones.log

import org.apache.logging.log4j.Logger

class Logger(private val logger: Logger, private var logMode: LogMode) {
    fun info(info: String, mode: LogMode) {
        log(info, LogType.INFO, mode)
    }

    fun warn(info: String, mode: LogMode) {
        log(info, LogType.WARN, mode)
    }

    fun error(info: String, mode: LogMode) {
        log(info, LogType.ERROR, mode)
    }

    private fun log(info: String, type: LogType, mode: LogMode) {
        if (mode <= logMode) {
            log(info, type)
        }
    }

    private fun log(info: String, type: LogType) {
        logger.log(type.level, info)
    }

    fun updateLogMode(mode: LogMode) {
        logMode = mode
    }
}