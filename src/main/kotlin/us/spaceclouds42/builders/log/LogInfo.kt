package us.spaceclouds42.builders.log

// TODO: ADD DOKKA
class LogInfo(
    private val msg: String,

    private val variables: Array<Any> = arrayOf(),
) {
    override fun toString(): String {
        val logInfo = msg

        for (i in variables.indices) {
            logInfo.replace("""#${i}""".toRegex(), variables[i].toString())
        }

        return logInfo
    }
}