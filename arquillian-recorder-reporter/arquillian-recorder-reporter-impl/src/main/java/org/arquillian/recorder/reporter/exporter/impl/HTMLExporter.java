/*
 * JBoss, Home of Professional Open Source
 * Copyright 2014, Red Hat, Inc. and/or its affiliates, and individual
 * contributors by the @authors tag. See the copyright.txt in the
 * distribution for a full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.arquillian.recorder.reporter.exporter.impl;

import java.io.File;
import java.io.InputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.util.JAXBSource;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.arquillian.extension.recorder.Configuration;
import org.arquillian.recorder.reporter.Exporter;
import org.arquillian.recorder.reporter.ReportType;
import org.arquillian.recorder.reporter.Reportable;
import org.arquillian.recorder.reporter.configuration.ReporterConfiguration;
import org.arquillian.recorder.reporter.impl.type.HTMLReport;

/**
 * Exports reports to HTML file according to XSLT transformation. Template can be set in configuration.
 *
 * @see {@link HTMLReport}
 *
 * @author <a href="smikloso@redhat.com">Stefan Miklosovic</a>
 *
 */
public class HTMLExporter implements Exporter {

    private static final String DEFAULT_XSL_TEMPLATE = "arquillian_reporter_template.xsl";

    private ReporterConfiguration configuration;

    private JAXBContext context;

    private TransformerFactory transformerFactory;

    public HTMLExporter(JAXBContext context) {
        this.context = context;
        this.transformerFactory = TransformerFactory.newInstance();
    }

    @Override
    public File export(Reportable report) throws Exception {

        StreamSource xslt;

        if (configuration.getTemplate().exists()) {
            xslt = new StreamSource(configuration.getTemplate());
        } else {
            InputStream is = getClass().getClassLoader().getResourceAsStream(DEFAULT_XSL_TEMPLATE);
            if (is == null) {
                throw new IllegalStateException("Unable to load default " + DEFAULT_XSL_TEMPLATE);
            } else {
                xslt = new StreamSource(is);
            }
        }

        JAXBSource source = new JAXBSource(context, report);
        StreamResult result = new StreamResult(configuration.getFile());

        Transformer transformer = transformerFactory.newTransformer(xslt);
        transformer.transform(source, result);

        return configuration.getFile();
    }

    @Override
    public Class<? extends ReportType> getReportType() {
        return HTMLReport.class;
    }

    @Override
    public void setConfiguration(Configuration<?> configuration) {
        this.configuration = (ReporterConfiguration) configuration;
    }

    public void setContext(JAXBContext context) {
        this.context = context;
    }

}