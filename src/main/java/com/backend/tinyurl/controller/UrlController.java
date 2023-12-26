package com.backend.tinyurl.controller;

import com.backend.tinyurl.model.NewTinyRequest;
import com.backend.tinyurl.service.RedisService;
import com.backend.tinyurl.service.UrlService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;


@RestController
public class UrlController {
    private final int MAX_RETRIES = 3;
    @Autowired
    UrlService urlService;
    @Autowired
    RedisService redisService;

    @Autowired
    ObjectMapper om;

    @Value("${base.url}")
    String baseUrl;

    @RequestMapping(value = "/tiny", method = RequestMethod.POST)
    public String generate(@RequestBody NewTinyRequest request) throws JsonProcessingException {
        String tinyCode = urlService.generateTinyCode();
        int i=0;

        while (!redisService.saveToRedis(tinyCode, om.writeValueAsString(request)) && i < MAX_RETRIES) {
            tinyCode = urlService.generateTinyCode();
            i++;
       }
        if (i == MAX_RETRIES) throw new RuntimeException("SPACE IS FULL");
        return baseUrl + tinyCode + "/";
    }

    @RequestMapping(value = "/{tiny}/", method = RequestMethod.GET)
    public ModelAndView getTiny(@PathVariable String tiny) throws JsonProcessingException {
        System.out.println("getRequest for tiny: " + tiny);
        Object tinyRequestStr = redisService.getFromRedis(tiny);

        if(tinyRequestStr == null)
            throw new RuntimeException(tiny + " not found");

        NewTinyRequest tinyRequest = om.readValue(tinyRequestStr.toString(),NewTinyRequest.class);
        if (tinyRequest.getLongUrl() != null)
            return new ModelAndView("redirect:" + tinyRequest.getLongUrl());

        throw new RuntimeException(tiny + " not found");
    }

    /*

    @RequestMapping(value = "/api/{tiny}/", method = RequestMethod.GET)
    public ModelAndView getLongUrl(@PathVariable String tiny) throws JsonProcessingException{
        var tinyRequestStr  = redisService.getFromRedis(tiny);
        //NewTinyRequest tinyRequest = om.readValue(tinyRequestStr,NewTinyRequest.class);

        if(tinyRequestStr == null)
            throw new RuntimeException("Tiny is not exist");

        return new ModelAndView("redirect:" + tinyRequestStr);
    }

    @RequestMapping(value = "/api/create/", method = RequestMethod.POST)
    public ResponseEntity<?> createTinyUrl(@RequestBody NewTinyRequest request) throws JsonProcessingException {
        var longUrl = request.getLongUrl();
        //var longUrl = om.writeValueAsString(request);
        var tinyUrl = urlService.generateShortUrl(longUrl);

        redisService.saveToRedis(tinyUrl, longUrl);

        return new ResponseEntity<>(tinyUrl, HttpStatus.OK);


    }
    */
}
