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

import com.google.api.services.drive.model.File;
import fi.tamk.cv.generator.model.Bio;
import fi.tamk.cv.generator.model.Info;
import fi.tamk.cv.generator.model.ProfileImage;
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

    @Autowired
    private SheetsHelper sheetsHelper;

    @Autowired
    private DriveHelper driveHelper;


    /**
     * Creates Google drive folder and sheet, and adds default information.
     * @param accessToken AccessToken is used to connect users Google account
     * @return User is returned
     * @throws IOException
     * @throws GeneralSecurityException
     */
    public User createSheet(String accessToken) throws IOException, GeneralSecurityException {
        String folderID = driveHelper.createNewFolder(accessToken);
        log.debug("Created folder: {}", folderID);
        User user = null;
        if (folderID != null) {
            String sheetID = getOwnedSheetID(accessToken);
            log.debug("Found sheet? : {}", sheetID == null);
            if (sheetID == null) {
                sheetID = sheetsHelper.createSheet(accessToken);
                driveHelper.moveSheetToFolder(accessToken, sheetID, folderID);
                user = sheetsHelper.createDefUser();
                sheetsHelper.writeToSheet(accessToken, sheetID, user);
            } else {
                user = sheetsHelper.read(sheetID, accessToken);
            }
        }
        return user;
    }

    /**
     * Writes data to sheet
     * @param accessToken Access token is used to connect users Google account
     * @param user The data that is written
     */
    public void addUserData(String accessToken, User user) {
        String sheetID = getOwnedSheetID(accessToken);

        if (sheetID != null) {
            sheetsHelper.writeToSheet(accessToken, sheetID, user);
        }
    }

    /**
     * Searches the Google Drive for sheet and returns the id of the sheet that is owned by user and not in trash
     * @param accessToken Access token is used to connect users Google account
     * @return Id of the sheet
     */
    public String getOwnedSheetID(String accessToken) {
        List<File> files = driveHelper.search(accessToken, DriveHelper.SPREADSHEET_NAME);
        String sheetID = null;
        for (File file : files) {
            if (file.getOwnedByMe() && !file.getTrashed()) {
                sheetID = file.getId();
                break;
            }
        }
        return sheetID;
    }

    /**
     * Searches Google drive for sheet and returns list of email of the people that own those sheets
     * @param accessToken Access token is used to connect users Google account
     * @return List of emails
     */
    public List<String> getEmailsOfNotOwnedByMe(String accessToken) {
        List<File> files = driveHelper.search(accessToken, DriveHelper.SPREADSHEET_NAME);
        List<String> emails = new ArrayList<>();
        for (File file : files) {
            if (!file.getOwnedByMe() && !file.getTrashed()) {
                emails.add(file.getOwners().get(0).getEmailAddress());
                break;
            }
        }
        return emails;
    }

    /**
     * Appends row of data to sheet, not in active use
     * @param accessToken Access token is used to connect users Google account
     * @param range Sheet tab name
     * @param dataType Data written to the sheet
     */
    public void appendDataType(String accessToken, String range, DataType dataType) {
        String sheetID = getOwnedSheetID(accessToken);
        Info data = (Info) sheetsHelper.readRange(accessToken, sheetID, range);
        data.getData().add(dataType);

        sheetsHelper.writeToSheet(accessToken, sheetID, range, data.toListOfLists());

    }

    /**
     * Writes data to the sheet
     * @param accessToken Access token is used to connect users Google account
     * @param bio The data written to sheet
     */
    public void addBioData(String accessToken, Bio bio){
        String sheetID = getOwnedSheetID(accessToken);
        sheetsHelper.writeToSheet(accessToken, sheetID, "bio", bio.toListOfLists());
    }

    /**
     * Reads data from the sheet.
     * @param accessToken Access token is used to connect users Google account
     * @return The data in form of {@link User}
     */
    public User getData(String accessToken) {
        String sheetID = getOwnedSheetID(accessToken);
        if (sheetID != null) {
            return sheetsHelper.read(sheetID, accessToken);
        } else {
            return null;
        }

    }

    /**
     * Writes data to the sheet
     * @param accessToken Access token is used to connect users Google account
     * @param image The data written to sheet
     */
    public void addProfileData(String accessToken, ProfileImage image) {
        String sheetID = getOwnedSheetID(accessToken);

        if (sheetID != null) {
            sheetsHelper.writeToSheet(accessToken, sheetID, "profile_image", image.toListOfLists());
        }
    }

    /**
     * Writes data to the sheet
     * @param accessToken Access token is used to connect users Google account
     * @param range Sheet tab name
     * @param info The data written to sheet
     */
    public void addInfoData(String accessToken, String range, Info info) {
        String sheetID = getOwnedSheetID(accessToken);

        for(int i = 1; i <= info.getData().size(); i++){
            info.getData().get(i - 1).setId(i);
        }

        if (sheetID != null) {
            sheetsHelper.clearSheet(accessToken, sheetID, range);
            sheetsHelper.writeToSheet(accessToken, sheetID, range, info.toListOfLists());
        }
    }

    /**
     * Shares reading rights of the folder to another user by email
     * @param accessToken Access token is used to connect users Google account
     * @param email Email of the user to which folder is shared
     * @throws IOException thrown on exception
     * @throws GeneralSecurityException thrown on exception
     */
    public void shareFolder(String accessToken, String email) throws IOException, GeneralSecurityException {
        driveHelper.shareFolder(accessToken, email);
    }
}
