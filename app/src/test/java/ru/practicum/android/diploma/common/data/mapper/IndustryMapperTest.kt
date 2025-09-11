package ru.practicum.android.diploma.common.data.mapper

import org.junit.Assert.*
import org.junit.Test
import ru.practicum.android.diploma.common.data.domain.api.IndustryDto

class IndustryMapperTest {

    @Test
    fun should_map_IndustryDtp_to_Industry() {
        val industryDto = IndustryDto(
            id = "5",
            name = "Услуги для бизнеса"
        )

        val industry = IndustryMapper.map(industryDto)

        assertEquals(5, industry.id)
        assertEquals("Услуги для бизнеса", industry.name)
    }

}
