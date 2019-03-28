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

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;

@Component
public class DriveHelper {
    private Logger log = LoggerFactory.getLogger(this.getClass());
    private static final String FOLDER_NAME = "CV-Generator-data";
    static final String SPREADSHEET_NAME = "CV-Generator-data-spreadsheet";
    private static final String APPLICATION_NAME = "quickstart-1550136441024";


    public static Drive getDriveService(String token) throws IOException, GeneralSecurityException {
        Credential credential = new GoogleCredential().setAccessToken(token);
        return new Drive.Builder(GoogleNetHttpTransport.newTrustedTransport(), JacksonFactory.getDefaultInstance(), credential).setApplicationName(APPLICATION_NAME).build();
    }


    public String createNewFolder(String token) throws IOException, GeneralSecurityException {
        String folderID = null;
        Drive service = getDriveService(token);
        File fileMetadata = new File();
        fileMetadata.setName(FOLDER_NAME);
        fileMetadata.setMimeType("application/vnd.google-apps.folder");
        List<File> files = search(token, FOLDER_NAME);
        log.debug("files is empty? {}, {}", files.size(), files.isEmpty());
        if (files.isEmpty()) {
            File file = service.files().create(fileMetadata).setFields("id").execute();
            log.debug("Folder ID: " + file.getId());
            folderID = file.getId();
        } else {
            boolean folderExists = false;
            for (File file : files) {
                if (file.getOwnedByMe() && !file.getTrashed()) {
                    log.debug("Folder ID: {}, trashed {}, ownedbyme {}", file.getId(), file.getTrashed(), file.getOwnedByMe());
                    folderID = file.getId();
                    folderExists = true;
                    break;
                }
            }
            if (!folderExists) {
                File createdFile = service.files().create(fileMetadata).setFields("id").execute();
                log.debug("Folder ID: " + createdFile.getId());
                folderID = createdFile.getId();

            }
        }
        return folderID;
    }


    public String moveSheetToFolder(String token, String sheetID, String folderID) throws IOException, GeneralSecurityException {
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

    /**
     * Searches for files that have certain name in their name.
     *
     * @param token accessToken that gives access to Google drive
     * @param name  The name of the searched file
     * @return List of files
     */
    public List<File> search(String token, String name) {
        Drive service;
        List<File> files = new ArrayList<>();
        try {
            service = getDriveService(token);
            files = service.files().list().setQ("name = '" + name + "'").setFields("files(id, name, mimeType, owners, ownedByMe, trashed)").execute().getFiles();

        } catch (IOException | GeneralSecurityException e) {
            log.debug("Error while searching the file, {}", e.getMessage());
            //e.printStackTrace();
        }

        return files;
    }

    public List<File> getFiles(String token, String name) throws IOException, GeneralSecurityException {
        Drive service = getDriveService(token);
        return service.files().list().setQ("name = '" + name + "'").execute().getFiles();
    }

}
