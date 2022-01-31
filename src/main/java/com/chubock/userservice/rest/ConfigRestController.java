package com.chubock.userservice.rest;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class ConfigRestController {

    private static final String HOST = "https://test.elegant-designs.net";
    private static final String OAUTH2_URL = HOST + "/user-service/oauth2/authorize";
    private static final String IOS_OAUTH2_REDIRECT_URL = "keys://oauth2/redirect";
    private static final String GOOGLE_OAUTH2_URL = OAUTH2_URL + "/google";
    private static final String FACEBOOK_OAUTH2_URL = OAUTH2_URL + "/facebook";
    private static final String IOS_GOOGLE_OAUTH2_URL = GOOGLE_OAUTH2_URL + "?redirect_uri=" + IOS_OAUTH2_REDIRECT_URL;
    private static final String IOS_FACEBOOK_OAUTH2_URL = FACEBOOK_OAUTH2_URL + "?redirect_uri=" + IOS_OAUTH2_REDIRECT_URL;

    private static final Map<String, String> CONFIG = new HashMap<>();

    static {
        CONFIG.put("IOS_OAUTH2_REDIRECT_URL", IOS_OAUTH2_REDIRECT_URL);
        CONFIG.put("IOS_GOOGLE_OAUTH2_URL", IOS_GOOGLE_OAUTH2_URL);
        CONFIG.put("IOS_FACEBOOK_OAUTH2_URL", IOS_FACEBOOK_OAUTH2_URL);
    }

    @GetMapping("/config")
    public Map<String, String> config() {
        return CONFIG;
    }

}
