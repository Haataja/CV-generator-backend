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

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

public class Course extends DataType{
    private String provider_name;
    private String course_name;
    private int grade;
    private LocalDate startdate;
    private LocalDate enddate;

    public Course() {
        super();
    }

    public Course(String provider_name, String course_name, int grade, LocalDate startdate, LocalDate enddate) {
        super("course");
        this.provider_name = provider_name;
        this.course_name = course_name;
        this.grade = grade;
        this.startdate = startdate;
        this.enddate = enddate;
    }

    public Course(long id, boolean visible, String provider_name, String course_name, int grade, LocalDate startdate, LocalDate enddate) {
        super("course", id, visible);
        this.provider_name = provider_name;
        this.course_name = course_name;
        this.grade = grade;
        this.startdate = startdate;
        this.enddate = enddate;
    }

    public List<Object> toList(){
        String startDate = this.startdate == null ? "": getStartdate().format(formatter);
        String endDate = this.startdate == null ? "": getEnddate().format(formatter);
        return Arrays.asList(getType(),getId(),isVisible(),getProvider_name(),getCourse_name(),getGrade(),
                startDate,endDate);
    }

    public String getProvider_name() {
        return provider_name;
    }

    public void setProvider_name(String provider_name) {
        this.provider_name = provider_name;
    }

    public String getCourse_name() {
        return course_name;
    }

    public void setCourse_name(String course_name) {
        this.course_name = course_name;
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
