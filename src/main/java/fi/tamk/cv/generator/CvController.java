/*
Copyright 2019 Hanna Haataja <hanna.haataja@tuni.fi>, Samu Koivulahti <samu.koivulahti@tuni.fi>. All rights reserved.

Redistribution and use in source and binary forms, with or without modification, are permitted provided that the
following conditions are met:

1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following
disclaimer.

2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following
disclaimer in the documentation and/or other materials provided with the distribution.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES,
INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/
package fi.tamk.cv.generator;

import fi.tamk.cv.generator.Google.GoogleServices;
import fi.tamk.cv.generator.Google.SheetsHelper;
import fi.tamk.cv.generator.model.*;
import fi.tamk.cv.generator.model.datatypes.*;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.time.LocalDate;
import java.util.ArrayList;

@RestController
public class CvController {
    private String accessToken;

    Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    GoogleServices googleServices;

    @Autowired
    private OAuth2AuthorizedClientService authorizedClientService;

    // login in http://localhost:8080/oauth2/authorize/google
    @GetMapping("/loginSuccess")
    public String getLoginInfo(OAuth2AuthenticationToken authentication) {
        OAuth2AuthorizedClient client = authorizedClientService
                .loadAuthorizedClient(authentication.getAuthorizedClientRegistrationId(), authentication.getName());
        log.info("Client: {}, token: {}", authentication.getName(), client.getAccessToken().getTokenValue());
        accessToken = client.getAccessToken().getTokenValue();
        return "loginSuccess";
    }

    @GetMapping("/error")
    public String getLoginError(OAuth2AuthenticationToken authentication) {
        return "Error while authenticating" + authentication.getAuthorizedClientRegistrationId();
    }

    @RequestMapping("/demo")
    public User demo() {
        User demoUser = new User(1, "demo", "person", LocalDate.of(1990, 1, 1));
        demoUser.setContact_info(new ContactInfo("demo.person@example.com","0011225566",true));
        demoUser.setAddress(new Address("something street", "111", "Suomi", "Tampere", true));
        demoUser.setProfile_image(new ProfileImage(
                "https://www.google.com/url?sa=i&source=images&cd=&ved=2ahUKEwi7x9Cf7oThAhUqwMQBHfmJB0kQjRx6BAgBEAU&url=https%3A%2F%2Fen.wiktionary.org%2Fwiki%2Fcat&psig=AOvVaw1uyeUhaOBH7godt4Uaobzd&ust=1552763849780520",
                true));
        demoUser.setBio(new Bio("this user has been created for demo and testing purposes", null,true));
        demoUser.setExperience(new Info(2, true));
        demoUser.getExperience().getData().add(new Experience("work",1, true, LocalDate.now(), LocalDate.now(), "something",
                "something", "something", new int[] { 1,2}));

        return demoUser;
    }

    @GetMapping(value = "/test", produces = "application/json")
    public String getTestJson() throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(new ClassPathResource("test.json").getInputStream()));
        StringBuilder builder = new StringBuilder();
        String line = null;

        while((line = reader.readLine()) != null) {
            builder.append(line);
        }

        reader.close();
        JSONObject object = new JSONObject(builder.toString());

        return object.toString();
    }

    @RequestMapping(value="/demo",method = RequestMethod.POST)
    public User postUser(){
        log.debug("HERE WITH POST!");
        return demo();
    }

    @RequestMapping("/write/demo")
    public String writeDemo() {
        User user = demo();
        googleServices.writeToCV(accessToken, user);
        return "ok";
    }

    @RequestMapping("/create")
    public String createSheetTemplate(){
        try {
            return googleServices.createSheet(accessToken);
        } catch (IOException | GeneralSecurityException e) {
            e.printStackTrace();
            return "error";
        }
    }

    @RequestMapping(value="/append/{range}", method=RequestMethod.POST)
    public String appendDataType(@PathVariable String range, @RequestBody DataType dataType){
        log.debug("Got here: {} and datatype {}", range, dataType.toString());
        return googleServices.appendDataType(accessToken,range, dataType);
    }

}
