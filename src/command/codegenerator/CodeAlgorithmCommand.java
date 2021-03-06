package command.codegenerator;

import java.util.List;
import java.util.Stack;

import diagram.flowchart.*;
import diagram.flowchart.Process;
import diagram.flowchart.type.*;
import exception.GenerateCodeException;
import interfaces.FlowChartElement;
import interfaces.ICommand;
import interfaces.IElement;
import main.Main;
import widget.window.MainWindow;

public class CodeAlgorithmCommand implements ICommand {

	private int codeCounter;
	private Stack<Judgment> stackOfJudgment;
	private Stack<FlowChartElement> doWhileStack;
	private NodeCode newCode;

	@Override
	public void execute() {
		/* Get start element */
		List<IElement> elements;
		elements = MainWindow.getInstance().getEditor().getActiveSubEditor().getElements();
		IElement currentElem = null;
		for (IElement e : elements) {
			if (e instanceof FlowChartElement) {
				((FlowChartElement) e).prepare();
			}
			if (e instanceof Terminator) {
				if (((Terminator) e).getText().equals(Terminator.START)) {
					currentElem = e;
				}
			}
		}

		/* Begin code flowchartElement, send father, his son, and new code */
		Terminator father = (Terminator) currentElem;
		FlowChartElement son = father.getFlow().getSon();
		newCode = new NodeCode();
		father.setNodeCode(newCode);
		codeCounter = 1;
		stackOfJudgment = new Stack<>();
		doWhileStack = new Stack<>();
		codeAlgorithm(father, son, father.getNodeCode().createSibling());
	}

