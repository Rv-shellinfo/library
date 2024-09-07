package com.shellinfo.common.data.local.data.emv_rupay

class HistoryQueue<T>(private val maxSize: Int) {

    private val deque: ArrayDeque<T> = ArrayDeque(maxSize)

    fun add(element: T): Boolean {
        if (deque.size == maxSize) {
            throw IllegalStateException("Queue full")
        }
        return deque.add(element)
    }

    fun offer(element: T): Boolean {
        return if (deque.size == maxSize) {
            false
        } else {
            deque.add(element)
        }
    }

    fun remove(): T {
        if (deque.isEmpty()) {
            throw NoSuchElementException("Queue empty")
        }
        return deque.removeFirst()
    }

    fun poll(): T? {
        return if (deque.isEmpty()) {
            null
        } else {
            deque.removeFirst()
        }
    }

    fun peek(): T? {
        return deque.firstOrNull()
    }

    fun size(): Int {
        return deque.size
    }

    fun isEmpty(): Boolean {
        return deque.isEmpty()
    }

    fun isFull(): Boolean {
        return deque.size == maxSize
    }

    override fun toString(): String {
        return deque.toString()
    }
}