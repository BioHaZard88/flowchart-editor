package interfaces;

public interface IElement {
	public void setX(int x);
	public int getX();
	public void setY(int y);
	public int getY();
	public void setWidth(int width);
	public int getWidth();
	public void setHeight(int height);
	public int getHeight();
	public void setLocation(int x, int y);
	public void select();
	public void deselect();
	public IDrawingState getState();
	public void draw();
	public void renderNormal();
	public void renderEdit();
	public boolean checkBoundary(int x, int y);
	public boolean checkBoundary(int x1, int y1, int x2, int y2);
}
