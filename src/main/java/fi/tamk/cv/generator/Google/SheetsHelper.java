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
            log.info(response.toString());
            log.info(response.get("valueRanges").toString());
            //List<List<Object>> rawValues = response.getValues();
            return parseUser(response.getValueRanges());
        } catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    private User parseUser(List<ValueRange> list){
        User user = new User();
        for(ValueRange vr : list){
            String sheet = vr.getRange().split("!")[0];
            List<List<Object>> values = vr.getValues();
            switch (sheet){
                case "basic":
                    user.setId(Long.parseLong((String) values.get(0).get(0)));
                    if(values.get(0).size() > 1){
                        user.setFirstname((String) values.get(0).get(1));
                        if(values.get(0).size() > 2){
                            user.setLastname((String) values.get(0).get(2));
                            if(values.get(0).size() > 3){
                                user.setBirthdate(parseLocalDate((String) values.get(0).get(3)));
                            }
                        }
                    }
                    break;
                case "contact_info":
                    if(values != null){
                        for(List<Object> contact:values){
                            user.getContact_info().add(new ContactInfo((String)contact.get(0),(String) contact.get(1),Boolean.parseBoolean((String) contact.get(2))));
                        }
                    }
                    break;
                case "address":
                    user.setAddress(new Address((String) values.get(0).get(0),(String) values.get(0).get(1),
                            (String) values.get(0).get(2),(String) values.get(0).get(3),
                            Boolean.parseBoolean((String) values.get(0).get(4))));
                    break;
                case "profile_image":
                    user.setProfile_image(new ProfileImage((String) values.get(0).get(0),
                            Boolean.parseBoolean((String) values.get(0).get(1))));
                    break;
                case "document_settings":
                    if(values.get(0).size() == 3){
                        user.setDocument_settings(new DocumentSettings((String) values.get(0).get(0),(String) values.get(0).get(1),
                                (String) values.get(0).get(2)));
                    } else if(values.get(0).size() == 2){
                        user.setDocument_settings(new DocumentSettings((String) values.get(0).get(0),(String) values.get(0).get(1),
                                null));
                    } else {
                        user.setDocument_settings(new DocumentSettings((String) values.get(0).get(0),null, null));
                    }
                    break;
                case "bio":
                    user.setBio(new Bio((String) values.get(0).get(0),
                            Boolean.parseBoolean((String) values.get(0).get(1))));
                    break;
                case "licences":
                    List<Object> LicenceInfos = values.get(0);
                    Info LicenceInfo = new Info(Integer.parseInt((String) LicenceInfos.get(0)), Boolean.parseBoolean((String) LicenceInfos.get(1)));
                    ArrayList<DataType> LicenceData = new ArrayList<>();
                    for(int i = 1; i < values.size(); i++){
                        LicenceData.add(new Licence((String) values.get(i).get(0),Long.parseLong((String) values.get(i).get(1)),
                                Boolean.parseBoolean((String) values.get(i).get(2)),(String) values.get(i).get(3),
                                (String) values.get(i).get(4)));
                    }
                    LicenceInfo.setData(LicenceData);
                    user.setLicences(LicenceInfo);
                    break;
                case "abilities_and_hobbies":
                    List<Object> AbilityInfos = values.get(0);
                    Info abilityInfo = new Info(Integer.parseInt((String) AbilityInfos.get(0)),
                            Boolean.parseBoolean((String) AbilityInfos.get(1)));
                    ArrayList<DataType> AbilityData = new ArrayList<>();
                    for(int i = 1; i < values.size(); i++){
                        DataType ability;
                        if(values.get(i).get(0).equals("hobby")){
                            ability = new Hobby(Long.parseLong((String) values.get(i).get(1)),
                                    Boolean.parseBoolean((String) values.get(i).get(2)),
                                    (String) values.get(i).get(3),(String) values.get(i).get(4),
                                    parseLocalDate((String) values.get(i).get(5)),parseLocalDate((String) values.get(i).get(6)));
                        } else {
                            ability = new Ability((String) values.get(i).get(0),Long.parseLong((String) values.get(i).get(1)),
                                    Boolean.parseBoolean((String) values.get(i).get(2)),(String) values.get(i).get(3),
                                    (String) values.get(i).get(4),Integer.parseInt((String) values.get(i).get(5)));
                        }

                        AbilityData.add(ability);
                    }
                    abilityInfo.setData(AbilityData);
                    user.setAbilities_and_hobbies(abilityInfo);
                    break;
                case "experience":
                    List<Object> ExpInfos = values.get(0);
                    Info expInfo = new Info(Integer.parseInt((String) ExpInfos.get(0)),
                            Boolean.parseBoolean((String) ExpInfos.get(1)));
                    ArrayList<DataType> expData = new ArrayList<>();
                    for(int i = 1; i < values.size(); i++){
                        DataType experience;
                        if(values.get(i).get(0).equals("work")){
                            experience = new ExperienceWork(Long.parseLong((String) values.get(i).get(1)),
                                    Boolean.parseBoolean((String) values.get(i).get(2)),parseLocalDate((String) values.get(i).get(3)),
                                    parseLocalDate((String) values.get(i).get(4)),(String) values.get(i).get(5),(String) values.get(i).get(6),
                                    (String) values.get(i).get(7));
                            setAchievementsAndResp(experience, values.get(i));

                        } else {
                            experience = new Experience(Long.parseLong((String) values.get(i).get(1)),
                                    Boolean.parseBoolean((String) values.get(i).get(2)),parseLocalDate((String) values.get(i).get(3)),
                                    parseLocalDate((String) values.get(i).get(4)),(String) values.get(i).get(5),(String) values.get(i).get(6),
                                    (String) values.get(i).get(7));
                            int size = values.get(i).size() - 8;
                            String[] achievements = new String[size];
                            for(int j = 0; j < size; j++){
                                achievements[j] = (String) values.get(i).get(j+8);
                            }
                            ((Experience) experience).setAchievements(achievements);
                        }

                        expData.add(experience);
                    }
                    expInfo.setData(expData);
                    user.setExperience(expInfo);
                    break;
                case "courses_and_education":
                    List<Object> courseInfos = values.get(0);
                    Info courseInfo = new Info(Integer.parseInt((String) courseInfos.get(0)),
                            Boolean.parseBoolean((String) courseInfos.get(1)));
                    ArrayList<DataType> courseData = new ArrayList<>();
                    for(int i = 1; i < values.size(); i++){
                        DataType course;
                        if(values.get(i).get(0).equals("course")){
                            course = new Course(Long.parseLong((String) values.get(i).get(1)),
                                    Boolean.parseBoolean((String) values.get(i).get(2)),
                                    (String) values.get(i).get(3),(String) values.get(i).get(4), Integer.parseInt((String) values.get(i).get(5)),
                                    parseLocalDate((String) values.get(i).get(6)),parseLocalDate((String) values.get(i).get(7)));
                        } else {
                            course = new Education(Long.parseLong((String) values.get(i).get(1)),
                                    Boolean.parseBoolean((String) values.get(i).get(2)),
                                    (String) values.get(i).get(3),(String) values.get(i).get(4),(String) values.get(i).get(5)
                                    , Integer.parseInt((String) values.get(i).get(6)),
                                    parseLocalDate((String) values.get(i).get(7)),parseLocalDate((String) values.get(i).get(8)));
                        }

                        courseData.add(course);
                    }
                    courseInfo.setData(courseData);
                    user.setCourses_and_education(courseInfo);
                    break;
                case "achievements_and_projects":
                    List<Object> projectInfos = values.get(0);
                    Info projectInfo = new Info(Integer.parseInt((String) projectInfos.get(0)),
                            Boolean.parseBoolean((String) projectInfos.get(1)));
                    ArrayList<DataType> projectData = new ArrayList<>();
                    for(int i = 1; i < values.size(); i++){
                        DataType project;
                        if(values.get(i).get(0).equals("achievement")){
                            project = new Achievement(Long.parseLong((String) values.get(i).get(1)),
                                    Boolean.parseBoolean((String) values.get(i).get(2)),
                                    (String) values.get(i).get(3),(String) values.get(i).get(4),parseLocalDate((String) values.get(i).get(5)));
                        } else {
                            project = new Project((String) values.get(i).get(0),Long.parseLong((String) values.get(i).get(1)),
                                    Boolean.parseBoolean((String) values.get(i).get(2)),
                                    (String) values.get(i).get(3),(String) values.get(i).get(4),parseLocalDate((String) values.get(i).get(5)));
                        }

                        projectData.add(project);
                    }
                    projectInfo.setData(projectData);
                    user.setCourses_and_education(projectInfo);
                    break;
                case "titles_and_degrees":
                    List<Object> titleInfos = values.get(0);
                    Info titleInfo = new Info(Integer.parseInt((String) titleInfos.get(0)),
                            Boolean.parseBoolean((String) titleInfos.get(1)));
                    ArrayList<DataType> titleData = new ArrayList<>();
                    for(int i = 1; i < values.size(); i++){
                        titleData.add(new Title((String) values.get(i).get(0),Long.parseLong((String) values.get(i).get(1)),
                                Boolean.parseBoolean((String) values.get(i).get(2)),(String) values.get(i).get(3),
                                parseLocalDate((String) values.get(i).get(4))));
                    }
                    titleInfo.setData(titleData);
                    user.setTitles_and_degrees(titleInfo);
                    break;
                case "references":
                    List<Object> referenceInfos = values.get(0);
                    Info referenceInfo = new Info(Integer.parseInt((String) referenceInfos.get(0)),
                            Boolean.parseBoolean((String) referenceInfos.get(1)));
                    ArrayList<DataType> referenceData = new ArrayList<>();
                    for(int i = 1; i < values.size(); i++){
                        referenceData.add(new Person(Long.parseLong((String) values.get(i).get(1)),
                                Boolean.parseBoolean((String) values.get(i).get(2)),(String) values.get(i).get(3),
                                (String) values.get(i).get(4),(String) values.get(i).get(5)));
                    }
                    referenceInfo.setData(referenceData);
                    user.setReferences(referenceInfo);
                    break;
            }


        }
        return user;
    }

    private void setAchievementsAndResp(DataType experience, List<Object> objects){
        boolean inResp = true;
        ArrayList<String> responsibilities = new ArrayList<>();
        ArrayList<String> achievements = new ArrayList<>();
        for(int i = 0; i < objects.size() - 8; i++){
            if(inResp){
                if(((String)objects.get(i + 8)).trim().equals("achievements")){
                    inResp = false;
                } else {
                    responsibilities.add((String) objects.get(i + 8));
                }
            } else {
                achievements.add((String) objects.get(i + 8));
            }
        }
        ((ExperienceWork) experience).setResponsibilities(responsibilities.toArray(new String[0]));
        ((ExperienceWork) experience).setAchievements(achievements.toArray(new String[0]));
    }

    private LocalDate parseLocalDate(String string){
        String[] splitDate = string.split("/");
        return LocalDate.of(Integer.parseInt(splitDate[2]),Integer.parseInt(splitDate[1]),Integer.parseInt(splitDate[0]));
    }

    public String createNewFolder(String token) throws IOException, GeneralSecurityException {

        Drive service = getDriveService(token);
        File fileMetadata = new File();
        fileMetadata.setName(FOLDER_NAME);
        fileMetadata.setMimeType("application/vnd.google-apps.folder");
        List<File> files = service.files().list().setQ("name = '" + FOLDER_NAME + "'").execute().getFiles();
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

        List<File> files = driveService.files().list().setQ("name = '" + SPREADSHEET_NAME + "'").execute().getFiles();
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

        return "Ok";
    }
}
