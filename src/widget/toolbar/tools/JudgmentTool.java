package widget.toolbar.tools;

import org.eclipse.swt.events.DragDetectEvent;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;

import diagram.element.Diamond;
import diagram.flowchart.Judgment;
import exception.CreateElementException;
import interfaces.IElement;
import main.Main;
import widget.tab.SubEditor;
import widget.toolbar.ToolStrip;

public class JudgmentTool extends ATool {

	private boolean isDrag;
	private MouseEvent downTemp;

	public JudgmentTool(ToolStrip parent, String name) {
		super(parent, name);
	}

	public JudgmentTool(ToolStrip parent) {
		super(parent, "Judgment tool");
	}

	@Override
	public void initialize() {
		setIconName("diamond.png");
		super.initialize();
	}

	@Override
	public void mouseDown(MouseEvent e) {
		downTemp = e;
		isDrag = false;
		getActiveSubEditor().deselectAll();
	}

	@Override
	public void mouseMove(MouseEvent e) {
		if (!isDrag) {
			return;
		}
		GC gc = ((SubEditor) getActiveSubEditor()).getGC();
		
		getActiveSubEditor().draw();

		Diamond.draw(gc, Math.min(downTemp.x, e.x), Math.min(downTemp.y, e.y), 
				Math.abs(downTemp.x - e.x), Math.abs(downTemp.y - e.y));
		gc.dispose();
	}

	@Override
	public void mouseUp(MouseEvent e) {
		try {
			if (!isDrag) {
				throw new CreateElementException("Drag to draw object.");
			}
			Point src = new Point(downTemp.x, downTemp.y);
			Point dst = new Point(e.x, e.y);
			IElement rect = new Judgment(new Diamond(src, dst));
			getActiveSubEditor().addElement(rect);
			rect.select();
			getActiveSubEditor().draw();
		} catch (CreateElementException ex) {
			Main.log(ex.getMessage());
		}
		isDrag = false;
		downTemp = null;
	}

	@Override
	public void dragDetected(DragDetectEvent e) {
		isDrag = true;
	}

}