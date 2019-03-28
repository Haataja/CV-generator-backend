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
import fi.tamk.cv.generator.model.*;
import fi.tamk.cv.generator.model.datatypes.*;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.time.LocalDate;


@RestController
@RequestMapping("api/")
@Scope("session")
public class CvController extends BaseController{

    Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    GoogleServices googleServices;


    @GetMapping("/error")
    public String getLoginError(OAuth2AuthenticationToken authentication) {
        return "Error while authenticating";
    }

    @RequestMapping("/demo")
    public User demo() {
        log.debug("Getting demo");
        User demoUser = new User("Tuksu", "Juksu", LocalDate.of(1990, 1, 1));
        demoUser.setContact_info(new ContactInfo("tuksu.juksu@email.com","0101234456",true));
        demoUser.setAddress(new Address("Esimerkkikatu 12", "33500", "Finland", "Tampere", true));
        demoUser.setProfile_image(new ProfileImage(
                "https://upload.wikimedia.org/wikipedia/commons/thumb/3/3a/Cat03.jpg/600px-Cat03.jpg",
                true));
        demoUser.setBio(new Bio("I'm absolutely best human on planet. No one is better than me. The best", "If you want to know more from me send me a email!",false));
        demoUser.setExperience(new Info(1, true));
        demoUser.getExperience().getData().add(new Experience("work",1, true, LocalDate.of(1992,2,21), LocalDate.of(2015,5,28), "Java consultant",
                "Tuksu's Coding palace", "Was a great place to work at", new int[] {1}));
        demoUser.getExperience().getData().add(new Experience("work",2, true, LocalDate.of(2005,2,1), LocalDate.now(), "Kotlin trainee",
                "Cool code Joonas", "Was a fun place to work at", new int[]{}));
        demoUser.getExperience().getData().add(new Experience("work",3, true, LocalDate.of(2001,5,1), LocalDate.of(2002,8,31), "Kotlin developer",
                "Samu's pro codezz", "Was a super nice place to work at", new int[]{2}));
        demoUser.getExperience().getData().add(new Experience("work",4, true, LocalDate.of(2003,5,1), LocalDate.of(2008,8,31), "Kotlin expert",
                "Hannateq", "Was a super nice place to work at", new int[]{3,4}));
        demoUser.getExperience().getData().add(new Experience("personal",5, true, LocalDate.of(2002,5,1), LocalDate.of(2003,8,31), "Leader of sports team in University",
                "Generic sports team", "Worked hard to achieve this dream", new int[]{}));
        demoUser.setEducation(new Info(2,true));
        demoUser.getEducation().getData().add(new Education(1,true,"Tampere University of Applied Sciences","University of Applied Sciences",
                "Bacheleor of Business Information Systems",4,LocalDate.of(2013,8,1),LocalDate.of(2017,12,20)));
        demoUser.getEducation().getData().add(new Course(2,true,"Tampere University","Kotlin basics",4,LocalDate.of(2011,11,1),LocalDate.of(2011,12,22)));
        demoUser.setProjects(new Info(3,true));
        demoUser.getProjects().getData().add(new Project("project",1,true,"Java/json-parser","I worked on a json-parser, which can read and write json data. I worked in a group of ten people. Project was a great success (as Borat would say it) and it reached top 10 most downloaded json-parsers month it was released.",LocalDate.of(2018,11,1)));
        demoUser.getProjects().getData().add(new Project("project",2,true,"Java/shpoping-list-applcation","Shopping list application where user can save their data as a json file. User can also read their shopping list from said json file. User can also save their shopping list to Dropbox and database. Standalone project, worked for 8 hours for ten months.",LocalDate.of(2011,12,12)));
        demoUser.getProjects().getData().add(new Project("project",3,true,"Kotlin/user-login-backend","User login backend for Samu's pro codezz. Worked with two other people on this project. My field of work was mostly releted on validating user's login credentials.",LocalDate.of(2006,1,1)));
        demoUser.getProjects().getData().add(new Project("achievement",1,true,"Award for best Kotlin code in 2015","I was awarded from my hard work with Ktolin",LocalDate.of(2014,5,25)));
        demoUser.setTitles(new Info(4,true));
        demoUser.getTitles().getData().add(new Title("title",1,true,"Vuoden hauis palkinto",LocalDate.of(2009,1,1)));
        demoUser.getTitles().getData().add(new Title("degree",2,true,"Penkkauksen maisterikoulutus",LocalDate.of(2013,1,1)));
        demoUser.setReferences(new Info(5,true));
        demoUser.getReferences().getData().add(new Person(1,true,"Kaisa Haikarainene","kaisa.haikarainen@email.com","0101153456"));
        demoUser.setMisc(new Info(6, true));
        demoUser.getMisc().getData().add(new Misc("language", "Swedish", "Bad"));
        demoUser.getMisc().getData().add(new Misc("language", "Finnish", "Mothers tongue"));
        demoUser.getMisc().getData().add(new Misc("language", "English", "Good"));

        return demoUser;
    }

    @RequestMapping("/get/user")
    public ResponseEntity<User> getData(){
        log.debug("Getting user");
        if (getAccessToken() != null){
            User user = googleServices.getData(getAccessToken());
            if(user != null){
                return new ResponseEntity<>(googleServices.getData(getAccessToken()), HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
        } else {
            return  new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }

    @GetMapping(value = "/test", produces = "application/json")
    public String getTestJson() throws IOException {
        log.debug("Getting test");
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
        googleServices.writeToCV(getAccessToken(), user);
        return "ok";
    }

    @RequestMapping("/get/create")
    public ResponseEntity<User> createSheetTemplate(){
        try {
            return new ResponseEntity<>(googleServices.createSheet(getAccessToken()),HttpStatus.CREATED);
        } catch (IOException | GeneralSecurityException e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping("/get/search")
    public String getOwnedByMe(){
        return googleServices.getOwnedSheetID(getAccessToken());
    }

    @RequestMapping(value="/append/{range}", method=RequestMethod.POST)
    public String appendDataType(@PathVariable String range, @RequestBody DataType dataType){
        log.debug("Got here: {} and datatype {}", range, dataType.toString());
        return googleServices.appendDataType(getAccessToken(),range, dataType);
    }


}
