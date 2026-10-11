# Comprehensive Viva Voce Questions & Answers (25+ Q&A)

**Course:** 26MCA1L211 · Java and Web Programming Practical · MCA Semester I  
**Subject:** Java, Servlets, JSP, XML/XSLT, JDBC, Security & Web Architecture  

---

### Q1: What is a Java Servlet and what is its lifecycle?
**Answer:** A Servlet is a Java class running on a web container (like Apache Tomcat) that extends the capabilities of servers that host applications accessed by a request-response programming model.
Its lifecycle consists of three primary phases managed by the container:
1. **`init(ServletConfig config)`**: Invoked once when the servlet is first loaded into memory. Used for initialization.
2. **`service(HttpServletRequest, HttpServletResponse)`**: Invoked for every incoming client request. Dispatches to `doGet()`, `doPost()`, `doPut()`, `doDelete()`, etc., based on the HTTP method.
3. **`destroy()`**: Invoked once when the servlet container unloads or shuts down the servlet. Used for releasing resources.

---

### Q2: Why did we use the `jakarta.servlet.*` package instead of `javax.servlet.*`?
**Answer:** With the transition from Java EE 8 to Jakarta EE 9 and 10, the namespace changed from `javax.*` to `jakarta.*`. Apache Tomcat 10.1+ implements the Jakarta EE 10 specifications (Servlet 6.0, JSP 3.1). Since our environment uses Tomcat 10.1 and Java 17/21, we use `jakarta.servlet.*`. Tomcat 9 or older uses the legacy `javax.servlet.*`.

---

### Q3: How does `HttpSession` maintain user state in stateless HTTP?
**Answer:** HTTP is a stateless protocol. Tomcat uses `HttpSession` to preserve conversational state across requests.
When a session is created (`request.getSession(true)`), Tomcat generates a unique identifier called `JSESSIONID` and sends it back in the `Set-Cookie` HTTP header.
On subsequent requests, the browser sends the `JSESSIONID` cookie in the request header, allowing Tomcat to retrieve the corresponding session object from its internal session table.

---

### Q4: What is Session Fixation and how do we prevent it in this project?
**Answer:** Session Fixation occurs when an attacker tricks a victim into using a known Session ID before logging in, allowing the attacker to hijack the session once the victim authenticates.
We prevent this by invoking `request.changeSessionId()` immediately upon successful credential validation in `LoginServlet`. This retains the session attributes while assigning a brand new, unpredictable session identifier.

---

### Q5: How do we invalidate a session on user logout?
**Answer:** In `LogoutServlet`, we call `session.invalidate()`. This releases all attributes stored in the session, detaches the session from the container, and prevents any further requests from reusing that session identifier.

---

### Q6: Why is JSTL `<c:out>` preferred over JSP scriptlets `<%= ... %>`?
**Answer:**
1. **XSS Protection:** `<c:out value="${param}" />` automatically escapes HTML sensitive characters (`<`, `>`, `&`, `"`, `'`), converting them into HTML entities, preventing Cross-Site Scripting (XSS). Scriptlets output raw strings without escaping.
2. **Separation of Concerns:** Scriptlets mix Java business code with presentation markup, making code unmaintainable. JSTL and Expression Language (EL) keep JSPs clean and readable.

---

### Q7: What is the role of Servlet Filters in our project?
**Answer:** Filters intercept requests and responses dynamically before reaching servlets or after leaving servlets.
In our application, we configured a deterministic filter chain:
1. **`EncodingFilter`**: Sets request and response character encoding to UTF-8.
2. **`SecurityHeadersFilter`**: Injects OWASP defense headers (`X-Content-Type-Options: nosniff`, `X-Frame-Options: SAMEORIGIN`, `Cache-Control`).
3. **`CsrfFilter`**: Validates the `X-CSRF-Token` header for state-changing operations.
4. **`AuthFilter`**: Intercepts requests to `/dashboard.jsp`, `/admin.jsp`, `/profile.jsp` and redirects unauthenticated users to `/login.html`.

---

### Q8: How does `PreparedStatement` prevent SQL Injection attacks?
**Answer:** `PreparedStatement` uses parameterized queries. The SQL query structure is pre-compiled by the database engine first. User inputs supplied via `setString()`, `setInt()` are treated strictly as literal data values, never as executable SQL commands, making SQL injection impossible regardless of input content.

---

### Q9: Why is PBKDF2 used instead of MD5 or SHA-256 for password hashing?
**Answer:** MD5 and single-round SHA-256 are fast cryptographic hashes designed for checksums, making them vulnerable to brute-force and rainbow table attacks.
PBKDF2 (Password-Based Key Derivation Function 2):
1. Applies a HMAC function with a random **16-byte salt** (defeating rainbow tables).
2. Performs thousands of iterations (e.g. 600,000 rounds), intentionally consuming CPU time to make dictionary and brute-force attacks computationally infeasible.

---

### Q10: What is XML and what makes an XML document "Well-Formed"?
**Answer:** Extensible Markup Language (XML) is a W3C standard for storing and transporting structured data.
An XML document is **well-formed** if:
1. It has exactly one root element.
2. Every start tag has a matching end tag (or is self-closing).
3. Tags are case-sensitive and correctly nested.
4. Attribute values are enclosed in quotation marks.
5. Special characters are escaped (`&lt;`, `&gt;`, `&amp;`, `&apos;`, `&quot;`).

---

### Q11: What is the difference between a Well-Formed XML and a Valid XML?
**Answer:**
- **Well-Formed XML:** Satisfies the basic XML syntactic grammar rules (matching tags, single root, proper nesting).
- **Valid XML:** Is well-formed AND conforms to an explicit schema definition (DTD or XML Schema / XSD) defining allowed element names, types, child orders, and attributes.

