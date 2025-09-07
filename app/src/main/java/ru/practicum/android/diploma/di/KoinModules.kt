package ru.practicum.android.diploma.di

import androidx.room.Room
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import ru.practicum.android.diploma.BuildConfig
import ru.practicum.android.diploma.ErrorMessageProvider
import ru.practicum.android.diploma.ErrorMessageProviderImpl
import ru.practicum.android.diploma.common.data.db.AppDataBase
import ru.practicum.android.diploma.common.data.db.FavouritesRepositoryImpl
import ru.practicum.android.diploma.common.data.mapper.VacancyMapper
import ru.practicum.android.diploma.common.data.model.NetworkClient
import ru.practicum.android.diploma.common.data.network.HeadHunterApi
import ru.practicum.android.diploma.common.data.network.RetrofitNetworkClient
import ru.practicum.android.diploma.common.domain.db.FavouritesInteractor
import ru.practicum.android.diploma.common.domain.db.FavouritesRepository
import ru.practicum.android.diploma.common.domain.impl.FavouritesInteractorImpl
import ru.practicum.android.diploma.search.data.repository.VacancyRepositoryImpl
import ru.practicum.android.diploma.search.domain.repository.VacancyRepository
import ru.practicum.android.diploma.search.domain.usecase.SearchUseCase
import ru.practicum.android.diploma.search.domain.usecase.SearchUseCaseImpl
import ru.practicum.android.diploma.search.ui.SearchViewModel
import ru.practicum.android.diploma.search.ui.model.VacancyToVacancyUiMapper
import java.util.concurrent.TimeUnit

// Общие зависимости
val appModule = module {

}

// Модуль для работы с Room
val databaseModule = module {
    factory { VacancyMapper }

    single {
        Room.databaseBuilder(androidContext(), AppDataBase::class.java, "database.db")
            .fallbackToDestructiveMigration(false)
            .build()
    }
}

// Модуль для работы с Room
val searchModule = module {
    // OkHttp client
    single {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BASIC
        }
        OkHttpClient.Builder()
            .addInterceptor(logging)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    // Gson
    single {
        GsonBuilder().create()
    }

    // Retrofit
    single {
        Retrofit.Builder()
            .baseUrl("https://practicum-diploma-8bc38133faba.herokuapp.com/")
            .client(get())
            .addConverterFactory(GsonConverterFactory.create(get()))
            .build()
    }

    // API
    single { get<Retrofit>().create(HeadHunterApi::class.java) }

    // NetworkClient
    single<NetworkClient> {
        val token: String = "Bearer ${BuildConfig.API_ACCESS_TOKEN}"
        RetrofitNetworkClient(get(), token)
    }

    // Repository / UseCase / Mapper / ViewModel
    single<VacancyRepository> { VacancyRepositoryImpl(get()) }
    single<SearchUseCase> { SearchUseCaseImpl(get()) }
    factory { VacancyToVacancyUiMapper() }

    single<ErrorMessageProvider> {
        ErrorMessageProviderImpl(androidContext())
    }

    viewModel { SearchViewModel(get(), get(), get()) }
}

val favouritesModule = module {

    single<FavouritesInteractor> {
        FavouritesInteractorImpl(get())
    }

    single<FavouritesRepository> {
        FavouritesRepositoryImpl(get(), get())
    }
}
