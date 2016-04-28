package widget.toolbar.tools;

import org.eclipse.swt.events.DragDetectEvent;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;

import diagram.element.RoundedRectangle;
import diagram.flowchart.Terminator;
import exception.CreateElementException;
import interfaces.IElement;
import widget.tab.SubEditor;
import widget.toolbar.ToolStrip;

public class EllipseTool extends ATool {

	private boolean isDrag;
	private MouseEvent downTemp;

	public EllipseTool(ToolStrip parent, String name) {
		super(parent, name);
	}

	public EllipseTool(ToolStrip parent) {
		super(parent, "Ellipse tool");
	}
	
	@Override
	public void initialize() {
		setIconName("terminator.png");
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

		RoundedRectangle.draw(gc, Math.min(downTemp.x, e.x), Math.min(downTemp.y, e.y), 
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
			IElement rect = new Terminator(src, dst);
			getActiveSubEditor().addElement(rect);
			rect.select();
			getActiveSubEditor().draw();
		} catch (CreateElementException ex) {
			System.out.println(ex.getMessage());
		}
		isDrag = false;
		downTemp = null;
	}

	@Override
	public void dragDetected(DragDetectEvent e) {
		isDrag = true;
	}

}
