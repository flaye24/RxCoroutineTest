package com.example.rxcoroutinetest

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.rx2.asObservable
import kotlinx.coroutines.rx2.rxObservable
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class MainActivity : AppCompatActivity() {

    private val compositeDisposable = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        solution1.setOnClickListener {
            val disposable = searchSolution1("test")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {members ->
                    tv_result.text = members.joinToString { it.id }
                }

            compositeDisposable.add(disposable)
        }

        solution2.setOnClickListener {
            val disposable = searchSolution2("test")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {members ->
                    tv_result.text = members.joinToString { it.id }
                }

            compositeDisposable.add(disposable)
        }
    }


    /**
     * Solution 1 using [rxObservable] to convert coroutine to RX
     */
    private fun searchSolution1(query: String): Observable<List<Member>> {
        return rxObservable(Dispatchers.IO) {
            try {
                send(members())
            } catch (e: Exception) {
                close(e)
            }
        }
    }

    /**
     * Solution 1 using [flow] and asObservable() convert coroutine to RX
     */
    private fun searchSolution2(query: String): Observable<List<Member>> {
        return flow {
            emit(members())
        }.flowOn(Dispatchers.IO)
            .asObservable()
    }


    private suspend fun members(): List<Member> = suspendCancellableCoroutine { continuation ->
        try {
            continuation.resume(listOf(Member("1"), Member("2")))
        } catch (e: Exception) {
            continuation.resumeWithException(e)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.clear()
    }
}