	/**
	 * Generate a code for every node according to the coding strategy.
	 * 
	 * @param fatherNode
	 * @param currentNode
	 * @param currentCode
	 */
	public void codeAlgorithm(FlowChartElement father, FlowChartElement currElem, NodeCode currCode) {
		Main.log("codeAlgorithm (" + (codeCounter++) + ")");
		Main.log("\t" + father);
		Main.log("\t" + currElem);
		Main.log("\t" + currCode);

		if (currElem instanceof Terminator) {
			returnCode();
			return; // exit point of recursion
		}
		if (!doWhileStack.empty() && currElem.getDoWhileNode() != doWhileStack.peek()) {
			/* This process is still surrounded by do-while */
			Main.log("\tReset traversed");
			currElem.resetTraversed();
			currElem.setDoWhileNode(doWhileStack.peek());
		}
		if (currElem instanceof Process) {
			Process currNode = (Process) currElem;
			/* Generate the code for CurrentNode */
			NodeCode codeOfSon = currCode.createSibling();
			if (!currNode.isTraversed()) {
				Main.log("\tProcess hasn't been traversed");
				currNode.traverse();
				currNode.setNodeCode(currCode);
				FlowChartElement son = currNode.getFlow().getSon();
				codeAlgorithm(currNode, son, codeOfSon);
			} else {
				/*
				 * Father is recognized as a do-while structure, mark the
				 * Judgment and link it with his Father, include do-while and
				 * nested do-while
				 */
				if ((father instanceof Judgment && father.getType() == null) || father.getType() == DoWhileType.get()) {
					Main.log("\tProcess father is do-while");
					father.setType(DoWhileType.get());
					NodeCode thisCode = currNode.getNodeCode();
					father.setNodeCode(thisCode); // do-while take over child code
					thisCode.clearChildren(); // and reset the child of the code

					Main.log("\tBegining to recode do-while child");
					doWhileStack.push(father); // increase do-while stack
					father.setDoWhileNode(doWhileStack.peek());
					FlowLine flow = ((Judgment) father).getFlow(currNode);
					NodeCode childCode = thisCode.createChild(flow.getCode());
					codeAlgorithm(father, currNode, childCode);
					doWhileStack.pop(); // reset do-while stack after finish recode
				}
			}
		}
		if (currElem instanceof Judgment) {
			Judgment currNode = (Judgment) currElem;
			if (!currNode.isTraversed()) {
				Main.log("\tJudgment hasn't been traversed");
				currNode.traverse();
				currNode.setNodeCode(currCode);

				if (currNode.getDirectConvergence() == null) {
					Main.log("\tPush into stackOfJudgment, to find convergence");
					stackOfJudgment.push(currNode);
				}

				/* Get direct convergence and process other children */
				Convergence convergenceSon = null;
				for (FlowLine fl : currNode.getFlows()) {
					FlowChartElement son = fl.getSon();
					if (son instanceof Convergence) {
						convergenceSon = (Convergence) son;
						continue;
					}
					NodeCode sonCode = currCode.createChild(fl.getCode());
					Main.log("\tGo to judgment's child");
					codeAlgorithm(currNode, son, sonCode);
				}

				/* Process direct convergence, judgment and convergence will be connected */
				if (convergenceSon != null) {
					Main.log("\tGo to judgment's convergence direct child");
					codeAlgorithm(currNode, convergenceSon, null);
				}

				/* loop structures have been recognized, the left is selections */
				if (currNode.getType() == null) {
					currNode.setType(SelectionType.get());
				}

				/* Continue to process the nodes behind Convergence */
				Convergence conv = currNode.getDirectConvergence();
				if (conv == null) {
					throw new GenerateCodeException("Direct convergence is null");
				}
				NodeCode sonCode = conv.getNodeCode().createSibling();
				FlowChartElement sonNode = conv.getFlow().getSon();
				Main.log("\tGo to judgment's direct convergence's child");
				codeAlgorithm(conv, sonNode, sonCode);
			}
			else { // been traversed.
				if (currNode.getType() == null) {
					currNode.setType(WhileType.get());
				}
				else { // and been recognized
					if ((father instanceof Judgment && father.getType() == null) || father.getType() == DoWhileType.get()) {
						Main.log("\tJudgment father is do-while");
						father.setType(DoWhileType.get());
						NodeCode thisCode = currNode.getNodeCode();
						father.setNodeCode(thisCode); // do-while take over child code
						thisCode.clearChildren(); // and reset the child of the code

						Main.log("\tBegining to recode do-while child");
						doWhileStack.push(father); // increase do-while stack
						father.setDoWhileNode(doWhileStack.peek());
						FlowLine flow = ((Judgment) father).getFlow(currNode);
						NodeCode childCode = thisCode.createChild(flow.getCode());
						codeAlgorithm(father, currNode, childCode);
						doWhileStack.pop(); // reset do-while stack after finish recode
					}
				}
			}
		}
		if (currElem instanceof Convergence) {
			Convergence currNode = (Convergence) currElem;
			/* match a judgment node and a convergence node */
			if (!currNode.isTraversed()) {
				Main.log("\tConvergence hasn't been traversed");
				currNode.traverse();
				/*
				 * as a judgment and a convergence is exist geminate, so the top
				 * judgment in stack must be able to match the current
				 * convergence
				 */
				if (currNode.getDirectJudgment() == null) {
					Main.log("\tConnect convergence with judgment");
					if (stackOfJudgment.isEmpty()) {
						throw new GenerateCodeException("Empty stackOfJudgment");
					}
					Judgment judgment = stackOfJudgment.pop();
					currNode.setDirectJudgment(judgment);
					judgment.setDirectConvergence(currNode);
				}
				/* Set this convergence node code from direct judgment */
				currNode.setNodeCode(currNode.getDirectJudgment().getNodeCode());
			} else {
				Main.log("\tConvergence has been traversed");
				currNode.setNodeCode(currNode.getDirectJudgment().getNodeCode());
				returnCode();
				return;
			}
		}
		returnCode();
	}

	public void returnCode() {
		Main.log("return " + --codeCounter);
	}
	
	public NodeCode getFirstCode() {
		return newCode;
	}

}
