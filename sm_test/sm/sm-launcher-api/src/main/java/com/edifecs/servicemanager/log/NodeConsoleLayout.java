package com.edifecs.servicemanager.log;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Layout;
import org.apache.log4j.Priority;
import org.apache.log4j.spi.LoggingEvent;
import org.fusesource.jansi.Ansi;
import org.fusesource.jansi.AnsiConsole;
import org.fusesource.jansi.Ansi.Color;

import com.edifecs.core.configuration.helper.SystemVariables;

public class NodeConsoleLayout extends Layout {

    private static final boolean COLOR =
            System.getProperty(SystemVariables.COLOR_DISABLED_KEY) == null;
    
    private static final Pattern STACK_TRACE_PATTERN = Pattern.compile(
            "(\\s+at [\\w\\$.<>]+)\\(([\\w\\$.]+):(\\d+)\\)");
    
    static {
        try {
            if (COLOR) {
                // Only the Agent should install the ANSI console.
                if (System.getProperty(SystemVariables.NODE_NAME_KEY) == null)
                    AnsiConsole.systemInstall();
            } else {
                Ansi.setEnabled(false);
            }
        } catch (Throwable ex) {
            System.err.println("Failed to set up ANSI colors. Console output"
                + " may be corrupted.");
        }
    }
    
    private static final SimpleDateFormat TIME_FORMAT =
            new SimpleDateFormat("HH:mm:ss");
    
    public static String nodeName = null;
    
    private String[] lastStackTrace;

    @Override public void activateOptions() {}

    @Override public String format(LoggingEvent evt) {
        Ansi ansi = Ansi.ansi();
        // Part 1: Level
        switch (evt.getLevel().toInt()) {
        case Priority.FATAL_INT:
            ansi.fg(Color.RED).a("!! FATAL !! ").reset();
            break;
        case Priority.ERROR_INT:
            ansi.fg(Color.RED).a("[E] ").reset();
            break;
        case Priority.WARN_INT:
            ansi.fg(Color.YELLOW).a("[W] ").reset();
            break;
        case Priority.INFO_INT:
            ansi.fg(Color.CYAN).a("[I] ").reset();
            break;
        case Priority.DEBUG_INT:
            ansi.fg(Color.GREEN).a("[D] ").reset();
            break;
        default:
            ansi.fg(Color.MAGENTA).a("[?] ").reset();
            break;
        }
        // Part 2: Time
        ansi.bold().fg(Color.WHITE)
            .a(TIME_FORMAT.format(new Date(evt.getTimeStamp())))
            .a(" ").reset();
        // Part 3: Node name
        if (nodeName != null)
            ansi.fg(Color.CYAN).a(nodeName).a(" > ").reset();
        // Part 4: Message
        ansi.a(evt.getMessage());
        // Part 5: Stack trace
        if (evt.getThrowableInformation() != null) {
            Color exceptionColor = Color.DEFAULT;
            switch (evt.getLevel().toInt()) {
            case Priority.FATAL_INT:
            case Priority.ERROR_INT:
                exceptionColor = Color.RED;
                break;
            case Priority.WARN_INT:
                exceptionColor = Color.YELLOW;
                break;
            }
            if (Arrays.equals(evt.getThrowableInformation().getThrowableStrRep(),
                              lastStackTrace)) {
                ansi.fg(exceptionColor).a(" (Same stack trace)");
            } else {
                lastStackTrace = 
                    evt.getThrowableInformation().getThrowableStrRep();
                for (String str : lastStackTrace) {
                    final Matcher matcher = STACK_TRACE_PATTERN.matcher(str);
                    if (matcher.matches()) {
                        ansi.newline().fg(exceptionColor).a(matcher.group(1))
                            .a("(").fg(Color.CYAN).a(matcher.group(2))
                            .fg(Color.DEFAULT).a(":").fg(Color.GREEN)
                            .a(matcher.group(3)).fg(exceptionColor).a(")");
                    } else {
                        ansi.newline().fg(exceptionColor).a(str);
                    }
                }
            }
        }
        ansi.reset().newline();
        return ansi.toString();
    }

    @Override public boolean ignoresThrowable() {
        return false;
    }
}
