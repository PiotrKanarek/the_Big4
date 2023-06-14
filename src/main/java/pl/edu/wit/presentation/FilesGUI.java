package pl.edu.wit.presentation;

import pl.edu.wit.validation.KasiaP;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.HashSet;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;


/**
 * Class responsible for GUI creation. GUI consists of three main JScrollPanes. First one contains
 * JTrees with directories and files, second one contains files copying source and destination paths,
 * third one contains Start button.
 * @author Bartłomiej Kilim
 * 
 */
public class FilesGUI {
	// Component which contains directory tree for each root drive
	private JTree fileTree = null;
	// Set of fileTrees, only purpose is to clear selection for trees which are not source of
	// current action.
	private Set<JTree> treeSet = null;
	// Main container of GUI
	private JFrame frame = null;
	// Component with source path
	private JTextField fieldFrom = null;
	// Component with destination path
	private JTextField fieldTo = null;
	// Object to store source path
	private String pathFrom = null;
	// Object to store destination path
	private String pathTo = null;
	// Fonts used by GUI components
	Font font = new Font(Font.DIALOG, Font.PLAIN, 14);
	Font font2 = new Font(Font.DIALOG, Font.PLAIN, 20);
	Font font3 = new Font(Font.DIALOG, Font.PLAIN, 45);
	// GUI main button, which action start validation and files copying logic
	private JButton startButton = null;

	// GUI constructor
	public FilesGUI() {

		frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(590, 630);
		frame.setResizable(true);
		frame.setTitle("FilesUI");

		frame.setLayout(new GridBagLayout());
		//Constraints used to spread out three JPanels in GUI main frame
		GridBagConstraints constraints = new GridBagConstraints();

		JScrollPane fTree = new JScrollPane(plantFileTreePanel());
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.ipady = 350;
		constraints.ipadx = 550;
		frame.getContentPane().add(fTree, constraints);

		JScrollPane paths = new JScrollPane(makeFromToPathPanel());
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.gridy = 1;
		constraints.ipady = 120;
		frame.getContentPane().add(paths, constraints);

		JScrollPane gBut = new JScrollPane(makeStartButton());
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.gridy = 2;
		constraints.ipady = 50;
		frame.getContentPane().add(gBut, constraints);
	}

	/**
	 * Function checks active root drives and displays one JTree for each root. Uses TreeModel as source of
	 * data.
	 * @return JPanel with JTrees. One JTree for every root drive.
	 */
	private JPanel plantFileTreePanel() {

		treeSet = new HashSet<>();
		// Container which stores all the file trees.
		JPanel panelTrees = new JPanel();
		panelTrees.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Resources"));
		// Array of active root drives used during file tree creation.
		File[] rootDrive = File.listRoots();
		panelTrees.setLayout(new GridLayout(1, rootDrive.length));

		for (File file : rootDrive) {
			fileTree = new JTree();
			fileTree.setModel(new FileTreeModel(file.toString()));

			fileTree.addTreeSelectionListener(new TreeSelectionListener() {
				public void valueChanged(TreeSelectionEvent e) {
					// Tree which is the source of event.
					JTree eventTree = (JTree) e.getSource();
					for (JTree tree : treeSet) {
						if (tree != eventTree) {
							tree.clearSelection();
						}
					}
				}
			});
			
			fileTree.setCellRenderer(new FilesTreeCellRenderer());

			treeSet.add(fileTree);
			panelTrees.add(fileTree);
		}

		return panelTrees;
	}

