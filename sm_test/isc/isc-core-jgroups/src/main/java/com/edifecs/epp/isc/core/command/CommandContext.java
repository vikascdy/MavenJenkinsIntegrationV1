package com.edifecs.epp.isc.core.command;

import com.edifecs.epp.isc.command.CommandMessage;

import scala.collection.JavaConverters;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * This is a hack to allow for dynamic access to command parameters. This needs to be removed and replaces with a more
 * supported implementation.
 */
@Deprecated
public final class CommandContext {
    private static ThreadLocal<CommandContext> current = new ThreadLocal<CommandContext>() {
        @Override
        protected CommandContext initialValue() {
            return new CommandContext();
        }
    };

    public static CommandContext current() {
        return current.get();
    }

    // stores the arguments <-> command map
    private Map<String, Map<String, Serializable>> argumentsMap = new HashMap<>();

    public void setArguments(String commandName, Map<String, Serializable> arguments) {
        argumentsMap.put(commandName, arguments);
    }

    public void extractArguments(CommandMessage command) {
        Map<String, ? extends Serializable> values = JavaConverters.asJavaMapConverter(command.args()).asJava();
        String commandName = command.name();
        Map<String, Serializable> args = getArgumentsForCommand(commandName);
        argumentsMap.put(commandName, args);
        args.clear();
        if(values!=null) {
            args.putAll(values);
        }
    }

    public Serializable getArgument(String commandName, String key) {
        return getArgumentsForCommand(commandName).get(key);
    }

    public Serializable consumeArgument(String commandName, String key) {
        return getArgumentsForCommand(commandName).remove(key);
    }

    private Map<String, Serializable> getArgumentsForCommand(String commandName) {
        Map<String, Serializable> returns = argumentsMap.get(commandName);
        return returns==null? new HashMap<String, Serializable>() : returns;
    }

    public void clear() {
        argumentsMap.clear();
    }
}
