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

import com.google.api.services.sheets.v4.model.ValueRange;
import fi.tamk.cv.generator.model.*;
import fi.tamk.cv.generator.model.datatypes.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * This class parses the Information got from the Google sheets to the java objects in model package.
 * If countering data mismatch from sheet to rest response check that parse functions are up to date.
 */
public class SheetParser {

    public static User parseUser(List<ValueRange> list) {
        User user = new User();
        for (ValueRange vr : list) {
            String sheet = vr.getRange().split("!")[0];
            List<List<Object>> values = vr.getValues();
            switch (sheet) {
                case "basic":
                    if (values != null && values.get(0).size() > 0) {
                        user.setFirstname((String) values.get(0).get(0));
                        if (values.get(0).size() > 1) {
                            user.setLastname((String) values.get(0).get(1));
                            if (values.get(0).size() > 2) {
                                user.setBirthdate(parseLocalDate((String) values.get(0).get(2)));
                            }
                        }
                    }
                    break;
                case "contact_info":
                    if (values != null) {
                        user.setContact_info(new ContactInfo((String) values.get(0).get(0), (String) values.get(0).get(1), Boolean.parseBoolean((String) values.get(0).get(2))));

                    }
                    break;
                case "address":
                    user.setAddress(new Address((String) values.get(0).get(0), (String) values.get(0).get(1),
                            (String) values.get(0).get(2), (String) values.get(0).get(3),
                            Boolean.parseBoolean((String) values.get(0).get(4))));
                    break;
                case "profile_image":
                    user.setProfile_image(new ProfileImage((String) values.get(0).get(0),
                            Boolean.parseBoolean((String) values.get(0).get(1))));
                    break;
                case "bio":
                    user.setBio(new Bio((String) values.get(0).get(0), (String) values.get(0).get(1),
                            Boolean.parseBoolean((String) values.get(0).get(2))));
                    break;
                case "misc":
                    List<Object> miscInfos = values.get(0);
                    Info miscInfo = new Info(Integer.parseInt((String) miscInfos.get(0)),
                            Boolean.parseBoolean((String) miscInfos.get(1)));
                    ArrayList<DataType> miscData = new ArrayList<>();
                    for (int i = 1; i < values.size(); i++) {
                        Misc misc = new Misc((String) values.get(i).get(0), Long.parseLong((String) values.get(i).get(1)),
                                Boolean.parseBoolean((String) values.get(i).get(2)), (String) values.get(i).get(3),
                                (String) values.get(i).get(4));
                        miscData.add(misc);
                    }
                    miscInfo.setData(miscData);
                    user.setEducation(miscInfo);
                    break;
                case "experience":
                    List<Object> ExpInfos = values.get(0);
                    Info expInfo = new Info(Integer.parseInt((String) ExpInfos.get(0)),
                            Boolean.parseBoolean((String) ExpInfos.get(1)));
                    ArrayList<DataType> expData = new ArrayList<>();
                    parseExpData(values, expData);
                    expInfo.setData(expData);
                    user.setExperience(expInfo);
                    break;
                case "education":
                    List<Object> courseInfos = values.get(0);
                    Info courseInfo = new Info(Integer.parseInt((String) courseInfos.get(0)),
                            Boolean.parseBoolean((String) courseInfos.get(1)));
                    ArrayList<DataType> courseData = new ArrayList<>();
                    parseCourseData(values, courseData);
                    courseInfo.setData(courseData);
                    user.setEducation(courseInfo);
                    break;
                case "projects":
                    List<Object> projectInfos = values.get(0);
                    Info projectInfo = new Info(Integer.parseInt((String) projectInfos.get(0)),
                            Boolean.parseBoolean((String) projectInfos.get(1)));
                    ArrayList<DataType> projectData = new ArrayList<>();
                    parseProjectData(values, projectData);
                    projectInfo.setData(projectData);
                    user.setProjects(projectInfo);
                    break;
                case "titles":
                    List<Object> titleInfos = values.get(0);
                    Info titleInfo = new Info(Integer.parseInt((String) titleInfos.get(0)),
                            Boolean.parseBoolean((String) titleInfos.get(1)));
                    ArrayList<DataType> titleData = new ArrayList<>();
                    for (int i = 1; i < values.size(); i++) {
                        if(values.get(i).size() > 4){
                            titleData.add(new Title((String) values.get(i).get(0), Long.parseLong((String) values.get(i).get(1)),
                                    Boolean.parseBoolean((String) values.get(i).get(2)), (String) values.get(i).get(3),
                                    parseLocalDate((String) values.get(i).get(4))));
                        } else {
                            titleData.add(new Title((String) values.get(i).get(0), Long.parseLong((String) values.get(i).get(1)),
                                    Boolean.parseBoolean((String) values.get(i).get(2)), (String) values.get(i).get(3),
                                    null));
                        }
                    }
                    titleInfo.setData(titleData);
                    user.setTitles(titleInfo);
                    break;
                case "references":
                    List<Object> referenceInfos = values.get(0);
                    Info referenceInfo = new Info(Integer.parseInt((String) referenceInfos.get(0)),
                            Boolean.parseBoolean((String) referenceInfos.get(1)));
                    ArrayList<DataType> referenceData = new ArrayList<>();
                    parseReferenceData(values, referenceData);
                    referenceInfo.setData(referenceData);
                    user.setReferences(referenceInfo);
                    break;
            }
        }
        return user;
    }

