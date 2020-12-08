package org.sda.driverpool.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sda.driverpool.component.DriverPoolFacade;
import org.sda.driverpool.dto.RecentDriverStatusUpdateDTO;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Set;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Slf4j
@Controller
@AllArgsConstructor
@RequestMapping(value = "/api", produces = APPLICATION_JSON_VALUE)
public class WebController {
    private final DriverPoolFacade facade;

    @ResponseBody
    @GetMapping(path = "/getAllDrivers")
    public Set<RecentDriverStatusUpdateDTO> getAllDrivers(
            @RequestParam(defaultValue = "false") boolean fromCache) {
        return facade.getAllDrivers(fromCache);
    }
}
