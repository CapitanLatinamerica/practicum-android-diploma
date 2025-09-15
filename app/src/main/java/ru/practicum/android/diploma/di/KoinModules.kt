package ru.practicum.android.diploma.di

import android.content.Context
import android.content.SharedPreferences
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
import ru.practicum.android.diploma.common.data.IndustryRepositoryImpl
import ru.practicum.android.diploma.common.data.VacancyRepositoryImpl
import ru.practicum.android.diploma.common.data.db.AppDataBase
import ru.practicum.android.diploma.common.data.mapper.AreaMapper
import ru.practicum.android.diploma.common.data.mapper.IndustryMapper
import ru.practicum.android.diploma.common.data.mapper.VacancyMapper
import ru.practicum.android.diploma.common.data.model.GetIndustriesUseCase
import ru.practicum.android.diploma.common.data.model.GetIndustriesUseCaseImpl
import ru.practicum.android.diploma.common.data.model.NetworkClient
import ru.practicum.android.diploma.common.data.network.HeadHunterApi
import ru.practicum.android.diploma.common.data.network.RetrofitNetworkClient
import ru.practicum.android.diploma.common.domain.IndustryRepository
import ru.practicum.android.diploma.common.domain.VacancyRepository
import ru.practicum.android.diploma.favourites.data.FavouritesRepositoryImpl
import ru.practicum.android.diploma.favourites.domain.db.FavouritesInteractor
import ru.practicum.android.diploma.favourites.domain.db.FavouritesRepository
import ru.practicum.android.diploma.favourites.domain.impl.FavouritesInteractorImpl
import ru.practicum.android.diploma.favourites.ui.FavouritesViewModel
import ru.practicum.android.diploma.filtercountry.data.CountryRepositoryImpl
import ru.practicum.android.diploma.filtercountry.domain.CountryInteractor
import ru.practicum.android.diploma.filtercountry.domain.CountryRepository
import ru.practicum.android.diploma.filtercountry.domain.impl.CountryInteractorImpl
import ru.practicum.android.diploma.filtercountry.ui.CountryViewModel
import ru.practicum.android.diploma.filterindustry.ui.IndustryVIewModel
import ru.practicum.android.diploma.filtersettings.data.FilterStorage
import ru.practicum.android.diploma.filtersettings.data.impl.FilteringRepositoryImpl
import ru.practicum.android.diploma.filtersettings.domain.FilteringRepository
import ru.practicum.android.diploma.filtersettings.domain.FilteringUseCase
import ru.practicum.android.diploma.filtersettings.domain.impl.FilteringUseCaseImpl
import ru.practicum.android.diploma.filtersettings.ui.FilteringViewModel
import ru.practicum.android.diploma.filtersettings.ui.mapper.FilterParametersMapper
import ru.practicum.android.diploma.filterworkplace.ui.WorkplaceViewModel
import ru.practicum.android.diploma.search.domain.usecase.SearchUseCase
import ru.practicum.android.diploma.search.domain.usecase.SearchUseCaseImpl
import ru.practicum.android.diploma.search.domain.usecase.SearchVacancyDetailsUseCase
import ru.practicum.android.diploma.search.domain.usecase.SearchVacancyDetailsUseCaseImpl
import ru.practicum.android.diploma.search.ui.SearchViewModel
import ru.practicum.android.diploma.search.ui.model.VacancyToVacancyUiMapper
import ru.practicum.android.diploma.vacancydetails.data.SharingInteractorImpl
import ru.practicum.android.diploma.vacancydetails.data.SharingRepositoryImpl
import ru.practicum.android.diploma.vacancydetails.domain.SharingInteractor
import ru.practicum.android.diploma.vacancydetails.domain.SharingRepository
import ru.practicum.android.diploma.vacancydetails.ui.VacancyDetailsViewModel
import java.util.concurrent.TimeUnit

private const val NETWORK_TIMEOUT_SECONDS = 30L
private const val NETWORK_CONNECT_TIMEOUT_SECONDS = 10L

// Общие зависимости
// val appModule = module {
//    single<SharedPreferences> { provideSharedPreferences(androidContext()) }
// }

