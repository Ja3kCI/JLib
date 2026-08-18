package me.jackwci.jlib.commands.internal;

import me.jackwci.jlib.commands.annotations.Command;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class RegisteredCommand {

    private final Object instance;
    private final Command annotation;
    private Method executeMethod;
    private final Map<String,Method> subcommands = new HashMap<>();

    public RegisteredCommand(Object instance, Command annotation) {
        this.instance = instance;
        this.annotation = annotation;
    }

    public Object instance() {
        return instance;
    }

    public Command annotation() {
        return annotation;
    }

    public Method executeMethod() {
        return executeMethod;
    }

    public void executeMethod(Method method) {
        this.executeMethod = method;
    }

    public Map<String, Method> subcommands() {
        return subcommands;
    }

    public void addSubcommand(String name, Method method) {
        subcommands.put(name.toLowerCase(), method);
    }


}