---

### Q12: What is XSLT and how does it work?
**Answer:** Extensible Stylesheet Language Transformations (XSLT) is a language for transforming XML documents into other formats, such as HTML or plain text.
An XSL stylesheet (`.xsl`) defines templates (`<xsl:template match="...">`) that match nodes in the XML tree and output corresponding HTML markup using elements like `<xsl:for-each>` and `<xsl:value-of>`.
In the browser, linking `<?xml-stylesheet type="text/xsl" href="file.xsl"?>` triggers client-side transformation automatically.

---

### Q13: What is Form Navigation as demonstrated in our XML lecture slides?
**Answer:** Form Navigation is the process where client-side JavaScript captures user input from an HTML `<form>` using `event.preventDefault()`, formats the values into a structured XML string using template literals, and transmits it via HTTP `POST` to a server endpoint for processing without triggering a traditional browser form navigation/page refresh.

---

### Q14: What is XML External Entity (XXE) Injection and how did we prevent it?
**Answer:** XXE is a vulnerability where an XML parser processes untrusted XML containing a DOCTYPE with external entity references, allowing attackers to read server files (`file:///etc/passwd`) or trigger internal SSRF.
In `XmlUtil`, we harden `DocumentBuilderFactory` by disabling DOCTYPE declarations:
```java
dbf.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
dbf.setFeature("http://xml.org/sax/features/external-general-entities", false);
```

---

### Q15: Compare JSON vs XML for web data exchange.
**Answer:**
| Feature | JSON | XML |
|---|---|---|
| **Syntax** | Key-value, lightweight | Tag-based, verbose |
| **Data Types** | Numbers, strings, booleans, arrays, objects | Text strings only (requires schema for types) |
| **Parsing** | Native in JavaScript (`JSON.parse`) | Requires DOM / SAX parser |
| **Transformation** | Custom JavaScript | Standardized XSLT language |
| **Validation** | JSON Schema (optional) | Built-in DTD / XSD support |
| **Attributes** | Not supported | Supported (`<book id="1">`) |

---

### Q16: What is Cross-Site Request Forgery (CSRF) and how is it mitigated here?
**Answer:** CSRF tricks an authenticated user's browser into executing unwanted actions on a trusted site by exploiting ambient credentials (cookies).
We mitigate this using the **Synchronizer Token Pattern**:
1. When the user logs in, a cryptographically random CSRF token is stored in their session.
2. Client-side JavaScript sends this token in the `X-CSRF-Token` header on state-changing requests (`POST`, `PUT`, `DELETE`).
3. `CsrfFilter` rejects requests where the header does not match the session token.

---

### Q17: Why was "Confirm Password" removed from the project?
**Answer:**
1. Eliminates redundant client-side friction; modern authentication relies on password visibility toggles and real-time password strength meters instead.
2. Strictly matches the user's explicit design requirement while preserving strict server-side validation (minimum 8 characters with at least one digit).

---

### Q18: What is the DAO (Data Access Object) Pattern and why is it used?
**Answer:** The DAO pattern separates low-level data accessing operations (SQL queries, Connection management, ResultSets) from high-level business logic.
By defining interfaces (`UserDao`, `ClubDao`, `EventDao`), business services interact with DAOs without depending on whether the underlying storage is MySQL, H2, or another relational database.

---

### Q19: What is the purpose of `web.xml` in Jakarta EE?
**Answer:** `web.xml` is the Web Application Deployment Descriptor. It provides centralized configuration for:
1. Servlet and Filter mappings and ordering.
2. Session timeout intervals and cookie attributes (`http-only`).
3. Welcome files list (e.g. `index.html`).
4. Custom error pages for HTTP status codes (404, 500).

---

### Q20: What is the difference between `RequestDispatcher.forward()` and `HttpServletResponse.sendRedirect()`?
**Answer:**
- **`forward()`**: Server-side internal transfer. The request and response objects are preserved, the browser URL does not change, and only 1 network request occurs.
- **`sendRedirect()`**: Client-side redirect. The server sends HTTP 302 with a `Location` header, instructing the browser to make a brand-new `GET` request to the target URL. The browser URL changes and request attributes are not preserved.

---

### Q21: What is Connection Pooling and why is it important in production?
**Answer:** Establishing a new database connection for every HTTP request is resource-intensive due to TCP handshakes and authentication. A connection pool pre-allocates a pool of active database connections that servlets borrow and return, significantly increasing throughput and reducing database latency.

---

### Q22: What happens if JavaScript is disabled on the client?
**Answer:** Client-side validation in JavaScript is purely for user experience and responsiveness. Our architecture enforces identical validation rules on the server inside `RegisterServlet` and `UserService`. If client-side JavaScript is bypassed, the server rejects invalid inputs and prevents corrupt data from reaching the database.

---

### Q23: How does the application handle concurrent event registrations?
**Answer:** In `EventService.registerForEvent()`, the system counts current registrations against the maximum capacity before inserting a new record. In the database, a unique constraint `UNIQUE (event_id, user_id)` guarantees that no student can be registered twice, preventing double-booking even under concurrent submissions.

---

### Q24: What is DOM (Document Object Model) parsing in Java?
**Answer:** DOM parsing (via `DocumentBuilder`) loads the entire XML document into memory as a tree structure of nodes (Document, Element, Attr, Text). This allows random access, navigation (parents, children, siblings), and dynamic modifications, making it ideal for document transformation and generation.

---

### Q25: What is the purpose of the Cargo Maven Plugin in our build configuration?
**Answer:** The Cargo plugin downloads and launches a lightweight Apache Tomcat 10.1 container during development (`mvn cargo:run`). This allows any examiner or developer to run the web application out-of-the-box on Java 17/21 without needing a pre-installed standalone Tomcat server on their machine.
