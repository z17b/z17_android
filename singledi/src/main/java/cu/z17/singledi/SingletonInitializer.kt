package cu.z17.singledi

open class SingletonInitializer<T : Any> {
    @Volatile
    private var instance: T? = null

    fun createInstance(creator: () -> T): T {
        return instance ?: synchronized(this) {
            instance ?: creator().also { instance = it }
        }
    }

    @Throws(SinglediException::class)
    fun getInstance(): T {
        if (instance == null) throw SinglediException()
        return instance!!
    }

    fun getInstanceOrNull(): T? {
        return instance
    }

    fun deleteInstance() {
        instance = null
    }
}