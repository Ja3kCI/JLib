package me.jackwci.jlib.commands;

import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import me.jackwci.jlib.commands.annotations.Command;
import me.jackwci.jlib.commands.annotations.Execute;
import me.jackwci.jlib.commands.annotations.Subcommand;
import me.jackwci.jlib.commands.internal.CommandScanner;
import me.jackwci.jlib.commands.internal.RegisteredCommand;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class Commands {

    private Commands() {
    }

    public static void registerPackage(
            JavaPlugin plugin,
            String packageName
    ) {

        List<RegisteredCommand> commands =
                discoverCommands(
                        plugin,
                        packageName
                );

        plugin.getLifecycleManager()
                .registerEventHandler(
                        LifecycleEvents.COMMANDS,
                        event -> {

                            for (
                                    RegisteredCommand command :
                                    commands
                            ) {

                                Command annotation =
                                        command.annotation();

                                JCommand paperCommand =
                                        new JCommand(command);

                                event.registrar().register(
                                        annotation.value(),
                                        annotation.description(),
                                        Arrays.asList(
                                                annotation.aliases()
                                        ),
                                        paperCommand
                                );
                            }

                        }
                );

        plugin.getLogger().info(
                "JLib registered "
                        + commands.size()
                        + " commands."
        );
    }

    private static List<RegisteredCommand>
    discoverCommands(
            JavaPlugin plugin,
            String packageName
    ) {

        List<Class<?>> commandClasses =
                CommandScanner.scan(
                        plugin.getClass()
                                .getClassLoader(),
                        packageName
                );

        List<RegisteredCommand> commands =
                new ArrayList<>();

        for (Class<?> commandClass : commandClasses) {

            Command annotation =
                    commandClass.getAnnotation(
                            Command.class
                    );

            Object instance =
                    instantiate(commandClass);

            RegisteredCommand registered =
                    new RegisteredCommand(
                            instance,
                            annotation
                    );

            inspectMethods(
                    commandClass,
                    registered
            );

            commands.add(registered);
        }

        return commands;
    }

    private static Object instantiate(
            Class<?> commandClass
    ) {

        try {

            Constructor<?> constructor =
                    commandClass
                            .getDeclaredConstructor();

            constructor.setAccessible(true);

            return constructor.newInstance();

        } catch (Exception exception) {

            throw new IllegalStateException(
                    "Command "
                            + commandClass.getName()
                            + " must have a no-argument constructor.",
                    exception
            );
        }
    }

    private static void inspectMethods(
            Class<?> commandClass,
            RegisteredCommand registered
    ) {

        for (
                Method method :
                commandClass.getDeclaredMethods()
        ) {

            if (
                    method.isAnnotationPresent(
                            Execute.class
                    )
            ) {

                if (
                        registered.executeMethod()
                                != null
                ) {

                    throw new IllegalStateException(
                            commandClass.getName()
                                    + " has multiple @Execute methods."
                    );
                }

                method.setAccessible(true);

                registered.executeMethod(method);
            }

            if (
                    method.isAnnotationPresent(
                            Subcommand.class
                    )
            ) {

                Subcommand annotation =
                        method.getAnnotation(
                                Subcommand.class
                        );

                method.setAccessible(true);

                registered.addSubcommand(
                        annotation.value(),
                        method
                );
            }
        }
    }
}