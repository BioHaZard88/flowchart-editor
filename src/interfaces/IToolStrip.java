package interfaces;

import java.util.List;

/**
 * Tool container.
 */
public interface IToolStrip {

	/**
	 * Get collection of tools.
	 *
	 * @return Collection of tools.
	 */
	public List<ITool> getTools();

	/**
	 * Add a tool.
	 *
	 * @param Tool.
	 */
	public void addTool(ITool tool);

	/**
	 * Remove all tools. Not implemented yet.
	 */
	public void resetTools();
}
