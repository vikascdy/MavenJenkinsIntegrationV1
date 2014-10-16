package com.edifecs.servicemanager.log;

import java.io.File;

import org.apache.log4j.RollingFileAppender;

import com.edifecs.core.configuration.helper.SystemVariables;

public class NodeFileAppender extends RollingFileAppender {

    @Override public void setFile(String file) {
        String nodeName = System.getProperty(SystemVariables.NODE_NAME_KEY);
        if (nodeName == null) nodeName = "servicemanager";
        nodeName = nodeName.replaceAll("\\W", "_");
        super.setFile(file + File.separator + nodeName + ".log");
    }
}
