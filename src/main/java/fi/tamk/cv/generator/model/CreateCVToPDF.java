package fi.tamk.cv.generator.model;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.time.LocalDate;
import java.util.ArrayList;
import com.nimbusds.jose.util.IOUtils;
import org.json.*;
import javax.imageio.ImageIO;


public class CreateCVToPDF {
    private Object firstName;
    private Object lastName;
    private Object birthDate;
    private Object contactInfo;
    private Object address;
    private Object profileImage;
    private Object bio;
    private Object misc;
    private Object experience;
    private Object education;
    private Object projects;
    private Object titles;
    private Object references;

    public CreateCVToPDF() {
        try {
            getJSONData();
            Document document = new Document(PageSize.A4);
            PdfWriter.getInstance(document, new FileOutputStream("iTextHelloWorld.pdf"));
            document.open();
            addData(document);
            document.close();
        } catch (DocumentException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void addData(Document document) {
        try {
            PdfPTable header = new PdfPTable(3);
            header.setWidths(new float[]{2, 1, 1});
            header.setWidthPercentage(100);
            PdfPTable table = new PdfPTable(2);
            table.setWidthPercentage(100);
            table.setHorizontalAlignment(Element.ALIGN_LEFT);
            table.setSpacingAfter(20f);
            createContactInfoTable(table);
            PdfPCell cell = new PdfPCell();
            cell.setBorder(Rectangle.NO_BORDER);
            cell.addElement(table);
            header.addCell(cell);
            Chunk chunk = new Chunk("Resume\r\n" + LocalDate.now());
            PdfPCell chunkCell = new PdfPCell();
            chunkCell.setBorder(Rectangle.NO_BORDER);
            chunkCell.addElement(chunk);
            header.addCell(chunkCell);

            if (checkVisibility(profileImage)) {
                BufferedImage image = null;
                URL url;
                ByteArrayOutputStream baos = null;
                Image iTextImage = null;
                try {
                    url = new URL(getProfileImageData());
                    image = ImageIO.read(url);
                    baos = new ByteArrayOutputStream();
                    ImageIO.write(image, "png", baos);
                    iTextImage = Image.getInstance(baos.toByteArray());
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                PdfPCell imageCell = new PdfPCell(iTextImage, true);
                imageCell.setBorder(Rectangle.NO_BORDER);
                header.addCell(imageCell);
            } else {
                PdfPCell emptyCell = new PdfPCell();
                emptyCell.setBorder(Rectangle.NO_BORDER);
                header.addCell(emptyCell);
            }
            document.add(header);

            if (checkVisibility(bio)) {
                PdfPTable bioTable = new PdfPTable(1);
                createBioTable(bioTable);
                bioTable.setSpacingAfter(20f);
                document.add(bioTable);
            }

            if (checkVisibility(experience)) {
                PdfPTable content = new PdfPTable(2);
                content.setWidthPercentage(100);
                content.setWidths(new float[]{2, 3});
                createContentTable(content, "Experience", getExperienceData());
                content.setSpacingAfter(20f);
                document.add(content);
            }

            if (checkVisibility(education)) {
                PdfPTable content = new PdfPTable(2);
                content.setWidthPercentage(100);
                content.setWidths(new float[]{2, 3});
                createContentTable(content, "Education", getEducationData());
                content.setSpacingAfter(20f);
                document.add(content);
            }

            if (checkVisibility(projects)) {
                PdfPTable content = new PdfPTable(2);
                content.setWidthPercentage(100);
                content.setWidths(new float[]{2, 3});
                createContentTable(content, "Projects", getProjectsData());
                content.setSpacingAfter(20f);
                document.add(content);
            }

            if (checkVisibility(titles)) {
                PdfPTable content = new PdfPTable(2);
                content.setWidthPercentage(100);
                content.setWidths(new float[]{2, 3});
                createContentTable(content, "Titles", getTitlesData());
                content.setSpacingAfter(20f);
                document.add(content);
            }

            if (checkVisibility(references)) {
                PdfPTable content = new PdfPTable(2);
                content.setWidthPercentage(100);
                content.setWidths(new float[]{2, 3});
                createContentTable(content, "References", getReferencesData());
                content.setSpacingAfter(20f);
                document.add(content);
            }

            if (checkVisibility(misc)) {
                PdfPTable content = new PdfPTable(2);
                content.setWidthPercentage(100);
                content.setWidths(new float[]{2, 3});
                createContentTable(content, "Miscellaneous", getMiscData());
                content.setSpacingAfter(20f);
                document.add(content);
            }
        } catch (DocumentException e) {
            e.printStackTrace();
        }
    }

    public void getJSONData() {
        File stream = new File("F:\\Coding\\CV-generator-backend\\src\\main\\resources\\test.json");
        try {
            String jsonTxt = IOUtils.readFileToString(stream, Charset.defaultCharset());
            JSONObject obj = new JSONObject(jsonTxt);
            firstName = obj.get("firstname");
            lastName = obj.get("lastname");
            birthDate = obj.get("birthdate");
            contactInfo = obj.get("contact_info");
            address = obj.get("address");
            profileImage = obj.get("profile_image");
            bio = obj.get("bio");
            misc = obj.get("misc");
            experience = obj.get("experience");
            education = obj.get("education");
            projects = obj.get("projects");
            titles = obj.get("titles");
            references = obj.get("references");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean checkVisibility(Object object) {
        JSONObject obj = (JSONObject) object;
        return obj.get("visible").toString().equals("true");
    }

    private void createContactInfoTable(PdfPTable table) {
        PdfPCell cell = new PdfPCell();
        cell.setFixedHeight(25f);
        cell.addElement(new Phrase(firstName.toString()));
        table.addCell(cell);
        PdfPCell cellLast = new PdfPCell();
        cell.setFixedHeight(25f);
        cellLast.addElement(new Phrase(lastName.toString()));
        table.addCell(cellLast);
        if (checkVisibility(address)) {
            cell.setColspan(2);
            ArrayList<String> listAddress = getAddressData();
            cell.setPhrase(new Phrase(listAddress.get(0)));
            table.addCell(cell);
            cell.setColspan(1);
            cell.addElement(new Phrase(listAddress.get(1)));
            table.addCell(cell);
            PdfPCell cella = new PdfPCell();
            cella.setFixedHeight(25f);
            cella.addElement(new Phrase(listAddress.get(3)));
            table.addCell(cella);
        }
        if (checkVisibility(contactInfo)) {
            cell.setColspan(2);
            ArrayList<String> list = getContactInfoData();
            cell.setPhrase(new Phrase(list.get(0)));
            table.addCell(cell);
            cell.setPhrase(new Phrase(list.get(1)));
            table.addCell(cell);
        }
    }

    private void createBioTable(PdfPTable table) {
        PdfPCell cell = new PdfPCell();
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setPhrase(new Phrase(getBioData()));
        table.setWidthPercentage(100);
        table.addCell(cell);
    }

    private void createContentTable(PdfPTable table, String title, ArrayList<ArrayList<String>> content) {
        if (!content.isEmpty()) {
            PdfPCell cell = new PdfPCell();
            cell.setBorder(Rectangle.NO_BORDER);
            cell.setPhrase(new Phrase(title));
            table.addCell(cell);
            cell = new PdfPCell();
            cell.setBorder(Rectangle.NO_BORDER);
            PdfPTable listTable = new PdfPTable(1);
            createContentListTable(listTable, content);
            listTable.getDefaultCell().setBorder(0);
            cell.addElement(listTable);
            table.addCell(cell);
        }
    }

    private void createContentListTable(PdfPTable table, ArrayList<ArrayList<String>> data) {
        PdfPCell cell = new PdfPCell();
        cell.setBorder(Rectangle.NO_BORDER);

        for (int i = 0; i < data.size(); i++) {
            for (int j = 0; j < data.get(i).size(); j++) {
                Phrase phrase = new Phrase(data.get(i).get(j));
                if (j == 0) {
                    Font font = phrase.getFont();
                    font.setStyle(Font.BOLD);
                    phrase.setFont(font);
                }
                cell.setPhrase(phrase);
                table.setSpacingAfter(15f);
                table.addCell(cell);
            }
        }
    }

    private ArrayList<String> getAddressData() {
        JSONObject obj = (JSONObject) address;
        String streetAddress = obj.getString("street_address");
        String zipCode = obj.getString("zipcode");
        String country = obj.getString("country");
        String city = obj.getString("city");
        ArrayList<String> list = new ArrayList<>();
        list.add(streetAddress);
        list.add(zipCode);
        list.add(country);
        list.add(city);
        return list;
    }

    private ArrayList<String> getContactInfoData() {
        JSONObject obj = (JSONObject) contactInfo;
        String email = obj.getString("email");
        String phone = obj.getString("phone");
        ArrayList<String> list = new ArrayList<>();
        list.add(email);
        list.add(phone);
        return list;
    }

    private String getBioData() {
        JSONObject obj = (JSONObject) bio;
        return obj.getString("value");
    }

    private String getProfileImageData() {
        JSONObject obj = (JSONObject) profileImage;
        return obj.getString("source");
    }

    private ArrayList<ArrayList<String>> getMiscData() {
        JSONObject obj = (JSONObject) misc;
        JSONArray array = obj.getJSONArray("data");
        ArrayList<ArrayList<String>> list = new ArrayList<>();
        for (int i = 0; i < array.length(); i++) {
            JSONObject object = array.getJSONObject(i);
            if (checkVisibility(object)) {
                ArrayList<String> valueList = new ArrayList<>();
                String value = object.get("value").toString();
                String name = object.get("name").toString();
                valueList.add(value);
                valueList.add(name);
                list.add(valueList);
            }
        }
        return list;
    }

    private ArrayList<ArrayList<String>> getExperienceData() {
        JSONObject obj = (JSONObject) experience;
        JSONArray array = obj.getJSONArray("data");
        ArrayList<ArrayList<String>> list = new ArrayList<>();
        for (int i = 0; i < array.length(); i++) {
            JSONObject object = array.getJSONObject(i);
            if (checkVisibility(object)) {
                ArrayList<String> valueList = new ArrayList<>();
                String name = object.get("name").toString();
                String title = object.get("title").toString();
                String description = object.get("description").toString();
                String startDate = object.get("startdate").toString();
                String endDate = object.get("enddate").toString();
                valueList.add(name);
                valueList.add(title);
                valueList.add(description);
                valueList.add("Start Date: " + startDate);
                valueList.add("End Date: " + endDate);
                list.add(valueList);
            }
        }
        return list;
    }

    private ArrayList<ArrayList<String>> getEducationData() {
        JSONObject obj = (JSONObject) education;
        JSONArray array = obj.getJSONArray("data");
        ArrayList<ArrayList<String>> list = new ArrayList<>();
        for (int i = 0; i < array.length(); i++) {
            JSONObject object = array.getJSONObject(i);
            if (checkVisibility(object)) {
                ArrayList<String> valueList = new ArrayList<>();
                if (object.get("type").toString().equals("education")) {
                    String schoolName = object.get("school_name").toString();
                    String schoolType = object.get("school_type").toString();
                    String fieldName = object.get("field_name").toString();
                    String startDate = object.get("startdate").toString();
                    String endDate = object.get("enddate").toString();
                    String grade = object.get("grade").toString();
                    valueList.add(schoolName);
                    valueList.add(schoolType);
                    valueList.add(fieldName);
                    valueList.add("Grade: " + grade);
                    valueList.add("Start Date: " + startDate);
                    valueList.add("End Date: " + endDate);
                } else {
                    String courseName = object.get("course_name").toString();
                    String providerName = object.get("provider_name").toString();
                    String grade = object.get("grade").toString();
                    String startDate = object.get("startdate").toString();
                    String endDate = object.get("enddate").toString();
                    valueList.add(courseName);
                    valueList.add(providerName);
                    valueList.add("Grade: " + grade);
                    valueList.add("Start Date: " + startDate);
                    valueList.add("End Date: " + endDate);
                }
                list.add(valueList);
            }
        }
        return list;
    }

    private ArrayList<ArrayList<String>> getProjectsData() {
        JSONObject obj = (JSONObject) projects;
        JSONArray array = obj.getJSONArray("data");
        ArrayList<ArrayList<String>> list = new ArrayList<>();
        for (int i = 0; i < array.length(); i++) {
            JSONObject object = array.getJSONObject(i);
            if (checkVisibility(object)) {
                ArrayList<String> valueList = new ArrayList<>();
                String name = object.get("name").toString();
                String description = object.get("description").toString();
                String completionDate = object.get("completion_date").toString();
                valueList.add(name);
                valueList.add(description);
                valueList.add("Completion Date: " + completionDate);
                list.add(valueList);
            }
        }
        return list;
    }

    private ArrayList<ArrayList<String>> getTitlesData() {
        JSONObject obj = (JSONObject) titles;
        JSONArray array = obj.getJSONArray("data");
        ArrayList<ArrayList<String>> list = new ArrayList<>();
        for (int i = 0; i < array.length(); i++) {
            JSONObject object = array.getJSONObject(i);
            if (checkVisibility(object)) {
                ArrayList<String> valueList = new ArrayList<>();
                String title = object.get("title").toString();
                String awarded = object.get("awarded").toString();
                valueList.add(title);
                valueList.add("Awarding Date: " + awarded);
                list.add(valueList);
            }
        }
        return list;
    }

    private ArrayList<ArrayList<String>> getReferencesData() {
        JSONObject obj = (JSONObject) references;
        JSONArray array = obj.getJSONArray("data");
        ArrayList<ArrayList<String>> list = new ArrayList<>();
        for (int i = 0; i < array.length(); i++) {
            JSONObject object = array.getJSONObject(i);
            if (checkVisibility(object)) {
                ArrayList<String> valueList = new ArrayList<>();
                String name = object.get("name").toString();
                String contactEmail = object.get("contact_email").toString();
                String contactPhone = object.get("contact_phone").toString();
                valueList.add(name);
                valueList.add(contactEmail);
                valueList.add(contactPhone);
                list.add(valueList);
            }
        }
        return list;
    }
}
