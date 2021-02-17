package eu.vojtechh.takeyourpill.klass

data class CallResult<out T>(private val successful: Boolean, val data: T? = null) {
    companion object {
        fun <T> failure() = CallResult<T>(false)
        fun <T> success(data: T) = CallResult(true, data)
    }

    val isSuccessful
        get() = successful

    val isFailed
        get() = !successful

    fun onSuccess(action: (T) -> Unit): CallResult<T> {
        if (isSuccessful) action(data!!)
        return this
    }

    fun onFailure(action: () -> Unit): CallResult<T> {
        if (isFailed) action()
        return this
    }
}