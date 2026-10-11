<?xml version="1.0" encoding="UTF-8"?>
<!--
    Faculty Reference Example: XSLT Stylesheet (3_5_XML-JCM-2.pptx)
    Transforms student.xml into styled HTML table
-->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:output method="html" indent="yes" encoding="UTF-8"/>
    <xsl:template match="/">
        <html>
            <head>
                <title>Student Records - Faculty XML/XSLT Demo</title>
                <style>
                    body {
                        font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
                        background: #0a0f1e;
                        color: #f8fafc;
                        padding: 30px;
                        margin: 0;
                    }
                    .container {
                        max-width: 800px;
                        margin: 0 auto;
                        background: rgba(255, 255, 255, 0.05);
                        border: 1px solid rgba(255, 255, 255, 0.1);
                        border-radius: 12px;
                        padding: 24px;
                        box-shadow: 0 8px 32px rgba(0, 0, 0, 0.4);
                    }
                    h2 {
                        color: #6366f1;
                        margin-top: 0;
                        border-bottom: 2px solid rgba(99, 102, 241, 0.3);
                        padding-bottom: 10px;
                    }
                    p.sub {
                        color: #94a3b8;
                        font-size: 0.9rem;
                    }
                    table {
                        width: 100%;
                        border-collapse: collapse;
                        margin-top: 18px;
                    }
                    th {
                        background: rgba(99, 102, 241, 0.25);
                        color: #38bdf8;
                        padding: 12px 14px;
                        text-align: left;
                        border: 1px solid rgba(255, 255, 255, 0.1);
                        font-size: 0.95rem;
                    }
                    td {
                        padding: 10px 14px;
                        border: 1px solid rgba(255, 255, 255, 0.08);
                        font-size: 0.9rem;
                    }
                    tr:nth-child(even) {
                        background: rgba(255, 255, 255, 0.02);
                    }
                    tr:hover {
                        background: rgba(99, 102, 241, 0.1);
                    }
                    .badge {
                        display: inline-block;
                        padding: 3px 8px;
                        border-radius: 12px;
                        background: rgba(6, 182, 212, 0.2);
                        color: #06b6d4;
                        font-size: 0.8rem;
                        font-weight: 600;
                    }
                </style>
            </head>
            <body>
                <div class="container">
                    <h2>Student Records (Rendered via XSLT)</h2>
                    <p class="sub">Transformed from <code>student.xml</code> using <code>student.xsl</code> matching faculty slide specification.</p>
                    <table>
                        <thead>
                            <tr>
                                <th>Name</th>
                                <th>Roll No</th>
                                <th>Course</th>
                                <th>Age</th>
                            </tr>
                        </thead>
                        <tbody>
                            <xsl:for-each select="students/student">
                                <tr>
                                    <td><strong><xsl:value-of select="name"/></strong></td>
                                    <td><xsl:value-of select="rollno"/></td>
                                    <td><span class="badge"><xsl:value-of select="course"/></span></td>
                                    <td><xsl:value-of select="age"/></td>
                                </tr>
                            </xsl:for-each>
                        </tbody>
                    </table>
                </div>
            </body>
        </html>
    </xsl:template>
</xsl:stylesheet>
