package kz.berrydrive.auth.controller;

import kz.berrydrive.common.constant.RestEndpointPrefixes;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(RestEndpointPrefixes.API + "/test")
public class TestController {

    @GetMapping
    public String test() {
        return "test";
    }
}
