package pl.edu.wit.presentation;

import java.awt.Component;
import java.io.File;

import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;

/**
 * Only purpose of this class is to view name of directories and files in JTree instead of full path.
 * @author Bartłomiej Kilim
 *
 */
@SuppressWarnings("serial")
class FilesTreeCellRenderer extends DefaultTreeCellRenderer {
		
	/**
	 * Replaces node's path with it's name (if name is available) displayed in JTree.
	 */
	@Override
	public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
		super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
		if (value instanceof File) {
			File file = (File) value;
			if (!file.getName().isEmpty()) {
				setText(file.getName());
			}
		}
		return this;
	}
}
