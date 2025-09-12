package ru.practicum.android.diploma.common.data.model

import ru.practicum.android.diploma.Resource
import ru.practicum.android.diploma.common.domain.IndustryRepository
import ru.practicum.android.diploma.common.domain.entity.Industry

class GetIndustriesUseCaseImpl(private val industryRepository: IndustryRepository) : GetIndustriesUseCase {
    override suspend fun execute(): Resource<List<Industry>> {
        return industryRepository.getIndustries()
    }

}
