package ru.practicum.android.diploma.common.data.mapper

import org.junit.Assert.*
import org.junit.Test
import ru.practicum.android.diploma.common.data.domain.api.AreaDto

class AreaMapperTest {
    @Test
    fun should_Map_Area_Dto_To_Area() {

        val areaDtoFirstChild = AreaDto(
            areas = null,
            id = "1",
            name = "Ереван",
            parentId = "5"
        )
        val areaDtoSecondChild = AreaDto(
            areas = null,
            id = "2",
            name = "Гюмри",
            parentId = "5"
        )

        val areaDtoParent = AreaDto(
            areas = listOf(areaDtoFirstChild, areaDtoSecondChild),
            id = "5",
            name = "Армения",
            parentId = null
        )

        val area = AreaMapper.mapAreaDtoToArea(areaDtoParent)

        assertEquals(1, area.areas[0].id)
        assertEquals(2, area.areas[1].id)
    }

}
