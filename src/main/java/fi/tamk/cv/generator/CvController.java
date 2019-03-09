package fi.tamk.cv.generator;

import fi.tamk.cv.generator.Google.SheetsHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

@RestController
public class CvController {

    Logger log = LoggerFactory.getLogger(this.getClass());
    @Autowired
    SheetsHelper sheetsHelper;

    @Autowired
    private OAuth2AuthorizedClientService authorizedClientService;

    @GetMapping("/loginSuccess")
    public String getLoginInfo(OAuth2AuthenticationToken authentication) {
        OAuth2AuthorizedClient client = authorizedClientService
                .loadAuthorizedClient(
                        authentication.getAuthorizedClientRegistrationId(),
                        authentication.getName());
        log.info("Client: {}, token: {}", client.getPrincipalName(), client.getAccessToken().getTokenValue());
        try {
            sheetsHelper.writeSomethingToSheet(client.getAccessToken().getTokenValue());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "loginSuccess";
    }

    @RequestMapping("/read")
    public List<List<Object>> read(@RequestParam(name = "id") String sheetID){
        return sheetsHelper.readFromSheet(sheetID, "null");
    }
}
