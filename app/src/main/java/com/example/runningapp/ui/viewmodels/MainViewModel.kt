package com.example.runningapp.ui.viewmodels

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.runningapp.db.Run
import com.example.runningapp.other.SortType
import com.example.runningapp.repository.MainRepository
import kotlinx.coroutines.launch

class MainViewModel @ViewModelInject constructor(
    val mainRepository: MainRepository):ViewModel() {

    private val runSortedByDate  = mainRepository.getAllRunsSortedByDate()
    private val runSortedByDistance  = mainRepository.getAllRunsSortedByDistance()
    private val runSortedByCaloriesBurned  = mainRepository.getAllRunsSortedByCaloriesBurned()
    private val runSortedByTimeInMillis  = mainRepository.getAllRunsSortedByTimeInMillis()
    private val runSortedByAvgSpeed  = mainRepository.getAllRunsSortedByAvgSpeed()

    val runs = MediatorLiveData<List<Run>>()
    var sortType = SortType.DATE

    init {
        runs.addSource(runSortedByDate){
            if(sortType == SortType.DATE){
                it?.let {
                    runs.value = it
                }
            }
        }
        runs.addSource(runSortedByAvgSpeed){
            if(sortType == SortType.AVG_SPEED){
                it?.let {
                    runs.value = it
                }
            }
        }
        runs.addSource(runSortedByDistance){
            if(sortType == SortType.DISTANCE){
                it?.let {
                    runs.value = it
                }
            }
        }
        runs.addSource(runSortedByTimeInMillis){
            if(sortType == SortType.RUNNING_TIME){
                it?.let {
                    runs.value = it
                }
            }
        }
        runs.addSource(runSortedByCaloriesBurned){
            if(sortType == SortType.CALORIES_BURNED){
                it?.let {
                    runs.value = it
                }
            }
        }
    }
    fun sortRuns(sortType: SortType) = when(sortType)
    {
        SortType.DATE -> runSortedByDate.value?.let { runs.value = it }
        SortType.CALORIES_BURNED -> runSortedByCaloriesBurned.value?.let { runs.value = it }
        SortType.DISTANCE -> runSortedByDistance.value?.let { runs.value = it }
        SortType.RUNNING_TIME -> runSortedByTimeInMillis.value?.let { runs.value = it }
        SortType.AVG_SPEED -> runSortedByAvgSpeed.value?.let { runs.value = it }
    }.also {
        this.sortType = sortType
    }

    fun insertRun(run: Run) = viewModelScope.launch {
        mainRepository.insertRun(run)
    }

}