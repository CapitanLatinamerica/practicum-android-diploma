package ru.practicum.android.diploma.common.data.network

import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import ru.practicum.android.diploma.BuildConfig
import ru.practicum.android.diploma.common.data.model.AreasRequest
import ru.practicum.android.diploma.common.data.model.AreasResponse
import ru.practicum.android.diploma.common.data.model.IndustriesRequest
import ru.practicum.android.diploma.common.data.model.IndustriesResponse
import ru.practicum.android.diploma.common.data.model.VacanciesRequest
import ru.practicum.android.diploma.common.data.model.VacanciesResponse
import ru.practicum.android.diploma.common.data.model.VacancyRequest
import ru.practicum.android.diploma.common.data.model.VacancyResponse

@RunWith(AndroidJUnit4::class)
class RetrofitNetworkClientTest {

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
        appContext
    )

    @Test
    fun should_get_vacancies() = runBlocking {
        val vacancies = networkClient.doRequest(VacanciesRequest()) as VacanciesResponse
        assertEquals(200000, vacancies.found)
        assertEquals(200, vacancies.resultCode)

    }

    @Test
    fun should_get_vacancy() = runBlocking {
        val vacancy =
            networkClient.doRequest(VacancyRequest("b4cb93e5-1266-45b1-a1dd-43d193bd0631")) as VacancyResponse
        assertEquals("DevOps Engineer в Google", vacancy.name)
        assertEquals(200, vacancy.resultCode)
    }

    @Test
    fun should_get_areas() = runBlocking {
        val areas = networkClient.doRequest(AreasRequest()) as AreasResponse
        assertEquals("Республика Марий Эл", areas.areaDto[0].areas?.get(0)?.name)
    }

    @Test
    fun should_get_industries() = runBlocking {
        val industries = networkClient.doRequest(IndustriesRequest()) as IndustriesResponse
        assertEquals("Перевозки, логистика, склад, ВЭД", industries.industriesDto[0].name)
    }

}
