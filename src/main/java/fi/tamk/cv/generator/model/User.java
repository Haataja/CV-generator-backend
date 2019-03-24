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
package fi.tamk.cv.generator.model;

import com.fasterxml.jackson.annotation.JsonInclude;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class User {
    private long id;
    private String firstname;
    private String lastname;
    private LocalDate birthdate;
    private ArrayList<ContactInfo> contact_info;
    private Address address;
    private ProfileImage profile_image;
    private DocumentSettings document_settings;
    private Bio bio;
    private Info licences;
    private Info abilities_and_hobbies;
    private Info experience;
    private Info courses_and_education;
    private Info achievements_and_projects;
    private Info titles_and_degrees;
    private Info references;

    public User() {
        contact_info = new ArrayList<>();
    }

    public User(long id, String firstname, String lastname, LocalDate birthdate) {
        this.id = id;
        this.firstname = firstname;
        this.lastname = lastname;
        this.birthdate = birthdate;
        contact_info = new ArrayList<>();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public List<List<Object>> fetchContactInfoAsList(){
        List<List<Object>> lists = null;
        if(contact_info.size() > 0){
            lists = Arrays.asList(contact_info.get(0).toList());
            for(int i = 1; i < contact_info.size(); i++){
                lists.add(Arrays.asList(contact_info.get(i).toList()));
            }
        }
        return lists;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public LocalDate getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(LocalDate birthdate) {
        this.birthdate = birthdate;
    }

    public ArrayList<ContactInfo> getContact_info() {
        return contact_info;
    }

    public void setContact_info(ArrayList<ContactInfo> contact_info) {
        this.contact_info = contact_info;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public ProfileImage getProfile_image() {
        return profile_image;
    }

    public void setProfile_image(ProfileImage profile_image) {
        this.profile_image = profile_image;
    }

    public DocumentSettings getDocument_settings() {
        return document_settings;
    }

    public void setDocument_settings(DocumentSettings document_settings) {
        this.document_settings = document_settings;
    }

    public Bio getBio() {
        return bio;
    }

    public void setBio(Bio bio) {
        this.bio = bio;
    }

    public Info getLicences() {
        return licences;
    }

    public void setLicences(Info licences) {
        this.licences = licences;
    }

    public Info getAbilities_and_hobbies() {
        return abilities_and_hobbies;
    }

    public void setAbilities_and_hobbies(Info abilities_and_hobbies) {
        this.abilities_and_hobbies = abilities_and_hobbies;
    }

    public Info getExperience() {
        return experience;
    }

    public void setExperience(Info experience) {
        this.experience = experience;
    }

    public Info getCourses_and_education() {
        return courses_and_education;
    }

    public void setCourses_and_education(Info courses_and_education) {
        this.courses_and_education = courses_and_education;
    }

    public Info getAchievements_and_projects() {
        return achievements_and_projects;
    }

    public void setAchievements_and_projects(Info achievements_and_projects) {
        this.achievements_and_projects = achievements_and_projects;
    }

    public Info getTitles_and_degrees() {
        return titles_and_degrees;
    }

    public void setTitles_and_degrees(Info titles_and_degrees) {
        this.titles_and_degrees = titles_and_degrees;
    }

    public Info getReferences() {
        return references;
    }

    public void setReferences(Info references) {
        this.references = references;
    }
}
