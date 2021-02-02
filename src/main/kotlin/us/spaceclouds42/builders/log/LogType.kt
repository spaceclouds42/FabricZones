package us.spaceclouds42.builders.log

import org.apache.logging.log4j.Level


// TODO: ADD DOKKA
enum class LogType(val level: Level) {
    INFO(Level.INFO),
    WARN(Level.WARN),
    ERROR(Level.ERROR),
}