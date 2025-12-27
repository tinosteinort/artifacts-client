package artifacts.business.common

object Loggers {

    fun <T> getLogger(cls: Class<T>): Logger {
        return Logger()
    }
}