package fi.tamk.cv.generator.model;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import fi.tamk.cv.generator.Google.GoogleServices;
import fi.tamk.cv.generator.model.datatypes.*;

import javax.imageio.ImageIO;
import javax.xml.crypto.Data;

public class CreatePDF {
    private User user;

    public CreatePDF(String name, String accessToken, GoogleServices services) {
        user = services.getData(accessToken);
        try {
            Document document = new Document(PageSize.A4);
            PdfWriter.getInstance(document, new FileOutputStream(name));
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

            if (user.getProfile_image().isVisible()) {
                BufferedImage image = null;
                URL url;
                ByteArrayOutputStream baos = null;
                Image iTextImage = null;
                try {
                    url = new URL(user.getProfile_image().getSource());
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

            if (user.getBio().isVisible()) {
                PdfPTable bioTable = new PdfPTable(1);
                createBioTable(bioTable);
                bioTable.setSpacingAfter(20f);
                document.add(bioTable);
            }

            if (user.getExperience().isVisible()) {
                PdfPTable content = new PdfPTable(2);
                content.setWidthPercentage(100);
                content.setWidths(new float[]{2, 3});
                createContentTable(content, "Experience", getExperienceData());
                content.setSpacingAfter(20f);
                document.add(content);
            }

            if (user.getEducation().isVisible()) {
                PdfPTable content = new PdfPTable(2);
                content.setWidthPercentage(100);
                content.setWidths(new float[]{2, 3});
                createContentTable(content, "Education", getEducationData());
                content.setSpacingAfter(20f);
                document.add(content);
            }

            if (user.getProjects().isVisible()) {
                PdfPTable content = new PdfPTable(2);
                content.setWidthPercentage(100);
                content.setWidths(new float[]{2, 3});
                createContentTable(content, "Projects", getProjectsData());
                content.setSpacingAfter(20f);
                document.add(content);
            }

            if (user.getTitles().isVisible()) {
                PdfPTable content = new PdfPTable(2);
                content.setWidthPercentage(100);
                content.setWidths(new float[]{2, 3});
                createContentTable(content, "Titles", getTitlesData());
                content.setSpacingAfter(20f);
                document.add(content);
            }

            if (user.getReferences().isVisible()) {
                PdfPTable content = new PdfPTable(2);
                content.setWidthPercentage(100);
                content.setWidths(new float[]{2, 3});
                createContentTable(content, "References", getReferencesData());
                content.setSpacingAfter(20f);
                document.add(content);
            }

            /*if (user.getMisc().isVisible()) {
                PdfPTable content = new PdfPTable(2);
                content.setWidthPercentage(100);
                content.setWidths(new float[]{2, 3});
                createContentTable(content, "Miscellaneous", getMiscData());
                content.setSpacingAfter(20f);
                document.add(content);
            }*/
        } catch (DocumentException e) {
            e.printStackTrace();
        }
    }

    private void createContactInfoTable(PdfPTable table) {
        PdfPCell cell = new PdfPCell();
        cell.setFixedHeight(25f);
        cell.addElement(new Phrase(user.getFirstname()));
        table.addCell(cell);
        PdfPCell cellLast = new PdfPCell();
        cell.setFixedHeight(25f);
        cellLast.addElement(new Phrase(user.getLastname()));
        table.addCell(cellLast);
        if (user.getAddress().isVisible()) {
            cell.setColspan(2);
            cell.setPhrase(new Phrase(user.getAddress().getStreet_address()));
            table.addCell(cell);
            cell.setColspan(1);
            cell.addElement(new Phrase(user.getAddress().getZipcode()));
            table.addCell(cell);
            PdfPCell cella = new PdfPCell();
            cella.setFixedHeight(25f);
            cella.addElement(new Phrase(user.getAddress().getCity()));
            table.addCell(cella);
        }
        if (user.getContact_info().getVisible()) {
            cell.setColspan(2);
            cell.setPhrase(new Phrase(user.getContact_info().getEmail()));
            table.addCell(cell);
            cell.setPhrase(new Phrase(user.getContact_info().getPhone()));
            table.addCell(cell);
        }
    }

    private void createBioTable(PdfPTable table) {
        PdfPCell cell = new PdfPCell();
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setPhrase(new Phrase(user.getBio().getValue()));
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

    private ArrayList<ArrayList<String>> getMiscData() {
        ArrayList<ArrayList<String>> listReturn = new ArrayList<>();
        List<DataType> listOfLists = user.getMisc().getData();
        for (int i = 0; i < listOfLists.size(); i++) {
            DataType list = listOfLists.get(i);
            ArrayList<String> valueList = new ArrayList<>();
            if (list instanceof Misc) {
                Misc obj = (Misc) list;
                if (obj.isVisible()) {
                    String name = obj.getName();
                    String value = obj.getValue();
                    valueList.add(value);
                    valueList.add(name);
                }
            }
            listReturn.add(valueList);
        }
        return listReturn;
    }

    private ArrayList<ArrayList<String>> getExperienceData() {
        ArrayList<ArrayList<String>> listReturn = new ArrayList<>();
        List<DataType> listOfLists = user.getExperience().getData();
        for (int i = 0; i < listOfLists.size(); i++) {
            DataType list = listOfLists.get(i);
            ArrayList<String> valueList = new ArrayList<>();
            if (list instanceof Experience) {
                Experience obj = (Experience) list;
                if (obj.isVisible()) {
                    String name = obj.getName();
                    String title = obj.getTitle();
                    String description = obj.getDescription();
                    String startDate = obj.getStartdate().toString();
                    String endDate = obj.getEnddate().toString();
                    valueList.add(name);
                    valueList.add(title);
                    valueList.add(description);
                    valueList.add("Start Date: " + startDate);
                    valueList.add("End Date: " + endDate);
                }
            }
            listReturn.add(valueList);
        }
        return listReturn;
    }

    private ArrayList<ArrayList<String>> getEducationData() {

        ArrayList<ArrayList<String>> listReturn = new ArrayList<>();
        List<DataType> listOfLists = user.getEducation().getData();
        for (int i = 0; i < listOfLists.size(); i++) {
            DataType list = listOfLists.get(i);
            ArrayList<String> valueList = new ArrayList<>();
            if (list instanceof Education) {
                Education obj = (Education) list;
                if (obj.isVisible()) {
                    String schoolName = obj.getSchool_name();
                    String schoolType = obj.getSchool_type();
                    String fieldName = obj.getField_name();
                    String startDate = obj.getStartdate().toString();
                    String endDate = obj.getEnddate().toString();
                    String grade = String.valueOf(obj.getGrade());
                    valueList.add(schoolName);
                    valueList.add(schoolType);
                    valueList.add(fieldName);
                    valueList.add("Grade: " + grade);
                    valueList.add("Start Date: " + startDate);
                    valueList.add("End Date: " + endDate);
                }
            } else if (list instanceof Course) {
                Course obj = (Course) list;
                if (obj.isVisible()) {
                    String courseName = obj.getCourse_name();
                    String providerName = obj.getProvider_name();
                    String grade = String.valueOf(obj.getGrade());
                    String startDate = obj.getStartdate().toString();
                    String endDate = obj.getEnddate().toString();
                    valueList.add(courseName);
                    valueList.add(providerName);
                    valueList.add("Grade: " + grade);
                    valueList.add("Start Date: " + startDate);
                    valueList.add("End Date: " + endDate);
                }
            }
            listReturn.add(valueList);
        }
        return listReturn;
    }

    private ArrayList<ArrayList<String>> getProjectsData() {
        ArrayList<ArrayList<String>> listReturn = new ArrayList<>();
        List<DataType> listOfLists = user.getProjects().getData();
        for (int i = 0; i < listOfLists.size(); i++) {
            DataType list = listOfLists.get(i);
            ArrayList<String> valueList = new ArrayList<>();
            if (list instanceof Project) {
                Project obj = (Project) list;
                    if (obj.isVisible()) {
                        String name = obj.getName();
                        String description = obj.getDescription();
                        String completionDate = obj.getCompletion_date().toString();
                        valueList.add(name);
                        valueList.add(description);
                        valueList.add("Completion Date: " + completionDate);
                    }
            }
            listReturn.add(valueList);
        }
        return listReturn;
    }

    private ArrayList<ArrayList<String>> getTitlesData() {
        ArrayList<ArrayList<String>> listReturn = new ArrayList<>();
        List<DataType> listOfLists = user.getTitles().getData();
        for (int i = 0; i < listOfLists.size(); i++) {
            DataType list = listOfLists.get(i);
            ArrayList<String> valueList = new ArrayList<>();
            if (list instanceof Title) {
                Title obj = (Title) list;
                    if (obj.isVisible()) {
                        String title = obj.getTitle();
                        String awarded = obj.getAwarded().toString();
                        valueList.add(title);
                        valueList.add("Awarding Date: " + awarded);
                    }
                }

            listReturn.add(valueList);
        }
        return listReturn;
    }

    private ArrayList<ArrayList<String>> getReferencesData() {
        ArrayList<ArrayList<String>> listReturn = new ArrayList<>();
        List<DataType> listOfLists = user.getReferences().getData();
        for (int i = 0; i < listOfLists.size(); i++) {
            DataType list = listOfLists.get(i);
            ArrayList<String> valueList = new ArrayList<>();
            if (list instanceof Person) {
                Person obj = (Person) list;
                    if (obj.isVisible()) {
                        String name = obj.getName();
                        String contactEmail = obj.getContact_email();
                        String contactPhone = obj.getContact_phone();
                        valueList.add(name);
                        valueList.add(contactEmail);
                        valueList.add(contactPhone);
                    }
            }
            listReturn.add(valueList);
        }
        return listReturn;
    }
}
