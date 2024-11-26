package com.shellinfo.common.data.local.data.emv_rupay

class HistoryQueue<T>(private val maxSize: Int = 4) : Iterable<T> {

    private val deque: ArrayDeque<T> = ArrayDeque(maxSize)

    override fun iterator(): Iterator<T> {
        return deque.iterator()
    }

    // Add method to support FIFO behavior and ensure queue has at most maxSize elements
    fun add(element: T): Boolean {
        if (deque.size == maxSize) {
            deque.removeFirst() // Remove the oldest element to maintain the size
        }
        return deque.add(element)
    }

    fun offer(element: T): Boolean {
        if (deque.size == maxSize) {
            deque.removeFirst() // Remove the oldest element
        }
        return deque.add(element)
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

    fun get(index: Int): T {
        if (index < 0 || index >= deque.size) {
            throw IndexOutOfBoundsException("Index: $index, Size: ${deque.size}")
        }
        return deque.elementAt(index) // Retrieves the element at the specified index
    }
}
