package diagram.flowchart;

import org.eclipse.swt.graphics.Point;

import diagram.element.Ellipse;
import diagram.element.Line;
import exception.CreateElementException;
import interfaces.FlowChartElement;
import interfaces.IDiagramElement;
import interfaces.IElement;
import interfaces.IType;
import widget.window.property.ProcessProperty;

public class Convergence extends Ellipse implements IDiagramElement, FlowChartElement {

	private FlowLine flow;
	private NodeCode nodeCode;
	private IType type;
	private boolean traversed;
	private int doWhile;
	private int recodeDoWhile;
	private Decision doWhileNode;

	private Decision directJudgment;

	public static final int FIX_DIAMETER = 12;

	public Convergence() {
	}

	public Convergence(Point src, Point dst) {
		super(src, dst);
	}

	@Override
	public void setWidth(int h) {
		super.setWidth(FIX_DIAMETER);
	}

	@Override
	public void setHeight(int h) {
		super.setHeight(FIX_DIAMETER);
	}

	@Override
	public void action() {
		ProcessProperty prop = new ProcessProperty(this);
		prop.show();
	}

	@Override
	public void connect(IElement element) {
		if (this.flow != null) {
			throw new CreateElementException("Convergence can't have more than one children.");
		}
		super.connect(element);
		if (element instanceof FlowLine) {
			FlowLine flow = (FlowLine) element;
			if (flow.checkConnected(this) == Line.CONNECTED_SRC) {
				this.flow = flow;
			}
		}
	}

	@Override
	public void disconnect(IElement element) {
		super.disconnect(element);
		flow = null;
	}

	public FlowLine getFlow() {
		return flow;
	}

	@Override
	public NodeCode getNodeCode() {
		return nodeCode;
	}

	@Override
	public void setNodeCode(NodeCode code) {
		this.nodeCode = code;
	}

	@Override
	public IType getType() {
		return type;
	}

	@Override
	public void setType(IType type) {
		this.type = type;
	}

	@Override
	public boolean hasBeenTraversed() {
		return traversed;
	}

	@Override
	public void resetTraversed() {
		traversed = false;
	}

	@Override
	public void traverse() {
		traversed = true;
	}

	@Override
	public int getDoWhileCounter() {
		return doWhile;
	}

	@Override
	public void setDoWhileCounter(int counter) {
		doWhile = counter;
	}

	@Override
	public int getRecodeDoWhileCounter() {
		return recodeDoWhile;
	}

	@Override
	public void setRecodeDoWhileCounter(int counter) {
		recodeDoWhile = counter;
	}

	@Override
	public FlowChartElement getDoWhileNode() {
		return doWhileNode;
	}

	@Override
	public void setDoWhileNode(FlowChartElement node) {
		doWhileNode = (Decision) node;
	}

	public Decision getDirectJudgment() {
		return directJudgment;
	}

	public void setDirectJudgment(Decision directJudgment) {
		this.directJudgment = directJudgment;
	}

	@Override
	public void prepare() {
		setType(null);
		setNodeCode(null);
		setDoWhileCounter(0);
		setRecodeDoWhileCounter(0);
		setDirectJudgment(null);
	}

}
