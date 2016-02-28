package command;

import java.util.List;

import interfaces.ICommand;
import interfaces.IElement;
import interfaces.ISubEditor;
import widget.window.MainWindow;

public class ElementPropertiesCommand implements ICommand {

	@Override
	public void execute() {
		ISubEditor subEditor;
		subEditor = MainWindow.getInstance().getEditor().getActiveSubEditor();
		List<IElement> elements = subEditor.getSelectedElements();
		if (elements.size() == 1) {
			IElement element = elements.get(0);
			element.action();
		}
	}

}