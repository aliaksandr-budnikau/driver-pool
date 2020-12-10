package org.sda.driverpool.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.sda.driverpool.component.DriverPoolFacade;
import org.sda.driverpool.dto.RecentDriverStatusUpdateDTO;
import org.sda.driverpool.entity.DriverStatus;
import org.sda.driverpool.event.RecentDriverStatusUpdateEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.HashSet;
import java.util.List;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@WebMvcTest(WebController.class)
public class WebControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DriverPoolFacade facade;

    @Test
    public void getAllDrivers_cachingParamPassing() throws Exception {
        mockMvc.perform(get("/api/getAllDrivers")).andDo(print()).andExpect(status().isOk());
        mockMvc.perform(get("/api/getAllDrivers?fromCache=false")).andDo(print()).andExpect(status().isOk());
        mockMvc.perform(get("/api/getAllDrivers?fromCache=true")).andDo(print()).andExpect(status().isOk());

        verify(facade, times(2)).getAllDrivers(eq(false));
        verify(facade, times(1)).getAllDrivers(eq(true));
    }

    @Test
    public void getAllDrivers_returnedEmptySet() throws Exception {
        when(facade.getAllDrivers(false)).thenReturn(new HashSet<>());

        MvcResult mvcResult = mockMvc.perform(get("/api/getAllDrivers")).andReturn();

        String contentAsString = mvcResult.getResponse().getContentAsString();

        List<RecentDriverStatusUpdateDTO> drivers = new ObjectMapper().readValue(contentAsString, new TypeReference<List<RecentDriverStatusUpdateDTO>>() {
        });
        assertNotNull(drivers);
        assertTrue(drivers.isEmpty());
    }

    @Test
    public void getAllDrivers_returnedAFewDrivers() throws Exception {
        RecentDriverStatusUpdateDTO dto1 = new RecentDriverStatusUpdateDTO();
        dto1.setDriverId("d1");
        dto1.setStatus("s1");
        dto1.setLongitude(1);
        dto1.setLatitude(1);
        dto1.setEventId("e1");

        RecentDriverStatusUpdateDTO dto2 = new RecentDriverStatusUpdateDTO();
        dto2.setDriverId("d2");
        dto2.setStatus("s2");
        dto2.setLongitude(2);
        dto2.setLatitude(2);
        dto2.setEventId("e2");

        when(facade.getAllDrivers(false)).thenReturn(new HashSet<>(asList(dto1, dto2)));

        MvcResult mvcResult = mockMvc.perform(get("/api/getAllDrivers")).andReturn();

        String contentAsString = mvcResult.getResponse().getContentAsString();

        List<RecentDriverStatusUpdateDTO> drivers = new ObjectMapper().readValue(contentAsString, new TypeReference<>() {
        });
        assertNotNull(drivers);
        assertEquals(dto1, drivers.get(0));
        assertEquals(dto2, drivers.get(1));
    }

    @Test
    public void handleRecentDriverStatusUpdateEvent() throws Exception {
        RecentDriverStatusUpdateEvent event = new RecentDriverStatusUpdateEvent("driver", 12f, 32f, DriverStatus.ON_RIDE);
        String body = new ObjectMapper().writeValueAsString(event);
        mockMvc.perform(post("/api/updateDriverRecentStatus").content(body).contentType(MediaType.APPLICATION_JSON))
                .andDo(print()).andExpect(status().isOk());

        verify(facade).handle(any(RecentDriverStatusUpdateEvent.class));
    }
}