// Модуль для работы с Room
val databaseModule = module {
    factory { VacancyMapper }

    single {
        Room.databaseBuilder(androidContext(), AppDataBase::class.java, "database.db")
            .fallbackToDestructiveMigration(false).build()
    }
}

// Модуль для работы с Room
val searchModule = module {

    // OkHttp client
    single {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BASIC
        }
        OkHttpClient.Builder().retryOnConnectionFailure(true)
            .connectTimeout(NETWORK_CONNECT_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .readTimeout(NETWORK_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .writeTimeout(NETWORK_TIMEOUT_SECONDS, TimeUnit.SECONDS).addInterceptor(logging).build()
    }

    // Gson
    single {
        GsonBuilder().create()
    }

    // Retrofit
    single {
        Retrofit.Builder().baseUrl("https://practicum-diploma-8bc38133faba.herokuapp.com/").client(get())
            .addConverterFactory(GsonConverterFactory.create(get())).build()
    }

    // API
    single { get<Retrofit>().create(HeadHunterApi::class.java) }

    // NetworkClient
    single<NetworkClient> {
        val token = "Bearer ${BuildConfig.API_ACCESS_TOKEN}"
        RetrofitNetworkClient(get(), token, androidContext())
    }

    // Repository / UseCase / Mapper / ViewModel
    single<VacancyRepository> {
        VacancyRepositoryImpl(get(), get(), get())
    }

    single<SearchUseCase> {
        SearchUseCaseImpl(get())
    }

    single<SearchVacancyDetailsUseCase> {
        SearchVacancyDetailsUseCaseImpl(get())
    }

    factory { VacancyToVacancyUiMapper() }

    single<ErrorMessageProvider> {
        ErrorMessageProviderImpl(androidContext())
    }

    viewModel { SearchViewModel(get(), get(), get(), get()) }

    single<FilteringUseCase> {
        FilteringUseCaseImpl(get())
    }

    single<FilteringRepository> {
        FilteringRepositoryImpl(get())
    }
}

// Модуль для деталей вакансии
val vacancyDetailsModule = module {

    single<SharingRepository> { SharingRepositoryImpl(get()) }
    single<SharingInteractor> { SharingInteractorImpl(get()) }

    viewModel { (vacancyId: String) ->
        VacancyDetailsViewModel(get(), get(), get(), vacancyId, get())
    }
}

val favouritesModule = module {

    single<FavouritesInteractor> {
        FavouritesInteractorImpl(get())
    }

    single<FavouritesRepository> {
        FavouritesRepositoryImpl(get(), get())
    }

    viewModel { FavouritesViewModel(get()) }
}

// fun provideSharedPreferences(context: Context): SharedPreferences {
//    return context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE)
// }

// Модуль для экрана "фильтры"
val filteringModule = module {

    single<SharedPreferences> {
        androidContext().getSharedPreferences(
            FilterStorage.FILTER_PARAMETERS_NAME,
            Context.MODE_PRIVATE
        )
    }

    single { FilterStorage(get(), get()) }

    single<FilteringRepository> {
        FilteringRepositoryImpl(get())
    }

    single<FilteringUseCase> {
        FilteringUseCaseImpl(get())
    }

    factory<FilterParametersMapper> { FilterParametersMapper() }

    viewModel { FilteringViewModel(get(), get()) }
}

val industryModule = module {

    factory { IndustryMapper }

    single<IndustryRepository> {
        IndustryRepositoryImpl(
            networkClient = get(),
            mapper = get()
        )
    }

    single<GetIndustriesUseCase> {
        GetIndustriesUseCaseImpl(get())
    }

    viewModel {
        IndustryVIewModel(get())
    }
}

val workplaceModule = module {
    viewModel { WorkplaceViewModel() }

    viewModel { CountryViewModel(get()) }

    factory { AreaMapper }

    single<CountryRepository> {
        CountryRepositoryImpl(
            networkClient = get(),
            mapper = get()
        )
    }

    single<CountryInteractor> {
        CountryInteractorImpl(get())
    }
}