	/**
	 * Function creates container with source and destination paths, delivered by two JTextFields,
	 * and two buttons to transfer selected path from JTree to JTextField if user wishes to do so.
	 * @return JPanel with source and destination paths and buttons to copy paths.
	 */
	private JPanel makeFromToPathPanel() {
		// Main container
		JPanel panelFT = new JPanel(new GridLayout(2, 1));
		fieldFrom = new JTextField(42);
		fieldFrom.setName("fieldFrom");
		fieldTo = new JTextField(42);
		fieldTo.setName("fieldTo");
		fieldFrom.setFont(font);
		fieldTo.setFont(font);

		//Container for JTextField and JButton
		JPanel panelFrom = new JPanel();
		//Button used to set path selected in JTree as source path for JTextField
		JButton bFrom = new JButton("From");

		//Sets path selected in JTree as source path for JTextField
		bFrom.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				for (JTree tree : treeSet) {
					if (!tree.isSelectionEmpty()) {
						fieldFrom.setText(tree.getSelectionPath().getLastPathComponent().toString());
					}
				}
			}
		});

		panelFrom.add(bFrom);
		panelFrom.add(fieldFrom);
		panelFrom.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Path From"));
		bFrom.setPreferredSize(new Dimension(60, 30));
		
		//Container for JTextField and JButton
		JPanel panelTo = new JPanel();
		//Button used to set path selected in JTree as destination path for JTextField
		JButton bTo = new JButton("To");

		//Sets path selected in JTree as destination path for JTextField
		bTo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				for (JTree tree : treeSet) {
					if (!tree.isSelectionEmpty()) {
						fieldTo.setText(tree.getSelectionPath().getLastPathComponent().toString());
					}
				}
			}
		});

		bTo.setPreferredSize(new Dimension(60, 30));
		panelTo.add(bTo);
		panelTo.add(fieldTo);
		panelTo.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Path To"));
		panelFT.add(panelFrom);
		panelFT.add(panelTo);

		return panelFT;
	}

	/**
	 * Main purpose is to create Start button
	 * @return JPanel with Start button
	 */
	private JPanel makeStartButton() {

		//Container for Start button
		JPanel panelStartB = new JPanel(new GridLayout(1, 1));
		panelStartB.setPreferredSize(new Dimension(500, 50));
		startButton = new JButton("START");
		startButton.setBorder(BorderFactory.createLineBorder(Color.RED, 2));
		startButton.setForeground(Color.RED);
		startButton.setFont(font3);
		panelStartB.add(startButton);

		//Event which launches application's validation and copying functionality
		startButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
/////////////////////////////////////////////////////////////////////////////////
				KasiaP kasiaP = new KasiaP();
				boolean fieldFromWalid = kasiaP.walidacja(fieldFrom);
				boolean fieldToWalid = kasiaP.walidacja(fieldTo);
/////////////////////////////////////////////////////////////////////////////////
				if (!fieldFromWalid && !fieldToWalid) {
					JOptionPane.showMessageDialog(frame, "Provided paths are incorrect");
					fieldFrom.setBorder(BorderFactory.createLineBorder(Color.red));
					fieldTo.setBorder(BorderFactory.createLineBorder(Color.red));
				}

				else if (!fieldFromWalid) {
					JOptionPane.showMessageDialog(frame, "Provided path is incorrect");
					fieldFrom.setBorder(BorderFactory.createLineBorder(Color.red));
				}

				else if (!fieldToWalid) {
					JOptionPane.showMessageDialog(frame, "Provided path is incorrect");
					fieldTo.setBorder(BorderFactory.createLineBorder(Color.red));
				}

				if (fieldFromWalid) {
					fieldFrom.setBorder(BorderFactory.createLineBorder(Color.gray));
				}
				if (fieldToWalid) {
					fieldTo.setBorder(BorderFactory.createLineBorder(Color.gray));
				}

				if (fieldFromWalid && fieldToWalid) {

					pathFrom = fieldFrom.getText().toString();
					pathTo = fieldTo.getText().toString();

					fieldFrom.setEditable(false);
					fieldTo.setEditable(false);
					startButton.setEnabled(false);
					frame.setEnabled(false);

					//Window with files copying feedback, during and after.
					WaitWindow wait = new WaitWindow(FilesGUI.this);
					new FilesWorker(wait, FilesGUI.this).execute();

				}
			}
		});

		return panelStartB;
	}

	public JFrame getFrame() {
		return frame;
	}

	public String getPathFrom() {
		return pathFrom;
	}

	public String getPathTo() {
		return pathTo;
	}
	
	public JTextField getFieldFrom() {
		return fieldFrom;
	}

	public JTextField getFieldTo() {
		return fieldTo;
	}
	
	public JButton getStartButton() {
		return startButton;
	}

}


