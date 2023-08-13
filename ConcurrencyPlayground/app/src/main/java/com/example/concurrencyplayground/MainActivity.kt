package com.example.concurrencyplayground

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.channels.produce
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.newSingleThreadContext

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        /***
         * Iterator
         */
        val myIterator = iterator {
            yield("First String")
            yield("Second String")
            yield("Third String")
        }
        Log.e("test", "get : " + myIterator.next()) // First String
        Log.e("test", "hasString : " + myIterator.hasNext().toString()) // true
        myIterator.forEach {
            Log.e("test", "get : $it") // Second String, Third String
        }
        Log.e("test", "hasString : " + myIterator.hasNext().toString()) // false

        /***
         * Sequence
         */
        val mySequence = sequence<String> {
            yield("First String")
            yield("Second String")
            yield("Third String")
        }
        Log.e("test", "elementAt : ${mySequence.elementAt(1)}") // Second String
        Log.e("test", "elementAtOrNull : ${mySequence.elementAtOrNull(3)}") // null
        Log.e("test", "elementAtOrElse : ${mySequence.elementAtOrElse(3) { "error at index=$it" }}") // error at index=3
        Log.e("test", "take : ${mySequence.take(2).joinToString(", ")}") // First String, Second String
        mySequence.forEach {
            Log.e("test", "get : $it") // First String \n Second String \n Third String
        }
        mySequence.forEach {
            Log.e("test", "get : $it") // First String \n Second String \n Third String
        }

        /***
         * Sequence Fibonacci
         */
        val seqFibonacci = sequence {
            yield(1)
            var current = 1
            var next = 1
            while (true) {
                yield(next)
                val tmpNext = current + next
                current = next
                next = tmpNext
            }
        }
        /***
         * Iterator Fibonacci
         */
        val iterFibonacci = iterator {
            yield(1)
            var current = 1
            var next = 1
            while (true) {
                yield(next)
                Log.e("test", "loop")
                val tmpNext = current + next
                current = next
                next = tmpNext
            }
        }

        /***
         * Producer
         */
        val coroutineContext = newSingleThreadContext("singleThread")
        val producer: ReceiveChannel<String> = CoroutineScope(Dispatchers.Default).produce<String>(coroutineContext) {
            send("First String")
            send("Second String")
            send("Third String")
        }
        CoroutineScope(coroutineContext).launch {
            Log.e("test", producer.receive()) // First String
            producer.consumeEach { string ->
                Log.e("test", string) // Second String \m Third String
            }
        }

        /***
         * Producer Fibonacci
         */
        val producerFibonacci = CoroutineScope(Dispatchers.Default).produce {
            send(1L)
            var current = 1L
            var next = 1L
            while (true) {
                send(next)
                val tmpNext = current + next
                current = next
                next = tmpNext
            }
        }
    }
}