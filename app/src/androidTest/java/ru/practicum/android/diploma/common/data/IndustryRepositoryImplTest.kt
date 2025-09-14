package ru.practicum.android.diploma.common.data

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
import ru.practicum.android.diploma.common.data.mapper.IndustryMapper
import ru.practicum.android.diploma.common.data.network.HeadHunterApi
import ru.practicum.android.diploma.common.data.network.JsonApiMock
import ru.practicum.android.diploma.common.data.network.RetrofitNetworkClient

@RunWith(AndroidJUnit4::class)
class IndustryRepositoryImplTest {

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
    fun should_return_industry_list() = runBlocking {
        val responseBody = JsonApiMock.getResponseFromFile("industries.json")
        server.enqueue(MockResponse().setResponseCode(200).setBody(responseBody))
        val industryRepositoryImpl = IndustryRepositoryImpl(
            networkClient,
            IndustryMapper
        )
        val resource = industryRepositoryImpl.getIndustries()
        assertEquals(3, resource.data?.size)
        assertEquals("Перевозки, логистика, склад, ВЭД", resource.data?.get(0)?.name)
        assertEquals(3, resource.data?.size)
    }

    @Test
    fun should_return_error() = runBlocking {
        val responseBody = ""

        server.enqueue(MockResponse().setResponseCode(345).setBody(responseBody))
        val industryRepositoryImpl = IndustryRepositoryImpl(
            networkClient,
            IndustryMapper
        )
        val resource = industryRepositoryImpl.getIndustries()
        assertEquals("Ошибка: код 345", resource.message)

    }

    @Test
    fun should_return_server_error_500() = runBlocking {
        val responseBody = ""

        server.enqueue(MockResponse().setResponseCode(500).setBody(responseBody))
        val industryRepositoryImpl = IndustryRepositoryImpl(
            networkClient,
            IndustryMapper
        )
        val resource = industryRepositoryImpl.getIndustries()
        assertEquals("Ошибка сервера", resource.message)
    }

}
