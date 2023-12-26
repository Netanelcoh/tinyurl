package com.backend.tinyurl.service;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
public class UrlService {
    private static final int TINY_Length = 6;
    Random random = new Random();
    public String generateTinyCode() {
        //encode 58-base: {a-z} / {l(lower case L}, {A-Z} / {O (capital o), I}, {1-9}
        String charPool = "abcdefghijkmnopqrstuvwxyzABCDEFGHGKLMNPQRSTUVWXYZ123456789";
        StringBuilder result = new StringBuilder();

        for(int i=0; i < TINY_Length; i++)
            result.append(charPool.charAt(random.nextInt(charPool.length())));

        return result.toString();
    }

    public String getShortUrl(String longUrl) {
        HashMap<String, String> urls = new HashMap<>();
        urls.put("www.ynet.co.il", "76gtr4");
        urls.put("www.sport5.co.il", "4rty32");

        for(Map.Entry<String, String> map : urls.entrySet()) {
            if(longUrl.compareTo(map.getKey()) == 0)
                return map.getValue();
        }

        return "";
    }

    public String getLongUrl(String shortUrl) {
        HashMap<String, String> urls = new HashMap<>();
        urls.put("www.ynet.co.il", "76gtr4");
        urls.put("www.sport5.co.il", "4rty32");

        for(Map.Entry<String, String> map : urls.entrySet()) {
            if(shortUrl.compareTo(map.getKey()) == 0)
                return map.getValue();
        }

        return null;

    }

}
