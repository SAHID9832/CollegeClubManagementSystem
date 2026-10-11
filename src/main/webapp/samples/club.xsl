<?xml version="1.0" encoding="UTF-8"?>
<!--
    College Club Management System - Club Catalog XSLT Stylesheet
    Transforms club.xml into modern glassmorphism web cards
-->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:output method="html" indent="yes" encoding="UTF-8"/>
    <xsl:template match="/">
        <html>
            <head>
                <title>College Clubs Directory - XSLT Catalog</title>
                <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
                <style>
                    * { box-sizing: border-box; margin: 0; padding: 0; }
                    body {
                        font-family: 'Segoe UI', system-ui, -apple-system, sans-serif;
                        background: #0a0f1e;
                        color: #f8fafc;
                        padding: 32px 20px;
                        line-height: 1.6;
                        background-image: radial-gradient(circle at 20% 20%, rgba(99, 102, 241, 0.15) 0%, transparent 40%),
                                          radial-gradient(circle at 80% 80%, rgba(6, 182, 212, 0.12) 0%, transparent 40%);
                        background-attachment: fixed;
                    }
                    .container {
                        max-width: 1100px;
                        margin: 0 auto;
                    }
                    .header {
                        text-align: center;
                        margin-bottom: 36px;
                    }
                    .badge-top {
                        display: inline-block;
                        padding: 4px 12px;
                        background: rgba(99, 102, 241, 0.2);
                        color: #818cf8;
                        border-radius: 20px;
                        font-size: 0.85rem;
                        font-weight: 600;
                        margin-bottom: 12px;
                        border: 1px solid rgba(99, 102, 241, 0.3);
                    }
                    h1 {
                        font-size: 2.2rem;
                        color: #ffffff;
                        margin-bottom: 8px;
                    }
                    p.subtitle {
                        color: #94a3b8;
                        font-size: 1rem;
                    }
                    .grid {
                        display: grid;
                        grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
                        gap: 24px;
                    }
                    .card {
                        background: rgba(255, 255, 255, 0.05);
                        border: 1px solid rgba(255, 255, 255, 0.1);
                        border-radius: 16px;
                        padding: 24px;
                        backdrop-filter: blur(16px);
                        transition: transform 0.2s ease, border-color 0.2s ease;
                    }
                    .card:hover {
                        transform: translateY(-4px);
                        border-color: rgba(99, 102, 241, 0.4);
                        box-shadow: 0 12px 32px rgba(0, 0, 0, 0.4);
                    }
                    .card-header {
                        display: flex;
                        justify-content: space-between;
                        align-items: center;
                        margin-bottom: 14px;
                    }
                    .club-name {
                        font-size: 1.3rem;
                        font-weight: 700;
                        color: #f1f5f9;
                    }
                    .category-tag {
                        font-size: 0.75rem;
                        font-weight: 600;
                        padding: 4px 10px;
                        border-radius: 12px;
                        text-transform: uppercase;
                        letter-spacing: 0.5px;
                    }
                    .tag-Technical { background: rgba(99, 102, 241, 0.25); color: #a5b4fc; }
                    .tag-Cultural { background: rgba(236, 72, 153, 0.25); color: #f472b6; }
                    .tag-Literary { background: rgba(245, 158, 11, 0.25); color: #fbbf24; }
                    .tag-Sports { background: rgba(16, 185, 129, 0.25); color: #34d399; }
                    .desc {
                        color: #94a3b8;
                        font-size: 0.92rem;
                        margin-bottom: 18px;
                        min-height: 48px;
                    }
                    .meta-row {
                        display: flex;
                        justify-content: space-between;
                        padding-top: 14px;
                        border-top: 1px solid rgba(255, 255, 255, 0.08);
                        font-size: 0.85rem;
                        color: #cbd5e1;
                    }
                    .coordinator {
                        color: #38bdf8;
                    }
                    .nav-back {
                        text-align: center;
                        margin-top: 36px;
                    }
                    .nav-back a {
                        color: #818cf8;
                        text-decoration: none;
                        font-weight: 600;
                    }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <span class="badge-top">XML + XSLT Dynamic Rendering</span>
                        <h1>College Club Directory</h1>
                        <p class="subtitle">Data extracted from <code>club.xml</code> transformed via <code>club.xsl</code></p>
                    </div>

                    <div class="grid">
                        <xsl:for-each select="clubs/club">
                            <div class="card">
                                <div class="card-header">
                                    <span class="club-name"><xsl:value-of select="name"/></span>
                                    <span>
                                        <xsl:attribute name="class">category-tag tag-<xsl:value-of select="@category"/></xsl:attribute>
                                        <xsl:value-of select="@category"/>
                                    </span>
                                </div>
                                <p class="desc"><xsl:value-of select="description"/></p>
                                <div class="meta-row">
                                    <div>
                                        <strong>Coordinator: </strong>
                                        <span class="coordinator"><xsl:value-of select="coordinator/name"/></span>
                                    </div>
                                    <div>
                                        <strong>Members: </strong>
                                        <span><xsl:value-of select="memberCount"/> / <xsl:value-of select="@capacity"/></span>
                                    </div>
                                </div>
                            </div>
                        </xsl:for-each>
                    </div>

                    <div class="nav-back">
                        <p><a href="../index.html">← Back to Campus Portal</a></p>
                    </div>
                </div>
            </body>
        </html>
    </xsl:template>
</xsl:stylesheet>
