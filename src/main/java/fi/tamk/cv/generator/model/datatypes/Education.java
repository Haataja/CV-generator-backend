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
package fi.tamk.cv.generator.model.datatypes;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

public class Education extends DataType{
    private String school_name;
    private String school_type;
    private String field_name;
    private int grade;
    private LocalDate startdate;
    private LocalDate enddate;

    public Education() {
        super();
    }

    public Education(String school_name, String school_type, String field_name, int grade, LocalDate startdate, LocalDate enddate) {
        super("education");
        this.school_name = school_name;
        this.school_type = school_type;
        this.field_name = field_name;
        this.grade = grade;
        this.startdate = startdate;
        this.enddate = enddate;
    }

    public Education(long id, boolean visible, String school_name, String school_type, String field_name, int grade, LocalDate startdate, LocalDate enddate) {
        super("education", id, visible);
        this.school_name = school_name;
        this.school_type = school_type;
        this.field_name = field_name;
        this.grade = grade;
        this.startdate = startdate;
        this.enddate = enddate;
    }

    public List<Object> toList(){
        return Arrays.asList(getType(),getId(),isVisible(),getSchool_name(),getSchool_type(),getField_name(),getGrade(),
                getStartdate().format(formatter),getEnddate().format(formatter));
    }

    public String getSchool_name() {
        return school_name;
    }

    public void setSchool_name(String school_name) {
        this.school_name = school_name;
    }

    public String getSchool_type() {
        return school_type;
    }

    public void setSchool_type(String school_type) {
        this.school_type = school_type;
    }

    public String getField_name() {
        return field_name;
    }

    public void setField_name(String field_name) {
        this.field_name = field_name;
    }

    public int getGrade() {
        return grade;
    }

    public void setGrade(int grade) {
        this.grade = grade;
    }

    public LocalDate getStartdate() {
        return startdate;
    }

    public void setStartdate(LocalDate startdate) {
        this.startdate = startdate;
    }

    public void setStartdate(String startdate) {
        this.startdate = LocalDate.parse(startdate, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }

    public LocalDate getEnddate() {
        return enddate;
    }

    public void setEnddate(LocalDate enddate) {
        this.enddate = enddate;
    }

    public void setEnddate(String enddate) {
        this.enddate = LocalDate.parse(enddate, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }
}
