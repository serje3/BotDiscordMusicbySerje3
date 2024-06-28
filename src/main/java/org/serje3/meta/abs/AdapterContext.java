package org.serje3.meta.abs;

import lombok.Getter;
import lombok.Setter;
import org.serje3.meta.interfaces.IAutoCompleteList;
import org.serje3.meta.interfaces.IButtonsList;
import org.serje3.meta.interfaces.ICommandList;
import org.serje3.meta.interfaces.IModalList;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

@Getter
@Setter
public abstract class AdapterContext implements ICommandList, IButtonsList, IAutoCompleteList, IModalList {
    protected List<Class<? extends Command>> commands = new ArrayList<>();
    protected List<Class<? extends Button>> buttons = new ArrayList<>();
    protected List<Class<? extends AutoComplete>> autoCompletes = new ArrayList<>();
    protected List<Class<? extends Modal>> modals = new ArrayList<>();


    @Override
    public void forEachCommand(Consumer<Command> func) {
        for (Class<? extends Command> commandClass : this.getCommands()) {
            try {
                Command command = commandClass.getDeclaredConstructor().newInstance();
                func.accept(command);
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                     NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public void forEachButton(Consumer<Button> func) {
        for (Class<? extends Button> buttonClass : this.getButtons()) {
            try {
                Button button = buttonClass.getDeclaredConstructor().newInstance();
                func.accept(button);
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                     NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public void forEachAutoComplete(Consumer<AutoComplete> func) {
        for (Class<? extends AutoComplete> autoCompleteClass : this.getAutoCompletes()) {
            try {
                AutoComplete autoComplete = autoCompleteClass.getDeclaredConstructor().newInstance();
                func.accept(autoComplete);
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                     NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public void forEachModal(Consumer<Modal> func) {
        for (Class<? extends Modal> modalClass : this.getModals()) {
            try {
                Modal modal = modalClass.getDeclaredConstructor().newInstance();
                func.accept(modal);
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                     NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
