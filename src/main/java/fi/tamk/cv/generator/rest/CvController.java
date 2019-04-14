/*
Copyright 2019 Hanna Haataja <hanna.haataja@tuni.fi>, Samu Koivulahti <samu.koivulahti@tuni.fi>,
               Joonas Lauhala <joonas.lauhala@tuni.fi>, Tuukka Juusela <tuukka.juusela@tuni.fi>. All rights reserved.

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
package fi.tamk.cv.generator.rest;

import fi.tamk.cv.generator.Google.GoogleServices;
import fi.tamk.cv.generator.model.*;
import org.apache.pdfbox.io.IOUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;

import java.io.*;
import java.security.GeneralSecurityException;

@RestController
@RequestMapping("api/")
public class CvController extends BaseController {

    Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    GoogleServices googleServices;

    @GetMapping(value = "/pdf", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> getPDF() {
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=curriculum_vitae.pdf");

        String fileName = RequestContextHolder.currentRequestAttributes().getSessionId();
        new CreatePDF(fileName, getAccessToken(), googleServices);
        File file = new File(fileName);

        try (InputStream in = new FileInputStream(file)) {
            byte[] media = IOUtils.toByteArray(in);
            headers.setCacheControl(CacheControl.noCache().getHeaderValue());
            return new ResponseEntity<>(media, headers, HttpStatus.OK);

        } catch (IOException e) {
            return new ResponseEntity<>(headers, HttpStatus.NO_CONTENT);

        } finally {
            if (file != null) file.delete();
        }
    }

    @RequestMapping("/get/user")
    public ResponseEntity<User> getData() {
        log.debug("Getting user");
        String token = getAccessToken();

        if (token != null) {
            User user = googleServices.getData(token);
            if (user != null) {
                return new ResponseEntity<>(user, HttpStatus.OK);
            } else {
                return createSheetTemplate();
            }
        } else {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }

    @GetMapping(value = "/test", produces = MediaType.APPLICATION_JSON_VALUE)
    public String getTestJson() throws IOException {
        log.debug("Getting test");
        BufferedReader reader = new BufferedReader(new InputStreamReader(new ClassPathResource("test.json").getInputStream()));
        StringBuilder builder = new StringBuilder();
        String line = null;

        while ((line = reader.readLine()) != null) {
            builder.append(line);
        }

        reader.close();
        JSONObject object = new JSONObject(builder.toString());

        return object.toString();
    }

    @RequestMapping("/get/create")
    public ResponseEntity<User> createSheetTemplate() {
        try {
            return new ResponseEntity<>(googleServices.createSheet(getAccessToken()), HttpStatus.CREATED);
        } catch (IOException | GeneralSecurityException e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping("/get/search")
    public String getOwnedByMe() {
        return googleServices.getOwnedSheetID(getAccessToken());
    }


    @RequestMapping(value = "/post/{range}", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> postInfoData(@PathVariable String range, @RequestBody Info info) {
        log.debug("Got range post: {}", range);
        if (getAccessToken() != null) {
            googleServices.addInfoData(getAccessToken(), range, info);
            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }

    @RequestMapping(value = "/post/bio", method = RequestMethod.POST)
    public ResponseEntity<?> postBioData(@RequestBody Bio bio) {
        log.debug("Got the bio post: {}", bio.getValue());
        if (getAccessToken() != null) {
            googleServices.addBioData(getAccessToken(), bio);
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }

    @RequestMapping(value = "/post/user", method = RequestMethod.POST)
    public ResponseEntity<?> postBasicData(@RequestBody User user) {
        log.debug("Got the basic post: {} {} ", user.getFirstname(), user.getLastname());
        if (getAccessToken() != null) {
            googleServices.addUserData(getAccessToken(), user);
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }

    @RequestMapping(value = "/post/profile", method = RequestMethod.POST)
    public ResponseEntity<?> postProfilePictureData(@RequestBody ProfileImage image) {
        log.debug("Got the profile image post: {} {} ", image.getSource(), image.isVisible());
        if (getAccessToken() != null) {
            googleServices.addProfileData(getAccessToken(), image);
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }

    @RequestMapping("/get/share/{email}")
    public ResponseEntity<?> share(@PathVariable String email) {
        if(getAccessToken() != null){
            try {
                googleServices.shareFolder(getAccessToken(), email);
                return new ResponseEntity<>(HttpStatus.OK);
            } catch (IOException|GeneralSecurityException e) {
                e.printStackTrace();
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } else {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }

}
