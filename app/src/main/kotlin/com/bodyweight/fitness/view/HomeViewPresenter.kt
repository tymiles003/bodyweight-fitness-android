package com.bodyweight.fitness.view

import android.content.Context
import android.util.AttributeSet

import com.bodyweight.fitness.R
import com.bodyweight.fitness.extension.debug
import com.bodyweight.fitness.model.RepositoryCategory
import com.bodyweight.fitness.model.RepositoryExercise
import com.bodyweight.fitness.model.RepositoryRoutine
import com.bodyweight.fitness.repository.Repository
import com.bodyweight.fitness.setLayoutWeight
import com.bodyweight.fitness.stream.RoutineStream
import com.bodyweight.fitness.stream.Stream

import com.trello.rxlifecycle.kotlin.bindToLifecycle

import kotlinx.android.synthetic.main.view_home.view.*
import org.joda.time.DateTime

data class CompletionRate(val percentage: Int, val label: String)

class HomeViewPresenter : AbstractPresenter() {
    override fun bindView(view: AbstractView) {
        super.bindView(view)

        Stream.drawerObservable()
                .bindToLifecycle(view)
                .filter { it.equals(R.id.action_menu_home) }
                .subscribe {
                    updateTodaysProgress()
                    updateStatistics()
                }

        Stream.repositoryObservable
                .bindToLifecycle(view)
                .subscribe {
                    updateTodaysProgress()
                    updateStatistics()
                }
    }

    fun updateTodaysProgress() {
        val view = (getView() as HomeView)

        if (Repository.repositoryRoutineForTodayExists()) {
            val repositoryRoutine = Repository.repositoryRoutineForToday

            repositoryRoutine.categories.getOrNull(0)?.let {
                val completionRate = getCompletionRateForCategory(true, it)

                view.setCategoryOne(it.title, completionRate.label, calculateLayoutWeight(completionRate.percentage))
            }

            repositoryRoutine.categories.getOrNull(1)?.let {
                val completionRate = getCompletionRateForCategory(true, it)

                view.setCategoryTwo(it.title, completionRate.label, calculateLayoutWeight(completionRate.percentage))
            }

            repositoryRoutine.categories.getOrNull(2)?.let {
                val completionRate = getCompletionRateForCategory(true, it)

                view.setCategoryThree(it.title, completionRate.label, calculateLayoutWeight(completionRate.percentage))
            }

            view.setStartWorkoutButtonTitle(title = getStartWorkoutButtonText(true))
        } else {
            val routine = RoutineStream.routine

            routine.categories.getOrNull(0)?.let {
                view.setCategoryOne(it.title, "0%", calculateLayoutWeight(0))
            }

            routine.categories.getOrNull(1)?.let {
                view.setCategoryTwo(it.title, "0%", calculateLayoutWeight(0))
            }

            routine.categories.getOrNull(2)?.let {
                view.setCategoryThree(it.title, "0%", calculateLayoutWeight(0))
            }

            view.setStartWorkoutButtonTitle(title = getStartWorkoutButtonText(false))
        }
    }

    fun updateStatistics() {
        val view = (getView() as HomeView)

        // lastWorkout = X years ago / X months ago / X weeks ago / X days ago / Today / Yesterday / Never

        val totalWorkouts = getNumberOfWorkouts()
        val lastWorkoutLabel = getLastWorkoutLabel()
        val last7Days = getNumberOfWorkouts(days = 7)
        val last30Days = getNumberOfWorkouts(days = 30)

        view.setNumberOfWorkouts("$totalWorkouts ${getNumberOfWorkoutsPostfix(totalWorkouts)}")
        view.setLastWorkout("$lastWorkoutLabel")
        view.setNumberOfWorkoutsLast7Days("$last7Days ${getNumberOfWorkoutsPostfix(last7Days)}")
        view.setNumberOfWorkoutsLast30Days("$last30Days ${getNumberOfWorkoutsPostfix(last30Days)}")
    }

