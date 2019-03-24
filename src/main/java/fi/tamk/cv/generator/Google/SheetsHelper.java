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
import com.google.api.client.http.FileContent;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.*;
import fi.tamk.cv.generator.model.*;
import fi.tamk.cv.generator.model.datatypes.*;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
public class SheetsHelper {
    private Logger log = LoggerFactory.getLogger(this.getClass());
    private static final String APPLICATION_NAME = "quickstart-1550136441024";
    private static final String SHEET_ID = "1yTCCewzBoaqy4ALWEj-0bOEkaRMhzftC_9lWt58xIuE";
    private String sheetID;
    private String folderID;
    private static final String FOLDER_NAME = "CV-Generator-data";
    private static final String SPREADSHEET_NAME = "CV-Generator-data-spreadsheet";



    public static Sheets getSheetsService(String token) throws IOException, GeneralSecurityException {
        Credential credential = new GoogleCredential().setAccessToken(token);
        return new Sheets.Builder(GoogleNetHttpTransport.newTrustedTransport(), JacksonFactory.getDefaultInstance(), credential).setApplicationName(APPLICATION_NAME).build();
    }

    public static Drive getDriveService(String token) throws IOException, GeneralSecurityException {
        Credential credential = new GoogleCredential().setAccessToken(token);
        return new Drive.Builder(GoogleNetHttpTransport.newTrustedTransport(), JacksonFactory.getDefaultInstance(), credential).setApplicationName(APPLICATION_NAME).build();
    }

