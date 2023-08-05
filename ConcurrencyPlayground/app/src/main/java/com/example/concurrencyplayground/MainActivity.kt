package com.example.concurrencyplayground

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.async
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        /** suspend 함수 **/
        CoroutineScope(Dispatchers.Main).launch {
            val userName1 = getUserName1().await() // Deferred 반납 방법
            val userName2 = getUserName2() // suspend 사용 방법 (더욱 간단하다!)
        }

        /** Coroutine 예외 **/
        // Default Coroutine
        /***
         * <실행결과>
         * 취소 완료
         */
        val cancelableJob1 = CoroutineScope(Dispatchers.Main).launch {
            try {
                // 작업
            } finally {
                // exception 발생 시 catch 이후 작업
                Log.e("test1", "취소 완료")
                delay(5000L)
                Log.e("test1", "5000L 기다리고 실행된 로그")
            }
        }
        CoroutineScope(Dispatchers.Main).launch {
            cancelableJob1.cancelAndJoin()
        }

        // Non-Cancellable Coroutine
        /***
         * <실행결과>
         * 취소 완료
         * 5000L 기다리고 실행된 로그
         */
        val cancelableJob2 = CoroutineScope(Dispatchers.Main).launch {
            try {
                // 작업
            } finally {
                // exception 발생 시 catch 이후 작업
                withContext(NonCancellable) {
                    Log.e("test2", "취소 완료")
                    delay(5000L)
                    Log.e("test2", "5000L 기다리고 실행된 로그")
                }
            }
        }
        CoroutineScope(Dispatchers.Main).launch {
            cancelableJob2.cancelAndJoin()
        }

        /** CoroutineContext 의 조합/분리/전환 **/
        val dispatcher = newSingleThreadContext("singleThreadContext")
        val exceptionHandler = CoroutineExceptionHandler { coroutineContext, throwable ->
            Log.e("test", "에러발생 에러발생!")
        }

        val totalContext = dispatcher + exceptionHandler // Context 결합
        CoroutineScope(totalContext).launch {
            // 작업
        }

        // Context 분리
        val originalDispatcher = totalContext.minusKey(exceptionHandler.key)
        val originalExceptionHandler = totalContext.minusKey(dispatcher.key)
        
        // Context 전환
        CoroutineScope(totalContext).launch {
            // dispatcher + exceptionHandler

            withContext(originalDispatcher) {
                // dispatcher
            }
        }
    }
    // case 1: Deferred 를 반납하는 Coroutine 사용법
    fun getUserName1(): Deferred<String> = CoroutineScope(Dispatchers.Default).async {
        delay(1000L)
        "name"
    }

    // case 2: suspend 함수로 만드는 방법
    suspend fun getUserName2(): String {
        delay(1000L)
        return "name"
    }
}