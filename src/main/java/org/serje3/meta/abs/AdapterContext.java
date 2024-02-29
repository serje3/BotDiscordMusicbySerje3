package org.serje3.meta.abs;

import lombok.Getter;
import lombok.Setter;
import org.serje3.meta.interfaces.IAutoCompleteList;
import org.serje3.meta.interfaces.IButtonsList;
import org.serje3.meta.interfaces.ICommandList;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

@Getter
@Setter
public abstract class AdapterContext implements ICommandList, IButtonsList, IAutoCompleteList {
    protected List<Class<?>> commands = new ArrayList<>();
    protected List<Class<?>> buttons = new ArrayList<>();
    protected List<Class<?>> autoCompletes = new ArrayList<>();


    @Override
    public void forEachCommand(Function<Command, ?> func) {
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

    @Override
    public void forEachButton(Function<Button, ?> func) {
        for (Class<?> buttonClass : this.getButtons()) {
            try {
                Button button = (Button) buttonClass.getDeclaredConstructor().newInstance();
                func.apply(button);
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                     NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public void forEachAutoComplete(Function<AutoComplete, ?> func) {
        for (Class<?> autoCompleteClass : this.getAutoCompletes()) {
            try {
                AutoComplete autoComplete = (AutoComplete) autoCompleteClass.getDeclaredConstructor().newInstance();
                func.apply(autoComplete);
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                     NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