    private static void parseExpData(List<List<Object>> values, ArrayList<DataType> expData) {
        for (int i = 1; i < values.size(); i++) {
            Experience experience;
            if(values.get(i).size() > 7){
                experience = new Experience((String) values.get(i).get(0), Long.parseLong((String) values.get(i).get(1)),
                        Boolean.parseBoolean((String) values.get(i).get(2)), parseLocalDate((String) values.get(i).get(3)),
                        parseLocalDate((String) values.get(i).get(4)), (String) values.get(i).get(5), (String) values.get(i).get(6),
                        (String) values.get(i).get(7));
            } else if(values.get(i).size() > 6){
                experience = new Experience((String) values.get(i).get(0), Long.parseLong((String) values.get(i).get(1)),
                        Boolean.parseBoolean((String) values.get(i).get(2)), parseLocalDate((String) values.get(i).get(3)),
                        parseLocalDate((String) values.get(i).get(4)), (String) values.get(i).get(5), (String) values.get(i).get(6),
                        null);
            } else if(values.get(i).size() > 5){
                experience = new Experience((String) values.get(i).get(0), Long.parseLong((String) values.get(i).get(1)),
                        Boolean.parseBoolean((String) values.get(i).get(2)), parseLocalDate((String) values.get(i).get(3)),
                        parseLocalDate((String) values.get(i).get(4)), (String) values.get(i).get(5), null,
                        null);
            } else if(values.get(i).size() > 4){
                experience = new Experience((String) values.get(i).get(0), Long.parseLong((String) values.get(i).get(1)),
                        Boolean.parseBoolean((String) values.get(i).get(2)), parseLocalDate((String) values.get(i).get(3)),
                        parseLocalDate((String) values.get(i).get(4)), null, null,
                        null);
            } else if(values.get(i).size() > 3){
                experience = new Experience((String) values.get(i).get(0), Long.parseLong((String) values.get(i).get(1)),
                        Boolean.parseBoolean((String) values.get(i).get(2)), parseLocalDate((String) values.get(i).get(3)),
                        null, null, null,
                        null);
            } else {
                experience = new Experience((String) values.get(i).get(0), Long.parseLong((String) values.get(i).get(1)),
                        Boolean.parseBoolean((String) values.get(i).get(2)), null,
                        null, null, null,
                        null);
            }
            int size = values.get(i).size() - 8;
            int[] achievements = new int[size];
            for (int j = 0; j < size; j++) {
                achievements[j] = (Integer) values.get(i).get(j + 8);
            }
            experience.setAchievements(achievements);
            expData.add(experience);
        }
    }


