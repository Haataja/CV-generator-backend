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

public class Ability extends DataType{
    private String name;
    private String efficiency_title;
    private long efficiency_level;

    public Ability(String name, String efficiency_title, long efficiency_level) {
        this.name = name;
        this.efficiency_title = efficiency_title;
        this.efficiency_level = efficiency_level;
    }

    public Ability(String type, String name, String efficiency_title, long efficiency_level) {
        super(type);
        this.name = name;
        this.efficiency_title = efficiency_title;
        this.efficiency_level = efficiency_level;
    }

    public Ability(String type, long id, boolean visible, String name, String efficiency_title, long efficiency_level) {
        super(type, id, visible);
        this.name = name;
        this.efficiency_title = efficiency_title;
        this.efficiency_level = efficiency_level;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEfficiency_title() {
        return efficiency_title;
    }

    public void setEfficiency_title(String efficiency_title) {
        this.efficiency_title = efficiency_title;
    }

    public long getEfficiency_level() {
        return efficiency_level;
    }

    public void setEfficiency_level(long efficiency_level) {
        this.efficiency_level = efficiency_level;
    }
}
