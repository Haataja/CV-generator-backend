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

public class Project extends DataType{
    private String name;
    private String description;
    private LocalDate completion_date;

    public Project() {
    }

    public Project(String type, String name, String description, LocalDate completion_date) {
        super(type);
        this.name = name;
        this.description = description;
        this.completion_date = completion_date;
    }

    public Project(String type, long id, boolean visible, String name, String description, LocalDate completion_date) {
        super(type, id, visible);
        this.name = name;
        this.description = description;
        this.completion_date = completion_date;
    }

    public List<Object> toList(){
        String name = getName() == null ? "" : getName();
        String desc = getDescription() == null ? "" : getDescription();
        String completion = getCompletion_date() == null ? "" : getCompletion_date().format(formatter);
        return Arrays.asList(getType(),getId(),isVisible(),name,desc,completion);
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getCompletion_date() {
        return completion_date;
    }

    public void setCompletion_date(LocalDate completion_date) {
        this.completion_date = completion_date;
    }

    public void setCompletion_date(String completion_date) {
        this.completion_date = LocalDate.parse(completion_date, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }
}