    private static void parseCourseData(List<List<Object>> values, ArrayList<DataType> courseData) {
        for (int i = 1; i < values.size(); i++) {
            DataType course;
            if (values.get(i).get(0).equals("course")) {
                if(values.get(i).size() > 7){
                    course = new Course(Long.parseLong((String) values.get(i).get(1)),
                            Boolean.parseBoolean((String) values.get(i).get(2)),
                            (String) values.get(i).get(3), (String) values.get(i).get(4), Integer.parseInt((String) values.get(i).get(5)),
                            parseLocalDate((String) values.get(i).get(6)), parseLocalDate((String) values.get(i).get(7)));
                } else if(values.get(i).size() > 6){
                    course = new Course(Long.parseLong((String) values.get(i).get(1)),
                            Boolean.parseBoolean((String) values.get(i).get(2)),
                            (String) values.get(i).get(3), (String) values.get(i).get(4), Integer.parseInt((String) values.get(i).get(5)),
                            parseLocalDate((String) values.get(i).get(6)), null);
                } else if(values.get(i).size() > 5){
                    course = new Course(Long.parseLong((String) values.get(i).get(1)),
                            Boolean.parseBoolean((String) values.get(i).get(2)),
                            (String) values.get(i).get(3), (String) values.get(i).get(4), Integer.parseInt((String) values.get(i).get(5)),
                            null, null);
                } else if(values.get(i).size() > 4){
                    course = new Course(Long.parseLong((String) values.get(i).get(1)),
                            Boolean.parseBoolean((String) values.get(i).get(2)),
                            (String) values.get(i).get(3), (String) values.get(i).get(4), 0,
                            null, null);
                } else if(values.get(i).size() > 3){
                    course = new Course(Long.parseLong((String) values.get(i).get(1)),
                            Boolean.parseBoolean((String) values.get(i).get(2)),
                            (String) values.get(i).get(3), null, 0,
                            null, null);
                } else {
                    course = new Course(Long.parseLong((String) values.get(i).get(1)),
                            Boolean.parseBoolean((String) values.get(i).get(2)),
                            null , null , 0,
                            null, null);
                }
            } else {
                if(values.get(i).size() > 8){
                    course = new Education(Long.parseLong((String) values.get(i).get(1)),
                            Boolean.parseBoolean((String) values.get(i).get(2)),
                            (String) values.get(i).get(3), (String) values.get(i).get(4), (String) values.get(i).get(5)
                            , Integer.parseInt((String) values.get(i).get(6)),
                            parseLocalDate((String) values.get(i).get(7)), parseLocalDate((String) values.get(i).get(8)));
                } else if (values.get(i).size() > 7){
                    course = new Education(Long.parseLong((String) values.get(i).get(1)),
                            Boolean.parseBoolean((String) values.get(i).get(2)),
                            (String) values.get(i).get(3), (String) values.get(i).get(4), (String) values.get(i).get(5)
                            , Integer.parseInt((String) values.get(i).get(6)),
                            parseLocalDate((String) values.get(i).get(7)), null);
                } else if (values.get(i).size() > 6){
                    course = new Education(Long.parseLong((String) values.get(i).get(1)),
                            Boolean.parseBoolean((String) values.get(i).get(2)),
                            (String) values.get(i).get(3), (String) values.get(i).get(4), (String) values.get(i).get(5)
                            , Integer.parseInt((String) values.get(i).get(6)),
                            null, null);
                } else if (values.get(i).size() > 5){
                    course = new Education(Long.parseLong((String) values.get(i).get(1)),
                            Boolean.parseBoolean((String) values.get(i).get(2)),
                            (String) values.get(i).get(3), (String) values.get(i).get(4), (String) values.get(i).get(5)
                            , 0,
                            null, null);
                } else if (values.get(i).size() > 4){
                    course = new Education(Long.parseLong((String) values.get(i).get(1)),
                            Boolean.parseBoolean((String) values.get(i).get(2)),
                            (String) values.get(i).get(3), (String) values.get(i).get(4), null
                            , 0,
                            null, null);
                } else if (values.get(i).size() > 3){
                    course = new Education(Long.parseLong((String) values.get(i).get(1)),
                            Boolean.parseBoolean((String) values.get(i).get(2)),
                            (String) values.get(i).get(3), null, null
                            , 0,
                            null, null);
                } else {
                    course = new Education(Long.parseLong((String) values.get(i).get(1)),
                            Boolean.parseBoolean((String) values.get(i).get(2)),
                            null, null, null
                            , 0,
                            null, null);
                }
            }
            courseData.add(course);
        }
    }


    private static LocalDate parseLocalDate(String string) {
        String[] splitDate = string.split("/");
        return LocalDate.of(Integer.parseInt(splitDate[2]), Integer.parseInt(splitDate[1]), Integer.parseInt(splitDate[0]));
    }

