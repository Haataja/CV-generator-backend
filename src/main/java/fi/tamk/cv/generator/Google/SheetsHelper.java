/*
Copyright 2019 Hanna Haataja <hanna.haataja@tuni.fi>. All rights reserved.

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
package fi.tamk.cv.generator.Google;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.Value;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.*;
import fi.tamk.cv.generator.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Component
public class SheetsHelper {
    private Logger log = LoggerFactory.getLogger(this.getClass());

    @Value("${cv.generator.google.project.id}")
    private static String APPLICATION_NAME;
    private static final String SPREADSHEET_NAME = "CV-Generator-data-spreadsheet";


    public static Sheets getSheetsService(String token) throws IOException, GeneralSecurityException {
        Credential credential = new GoogleCredential().setAccessToken(token);
        return new Sheets.Builder(GoogleNetHttpTransport.newTrustedTransport(), JacksonFactory.getDefaultInstance(), credential).setApplicationName(APPLICATION_NAME).build();
    }

    public Object readRange(String accessToken, String sheetID, String range) {
        try {
            Sheets service = getSheetsService(accessToken);
            ValueRange response = service.spreadsheets().values()
                    .get(sheetID, range + "!A1:Z1000")
                    .execute();
            return SheetParser.parseObject(response.getValues(), range);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    public User read(String sheetID, String token) {
        ArrayList<String> ranges = new ArrayList<>();
        ranges.add("basic"); // id, first,last,birthdate
        ranges.add("contact_info"); // type,value,visible
        ranges.add("address");
        ranges.add("profile_image");
        ranges.add("bio");
        ranges.add("misc");
        ranges.add("experience");
        ranges.add("education");
        ranges.add("projects");
        ranges.add("titles");
        ranges.add("references");

        try {
            Sheets service = getSheetsService(token);
            Sheets.Spreadsheets.Values.BatchGet request = service.spreadsheets().values().batchGet(sheetID);
            request.setRanges(ranges);
            BatchGetValuesResponse response = request.execute();
            log.debug(response.toString());
            log.debug(response.get("valueRanges").toString());
            //List<List<Object>> rawValues = response.getValues();
            return SheetParser.parseUser(response.getValueRanges());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String createSheet(String token) throws IOException, GeneralSecurityException {
        String sheetID;
        Sheets service = getSheetsService(token);
        Spreadsheet spreadsheet = new Spreadsheet().setProperties(new SpreadsheetProperties().setTitle(SPREADSHEET_NAME));
        spreadsheet = service.spreadsheets().create(spreadsheet)
                .setFields("spreadsheetId")
                .execute();
        sheetID = spreadsheet.getSpreadsheetId();
        log.debug("Spreadsheet ID: " + spreadsheet.getSpreadsheetId());

        makeTabsToSheet(token, sheetID);

        return sheetID;
    }

    public User createDefUser() {
        User user = new User();

        Address address = new Address();
        address.setVisible(true);
        user.setAddress(address);

        user.setProfile_image(new ProfileImage());
        user.setBio(new Bio());
        user.setMisc(new Info(0, true));
        user.setExperience(new Info(0, true));
        user.setEducation(new Info(0, true));
        user.setProjects(new Info(0, true));
        user.setTitles(new Info(0, true));
        user.setReferences(new Info(0, true));

        return user;
    }

    // should this be done in the create sheet?
    public String makeTabsToSheet(String accessToken, String sheetID) {
        List<Request> requests = new ArrayList<>();
        BatchUpdateSpreadsheetRequest requestBody = new BatchUpdateSpreadsheetRequest();
        requests.add(new Request().setAddSheet(new AddSheetRequest().setProperties(new SheetProperties().setTitle("basic"))));
        requests.add(new Request().setAddSheet(new AddSheetRequest().setProperties(new SheetProperties().setTitle("contact_info"))));
        requests.add(new Request().setAddSheet(new AddSheetRequest().setProperties(new SheetProperties().setTitle("address"))));
        requests.add(new Request().setAddSheet(new AddSheetRequest().setProperties(new SheetProperties().setTitle("profile_image"))));
        requests.add(new Request().setAddSheet(new AddSheetRequest().setProperties(new SheetProperties().setTitle("bio"))));
        requests.add(new Request().setAddSheet(new AddSheetRequest().setProperties(new SheetProperties().setTitle("misc"))));
        requests.add(new Request().setAddSheet(new AddSheetRequest().setProperties(new SheetProperties().setTitle("experience"))));
        requests.add(new Request().setAddSheet(new AddSheetRequest().setProperties(new SheetProperties().setTitle("education"))));
        requests.add(new Request().setAddSheet(new AddSheetRequest().setProperties(new SheetProperties().setTitle("projects"))));
        requests.add(new Request().setAddSheet(new AddSheetRequest().setProperties(new SheetProperties().setTitle("titles"))));
        requests.add(new Request().setAddSheet(new AddSheetRequest().setProperties(new SheetProperties().setTitle("references"))));
        requests.add(new Request().setDeleteSheet(new DeleteSheetRequest().setSheetId(0)));
        requestBody.setRequests(requests);

        try {
            BatchUpdateSpreadsheetResponse response = getSheetsService(accessToken).spreadsheets().batchUpdate(sheetID, requestBody).execute();
            log.debug(response.toString());
        } catch (IOException | GeneralSecurityException e) {
            log.warn("Something went wrong in the making of the tabs in the sheets: {}", e.getMessage());
            return "Error";
            //e.printStackTrace();
        }

        return "Ok";
    }

    public void writeToSheet(String accessToken, String sheetID, String range, List<List<Object>> values) {
        ValueRange body = new ValueRange().setValues(values);
        log.debug("{}", body);
        UpdateValuesResponse result = null;
        try {
            result = getSheetsService(accessToken).spreadsheets().values()
                    .update(sheetID, range + "!A1", body)
                    .setValueInputOption("RAW")
                    .execute();
        } catch (IOException | GeneralSecurityException e) {
            log.warn("Error while writing to sheet: sheetID {} and range {}", sheetID, range);
            e.printStackTrace();
        }
        //log.debug("Result {}", result);
    }

    public void writeToSheet(String accessToken, String sheetID, User user) {
        String firstname = user.getFirstname();
        String lastname = user.getLastname();
        LocalDate birthdate = user.getBirthdate();
        if (firstname == null) {
            firstname = "";
        }
        if (lastname == null) {
            lastname = "";
        }
        String birthDate;
        if (birthdate != null) {
            birthDate = birthdate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        } else {
            birthDate = "";
        }

        List<List<Object>> basicList = Arrays.asList(Arrays.asList(firstname, lastname, birthDate));
        writeToSheet(accessToken, sheetID, "basic", basicList);
        if (user.fetchContactInfoAsList() != null) {
            writeToSheet(accessToken, sheetID, "contact_info", user.fetchContactInfoAsList());
        }

        if (user.getAddress() != null) {
            writeToSheet(accessToken, sheetID, "address", user.getAddress().toListOfLists());
        }

        if (user.getProfile_image() != null) {
            writeToSheet(accessToken, sheetID, "profile_image", user.getProfile_image().toListOfLists());
        }

        if (user.getBio() != null) {
            writeToSheet(accessToken, sheetID, "bio", user.getBio().toListOfLists());
        }

        if (user.getMisc() != null) {
            writeToSheet(accessToken, sheetID, "misc", user.getMisc().toListOfLists());
        }

        if (user.getExperience() != null) {
            writeToSheet(accessToken, sheetID, "experience", user.getExperience().toListOfLists());
        }

        if (user.getEducation() != null) {
            writeToSheet(accessToken, sheetID, "education", user.getEducation().toListOfLists());
        }

        if (user.getProjects() != null) {
            writeToSheet(accessToken, sheetID, "projects", user.getProjects().toListOfLists());
        }

        if (user.getTitles() != null) {
            writeToSheet(accessToken, sheetID, "titles", user.getTitles().toListOfLists());
        }

        if (user.getReferences() != null) {
            writeToSheet(accessToken, sheetID, "references", user.getReferences().toListOfLists());
        }
    }

    public void clearSheet(String accessToken, String sheetID, String range) {
        BatchClearValuesResponse result = null;
        BatchClearValuesRequest clearValuesRequest = new BatchClearValuesRequest();
        clearValuesRequest.setRanges(Collections.singletonList(range + "!A1:Z100"));
        try {
            result = getSheetsService(accessToken).spreadsheets().values()
                    .batchClear(sheetID, clearValuesRequest)
                    .execute();
        } catch (IOException | GeneralSecurityException e) {
            log.warn("Error while clearing the sheet: range {}", range);
            //e.printStackTrace();
        }
    }
}
