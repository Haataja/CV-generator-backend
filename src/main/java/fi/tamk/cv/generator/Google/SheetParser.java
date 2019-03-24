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

public class SheetParser {

    public static User parseUser(List<ValueRange> list) {
        User user = new User();
        for (ValueRange vr : list) {
            String sheet = vr.getRange().split("!")[0];
            List<List<Object>> values = vr.getValues();
            switch (sheet) {
                case "basic":
                    user.setId(Long.parseLong((String) values.get(0).get(0)));
                    if (values.get(0).size() > 1) {
                        user.setFirstname((String) values.get(0).get(1));
                        if (values.get(0).size() > 2) {
                            user.setLastname((String) values.get(0).get(2));
                            if (values.get(0).size() > 3) {
                                user.setBirthdate(parseLocalDate((String) values.get(0).get(3)));
                            }
                        }
                    }
                    break;
                case "contact_info":
                    if (values != null) {
                        for (List<Object> contact : values) {
                            user.getContact_info().add(new ContactInfo((String) contact.get(0), (String) contact.get(1), Boolean.parseBoolean((String) contact.get(2))));
                        }
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
                case "document_settings":
                    if (values != null) {
                        if (values.get(0).size() == 3) {
                            user.setDocument_settings(new DocumentSettings((String) values.get(0).get(0), (String) values.get(0).get(1),
                                    (String) values.get(0).get(2)));
                        } else if (values.get(0).size() == 2) {
                            user.setDocument_settings(new DocumentSettings((String) values.get(0).get(0), (String) values.get(0).get(1),
                                    null));
                        } else {
                            user.setDocument_settings(new DocumentSettings((String) values.get(0).get(0), null, null));
                        }
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
                    for (int i = 1; i < values.size(); i++) {
                        LicenceData.add(new Licence((String) values.get(i).get(0), Long.parseLong((String) values.get(i).get(1)),
                                Boolean.parseBoolean((String) values.get(i).get(2)), (String) values.get(i).get(3),
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
                    ParseAbilityData(values, AbilityData);
                    abilityInfo.setData(AbilityData);
                    user.setAbilities_and_hobbies(abilityInfo);
                    break;
                case "experience":
                    List<Object> ExpInfos = values.get(0);
                    Info expInfo = new Info(Integer.parseInt((String) ExpInfos.get(0)),
                            Boolean.parseBoolean((String) ExpInfos.get(1)));
                    ArrayList<DataType> expData = new ArrayList<>();
                    ParseExperienceData(values, expData);
                    expInfo.setData(expData);
                    user.setExperience(expInfo);
                    break;
                case "courses_and_education":
                    List<Object> courseInfos = values.get(0);
                    Info courseInfo = new Info(Integer.parseInt((String) courseInfos.get(0)),
                            Boolean.parseBoolean((String) courseInfos.get(1)));
                    ArrayList<DataType> courseData = new ArrayList<>();
                    parseCourseData(values, courseData);
                    courseInfo.setData(courseData);
                    user.setCourses_and_education(courseInfo);
                    break;
                case "achievements_and_projects":
                    List<Object> projectInfos = values.get(0);
                    Info projectInfo = new Info(Integer.parseInt((String) projectInfos.get(0)),
                            Boolean.parseBoolean((String) projectInfos.get(1)));
                    ArrayList<DataType> projectData = new ArrayList<>();
                    parseProjectData(values, projectData);
                    projectInfo.setData(projectData);
                    user.setCourses_and_education(projectInfo);
                    break;
                case "titles_and_degrees":
                    List<Object> titleInfos = values.get(0);
                    Info titleInfo = new Info(Integer.parseInt((String) titleInfos.get(0)),
                            Boolean.parseBoolean((String) titleInfos.get(1)));
                    ArrayList<DataType> titleData = new ArrayList<>();
                    for (int i = 1; i < values.size(); i++) {
                        titleData.add(new Title((String) values.get(i).get(0), Long.parseLong((String) values.get(i).get(1)),
                                Boolean.parseBoolean((String) values.get(i).get(2)), (String) values.get(i).get(3),
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
                    for (int i = 1; i < values.size(); i++) {
                        referenceData.add(new Person(Long.parseLong((String) values.get(i).get(1)),
                                Boolean.parseBoolean((String) values.get(i).get(2)), (String) values.get(i).get(3),
                                (String) values.get(i).get(4), (String) values.get(i).get(5)));
                    }
                    referenceInfo.setData(referenceData);
                    user.setReferences(referenceInfo);
                    break;
            }


        }
        return user;
    }

    private static void ParseAbilityData(List<List<Object>> values, ArrayList<DataType> abilityData) {
        for (int i = 1; i < values.size(); i++) {
            DataType ability;
            if (values.get(i).get(0).equals("hobby")) {
                ability = new Hobby(Long.parseLong((String) values.get(i).get(1)),
                        Boolean.parseBoolean((String) values.get(i).get(2)),
                        (String) values.get(i).get(3), (String) values.get(i).get(4),
                        parseLocalDate((String) values.get(i).get(5)), parseLocalDate((String) values.get(i).get(6)));
            } else {
                ability = new Ability((String) values.get(i).get(0), Long.parseLong((String) values.get(i).get(1)),
                        Boolean.parseBoolean((String) values.get(i).get(2)), (String) values.get(i).get(3),
                        (String) values.get(i).get(4), Integer.parseInt((String) values.get(i).get(5)));
            }

            abilityData.add(ability);
        }
    }

    private static void ParseExperienceData(List<List<Object>> values, ArrayList<DataType> expData) {
        for (int i = 1; i < values.size(); i++) {
            DataType experience;
            if (values.get(i).get(0).equals("work")) {
                experience = new ExperienceWork(Long.parseLong((String) values.get(i).get(1)),
                        Boolean.parseBoolean((String) values.get(i).get(2)), parseLocalDate((String) values.get(i).get(3)),
                        parseLocalDate((String) values.get(i).get(4)), (String) values.get(i).get(5), (String) values.get(i).get(6),
                        (String) values.get(i).get(7));
                setAchievementsAndResp(experience, values.get(i));

            } else {
                experience = new Experience(Long.parseLong((String) values.get(i).get(1)),
                        Boolean.parseBoolean((String) values.get(i).get(2)), parseLocalDate((String) values.get(i).get(3)),
                        parseLocalDate((String) values.get(i).get(4)), (String) values.get(i).get(5), (String) values.get(i).get(6),
                        (String) values.get(i).get(7));
                int size = values.get(i).size() - 8;
                String[] achievements = new String[size];
                for (int j = 0; j < size; j++) {
                    achievements[j] = (String) values.get(i).get(j + 8);
                }
                ((Experience) experience).setAchievements(achievements);
            }

            expData.add(experience);
        }
    }

    private static void parseCourseData(List<List<Object>> values, ArrayList<DataType> courseData) {
        for (int i = 1; i < values.size(); i++) {
            DataType course;
            if (values.get(i).get(0).equals("course")) {
                course = new Course(Long.parseLong((String) values.get(i).get(1)),
                        Boolean.parseBoolean((String) values.get(i).get(2)),
                        (String) values.get(i).get(3), (String) values.get(i).get(4), Integer.parseInt((String) values.get(i).get(5)),
                        parseLocalDate((String) values.get(i).get(6)), parseLocalDate((String) values.get(i).get(7)));
            } else {
                course = new Education(Long.parseLong((String) values.get(i).get(1)),
                        Boolean.parseBoolean((String) values.get(i).get(2)),
                        (String) values.get(i).get(3), (String) values.get(i).get(4), (String) values.get(i).get(5)
                        , Integer.parseInt((String) values.get(i).get(6)),
                        parseLocalDate((String) values.get(i).get(7)), parseLocalDate((String) values.get(i).get(8)));
            }

            courseData.add(course);
        }
    }

    private static void parseProjectData(List<List<Object>> values, ArrayList<DataType> projectData) {
        for (int i = 1; i < values.size(); i++) {
            DataType project;
            if (values.get(i).get(0).equals("achievement")) {
                project = new Achievement(Long.parseLong((String) values.get(i).get(1)),
                        Boolean.parseBoolean((String) values.get(i).get(2)),
                        (String) values.get(i).get(3), (String) values.get(i).get(4), parseLocalDate((String) values.get(i).get(5)));
            } else {
                project = new Project((String) values.get(i).get(0), Long.parseLong((String) values.get(i).get(1)),
                        Boolean.parseBoolean((String) values.get(i).get(2)),
                        (String) values.get(i).get(3), (String) values.get(i).get(4), parseLocalDate((String) values.get(i).get(5)));
            }

            projectData.add(project);
        }
    }

    private static void setAchievementsAndResp(DataType experience, List<Object> objects) {
        boolean inResp = true;
        ArrayList<String> responsibilities = new ArrayList<>();
        ArrayList<String> achievements = new ArrayList<>();
        for (int i = 0; i < objects.size() - 8; i++) {
            if (inResp) {
                if (((String) objects.get(i + 8)).trim().equals("achievements")) {
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

    private static LocalDate parseLocalDate(String string) {
        String[] splitDate = string.split("/");
        return LocalDate.of(Integer.parseInt(splitDate[2]), Integer.parseInt(splitDate[1]), Integer.parseInt(splitDate[0]));
    }

    public static Object parseObject(List<List<Object>> data, String range) {
        Object object = null;
        switch (range) {
            case "contact_info":
                if (data != null) {
                    for (List<Object> contact : data) {
                        object = new ContactInfo((String) contact.get(0), (String) contact.get(1), Boolean.parseBoolean((String) contact.get(2)));
                    }
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
            case "document_settings":
                if (data != null) {
                    if (data.get(0).size() == 3) {
                        object = new DocumentSettings((String) data.get(0).get(0), (String) data.get(0).get(1),
                                (String) data.get(0).get(2));
                    } else if (data.get(0).size() == 2) {
                        object = new DocumentSettings((String) data.get(0).get(0), (String) data.get(0).get(1),
                                null);
                    } else {
                        object = new DocumentSettings((String) data.get(0).get(0), null, null);
                    }
                }
                break;
            case "bio":
                object = new Bio((String) data.get(0).get(0),
                        Boolean.parseBoolean((String) data.get(0).get(1)));
                break;
            case "licences":
                List<Object> LicenceInfos = data.get(0);
                Info LicenceInfo = new Info(Integer.parseInt((String) LicenceInfos.get(0)), Boolean.parseBoolean((String) LicenceInfos.get(1)));
                ArrayList<DataType> LicenceData = new ArrayList<>();
                for (int i = 1; i < data.size(); i++) {
                    LicenceData.add(new Licence((String) data.get(i).get(0), Long.parseLong((String) data.get(i).get(1)),
                            Boolean.parseBoolean((String) data.get(i).get(2)), (String) data.get(i).get(3),
                            (String) data.get(i).get(4)));
                }
                LicenceInfo.setData(LicenceData);
                object = LicenceInfo;
                break;
            case "abilities_and_hobbies":
                List<Object> AbilityInfos = data.get(0);
                Info abilityInfo = new Info(Integer.parseInt((String) AbilityInfos.get(0)),
                        Boolean.parseBoolean((String) AbilityInfos.get(1)));
                ArrayList<DataType> AbilityData = new ArrayList<>();
                ParseAbilityData(data, AbilityData);
                abilityInfo.setData(AbilityData);
                object = abilityInfo;
                break;
            case "experience":
                List<Object> ExpInfos = data.get(0);
                Info expInfo = new Info(Integer.parseInt((String) ExpInfos.get(0)),
                        Boolean.parseBoolean((String) ExpInfos.get(1)));
                ArrayList<DataType> expData = new ArrayList<>();
                ParseExperienceData(data, expData);
                expInfo.setData(expData);
                object = expInfo;
                break;
            case "courses_and_education":
                List<Object> courseInfos = data.get(0);
                Info courseInfo = new Info(Integer.parseInt((String) courseInfos.get(0)),
                        Boolean.parseBoolean((String) courseInfos.get(1)));
                ArrayList<DataType> courseData = new ArrayList<>();
                parseCourseData(data, courseData);
                courseInfo.setData(courseData);
                object = courseInfo;
                break;
            case "achievements_and_projects":
                List<Object> projectInfos = data.get(0);
                Info projectInfo = new Info(Integer.parseInt((String) projectInfos.get(0)),
                        Boolean.parseBoolean((String) projectInfos.get(1)));
                ArrayList<DataType> projectData = new ArrayList<>();
                parseProjectData(data, projectData);
                projectInfo.setData(projectData);
                object = projectInfo;
                break;
            case "titles_and_degrees":
                List<Object> titleInfos = data.get(0);
                Info titleInfo = new Info(Integer.parseInt((String) titleInfos.get(0)),
                        Boolean.parseBoolean((String) titleInfos.get(1)));
                ArrayList<DataType> titleData = new ArrayList<>();
                for (int i = 1; i < data.size(); i++) {
                    titleData.add(new Title((String) data.get(i).get(0), Long.parseLong((String) data.get(i).get(1)),
                            Boolean.parseBoolean((String) data.get(i).get(2)), (String) data.get(i).get(3),
                            parseLocalDate((String) data.get(i).get(4))));
                }
                titleInfo.setData(titleData);
                object = titleInfo;
                break;
            case "references":
                List<Object> referenceInfos = data.get(0);
                Info referenceInfo = new Info(Integer.parseInt((String) referenceInfos.get(0)),
                        Boolean.parseBoolean((String) referenceInfos.get(1)));
                ArrayList<DataType> referenceData = new ArrayList<>();
                for (int i = 1; i < data.size(); i++) {
                    referenceData.add(new Person(Long.parseLong((String) data.get(i).get(1)),
                            Boolean.parseBoolean((String) data.get(i).get(2)), (String) data.get(i).get(3),
                            (String) data.get(i).get(4), (String) data.get(i).get(5)));
                }
                referenceInfo.setData(referenceData);
                object = referenceData;
                break;
        }
        return object;
    }

}
