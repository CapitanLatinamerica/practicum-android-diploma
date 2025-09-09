package ru.practicum.android.diploma.search.domain.usecase

import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import ru.practicum.android.diploma.BuildConfig
import ru.practicum.android.diploma.Tools
import ru.practicum.android.diploma.common.data.VacancyRepositoryImpl
import ru.practicum.android.diploma.common.data.mapper.VacancyMapper
import ru.practicum.android.diploma.common.data.network.HeadHunterApi
import ru.practicum.android.diploma.common.data.network.RetrofitNetworkClient
import ru.practicum.android.diploma.common.domain.entity.Vacancy

class SearchVacancyDetailsUseCaseImplTest {

    val token = "Bearer ${BuildConfig.API_ACCESS_TOKEN}"

    val retrofit: Retrofit =
        Retrofit.Builder().baseUrl("https://practicum-diploma-8bc38133faba.herokuapp.com/")
            .addConverterFactory(
                GsonConverterFactory.create()
            ).build()

    val api = retrofit.create(HeadHunterApi::class.java)

    val networkClient = RetrofitNetworkClient(
        headHunterApi = api,
        token
    )

    @Test
    fun should_get_vacancy() = runBlocking {
        val vacancy = getVacancy()
        val expectedVacancy = Vacancy(
            id = "2f6620c2-78fb-4800-90af-82a66dac78e9",
            name = "Android Developer в Microsoft",
            salaryCurrency = "RUB",
            salaryFrom = 50000,
            salaryTo = 100000,
            logo = "https://upload.wikimedia.org/wikipedia/commons/thumb/4/44/Microsoft_logo.svg/1200px-Microsoft_logo.svg.png",
            area = "Синодское",
            employer = "Microsoft",
            experience = "Нет опыта",
            employment = "Полная занятость",
            schedule = "Полный день",
            description = "Задачи, которые могут стать твоими:\n\nРазработка новой функциональности мобильного приложения под Android, его архитектуры и исправление существующих недостатков;\nНаписание качественного, чистого, читаемого кода, code-review;\nРазработка общих архитектурных решений;\nВзаимодействие с менеджерами, дизайнерами, бекендерами, тестировщиками;\nПроактивно участвовать в жизни продукта: обсуждении требований, планировании проектов, проектировании дизайна, прототипов, спецификаций;\nДелиться технической экспертизой: предлагать, обсуждать и интегрировать новые решения;\nДекомпозировать, оценивать сроки реализации задач и выдерживать их;\nПроектировать клиент-серверное взаимодействие;\nРазбираться в чужом коде и проводить его рефакторинг;\nДоносить свои мысли и отстаивать свою точку зрения перед остальными членами команды;\nНе просто накидывать идеи, а реализовывать и доводить их до конца в общем проекте;\n\nЧто нужно знать:\n\nAndroid SDK, Android Support Libraries\nПаттерны построения мобильного UI/UX, принципы Material Desig\nПаттерны проектирования, ООП, SOLID, понимание функционального реактивного кода, Clean; Architecture\nKotlin\nDagger, Kotlin Coroutines, Kotlin Flow, Compose, MVVM / MVI, Room\nGradle Multi Modules\nЗнание архитектуры OS Android и особенностей его версий 21+\nВладение техническим английским языком на уровне чтения и понимания\n\nБудет плюсом:\n\nОпыт RxJava\nОпыт написания Unit и UI тестов\nОпыт в Backend Driven UI подходе\nРабота с Gradle\nОпыт работы с CI&DI"
        )
        assertEquals(expectedVacancy, vacancy)
    }

    suspend fun getVacancy(): Vacancy? {
        val searchVacancyDetailsUseCaseImpl = SearchVacancyDetailsUseCaseImpl(
            VacancyRepositoryImpl(networkClient = networkClient)
        )
        return searchVacancyDetailsUseCaseImpl.getVacancyById("af7dd6b8-2367-4695-82df-3470717cee2a")
    }

}
