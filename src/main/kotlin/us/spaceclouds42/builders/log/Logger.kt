package us.spaceclouds42.builders.log

import org.apache.logging.log4j.LogManager

// TODO: ADD DOKKA
class Logger {
    private val logger = LogManager.getLogger("FabricBuilders")
    private var logMode = LogMode.WTF

    fun info(info: LogInfo, mode: LogMode) {
        log(info, LogType.INFO, mode)
    }

    fun warn(info: LogInfo, mode: LogMode) {
        log(info, LogType.WARN, mode)
    }

    fun error(info: LogInfo, mode: LogMode) {
        log(info, LogType.ERROR, mode)
    }

    private fun log(info: LogInfo, type: LogType, mode: LogMode) {
        if (mode <= logMode) {
            log(info, type)
        }
    }

    private fun log(info: LogInfo, type: LogType) {
        logger.log(type.level, info.toString())
    }

    fun updateLogMode(mode: LogMode) {
        logMode = mode
    }
}