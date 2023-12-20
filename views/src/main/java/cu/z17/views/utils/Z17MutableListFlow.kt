package cu.z17.views.utils

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow

class Z17MutableListFlow<T>(initial: List<T> = emptyList()) {
    private val mutableList: MutableStateFlow<List<T>> = MutableStateFlow(initial)

    val value = flow {
        mutableList.collect {
            emit(it)
        }
    }

    fun change(element: T, index: Int) {
        val newArr = arrayListOf<T>()
        newArr.addAll(mutableList.value)

        newArr[index] = element
        mutableList.value = newArr.toList()
    }

    fun set(element: T) {
        val newArr = arrayListOf(element)
        mutableList.value = newArr.toList()
    }

    fun set(elements: List<T>) {
        val newArr = arrayListOf<T>()
        newArr.addAll(elements)

        mutableList.value = newArr.toList()
    }

    fun add(element: T) {
        val newArr = arrayListOf<T>()
        newArr.addAll(mutableList.value)
        newArr.add(element)

        mutableList.value = newArr.toList()
    }

    fun addAll(elements: List<T>) {
        val newArr = arrayListOf<T>()
        newArr.addAll(mutableList.value)
        newArr.addAll(elements)

        mutableList.value = newArr.toList()
    }

    fun removeAll(condition: (T) -> Boolean) {
        val newArr = arrayListOf<T>()
        newArr.addAll(mutableList.value)

        newArr.removeAll { condition(it) }

        mutableList.value = newArr.toList()
    }

    fun removeAt(index: Int) {
        val newArr = arrayListOf<T>()
        newArr.addAll(mutableList.value)
        if (index < newArr.size) {
            newArr.removeAt(index)

            mutableList.value = newArr.toList()
        }
    }

    fun removeLast(): T? {
        val newArr = arrayListOf<T>()
        newArr.addAll(mutableList.value)

        if (0 < newArr.size) {
            val removed = newArr.removeAt(newArr.size - 1)

            mutableList.value = newArr.toList()

            return removed
        }

        return null
    }

}