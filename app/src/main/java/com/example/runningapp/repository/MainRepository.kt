package com.example.runningapp.repository

import com.example.runningapp.db.Run
import com.example.runningapp.db.RunDao
import javax.inject.Inject


class MainRepository@Inject constructor(
    val runDao:RunDao
){
    suspend fun insertRun(run: Run)=runDao.insertRun(run)
    suspend fun deleteRun(run: Run)=runDao.deleteRun(run)

    fun getAllRusSortedByDate()=runDao.getAllRunsSortedByDate()
    fun getAllRusSortedByDistance()=runDao.getAllRunsSortedByDistance()
    fun getAllRusSortedByTimeInMillis()=runDao.getAllRunsSortedByTimeInMillis()
    fun getAllRusSortedByAvgSpeed()=runDao.getAllRunsSortedByAvgSpeed()
    fun getAllRusSortedByCaloriesBurned()=runDao.getAllRunsSortedByCaloriesBurned()

    fun getTotalAvgSpeed()=runDao.getTotalAverageSpeed()
    fun getTotalDistance()=runDao.getTotalDistance()
    fun getTotalCaloriesBurned()=runDao.getTotalCaloriesBurned()
    fun getTotalTimeInMillis()=runDao.getTotalTimeInMillis()

}