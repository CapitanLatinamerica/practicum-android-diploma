package ru.practicum.android.diploma.common.data.network

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
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

    lateinit var server: MockWebServer
    val token = "Bearer ${BuildConfig.API_ACCESS_TOKEN}"
    lateinit var retrofit: Retrofit
    lateinit var networkClient: RetrofitNetworkClient
    val appContext = InstrumentationRegistry.getInstrumentation().targetContext

    @Before
    fun setUp() {
        server = MockWebServer()
        server.start()
        retrofit =
            Retrofit.Builder().baseUrl(server.url("/"))
                .addConverterFactory(
                    GsonConverterFactory.create()
                ).build()
        val api = retrofit.create(HeadHunterApi::class.java)
        networkClient = RetrofitNetworkClient(
            headHunterApi = api,
            token,
            appContext
        )
    }

    @After
    fun shutDownServer() {
        server.shutdown()
    }

    @Test
    fun should_get_vacancies() = runBlocking {
        val responseBody = JsonApiMock.getResponseFromFile("vacancies.json")
        server.enqueue(MockResponse().setResponseCode(200).setBody(responseBody))

        val vacancies = networkClient.doRequest(VacanciesRequest()) as VacanciesResponse
        assertEquals(2, vacancies.found)
        assertEquals("df412536-34ed-4d18-8b5d-47f45c148106", vacancies.items[1].id)
        assertEquals("Backend Developer в Amazon", vacancies.items[1].name)

        assertEquals(200, vacancies.resultCode)

    }

    @Test
    fun should_get_vacancy() = runBlocking {
        val responseBody = JsonApiMock.getResponseFromFile("vacancy.json")
        server.enqueue(
            MockResponse().setBody(
                responseBody
            ).setResponseCode(200)
        )

        val vacancy =
            networkClient.doRequest(VacancyRequest("b4cb93e5-1266-45b1-a1dd-43d193bd0631")) as VacancyResponse
        assertEquals("DevOps Engineer в Google", vacancy.name)
        assertEquals(200, vacancy.resultCode)
    }

    @Test
    fun should_get_Vacancy_not_found_404() = runBlocking {
        val requestBody = "Vacancy not found"
        server.enqueue(MockResponse().setResponseCode(404).setBody(requestBody))
        val vacancy =
            networkClient.doRequest(VacancyRequest("b4cb93e5-1266-45b1-a1dd-43d193bd0632"))
        assertEquals(404, vacancy.resultCode)
    }

    @Test
    fun should_get_areas() = runBlocking {
        val requestBody = JsonApiMock.getResponseFromFile("areas.json")
        server.enqueue(
            MockResponse().setResponseCode(200).setBody(requestBody)
        )

        val areas = networkClient.doRequest(AreasRequest()) as AreasResponse
        val areaParent = areas.areaDto[0]
        val areaChild = areaParent.areas?.get(0)
        assertEquals("Россия", areaParent?.name)
        assertEquals("113", areaParent?.id)
        assertEquals("Республика Марий Эл", areaChild?.name)
        assertEquals("1620", areaChild?.id)

    }

    @Test
    fun should_get_areas_server_error() = runBlocking {
        server.enqueue(MockResponse().setResponseCode(500))
        val areas = networkClient.doRequest(AreasRequest())
        assertEquals(500, areas.resultCode)
    }

    // https://practicum-diploma-8bc38133faba.herokuapp.com/industries
    @Test
    fun should_get_industries() = runBlocking {
        val responseBody = JsonApiMock.getResponseFromFile("industries.json")
        server.enqueue(MockResponse().setResponseCode(200).setBody(responseBody))
        val industries = networkClient.doRequest(IndustriesRequest()) as IndustriesResponse
        assertEquals("7", industries.industriesDto[1].id)
        assertEquals("Информационные технологии, системная интеграция, интернет", industries.industriesDto[1].name)
    }

    @Test
    fun should_get_should_get_industries_server_error() = runBlocking {
        server.enqueue(MockResponse().setResponseCode(500))
        val industries = networkClient.doRequest(AreasRequest())
        assertEquals(500, industries.resultCode)
    }

}
