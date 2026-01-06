package artifacts.business.util

class Logger(private val cls: Class<*>) {

    fun info(message: String) {
        println("Info[${cls.simpleName}]: $message")
    }
    fun error(message: String) {
        println("Error[${cls.simpleName}]: $message")
    }
}