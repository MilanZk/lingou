package com.lingou

import java.lang.IllegalArgumentException
import java.lang.IllegalStateException
import java.util.concurrent.CopyOnWriteArraySet
import java.util.concurrent.atomic.AtomicInteger

object LingouRepo {
    private val idCounter = AtomicInteger()
    private val lingous = mutableListOf<Ingou>(Ingou("Ingou", "Ingou description"))

    fun add(ingou: Ingou): Ingou {

        if (lingous.contains(ingou)) {
            return lingous.find { it == ingou }!!
        }
        ingou.idIngou = idCounter.incrementAndGet()
        lingous.add(ingou)
        return ingou
    }

    fun get(id: String) =
        lingous.find { it.idIngou.toString() == id } ?: throw IllegalStateException("No entity found for this $id")

    fun get(id: Int) = get(id.toString())

    fun getAll() = lingous.toList()

    fun remove(ingou: Ingou) {
        if (!lingous.contains(ingou)) {
            throw IllegalArgumentException("Person not stored in repo.")
        }

        lingous.remove(ingou)
    }

    fun remove(id: String) = lingous.remove(get(id))

    fun remove(id: Int) = lingous.remove(get(id))

    fun clear() = lingous.clear()

}