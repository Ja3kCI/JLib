package me.jackwci.jlib.commands;

import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import me.jackwci.jlib.commands.annotations.Permission;
import me.jackwci.jlib.commands.annotations.PlayerOnly;
import me.jackwci.jlib.commands.internal.RegisteredCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class JCommand implements BasicCommand {

    private final RegisteredCommand command;

    public JCommand(RegisteredCommand command) {
        this.command = command;
    }

    @Override
    public void execute(
            CommandSourceStack source,
            String[] args
    ) {

        CommandSender sender =
                source.getSender();

        Class<?> commandClass =
                command.instance().getClass();

        if (
                commandClass.isAnnotationPresent(PlayerOnly.class)
                        && !(sender instanceof Player)
        ) {

            sender.sendRichMessage(
                    "<red>This command can only be used by players."
            );

            return;
        }

        Permission classPermission =
                commandClass.getAnnotation(
                        Permission.class
                );

        if (
                classPermission != null
                        && !sender.hasPermission(
                        classPermission.value()
                )
        ) {

            sender.sendRichMessage(
                    "<red>You do not have permission."
            );

            return;
        }

        /*
         * Check subcommands first
         */
        if (args.length > 0) {

            Method subcommand =
                    command
                            .subcommands()
                            .get(
                                    args[0].toLowerCase()
                            );

            if (subcommand != null) {

                String[] subArgs =
                        new String[
                                args.length - 1
                                ];

                System.arraycopy(
                        args,
                        1,
                        subArgs,
                        0,
                        subArgs.length
                );

                invoke(
                        subcommand,
                        sender,
                        subArgs
                );

                return;
            }
        }

        /*
         * Root command
         */
        if (command.executeMethod() == null) {

            sender.sendRichMessage(
                    "<red>Unknown subcommand."
            );

            return;
        }

        invoke(
                command.executeMethod(),
                sender,
                args
        );
    }

    private void invoke(
            Method method,
            CommandSender sender,
            String[] args
    ) {

        /*
         * Method-level PlayerOnly
         */
        if (
                method.isAnnotationPresent(PlayerOnly.class)
                        && !(sender instanceof Player)
        ) {

            sender.sendRichMessage(
                    "<red>This command can only be used by players."
            );

            return;
        }

        /*
         * Method-level permission
         */
        Permission permission =
                method.getAnnotation(
                        Permission.class
                );

        if (
                permission != null
                        && !sender.hasPermission(
                        permission.value()
                )
        ) {

            sender.sendRichMessage(
                    "<red>You do not have permission."
            );

            return;
        }

        CommandContext context =
                new CommandContext(
                        sender,
                        args
                );

        try {

            if (method.getParameterCount() == 0) {

                method.invoke(
                        command.instance()
                );

                return;
            }

            if (
                    method.getParameterCount() == 1
                            && method.getParameterTypes()[0]
                            == CommandContext.class
            ) {

                method.invoke(
                        command.instance(),
                        context
                );

                return;
            }

            throw new IllegalStateException(
                    "Command method "
                            + method.getName()
                            + " must have no parameters or one CommandContext parameter."
            );

        } catch (
                IllegalAccessException
                | InvocationTargetException exception
        ) {

            throw new RuntimeException(
                    "Failed to execute command method: "
                            + method.getName(),
                    exception
            );
        }
    }

    @Override
    public Collection<String> suggest(
            CommandSourceStack source,
            String[] args
    ) {

        List<String> suggestions =
                new ArrayList<>();

        if (args.length <= 1) {

            String current =
                    args.length == 0
                            ? ""
                            : args[0].toLowerCase();

            for (
                    String subcommand :
                    command.subcommands().keySet()
            ) {

                if (
                        subcommand.startsWith(current)
                ) {

                    suggestions.add(subcommand);

                }
            }

        }

        return suggestions;
    }
}