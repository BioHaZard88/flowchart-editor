package widget.tab;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.TabFolder;
import interfaces.IEditor;
import interfaces.ISubEditor;
import widget.Window;

public class Editor extends TabFolder implements IEditor {

	private List<ISubEditor> subEditors;

	public Editor(Window parent, int style) {
		super(parent, style);
		this.initialize();
	}

	public Editor(Window parent) {
		this(parent, SWT.NONE);
	}

	@Override
	public void checkSubclass() {
	}

	@Override
	public void initialize() {
		subEditors = new ArrayList<>();

		GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
		this.setLayoutData(gridData);

		for (int i = 1; i <= 5; i++) {
			newSubEditor();
		}
	}

	@Override
	public void addSubEditor(ISubEditor subEditor) {
		subEditors.add(subEditor);
	}

	@Override
	public void newSubEditor() {
		ISubEditor subEditor = new SubEditor(this);
		subEditor.setTitle("SubEditor " + (subEditors.size() + 1));
		addSubEditor(subEditor);
	}

	@Override
	public ISubEditor getActiveSubEditor() {
		int index = this.getSelectionIndex();
		ISubEditor ans = subEditors.get(index);
		System.out.println("Close index " + index + " (" + ans.getTitle() + ")");
		return ans;
	}

	@Override
	public List<ISubEditor> getSubEditors() {
		return subEditors;
	}

	public void close() {
		ISubEditor subEditor = this.getActiveSubEditor();
		subEditor.close();
		subEditors.remove(subEditor);
	}

}