package com.example.concurrencyplayground

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.newSingleThreadContext
import java.lang.UnsupportedOperationException

class MainActivity : AppCompatActivity() {
    val networkDispatcher = newSingleThreadContext(name = "networkCall")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // launch : 반환값을 갖지 않는 실행 전용 Job 객체 반환 (연산이 실패하면 exception 을 전파받는다)
        val task1 = CoroutineScope(Dispatchers.Default).launch {
            doSomeThing()
        }

        // async : 반환값을 갖는 Deferred 객체 반환
        val task2 = GlobalScope.async {
            doSomeThing()
        }

        task1.isActive // job 이 시작되었지만, 아직 완료상태가 아닐 때 true (completed 이거나 cancelled 가 true 일 때 false)
        task1.isCompleted // 성공/실패와 관련없이 task 가 완료되었을 때 true
        task1.isCancelled // 실패 혹은 parent or child Coroutine 의 cancel 등으로 인하여 Job 이 cancel 되었을 때 true

        CoroutineScope(networkDispatcher).launch {
            task2.join() // Exception 발생으로 인하여, Unit 반환
            task2.await() // Exception 발생으로 인하여 전파된 Exception 여기서 발생
        }

        CoroutineScope(Dispatchers.Main).launch {
            // UI Dispatcher 내에서 동작
        }

        CoroutineScope(Dispatchers.IO).launch {
            // IO Dispatcher 내에서 동작
        }

        CoroutineScope(Dispatchers.Default).launch {
            // Default Dispatcher 내에서 동작
        }
    }

    suspend fun doSomeThing() {
        throw UnsupportedOperationException("failed")
    }
}