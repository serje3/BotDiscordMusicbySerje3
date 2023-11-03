package org.serje3.meta.abs;

import lombok.Getter;
import lombok.Setter;
import org.serje3.meta.interfaces.ICommandList;
import org.serje3.utils.commands.MusicCommandList;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

@Getter
@Setter
public abstract class CommandList implements ICommandList {
    protected List<Class<?>> commands = new ArrayList<>();


    @Override
    public void forEach(Function<Command, ?> func) {
        for (Class<?> commandClass : this.getCommands()) {
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
