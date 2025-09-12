package ru.practicum.android.diploma.common.data

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import ru.practicum.android.diploma.BuildConfig
import ru.practicum.android.diploma.common.data.mapper.IndustryMapper
import ru.practicum.android.diploma.common.data.network.HeadHunterApi
import ru.practicum.android.diploma.common.data.network.RetrofitNetworkClient

@RunWith(AndroidJUnit4::class)
class IndustryRepositoryImplTest {

    val token = "Bearer ${BuildConfig.API_ACCESS_TOKEN}"

    val retrofit: Retrofit =
        Retrofit.Builder().baseUrl("https://practicum-diploma-8bc38133faba.herokuapp.com/")
            .addConverterFactory(
                GsonConverterFactory.create()
            ).build()

    val api = retrofit.create(HeadHunterApi::class.java)
    val appContext = InstrumentationRegistry.getInstrumentation().targetContext
    val networkClient = RetrofitNetworkClient(
        headHunterApi = api,
        token,
        context = appContext
    )

    @Test
    fun should_return_industry_list() = runBlocking {
        val industryRepositoryImpl = IndustryRepositoryImpl(
            networkClient,
            IndustryMapper
        )
        val resource = industryRepositoryImpl.getIndustries()
        assertEquals(30, resource.data?.size)
    }
}
