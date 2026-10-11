package com.collegeclub.util;

import com.collegeclub.exception.InvalidRequestException;
import com.collegeclub.model.Club;
import com.collegeclub.model.Student;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Enterprise Secure XML Processing Utility
 * Implements W3C DOM parsing and generation with complete XXE (XML External Entity)
 * injection protection as required by syllabus security specifications.
 */
public final class XmlUtil {

    private XmlUtil() {}

    /**
     * Creates a hardened, XXE-safe DocumentBuilderFactory.
     */
    public static DocumentBuilderFactory createSecureDocumentBuilderFactory() throws ParserConfigurationException {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);

        try {
            // Disallow inline DOCTYPE declarations to prevent DTD entity expansion
            dbf.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
            // Disable external general entities
            dbf.setFeature("http://xml.org/sax/features/external-general-entities", false);
            // Disable external parameter entities
            dbf.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
            // Disable loading external DTDs
            dbf.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
        } catch (ParserConfigurationException ignored) {
            // Fallback for parser engines that don't recognize specific feature names
        }

        dbf.setXIncludeAware(false);
        dbf.setExpandEntityReferences(false);
        try {
            dbf.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
        } catch (ParserConfigurationException ignored) {}

        return dbf;
    }

    /**
     * Parses an XML string into a DOM Document using safe settings.
     */
    public static Document parseXml(String xmlString) throws InvalidRequestException {
        if (xmlString == null || xmlString.trim().isEmpty()) {
            throw new InvalidRequestException("XML payload cannot be empty");
        }
        try {
            DocumentBuilderFactory dbf = createSecureDocumentBuilderFactory();
            DocumentBuilder db = dbf.newDocumentBuilder();
            return db.parse(new InputSource(new StringReader(xmlString.trim())));
        } catch (Exception e) {
            throw new InvalidRequestException("Failed to parse XML: " + e.getMessage(), e);
        }
    }

    /**
     * Extracts text content of the first element matching tagName under parent.
     */
    public static String getChildText(Element parent, String tagName) {
        NodeList list = parent.getElementsByTagName(tagName);
        if (list.getLength() > 0 && list.item(0).getTextContent() != null) {
            return list.item(0).getTextContent().trim();
        }
        return "";
    }

    /**
     * Parses the faculty slide <student> XML structure.
     * <student><name>...</name><rollno>...</rollno><course>...</course><age>...</age></student>
     */
    public static Map<String, String> parseFacultyStudentXml(String xmlString) throws InvalidRequestException {
        Document doc = parseXml(xmlString);
        Element root = doc.getDocumentElement();
        if (!"student".equalsIgnoreCase(root.getNodeName())) {
            throw new InvalidRequestException("Root element must be <student>");
        }

        Map<String, String> data = new HashMap<>();
        data.put("name", getChildText(root, "name"));
        data.put("rollno", getChildText(root, "rollno"));
        data.put("course", getChildText(root, "course"));
        data.put("age", getChildText(root, "age"));
        return data;
    }

    /**
     * Serializes a list of clubs into XML format matching the faculty sample catalog.
     */
    public static String clubsToXml(List<Club> clubs) {
        try {
            DocumentBuilderFactory dbf = createSecureDocumentBuilderFactory();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.newDocument();

            Element root = doc.createElement("clubs");
            doc.appendChild(root);

            for (Club club : clubs) {
                Element clubEl = doc.createElement("club");
                clubEl.setAttribute("id", String.valueOf(club.getClubId()));
                clubEl.setAttribute("category", club.getCategory() != null ? club.getCategory() : "General");
                clubEl.setAttribute("capacity", String.valueOf(club.getCapacity()));
                clubEl.setAttribute("active", String.valueOf(club.isActive()));

                Element nameEl = doc.createElement("name");
                nameEl.setTextContent(club.getClubName() != null ? club.getClubName() : "");
                clubEl.appendChild(nameEl);

                Element descEl = doc.createElement("description");
                descEl.setTextContent(club.getDescription() != null ? club.getDescription() : "");
                clubEl.appendChild(descEl);

                Element coordEl = doc.createElement("coordinator");
                Element coordName = doc.createElement("name");
                coordName.setTextContent(club.getCoordinatorName() != null ? club.getCoordinatorName() : "Faculty Coordinator");
                coordEl.appendChild(coordName);
                Element coordEmail = doc.createElement("email");
                coordEmail.setTextContent(club.getCoordinatorEmail() != null ? club.getCoordinatorEmail() : "");
                coordEl.appendChild(coordEmail);
                clubEl.appendChild(coordEl);

                Element memberCount = doc.createElement("memberCount");
                memberCount.setTextContent(String.valueOf(club.getMemberCount()));
                clubEl.appendChild(memberCount);

                root.appendChild(clubEl);
            }

            return transformToString(doc);
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate clubs XML: " + e.getMessage(), e);
        }
    }

    /**
     * Serializes a student entity into XML matching the faculty slide format.
     */
    public static String studentToXml(Student student) {
        try {
            DocumentBuilderFactory dbf = createSecureDocumentBuilderFactory();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.newDocument();

            Element root = doc.createElement("student");
            doc.appendChild(root);

            Element name = doc.createElement("name");
            name.setTextContent(student.getFullName() != null ? student.getFullName() : student.getUsername());
            root.appendChild(name);

            Element rollno = doc.createElement("rollno");
            rollno.setTextContent(student.getRollNumber() != null ? student.getRollNumber() : "");
            root.appendChild(rollno);

            Element course = doc.createElement("course");
            course.setTextContent(student.getCourse() != null ? student.getCourse() : "MCA");
            root.appendChild(course);

            Element department = doc.createElement("department");
            department.setTextContent(student.getDepartment() != null ? student.getDepartment() : "");
            root.appendChild(department);

            Element year = doc.createElement("year");
            year.setTextContent(String.valueOf(student.getYear()));
            root.appendChild(year);

            return transformToString(doc);
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate student XML: " + e.getMessage(), e);
        }
    }

    /**
     * Transforms a DOM Document to formatted XML string using Transformer.
     */
    public static String transformToString(Document doc) {
        try {
            TransformerFactory tf = TransformerFactory.newInstance();
            try {
                tf.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
            } catch (Exception ignored) {}

            Transformer transformer = tf.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");

            StringWriter writer = new StringWriter();
            transformer.transform(new DOMSource(doc), new StreamResult(writer));
            return writer.toString();
        } catch (Exception e) {
            throw new RuntimeException("Transformer exception: " + e.getMessage(), e);
        }
    }
}