    public static Object parseObject(List<List<Object>> data, String range) {
        Object object = null;
        switch (range) {
            case "contact_info":
                if (data != null) {
                    object = new ContactInfo((String) data.get(0).get(0), (String) data.get(0).get(1), Boolean.parseBoolean((String) data.get(0).get(2)));

                }
                break;
            case "address":
                object = new Address((String) data.get(0).get(0), (String) data.get(0).get(1),
                        (String) data.get(0).get(2), (String) data.get(0).get(3),
                        Boolean.parseBoolean((String) data.get(0).get(4)));
                break;
            case "profile_image":
                object = new ProfileImage((String) data.get(0).get(0),
                        Boolean.parseBoolean((String) data.get(0).get(1)));
                break;
            case "bio":
                object = new Bio((String) data.get(0).get(0), (String) data.get(0).get(1),
                        Boolean.parseBoolean((String) data.get(0).get(2)));
                break;
            case "misc":
                List<Object> miscInfos = data.get(0);
                object = new Info(Integer.parseInt((String) miscInfos.get(0)),
                        Boolean.parseBoolean((String) miscInfos.get(1)));
                ArrayList<DataType> miscData = new ArrayList<>();
                for (int i = 1; i < data.size(); i++) {
                    Misc misc = new Misc((String) data.get(i).get(0), Long.parseLong((String) data.get(i).get(1)),
                            Boolean.parseBoolean((String) data.get(i).get(2)), (String) data.get(i).get(3),
                            (String) data.get(i).get(4));
                    miscData.add(misc);
                }
                ((Info) object).setData(miscData);
                break;
            case "experience":
                List<Object> ExpInfos = data.get(0);
                object = new Info(Integer.parseInt((String) ExpInfos.get(0)),
                        Boolean.parseBoolean((String) ExpInfos.get(1)));
                ArrayList<DataType> expData = new ArrayList<>();
                parseExpData(data, expData);
                ((Info) object).setData(expData);
                break;
            case "education":
                List<Object> courseInfos = data.get(0);
                object = new Info(Integer.parseInt((String) courseInfos.get(0)),
                        Boolean.parseBoolean((String) courseInfos.get(1)));
                ArrayList<DataType> courseData = new ArrayList<>();
                parseCourseData(data, courseData);
                ((Info) object).setData(courseData);

                break;
            case "projects":
                List<Object> projectInfos = data.get(0);
                object = new Info(Integer.parseInt((String) projectInfos.get(0)),
                        Boolean.parseBoolean((String) projectInfos.get(1)));
                ArrayList<DataType> projectData = new ArrayList<>();
                parseProjectData(data, projectData);
                ((Info) object).setData(projectData);
                break;
            case "titles":
                List<Object> titleInfos = data.get(0);
                object = new Info(Integer.parseInt((String) titleInfos.get(0)),
                        Boolean.parseBoolean((String) titleInfos.get(1)));
                ArrayList<DataType> titleData = new ArrayList<>();
                for (int i = 1; i < data.size(); i++) {
                    if(data.get(i).size() > 4){
                        titleData.add(new Title((String) data.get(i).get(0), Long.parseLong((String) data.get(i).get(1)),
                                Boolean.parseBoolean((String) data.get(i).get(2)), (String) data.get(i).get(3),
                                parseLocalDate((String) data.get(i).get(4))));
                    } else {
                        titleData.add(new Title((String) data.get(i).get(0), Long.parseLong((String) data.get(i).get(1)),
                                Boolean.parseBoolean((String) data.get(i).get(2)), (String) data.get(i).get(3),
                                null));
                    }
                }
                ((Info) object).setData(titleData);
                break;
            case "references":
                List<Object> referenceInfos = data.get(0);
                object = new Info(Integer.parseInt((String) referenceInfos.get(0)),
                        Boolean.parseBoolean((String) referenceInfos.get(1)));
                ArrayList<DataType> referenceData = new ArrayList<>();
                parseReferenceData(data, referenceData);
                ((Info) object).setData(referenceData);
                break;
        }
        return object;
    }

    private static void parseReferenceData(List<List<Object>> data, ArrayList<DataType> referenceData) {
        for (int i = 1; i < data.size(); i++) {
            if (data.get(i).size() > 5) {
                referenceData.add(new Person((String) data.get(i).get(0),Long.parseLong((String) data.get(i).get(1)),
                        Boolean.parseBoolean((String) data.get(i).get(2)), (String) data.get(i).get(3),
                        (String) data.get(i).get(4), (String) data.get(i).get(5)));
            } else if (data.get(i).size() > 4) {
                referenceData.add(new Person((String) data.get(i).get(0),Long.parseLong((String) data.get(i).get(1)),
                        Boolean.parseBoolean((String) data.get(i).get(2)), (String) data.get(i).get(3),
                        (String) data.get(i).get(4), null));
            }
        }
    }

    private static void parseProjectData(List<List<Object>> data, ArrayList<DataType> projectData) {
        for (int i = 1; i < data.size(); i++) {
            Project project;
            if(data.get(i).size() > 5){
                project = new Project((String) data.get(i).get(0), Long.parseLong((String) data.get(i).get(1)),
                        Boolean.parseBoolean((String) data.get(i).get(2)),
                        (String) data.get(i).get(3), (String) data.get(i).get(4), parseLocalDate((String) data.get(i).get(5)));
            } else if (data.get(i).size() > 4){
                project = new Project((String) data.get(i).get(0), Long.parseLong((String) data.get(i).get(1)),
                        Boolean.parseBoolean((String) data.get(i).get(2)),
                        (String) data.get(i).get(3), (String) data.get(i).get(4), null);
            } else if (data.get(i).size() > 3){
                project = new Project((String) data.get(i).get(0), Long.parseLong((String) data.get(i).get(1)),
                        Boolean.parseBoolean((String) data.get(i).get(2)),
                        (String) data.get(i).get(3), null, null);
            } else {
                project = new Project((String) data.get(i).get(0), Long.parseLong((String) data.get(i).get(1)),
                        Boolean.parseBoolean((String) data.get(i).get(2)),
                        null, null, null);
            }
            projectData.add(project);
        }
    }

}
