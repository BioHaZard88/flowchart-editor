package widget.menu.menuitem;

import widget.Window;
import widget.menu.AMenu;
import widget.tab.Editor;

public class New extends AMenuItem {

	public New(AMenu parent) {
		super(parent);
	}

	@Override
	public void initialize() {
		System.out.println("Initialize New");
		setTitle("&New\t\tCtrl+N");
		super.setShortcut('N');
	}

	@Override
	public void execute() {
		System.out.println("New");
		Editor editor = (Editor) Window.getInstance().getEditor();
		editor.newSubEditor();
	}

}
