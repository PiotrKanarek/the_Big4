package pl.edu.wit.presentation;

import javax.swing.*;
import javax.swing.event.TreeModelListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class UserInterface {

    private JTree fileTree = null;
    private Set<JTree> treeSet = null;
    private JFrame frame = null;
    private JTextField fieldFrom = null;
    private JTextField fieldTo = null;
    private String pathFrom = null;
    private String pathTo = null;

    public UserInterface() throws InvocationTargetException, InterruptedException, ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException {

        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(590, 630);
        frame.setResizable(true);
        frame.setTitle("FilesUI");

        frame.setLayout(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();

        JScrollPane fTree = new JScrollPane(plantFileTree());
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.ipady = 350;
        constraints.ipadx = 550;
        frame.getContentPane().add(fTree, constraints);

        JScrollPane paths = new JScrollPane(fromToPath());
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.gridy = 1;
        constraints.ipady = 120;
        frame.getContentPane().add(paths, constraints);

        JScrollPane gBut = new JScrollPane(getGoButton());
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.gridy = 2;
        constraints.ipady = 50;
        frame.getContentPane().add(gBut, constraints);
    }

    class FileTreeModel implements TreeModel { // Generuje zawartość drzewa

        private File root;

        public FileTreeModel(String path) {
            root = new File(path);
        }

        @Override
        public Object getChild(Object parent, int index) {
            if (parent == null)
                return null;
            return ((File) parent).listFiles()[index];
        }

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

        @Override
        public void addTreeModelListener(TreeModelListener l) {
        }

        @Override
        public void removeTreeModelListener(TreeModelListener l) {
        }

        @Override
        public void valueForPathChanged(TreePath path, Object newValue) {
        }
    }

    private JPanel plantFileTree() { // Wyświetla zawartość nowych drzew

        treeSet = new HashSet<>();
        JPanel panelTrees = new JPanel();
        panelTrees.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Resources"));
        File[] rootDrive = File.listRoots();
        panelTrees.setLayout(new GridLayout(1, rootDrive.length));

        for (File file : rootDrive) {
            fileTree = new JTree();
            fileTree.setModel(new FileTreeModel(file.toString()));

            /////////////////////////////////////////////////

            fileTree.addTreeSelectionListener(new TreeSelectionListener() {
                public void valueChanged(TreeSelectionEvent e) {
                    JTree eventTree = (JTree) e.getSource();
                    for (JTree tree : treeSet) {
                        if (tree != eventTree) {
                            tree.clearSelection();
                        }
                    }
                }
            });

            ////////////////////////////////////////////////////

            treeSet.add(fileTree);
            panelTrees.add(fileTree);
        }

        return panelTrees;
    }

    private JPanel fromToPath() {

        JPanel panelFT = new JPanel(new GridLayout(2, 1));
        fieldFrom = new JTextField(42);
        fieldFrom.setName("fieldFrom");
        fieldTo = new JTextField(42);
        fieldTo.setName("fieldTo");
        //fieldFrom.setText("");
        //fieldTo.setText("");

        Font font = new Font(Font.DIALOG, Font.PLAIN, 14);
        fieldFrom.setFont(font);
        fieldTo.setFont(font);

        JPanel panelFrom = new JPanel();
        JButton bFrom = new JButton("From");

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
        JPanel panelTo = new JPanel();
        JButton bTo = new JButton("To");

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

    private JPanel getGoButton() {

        JPanel panelGoB = new JPanel(new GridLayout(1, 1));
        panelGoB.setPreferredSize(new Dimension(500, 50));
        JButton goButton = new JButton("START");
        goButton.setBackground(Color.RED);
        goButton.setFont(new Font(Font.DIALOG, Font.PLAIN, 45));
        panelGoB.add(goButton);

        goButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                //Czy tak ma wyglądać wywołanie walidacji?
                KasiaP kasia = new KasiaP();
                boolean fieldFromWalid = kasia.walidacja(fieldFrom);
                boolean fieldToWalid = kasia.walidacja(fieldTo);

                if (!fieldFromWalid) {
                    JOptionPane.showMessageDialog(frame, "Podaj poprawną ścieżkę");
                    fieldFrom.setBorder(BorderFactory.createLineBorder(Color.red));
                } else {
                    fieldFrom.setBorder(BorderFactory.createLineBorder(Color.gray));
                }

                if (!fieldToWalid) {
                    JOptionPane.showMessageDialog(frame, "Podaj poprawną ścieżkę");
                    fieldTo.setBorder(BorderFactory.createLineBorder(Color.red));
                } else {
                    fieldTo.setBorder(BorderFactory.createLineBorder(Color.gray));
                }

                if (fieldFromWalid && fieldToWalid) {
                    pathFrom = fieldFrom.getText().toString();
                    pathTo = fieldTo.getText().toString();
                    fieldTo.setBorder(BorderFactory.createLineBorder(Color.gray));
                    fieldFrom.setBorder(BorderFactory.createLineBorder(Color.gray));
                    fieldFrom.setEditable(false);
                    fieldTo.setEditable(false);
                    goButton.setEnabled(false);
                }
            }
        });

        return panelGoB;
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
}