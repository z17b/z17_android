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

    fun contains(condition: (T) -> Boolean): Boolean {
        var isIn = false
        mutableList.value.forEach {
            isIn = condition(it)
            if (isIn) return@forEach
        }
        return isIn
    }

    fun change(element: T, index: Int): List<T> {
        val newArr = arrayListOf<T>()
        newArr.addAll(mutableList.value)

        newArr[index] = element
        mutableList.value = newArr.toList()

        return mutableList.value
    }

    fun set(element: T): List<T> {
        val newArr = arrayListOf(element)
        mutableList.value = newArr.toList()

        return mutableList.value
    }

    fun set(elements: List<T>): List<T> {
        val newArr = arrayListOf<T>()
        newArr.addAll(elements)

        mutableList.value = newArr.toList()

        return mutableList.value
    }

    fun add(element: T): List<T> {
        val newArr = arrayListOf<T>()
        newArr.addAll(mutableList.value)
        newArr.add(element)

        mutableList.value = newArr.toList()

        return mutableList.value
    }

    fun addAll(elements: List<T>): List<T> {
        val newArr = arrayListOf<T>()
        newArr.addAll(mutableList.value)
        newArr.addAll(elements)

        mutableList.value = newArr.toList()

        return mutableList.value
    }

    fun removeAll(condition: (T) -> Boolean): List<T> {
        val newArr = arrayListOf<T>()
        newArr.addAll(mutableList.value)

        newArr.removeAll { condition(it) }

        mutableList.value = newArr.toList()

        return mutableList.value
    }

    fun removeAll() {
        val newArr = arrayListOf<T>()
        newArr.addAll(mutableList.value)

        newArr.clear()

        mutableList.value = newArr.toList()
    }

    fun removeAt(index: Int): List<T> {
        val newArr = arrayListOf<T>()
        newArr.addAll(mutableList.value)
        if (index < newArr.size) {
            newArr.removeAt(index)

            mutableList.value = newArr.toList()
        }

        return mutableList.value
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

    fun removeFirst(): T? {
        val newArr = arrayListOf<T>()
        newArr.addAll(mutableList.value)

        if (0 < newArr.size) {
            val removed = newArr.removeAt(0)

            mutableList.value = newArr.toList()

            return removed
        }

        return null
    }

}