    public List<List<Object>> readFromSheet(String sheetID, String token){
        try{
            Sheets service = getSheetsService(token);
            ValueRange response = service.spreadsheets().values()
                    .get(sheetID, "contact infromation!A1:J2")
                    .execute();
            return response.getValues();
        } catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public UpdateValuesResponse writeSomethingToSheet(String token) throws IOException {
        ValueRange body = new ValueRange()
                .setValues(Arrays.asList(
                        Arrays.asList("Expenses January"),
                        Arrays.asList("books", "30"),
                        Arrays.asList("pens", "10"),
                        Arrays.asList("Expenses February"),
                        Arrays.asList("clothes", "20"),
                        Arrays.asList("shoes", "5")));
        UpdateValuesResponse result = null;
        try {
            result = getSheetsService(token).spreadsheets().values()
                    .update(SHEET_ID, "A1", body)
                    .setValueInputOption("RAW")
                    .execute();
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }
        return result;
    }

    public Object readRange(String accessToken, String sheetID, String range){
        try{
            Sheets service = getSheetsService(accessToken);
            ValueRange response = service.spreadsheets().values()
                    .get(sheetID, range+"!A1:Z1000")
                    .execute();
            return SheetParser.parseObject(response.getValues(), range);
        } catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }


    public User read(String sheetID, String token){
        ArrayList<String> ranges = new ArrayList<>();
        ranges.add("basic"); // id, first,last,birthdate
        ranges.add("contact_info"); // type,value,visible
        ranges.add("address");
        ranges.add("profile_image");
        ranges.add("document_settings");
        ranges.add("bio");//licences
        ranges.add("licences");
        ranges.add("abilities_and_hobbies");
        ranges.add("experience");
        ranges.add("courses_and_education");
        ranges.add("achievements_and_projects");
        ranges.add("titles_and_degrees");
        ranges.add("references");

        try{
            Sheets service = getSheetsService(token);
            Sheets.Spreadsheets.Values.BatchGet request = service.spreadsheets().values().batchGet(sheetID);
            request.setRanges(ranges);
            BatchGetValuesResponse response = request.execute();
            log.debug(response.toString());
            log.debug(response.get("valueRanges").toString());
            //List<List<Object>> rawValues = response.getValues();
            return SheetParser.parseUser(response.getValueRanges());
        } catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }


    public String createNewFolder(String token) throws IOException, GeneralSecurityException {

        Drive service = getDriveService(token);
        File fileMetadata = new File();
        fileMetadata.setName(FOLDER_NAME);
        fileMetadata.setMimeType("application/vnd.google-apps.folder");
        List<File> files = new ArrayList<>();//service.files().list().setQ("name = '" + FOLDER_NAME + "'").execute().getFiles();
        if (!(files.size() > 0)) {
            File file = service.files().create(fileMetadata).setFields("id").execute();
            System.out.println("Folder ID: " + file.getId());
            folderID = file.getId();
        } else {
            JSONArray array = new JSONArray(files.toString());
            JSONObject object = array.getJSONObject(0);
            folderID = object.getString("id");
        }
        return "Folder " + FOLDER_NAME + " Created! Folder id: " + folderID;
    }

    public String createSheet(String token) throws IOException, GeneralSecurityException {
        
        Sheets service = getSheetsService(token);
        Drive driveService = getDriveService(token);
        Spreadsheet spreadsheet = new Spreadsheet().setProperties(new SpreadsheetProperties().setTitle(SPREADSHEET_NAME));

        List<File> files = new ArrayList<>();//driveService.files().list().setQ("name = '" + SPREADSHEET_NAME + "'").execute().getFiles();
        if (!(files.size() > 0)) {
            spreadsheet = service.spreadsheets().create(spreadsheet)
            .setFields("spreadsheetId")
            .execute();
            sheetID = spreadsheet.getSpreadsheetId();
            System.out.println("Spreadsheet ID: " + spreadsheet.getSpreadsheetId());
        } else {
            JSONArray array = new JSONArray(files.toString());
            JSONObject object = array.getJSONObject(0);
            sheetID = object.getString("id");
        }

        return sheetID;
    }

    public String moveSheetToFolder(String token) throws IOException, GeneralSecurityException {
        Drive service = getDriveService(token);
        File file = service.files().get(sheetID)
        .setFields("parents")
        .execute();

        StringBuilder previousParents = new StringBuilder();
        for (String parent : file.getParents()) {
            previousParents.append(parent);
            previousParents.append(',');
        }

        file = service.files().update(sheetID, null)
        .setAddParents(folderID)
        .setRemoveParents(previousParents.toString())
        .setFields("id, parents")
        .execute();

        System.out.println("Moved from location: " + previousParents.toString() + " to location: " + FOLDER_NAME);

        return "Sheet: " + SPREADSHEET_NAME + " was moved from location: " + previousParents.toString() + " to location: " + FOLDER_NAME;
    }

    public User createDefUser(long id){
        User user = new User();
        user.setId(id);
        user.setContact_info(new ArrayList<>());

        Address address = new Address();
        address.setVisible(true);
        user.setAddress(address);

        user.setProfile_image(new ProfileImage());
        user.setBio(new Bio());
        user.setLicences(new Info(0,true));
        user.setAbilities_and_hobbies(new Info(0, true));
        user.setExperience(new Info(0, true));
        user.setCourses_and_education(new Info(0, true));
        user.setAchievements_and_projects(new Info(0,true));
        user.setTitles_and_degrees(new Info(0,true));
        user.setReferences(new Info(0, true));

        return user;
    }

    // should this be done in the create sheet?
    public String makeTabsToSheet(String accessToken, String sheetID){
        List<Request> requests = new ArrayList<>();
        BatchUpdateSpreadsheetRequest requestBody = new BatchUpdateSpreadsheetRequest();
        requests.add(new Request().setAddSheet(new AddSheetRequest().setProperties(new SheetProperties().setTitle("basic"))));
        requests.add(new Request().setAddSheet(new AddSheetRequest().setProperties(new SheetProperties().setTitle("contact_info"))));
        requests.add(new Request().setAddSheet(new AddSheetRequest().setProperties(new SheetProperties().setTitle("address"))));
        requests.add(new Request().setAddSheet(new AddSheetRequest().setProperties(new SheetProperties().setTitle("profile_image"))));
        requests.add(new Request().setAddSheet(new AddSheetRequest().setProperties(new SheetProperties().setTitle("document_settings"))));
        requests.add(new Request().setAddSheet(new AddSheetRequest().setProperties(new SheetProperties().setTitle("bio"))));
        requests.add(new Request().setAddSheet(new AddSheetRequest().setProperties(new SheetProperties().setTitle("licences"))));
        requests.add(new Request().setAddSheet(new AddSheetRequest().setProperties(new SheetProperties().setTitle("abilities_and_hobbies"))));
        requests.add(new Request().setAddSheet(new AddSheetRequest().setProperties(new SheetProperties().setTitle("experience"))));
        requests.add(new Request().setAddSheet(new AddSheetRequest().setProperties(new SheetProperties().setTitle("courses_and_education"))));
        requests.add(new Request().setAddSheet(new AddSheetRequest().setProperties(new SheetProperties().setTitle("achievements_and_projects"))));
        requests.add(new Request().setAddSheet(new AddSheetRequest().setProperties(new SheetProperties().setTitle("titles_and_degrees"))));
        requests.add(new Request().setAddSheet(new AddSheetRequest().setProperties(new SheetProperties().setTitle("references"))));
        requests.add(new Request().setDeleteSheet(new DeleteSheetRequest().setSheetId(0)));
        requestBody.setRequests(requests);

        try {
            BatchUpdateSpreadsheetResponse response = getSheetsService(accessToken).spreadsheets().batchUpdate(sheetID, requestBody).execute();
            log.debug(response.toString());
        } catch (IOException|GeneralSecurityException e) {
            log.error("Something went wrong in the making of the tabs in the sheets: {}", e.getMessage());
            return "Error";
            //e.printStackTrace();
        }

        User user = createDefUser(0);
        writeToSheet(accessToken, sheetID, user);

        return "Ok";
    }

    public void writeToSheet(String accessToken,String sheetID, String range, List<List<Object>> values){
        ValueRange body = new ValueRange().setValues(values);
        log.debug("{}", body);
        UpdateValuesResponse result = null;
        try {
            result = getSheetsService(accessToken).spreadsheets().values()
                    .update(sheetID, range + "!A1", body)
                    .setValueInputOption("RAW")
                    .execute();
        } catch (IOException|GeneralSecurityException e) {
            e.printStackTrace();
        }
        log.debug("Result {}", result);
    }

    public void writeToSheet(String accessToken,String sheetID, User user){
        String firstname = user.getFirstname();
        String lastname = user.getLastname();
        LocalDate birthdate = user.getBirthdate();
        if(firstname == null){
            firstname = "";
        }
        if(lastname == null){
            lastname = "";
        }
        String birthDate;
        if(birthdate != null){
            birthDate = birthdate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        }else {
            birthDate = "";
        }

        List<List<Object>> basicList = Arrays.asList(Arrays.asList(""+user.getId(),firstname,lastname,birthDate));
        writeToSheet(accessToken,sheetID,"basic",basicList);
        if(user.fetchContactInfoAsList() != null){
            writeToSheet(accessToken,sheetID,"contact_info",user.fetchContactInfoAsList());
        }

        if(user.getAddress() != null){
            writeToSheet(accessToken, sheetID, "address",user.getAddress().toListOfLists());
        }

        if(user.getProfile_image() != null){
            writeToSheet(accessToken, sheetID, "profile_image",user.getProfile_image().toListOfLists());
        }

        if(user.getDocument_settings() != null){
            writeToSheet(accessToken, sheetID, "document_settings",user.getDocument_settings().toListOfLists());
        }

        if(user.getBio() != null){
            writeToSheet(accessToken, sheetID, "bio",user.getBio().toListOfLists());
        }

        if(user.getLicences() != null){
            writeToSheet(accessToken, sheetID, "licences",user.getLicences().toListOfLists());
        }
        if(user.getAbilities_and_hobbies() != null){
            writeToSheet(accessToken, sheetID, "abilities_and_hobbies",user.getAbilities_and_hobbies().toListOfLists());
        }

        if(user.getExperience() != null){
            writeToSheet(accessToken, sheetID, "experience",user.getExperience().toListOfLists());
        }

        if(user.getCourses_and_education() != null){
            writeToSheet(accessToken, sheetID, "courses_and_education",user.getCourses_and_education().toListOfLists());
        }

        if(user.getAchievements_and_projects() != null){
            writeToSheet(accessToken, sheetID, "achievements_and_projects",user.getAbilities_and_hobbies().toListOfLists());
        }

        if(user.getTitles_and_degrees() != null){
            writeToSheet(accessToken, sheetID, "titles_and_degrees",user.getTitles_and_degrees().toListOfLists());
        }

        if(user.getReferences() != null){
            writeToSheet(accessToken, sheetID, "references",user.getReferences().toListOfLists());
        }
    }
}
