package ru.practicum.android.diploma.main.ui

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.room.Room
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import ru.practicum.android.diploma.BuildConfig
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.common.data.db.AppDataBase
import ru.practicum.android.diploma.common.data.db.entity.VacancyEntity
import ru.practicum.android.diploma.common.data.model.AreasRequest
import ru.practicum.android.diploma.common.data.model.FilteredVacancyRequest
import ru.practicum.android.diploma.common.data.model.VacanciesRequest
import ru.practicum.android.diploma.common.data.model.VacancyRequest
import ru.practicum.android.diploma.common.data.network.HeadHunterApi
import ru.practicum.android.diploma.common.data.network.RetrofitNetworkClient

class RootActivity : AppCompatActivity() {

    private val tag = "REQUESTS"
    
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_root)

        networkRequestExample(accessToken = BuildConfig.API_ACCESS_TOKEN)


        suspend fun testDatabase() {
            val db = Room.databaseBuilder(
                applicationContext,
                AppDataBase::class.java, "HeadHunterDatabase"
            ).build()



            db.vacancyDao().insertVacancies(
                mutableListOf(
                    VacancyEntity(
                        id = "b4cb93e5-1266-45b1-a1dd-43d193bd0621",
                        name = "DevOps Engineer в Google",
                        salaryCurrency = "HKD",
                        salaryFrom = 8000,
                        salaryTo = 18000,
                        logoPath = "/path/",
                        area = "Грузия",
                        employer = "Google",
                        experience = "Нет опыта",
                        employment = "Полная занятость",
                        schedule = "Полный день",
                        description = "описение",
                    )
                )
            )

            Log.d("DB", "testDatabase: ${db.vacancyDao().count()}")


        }

        lifecycleScope.launch { testDatabase() }
    }

    private fun networkRequestExample(accessToken: String) {

        val retrofit: Retrofit =
            Retrofit.Builder().baseUrl("https://practicum-diploma-8bc38133faba.herokuapp.com/")
                .addConverterFactory(
                    GsonConverterFactory.create()
                ).build()

        val api = retrofit.create(HeadHunterApi::class.java)

        val networkClient = RetrofitNetworkClient(
            headHunterApi = api,
            accessToken

        )






        lifecycleScope.launch {


            val responseVacancies = networkClient.doRequest(VacanciesRequest())

            Log.d(tag, "responseVacancies: ${responseVacancies.resultCode}")
            Log.d(tag, "responseVacancies: $responseVacancies")

            val responseVacancy =
                networkClient.doRequest(VacancyRequest("b4cb93e5-1266-45b1-a1dd-43d193bd0631"))

            Log.d(tag, "responseVacancies: ${responseVacancy.resultCode}")
            Log.d(tag, "responseVacancies: $responseVacancy")


            val responseAreas = networkClient.doRequest(AreasRequest())
            Log.d(tag, "responseAreas: ${responseAreas.resultCode}")
            Log.d(tag, "responseAreas: $responseAreas")

            val responseIndustries = networkClient.doRequest(AreasRequest())
            Log.d(tag, "responseAreas: $responseIndustries")

            val responseFilteredVacancies = networkClient.doRequest(
                FilteredVacancyRequest(
                    areaId = 1,
                    industryId = null,
                    text = null,
                    salary = null,
                    null,
                    onlyWithSalary = null
                )
            )

            Log.d(tag, "responseFilteredVacancies: $responseFilteredVacancies")


        }


    }

}
