package fi.tamk.cv.generator;

import com.google.api.services.sheets.v4.model.UpdateValuesResponse;
import fi.tamk.cv.generator.Google.SheetsHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

@RestController
public class CvController {
    @Autowired
    SheetsHelper sheetsHelper;

    @RequestMapping("/addStuff")
    public UpdateValuesResponse Add(){
        UpdateValuesResponse response = null;
        try {
            response = sheetsHelper.writeSomethingToSheet();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response;
    }

    @RequestMapping("/read")
    public List<List<Object>> read(@RequestParam(name = "id") String sheetID){
        return sheetsHelper.readFromSheet(sheetID);
    }
}
