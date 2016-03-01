package diagram.element;

import java.util.ArrayList;

import org.eclipse.swt.graphics.Point;

import diagram.state.NormalState;
import interfaces.IEditableElement;
import interfaces.IElement;

public abstract class AEditable extends AElement implements IEditableElement {

	private ArrayList<EditPoint> editPoints;
	
	public AEditable() {
		editPoints = new ArrayList<>();
	}

	@Override
	public void select() {
		if (!isActive()) {
			super.select();
			createEditPoints();	
		}
	}

	public void deselect() {
		if (!isActive()) {
			return;
		}
		System.out.println("Deselect " + this.toString());
		editPoints.clear();
		state = NormalState.getInstance();
	}

	public void addEditPoint(EditPoint e) {
		editPoints.add(e);
	}
	
	@Override
	public IElement checkBoundary(int x, int y) {
		for (IElement e : editPoints) {
			IElement ans = e.checkBoundary(x, y);
			if (ans != null) {
				return ans;
			}
		}
		return null;
	}

	@Override
	public void renderEdit() {
		renderNormal();
		for (EditPoint ep : editPoints) {
			ep.draw();
		}
	}

	@Override
	public void drag(int x1, int y1, int x2, int y2, IElement e) {
		deselect();
		select();
	}

	public void createEditPoints(ArrayList<Point> points) {
		for (int i = 0; i < points.size(); i++) {
			Point point = points.get(i);
			EditPoint ep = new EditPoint(this, point.x, point.y, i);
			ep.setCanvas(canvas);
			addEditPoint(ep);
		}
	}

}