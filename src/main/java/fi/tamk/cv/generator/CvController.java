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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@RestController
public class CvController {
    private String accessToken;

    Logger log = LoggerFactory.getLogger(this.getClass());
    @Autowired
    SheetsHelper sheetsHelper;

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

    @RequestMapping("/read")
    public User read(@RequestParam(name = "id") String sheetID) {
        return sheetsHelper.read(sheetID, accessToken);
    }

    @RequestMapping("/demo")
    public User demo() {
        User demoUser = new User(1, "demo", "person", LocalDate.of(1990, 1, 1));
        demoUser.getContact_info().add(new ContactInfo("email", "demo.person@example.com", true));
        demoUser.getContact_info().add(new ContactInfo("phone", "001122335544", true));
        demoUser.setAddress(new Address("something street", "111", "Suomi", "Tampere", true));
        demoUser.setProfile_image(new ProfileImage(
                "https://www.google.com/url?sa=i&source=images&cd=&ved=2ahUKEwi7x9Cf7oThAhUqwMQBHfmJB0kQjRx6BAgBEAU&url=https%3A%2F%2Fen.wiktionary.org%2Fwiki%2Fcat&psig=AOvVaw1uyeUhaOBH7godt4Uaobzd&ust=1552763849780520",
                true));
        demoUser.setDocument_settings(new DocumentSettings("en", null, null));
        demoUser.setBio(new Bio("this user has been created for demo and testing purposes", true));
        demoUser.setLicences(new Info(1, true, new ArrayList<>()));
        demoUser.getLicences().getData().add(new Licence("drivers_licence", 1, true, "B", "drivers_licence"));
        demoUser.getLicences().getData().add(new Licence("other", 2, true, "something", "other"));
        demoUser.setAbilities_and_hobbies(new Info(2, true));
        demoUser.getAbilities_and_hobbies().getData().add(new Ability("language", 1, true, "Swedish", "barely", 1));
        demoUser.getAbilities_and_hobbies().getData()
                .add(new Hobby(2, true, "Scuba Diving", "Diving with gear", LocalDate.of(2010, 1, 1), LocalDate.now()));
        demoUser.setExperience(new Info(2, true));
        demoUser.getExperience().getData().add(new Experience(1, true, LocalDate.now(), LocalDate.now(), "something",
                "something", "something", new String[] { "" }));
        demoUser.getExperience().getData().add(new ExperienceWork(2, true, LocalDate.now(), LocalDate.now(),
                "something", "something", "something", new String[] { "Having a cat" }, new String[] { "the cat" }));

        return demoUser;
    }

    @RequestMapping("/createFolder")
    public String createFolder() {
        try {
            return sheetsHelper.createNewFolder(accessToken);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (GeneralSecurityException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    @RequestMapping("/createSheet")
    public String createSheet() {
        try {
            return sheetsHelper.createSheet(accessToken);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (GeneralSecurityException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    @RequestMapping("/moveSheetToFolder")
    public String moveSheetToFolder() {
        try {
            return sheetsHelper.moveSheetToFolder(accessToken);
        } catch (IOException | GeneralSecurityException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
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
}
