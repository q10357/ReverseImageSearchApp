package no.kristiania.android.reverseimagesearchapp.core.util


//We use this class to distinguish between Loading, success and error states
//When we send request on the network, it is good to have a class that makes it simple
//To store the data and pass it to the viewModels
//Are we still loading etc
data class Resource<out T>(val status: Status, val data: T?, val message: String? = null) {
    companion object {

        fun <T> success(data: T?, message: String?= ""): Resource<T> {
            return Resource(Status.SUCCESS, data, message)
        }

        fun <T> error(msg: String?, data: T? = null): Resource<T> {
            return Resource(Status.ERROR, data, msg)
        }

        fun <T> loading(msg: String = "loading...", data: T? = null): Resource<T> {
            return Resource(Status.LOADING, data, null)
        }
    }
}

enum class Status {
    SUCCESS,
    ERROR,
    LOADING
}