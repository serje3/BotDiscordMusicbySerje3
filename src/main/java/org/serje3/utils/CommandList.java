package org.serje3.utils;

import org.serje3.commands.music.*;
import org.serje3.commands.music.filters.BassBoostCommand;
import org.serje3.commands.music.filters.EBANUTIYBassBoostCommand;
import org.serje3.commands.music.filters.NormalizeFilterCommand;
import org.serje3.commands.music.queue.*;
import org.serje3.meta.abs.Command;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class CommandList {
    private static final List<Class<?>> commands = new ArrayList<>() {
        {
//            add(PlayCommand.class); deprecated
            add(GachiCommand.class);
            add(PauseCommand.class);
            add(JoinCommand.class);
            add(LeaveCommand.class);
            add(TTSCommand.class);
            add(QueueCommand.class);
            add(QueueTracksCommand.class);
            add(QueueSkipCommand.class);
            add(QueueClearCommand.class);
            add(QueueNowCommand.class);
            add(NormalizeFilterCommand.class);
            add(BassBoostCommand.class);
            add(EBANUTIYBassBoostCommand.class);
        }
    };

    public static List<Class<?>> getCommands() {
        return commands.stream().toList();
    }

    public static void forEach(Function<Command, ?> func) {
        for (Class<?> commandClass : CommandList.getCommands()) {
            try {
                Command command = (Command) commandClass.getDeclaredConstructor().newInstance();
                func.apply(command);
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                     NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
