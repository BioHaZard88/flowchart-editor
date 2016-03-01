package widget.toolbar.tools;

import org.eclipse.swt.events.DragDetectEvent;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.GC;

import diagram.element.FlowLine;
import diagram.element.TwoDimensional;
import exception.CreateElementException;
import interfaces.IElement;
import widget.tab.SubEditor;
import widget.toolbar.ToolStrip;
import widget.window.MainWindow;

public class LineTool extends ATool {

	private boolean isDrag;
	private TwoDimensional srcElement;
	private TwoDimensional dstElement;
	private MouseEvent downTemp;

	public LineTool(ToolStrip parent, String name) {
		super(parent, name);
	}

	public LineTool(ToolStrip parent) {
		super(parent, "Line tool");
	}

	@Override
	public void initialize() {
		setIconName("arrow.png");
		super.initialize();
		isDrag = false;
	}

	@Override
	public void mouseDown(MouseEvent e) {
		srcElement = null;
		dstElement = null;
		IElement src = getActiveSubEditor().getElement(e.x, e.y);
		if (src != null && src instanceof TwoDimensional) {
			srcElement = (TwoDimensional) src;
			getActiveSubEditor().deselectAll();
			getActiveSubEditor().draw();
		}
		downTemp = e;
	}

	@Override
	public void mouseMove(MouseEvent e) {
		if (!isDrag) {
			return;
		}
		SubEditor s;
		s = (SubEditor) getActiveSubEditor();
		GC gc = s.getGC();

		MainWindow.getInstance().getEditor().getActiveSubEditor().draw();

		FlowLine.draw(gc, downTemp.x, downTemp.y, e.x, e.y);
		gc.dispose();
	}

	@Override
	public void mouseUp(MouseEvent e) {
		try {
			if (!isDrag) {
				throw new CreateElementException("Drag to draw object.");
			}
			IElement dst = getActiveSubEditor().getElement(e.x, e.y);
			if (dst != null && dst instanceof TwoDimensional) {
				dstElement = (TwoDimensional) dst;
			}
			/* Yang diklik itu ada element nya apa nggak */
			if (srcElement == null || dstElement == null || srcElement == dstElement) {
				throw new CreateElementException("Connect element properly.");
			}
			/* Cek apakeh element udah tersambung */
			boolean alredy = false;
			for (IElement element : getActiveSubEditor().getElements()) {
				if (element instanceof FlowLine) {
					FlowLine flow = (FlowLine) element;
					int isSrc = flow.checkConnected(srcElement);
					int isDst = flow.checkConnected(dstElement);
					alredy = isSrc == FlowLine.CONNECTED_SRC && isDst == FlowLine.CONNECTED_DST;
				}
			}
			if (alredy) {
				throw new CreateElementException("Object alredy connected.");
			}
			FlowLine flowLine = new FlowLine(srcElement, dstElement);
			getActiveSubEditor().addElement(flowLine);
			flowLine.select();
			getActiveSubEditor().draw();
		} catch (CreateElementException ex) {
			System.out.println(ex.getMessage());
			getActiveSubEditor().draw();
		}
		isDrag = false;
		downTemp = null;
	}

	@Override
	public void dragDetected(DragDetectEvent e) {
		isDrag = true;
	}

}
