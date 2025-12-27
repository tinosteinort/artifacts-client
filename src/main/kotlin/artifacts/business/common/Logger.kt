package artifacts.business.common

class Logger {

    fun info(message: String) {
        println("Info: ${message}")
    }
    fun error(message: String) {
        println("Error: ${message}")
    }
}