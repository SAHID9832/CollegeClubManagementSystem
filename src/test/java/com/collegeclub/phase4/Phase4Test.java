package com.collegeclub.phase4;

import com.collegeclub.exception.InvalidRequestException;
import com.collegeclub.model.Club;
import com.collegeclub.model.Student;
import com.collegeclub.util.XmlUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Phase 4 - Server-Side Integration & XML Processing Tests")
class Phase4Test {

    @Test
    @DisplayName("Should parse faculty student XML correctly matching lecture slide format")
    void testParseFacultyStudentXml() throws Exception {
        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<student>\n" +
                "  <name>Arun Kumar</name>\n" +
                "  <rollno>26MCA001</rollno>\n" +
                "  <course>MCA</course>\n" +
                "  <age>22</age>\n" +
                "</student>";

        Map<String, String> data = XmlUtil.parseFacultyStudentXml(xml);

        assertNotNull(data);
        assertEquals("Arun Kumar", data.get("name"));
        assertEquals("26MCA001", data.get("rollno"));
        assertEquals("MCA", data.get("course"));
        assertEquals("22", data.get("age"));
    }

    @Test
    @DisplayName("Should serialize clubs to XML matching catalog specification")
    void testClubsToXml() {
        Club c = new Club(1, "Coding Club", "Description", "Technical", "Dr. Arul", 50);
        List<Club> clubs = Collections.singletonList(c);

        String xml = XmlUtil.clubsToXml(clubs);

        assertNotNull(xml);
        assertTrue(xml.contains("<clubs>"));
        assertTrue(xml.contains("<club"));
        assertTrue(xml.contains("id=\"1\""));
        assertTrue(xml.contains("<name>Coding Club</name>"));
        assertTrue(xml.contains("<coordinator>"));
        assertTrue(xml.contains("<name>Dr. Arul</name>"));
    }

    @Test
    @DisplayName("Should serialize student to XML matching student entity specification")
    void testStudentToXml() {
        Student s = new Student(101, "arun", "hash123", "arun@college.edu", "Arun Kumar", "26MCA001", "Computer Applications", 2);

        String xml = XmlUtil.studentToXml(s);

        assertNotNull(xml);
        assertTrue(xml.contains("<student>"));
        assertTrue(xml.contains("<name>Arun Kumar</name>"));
        assertTrue(xml.contains("<rollno>26MCA001</rollno>"));
        assertTrue(xml.contains("<course>MCA</course>"));
    }

    @Test
    @DisplayName("Should reject malicious XXE doctype declarations")
    void testXxeProtection() {
        String xxePayload = "<?xml version=\"1.0\"?>\n" +
                "<!DOCTYPE foo [<!ENTITY xxe SYSTEM \"file:///etc/passwd\">]>\n" +
                "<student><name>&xxe;</name><rollno>1</rollno><course>MCA</course><age>20</age></student>";

        assertThrows(InvalidRequestException.class, () -> {
            XmlUtil.parseXml(xxePayload);
        });
    }

    @Test
    @DisplayName("Should reject malformed XML with unclosed tags")
    void testMalformedXmlRejection() {
        String malformed = "<student><name>Unclosed";
        assertThrows(InvalidRequestException.class, () -> {
            XmlUtil.parseXml(malformed);
        });
    }

    @Test
    @DisplayName("Should reject empty XML input")
    void testEmptyXmlRejection() {
        assertThrows(InvalidRequestException.class, () -> {
            XmlUtil.parseXml("");
        });
        assertThrows(InvalidRequestException.class, () -> {
            XmlUtil.parseXml("   ");
        });
    }
}
