/*
Copyright 2019 Hanna Haataja <hanna.haataja@tuni.fi>, Samu Koivulahti <samu.koivulahti@tuni.fi>. All rights reserved.

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

import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import fi.tamk.cv.generator.model.Info;
import fi.tamk.cv.generator.model.User;
import fi.tamk.cv.generator.model.datatypes.DataType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;

@Component
public class GoogleServices {
    Logger log = LoggerFactory.getLogger(this.getClass());
    public static final String APPLICATION_NAME = "quickstart-1550136441024";

    @Autowired
    private SheetsHelper sheetsHelper;

    @Autowired
    private DriveHelper driveHelper;


    public String createSheet(String accessToken) throws IOException, GeneralSecurityException {
        String folderID = driveHelper.createNewFolder(accessToken);
        log.debug("Created folder: {}", folderID);
        if(folderID != null){
            String sheetID = getOwnedSheetID(accessToken);
            log.debug("Found sheet? : {}", sheetID == null);
            if(sheetID == null){
                sheetID = sheetsHelper.createSheet(accessToken);
                log.debug("Created sheet id: {}", sheetID);
            }
            if(sheetID != null){
                log.debug("folder: {} sheet: {}", folderID, sheetID);
                driveHelper.moveSheetToFolder(accessToken, sheetID,folderID);
                User user = sheetsHelper.createDefUser();
                sheetsHelper.writeToSheet(accessToken, sheetID, user);
            }
        }
        return "ok";
    }

    public void writeToCV(String accessToken, User user){
        String sheetID = getOwnedSheetID(accessToken);

        if(sheetID != null){
            sheetsHelper.writeToSheet(accessToken,sheetID,user);
        }
    }

    public String getOwnedSheetID(String accessToken){
        List<File> files = driveHelper.search(accessToken, DriveHelper.SPREADSHEET_NAME);
        String sheetID = null;
        for(File file: files){
            if(file.getOwnedByMe() && !file.getTrashed()){
                sheetID = file.getId();
                break;
            }
        }
        return sheetID;
    }

    public List<String> getEmailsOfNotOwnedByMe(String accessToken){
        List<File> files = driveHelper.search(accessToken, DriveHelper.SPREADSHEET_NAME);
        List<String> emails = new ArrayList<>();
        for(File file: files){
            if(!file.getOwnedByMe() && !file.getTrashed()){
                emails.add( file.getOwners().get(0).getEmailAddress());
                break;
            }
        }
        return emails;
    }

    public String appendDataType(String accessToken ,String range, DataType dataType) {
        String sheetID = getOwnedSheetID(accessToken);
        Info data = (Info) sheetsHelper.readRange(accessToken, sheetID, range);
        data.getData().add(dataType);

        sheetsHelper.writeToSheet(accessToken, sheetID, range,data.toListOfLists());

        return "ok";
    }

    public User getData(String accessToken){
        String sheetID = getOwnedSheetID(accessToken);
        if(sheetID != null){
            return sheetsHelper.read(sheetID, accessToken);
        } else {
            return new User();
        }

    }
}