    fun getCompletionRateForCategory(repositoryRoutineForTodayExists: Boolean, repositoryCategory: RepositoryCategory? = null): CompletionRate {
        if (repositoryRoutineForTodayExists && repositoryCategory != null) {
            val exercises = repositoryCategory.exercises.filter {
                it.isVisible == true || RepositoryExercise.isCompleted(it)
            }

            val numberOfExercises: Int = exercises.size
            val numberOfCompletedExercises: Int = exercises.filter {
                RepositoryExercise.isCompleted(it)
            }.size

            if (numberOfExercises == 0) {
                return CompletionRate(0, "0%")
            }

            val completionRate = numberOfCompletedExercises * 100 / numberOfExercises

            return CompletionRate(completionRate, "$completionRate%")
        } else {
            return CompletionRate(0, "0%")
        }
    }

    fun calculateLayoutWeight(completionRate: Int): Float {
        if (completionRate <= 10) {
            return 7f
        }

        val weight = completionRate * 0.7f;

        if (weight > 70f) {
            return 70f
        }

        return weight
    }

    fun getStartWorkoutButtonText(repositoryRoutineForTodayExists: Boolean): String {
        if (repositoryRoutineForTodayExists) {
            return "Go to Workout"
        }

        return "Start Workout"
    }

    fun getLastWorkoutLabel(): String {
        val startTime = DateTime().withTimeAtStartOfDay()

        val results = Repository.realm.where(RepositoryRoutine::class.java)
                .lessThan("startTime", startTime.toDate())
                .findAll()

        if (results.isNotEmpty()) {
            results.lastOrNull()?.let {
                return it.startTime.toString()
            }
        }

        return "Never"
    }

    fun getNumberOfWorkouts(): Int {
        return Repository.realm.where(RepositoryRoutine::class.java)
                .count()
                .toInt()
    }

    fun getNumberOfWorkouts(days: Int = 7): Int {
        val start = DateTime()
                .withTimeAtStartOfDay()
                .plusDays(1)
                .minusSeconds(1) // 23:59

        val end = start.minusDays(days) // 23:59 - 7 Days

        return Repository.realm.where(RepositoryRoutine::class.java)
                .between("startTime", end.toDate(), start.toDate())
                .count()
                .toInt()
    }

    fun getNumberOfWorkoutsPostfix(count: Int): String {
        if (count == 1) {
            return "Workout"
        } else {
            return "Workouts"
        }
    }

    fun startWorkout() {
        Stream.setDrawer(R.id.action_menu_workout)
    }
}

open class HomeView : AbstractView {
    override var presenter: AbstractPresenter = HomeViewPresenter()

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    override fun onCreateView() {
        super.onCreateView()

        start_workout.setOnClickListener {
            (presenter as HomeViewPresenter).startWorkout()
        }
    }

    fun setStartWorkoutButtonTitle(title: String) {
        start_workout.text = title
    }

    fun setCategoryOne(title: String, completionRateLabel: String, completionRateValue: Float) {
        category_one_title.text = title
        category_one_completion_rate_label.text = completionRateLabel
        category_one_completion_rate_value.setLayoutWeight(completionRateValue)
    }

    fun setCategoryTwo(title: String, completionRateLabel: String, completionRateValue: Float) {
        category_two_title.text = title
        category_two_completion_rate_label.text = completionRateLabel
        category_two_completion_rate_value.setLayoutWeight(completionRateValue)
    }

    fun setCategoryThree(title: String, completionRateLabel: String, completionRateValue: Float) {
        category_three_title.text = title
        category_three_completion_rate_label.text = completionRateLabel
        category_three_completion_rate_value.setLayoutWeight(completionRateValue)
    }

    fun setNumberOfWorkouts(title: String) {
        total_workouts_value.text = title
    }

    fun setLastWorkout(title: String) {
        last_workout_value.text = title
    }

    fun setNumberOfWorkoutsLast7Days(title: String) {
        last_7_days_value.text = title
    }

    fun setNumberOfWorkoutsLast30Days(title: String) {
        last_30_days_value.text = title
    }
}