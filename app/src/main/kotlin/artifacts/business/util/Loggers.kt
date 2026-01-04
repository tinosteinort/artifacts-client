package artifacts.business.util

object Loggers {

    fun <T> getLogger(cls: Class<T>): Logger {
        return Logger()
    }
}