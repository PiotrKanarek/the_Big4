package pl.edu.wit.file;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

/**
 * Class provides directories and files content for JTrees
 * @author Bartłomiej Kilim
 *
 */
class FileTreeModel implements TreeModel {

	//Root of each JTree. One root for each root directory
	private File root;

	//Constructor with root drive as parameter
	public FileTreeModel(String path) {
		root = new File(path);
	}

	/**
	 * Gets child at specified index for parent
	 * @return Child of passed parent at requested index of child nodes array
	 */
	@Override
	public Object getChild(Object parent, int index) {
		if (parent == null) {
			return null;
		} else {
			return ((File) parent).listFiles()[index];
		}
	}

	/**
	 * Gets number of child nodes
	 * @return Returns number of child nodes of specified parent
	 */
	@Override
	public int getChildCount(Object parent) {
		if (parent == null)
			return 0;
		if (((File) parent).listFiles() != null) {
			return ((File) parent).listFiles().length;
		} else {
			return 0;
		}
	}

	/**
	 * Gets index of child in parent's array with all child nodes
	 * @return Returns index of child node
	 */
	@Override
	public int getIndexOfChild(Object parent, Object child) {
		List<File> list = Arrays.asList(((File) parent).listFiles());
		return list.indexOf(child);
	}

	@Override
	public Object getRoot() {
		return root;
	}

	@Override
	public boolean isLeaf(Object node) {
		return ((File) node).isFile();
	}

	/**
	 * Not implemented as this JTree is not editable
	 */
	@Override
	public void addTreeModelListener(TreeModelListener l) {}

	/**
	 * Not implemented as this JTree is not editable
	 */
	@Override
	public void removeTreeModelListener(TreeModelListener l) {}

	/**
	 * Not implemented as this JTree is not editable
	 */
	@Override
	public void valueForPathChanged(TreePath path, Object newValue) {}
}
