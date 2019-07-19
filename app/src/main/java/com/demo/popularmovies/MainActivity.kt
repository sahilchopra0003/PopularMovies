package com.demo.popularmovies

import android.os.Bundle
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.activity_main.*

const val KEY_STATE = "State"

class MainActivity : AppCompatActivity(), MoviesView {

    private lateinit var lifecycleStage: MviLifecycle
    private val lifecycleEvents = PublishSubject.create<MviLifecycle>()
    private val refreshEvents = PublishSubject.create<Unit>()
    private val userIntentions = MoviesIntention(refreshEvents)
    private val cachedRepository = CachedRepositoryImpl(RestApi.getService(), AppDatabase(this))
    private val previousStates = BehaviorSubject.create<MoviesState>()
    private val compositeDisposable = CompositeDisposable()
    private lateinit var moviesListAdapter: MoviesListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        moviesListRecyclerView.layoutManager = LinearLayoutManager(this)
        moviesListAdapter = MoviesListAdapter(arrayListOf())
        moviesListRecyclerView.adapter = moviesListAdapter

        lifecycleStage = if (savedInstanceState == null)
            MviLifecycle.CREATED
        else
            MviLifecycle.RESUMED

        if (savedInstanceState != null) {
            val previousState = savedInstanceState.getParcelable<MoviesState>(KEY_STATE) as MoviesState
            previousStates.onNext(previousState)
        }

        retryTextView.setOnClickListener { refreshEvents.onNext(Unit) }

    }

    override fun onStart() {
        super.onStart()
        MoviesModel.bind(lifecycleEvents, userIntentions, cachedRepository, previousStates)
            .doOnNext { previousStates.onNext(it) }
            .subscribe { render(it) }
            .addTo(compositeDisposable)
        lifecycleEvents.onNext(lifecycleStage)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelable(KEY_STATE, previousStates.value)
    }

    override fun onDestroy() {
        compositeDisposable.clear()
        super.onDestroy()
    }

    override fun showLoadingView(show: Boolean) {
        if (show)
            progressBar.visibility = VISIBLE
        else
            progressBar.visibility = GONE

        moviesListRecyclerView.visibility = GONE
    }

    override fun showNoDataLabel(show: Boolean) {
        if (show) {
            msgTextView.visibility = VISIBLE
            retryTextView.visibility = VISIBLE
        } else {
            msgTextView.visibility = GONE
            retryTextView.visibility = GONE
        }
        msgTextView.setText("There are no items in the list for now. Please try again later...")
        moviesListRecyclerView.visibility = GONE
    }

    override fun showError(errorMsg: String, show: Boolean) {
        if (show) {
            msgTextView.visibility = VISIBLE
            retryTextView.visibility = VISIBLE
        } else {
            msgTextView.visibility = GONE
            retryTextView.visibility = GONE
        }
        msgTextView.text = errorMsg
        moviesListRecyclerView.visibility = GONE
    }

    override fun refreshList(newList: List<Movie>) {
        moviesListRecyclerView.visibility = VISIBLE
        moviesListAdapter.refreshList(ArrayList(newList))
    }
}
