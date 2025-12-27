package artifacts.business.util

sealed class Outcome<out T, out E> {

    data class Success<T>(val value: T) : Outcome<T, Nothing>()
    data class Error<E>(val value: E) : Outcome<Nothing, E>()

    companion object {

        fun <T> success(value: T) = Success(value)
        fun <E> error(error: E) = Error(error)
    }
}
