package org.lostnomore.backend.item.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.lostnomore.backend.common.ControllerTest;
import org.lostnomore.backend.item.dto.response.LostItemsSearchDto;
import org.lostnomore.backend.item.service.LostItemService;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(LostItemController.class)
class LostItemControllerTest extends ControllerTest {

    @InjectMocks
    private LostItemController lostItemController;

    @MockitoBean
    private LostItemService lostItemService;

    @Test
    @DisplayName("분실물 검색 API 호출 시 200 응답을 반환한다.")
    void testSearchLostItems() throws Exception {
        // given
        LocalDate localDate = LocalDate.of(2024, 2, 1);
        LocalDate endDate = LocalDate.of(2024, 2, 10);

        LostItemsSearchDto mockResponse = new LostItemsSearchDto(List.of(
                new LostItemsSearchDto.LostItemSearchDto(1L, 37.57, 126.98),
                new LostItemsSearchDto.LostItemSearchDto(2L, 37.55, 127.00)
        ));

        when(lostItemService.searchLostItems(any(), any(), any(), any(), any(), any(), any(), any(), any()))
                .thenReturn(mockResponse);

        // when & then
        mockMvc.perform(get("/items/search/map")
                        .param("date_start", "2024-02-01")
                        .param("date_end", "2024-02-10")
                        .param("top_left_lat", "37.57")
                        .param("top_left_lon", "126.98")
                        .param("bottom_right_lat", "37.55")
                        .param("bottom_right_lon", "127.00"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").value(true))
                .andExpect(jsonPath("$.data.lostItems[0].id").value(1L))
                .andExpect(jsonPath("$.data.lostItems[0].latitude").value(37.57))
                .andExpect(jsonPath("$.data.lostItems[0].longitude").value(126.98))
                .andExpect(jsonPath("$.data.lostItems[1].id").value(2L))
                .andExpect(jsonPath("$.data.lostItems[1].latitude").value(37.55))
                .andExpect(jsonPath("$.data.lostItems[1].longitude").value(127.00));
    }




}