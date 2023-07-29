package com.example.concurrencyplayground

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        /*** Job Basic ***/
        // Job 객체 생성
        val job1 = CoroutineScope(Dispatchers.Default).launch {
            // 바로 시작하는 Jobs
        }
        val job2 = CoroutineScope(Dispatchers.Default).launch(start = CoroutineStart.LAZY) {
            // 바로 시작하지 않는 Job
        }

        // Job 시작
        job2.start() // 시작하지 않은 Job2 을 시작한다. (Job 완료 기다림 X)
        GlobalScope.launch {
            job2.join() // 시작하지 않은 Job2 을 시작한다. (Job 완료 기다림)
        }

        // job 취소
        job2.cancel()

        // job 의 상태 확인
        job1.isActive // Job 활성상태 확인
        job1.isCompleted // Job 실행 완료 여부 확인 (취소완료도 실행완료이다)
        job1.isCancelled // Job 취소 여부, 취소가 요청되면 바로 true 를 반환한다.

        /*** error handling in Job ***/
        // exception with handler
        val exceptionHandler = CoroutineExceptionHandler { coroutineContext, throwable ->
            Log.e("test", "error message : ${throwable.message})")
        }
        CoroutineScope(exceptionHandler).launch { 
            // 작업
        }

        // exception with invokeOnCompletion
        CoroutineScope(Dispatchers.Default).launch {
            // 작업
        }.invokeOnCompletion { throwable ->
            Log.e("test", "error message : ${throwable?.message})")
        }

        /*** Deferred Basic ***/
        // Job 객체 생성
        val deferred = CoroutineScope(Dispatchers.Default).async {
            // 작업
        }
        val deferred2 = CompletableDeferred<String>()

        // Job 시작
        GlobalScope.launch {
            deferred.join()     // job(deferred) 이 끝나기를 기다림, exception 전파 X
            deferred.await()    // job(deferred) 이 끝나기를 기다림, exception 전파 O
        }
    }
}