package segmentation;

import ij.IJ;
import ij.ImagePlus;
import ij.WindowManager;
import java.awt.BorderLayout;
import javax.swing.JPanel;
import javax.swing.JFrame;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;
import javax.swing.JOptionPane;
import javax.swing.JTree;
import javax.swing.JScrollPane;
import javax.swing.JMenuBar;
import javax.swing.JToolBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JButton;
import javax.swing.ImageIcon;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import javax.swing.JPopupMenu;

public class ImageObjectExplorerView extends JFrame implements Observer, TreeModelListener {

	private static final long serialVersionUID = 1L;
	
	protected ImageObjectExplorer model; 

	private JPanel jContentPane = null;

	private JTree imageObjectTree = null;

	private JScrollPane treeScrollPane = null;

	private JMenuBar mainMenuBar = null;

	private JToolBar maiinToolBar = null;

	private JMenu fileMenu = null;

	private JMenuItem newMenuItem = null;

	private JButton newObjectTreeButton = null;

	protected DefaultTreeModel treeModel;  //  @jve:decl-index=0:

	private JMenuItem subobjectsFromMaskMenuItem = null;

	private JPopupMenu treePopupMenu = null;

	private JMenuItem findNeighborsMenuItem = null;

	private JMenuItem measureMenuItem = null;

	private JMenuItem selectMenuItem = null;

	private JMenuItem sortMenuItem = null;

	private JMenu OptionsMenu = null;

	private JMenuItem editOptionsMenuItem = null;

	private JButton editOptionsButton = null;

	/**
	 * This is the default constructor
	 */
	public ImageObjectExplorerView() {
		super();
		model = new ImageObjectExplorer();
		model.addObserver(this);
		model.setView(this);
		initialize();
	}

	public ImageObjectExplorerView(ImageObjectExplorer explorer) {
		super();
		model = explorer;
		model.addObserver(this);
		initialize();
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		this.setSize(423, 361);
		this.setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/resources/images/extract-foreground-objects.png")));
		this.setJMenuBar(getMainMenuBar());
		this.setContentPane(getJContentPane());
		this.setTitle("MRI Image-Object Explorer");
	}

	/**
	 * This method initializes jContentPane
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJContentPane() {
		if (jContentPane == null) {
			jContentPane = new JPanel();
			jContentPane.setLayout(new BorderLayout());
			jContentPane.add(getTreeScrollPane(), BorderLayout.CENTER);
			jContentPane.add(getMaiinToolBar(), BorderLayout.NORTH);
		}
		return jContentPane;
	}

	/**
	 * This method initializes imageObjectTree	
	 * 	
	 * @return javax.swing.JTree	
	 */
	private JTree getImageObjectTree() {
		if (imageObjectTree == null) {
			treeModel = new DefaultTreeModel(model.getRootNode());
			treeModel.addTreeModelListener(this);
			imageObjectTree = new JTree(treeModel);
			imageObjectTree.setShowsRootHandles(true);
			imageObjectTree.addMouseListener(new java.awt.event.MouseAdapter() {
				public void mousePressed(java.awt.event.MouseEvent e) {
					if (e.getButton()==MouseEvent.BUTTON3) {
						getTreePopupMenu().show(getImageObjectTree(), e.getX(), e.getY());
					}
				}
			});
		}
		return imageObjectTree;
	}

	/**
	 * This method initializes treeScrollPane	
	 * 	
	 * @return javax.swing.JScrollPane	
	 */
	private JScrollPane getTreeScrollPane() {
		if (treeScrollPane == null) {
			treeScrollPane = new JScrollPane();
			treeScrollPane.setViewportView(getImageObjectTree());
		}
		return treeScrollPane;
	}

	/**
	 * This method initializes mainMenuBar	
	 * 	
	 * @return javax.swing.JMenuBar	
	 */
	private JMenuBar getMainMenuBar() {
		if (mainMenuBar == null) {
			mainMenuBar = new JMenuBar();
			mainMenuBar.add(getFileMenu());
			mainMenuBar.add(getOptionsMenu());
		}
		return mainMenuBar;
	}

	/**
	 * This method initializes maiinToolBar	
	 * 	
	 * @return javax.swing.JToolBar	
	 */
	private JToolBar getMaiinToolBar() {
		if (maiinToolBar == null) {
			maiinToolBar = new JToolBar();
			maiinToolBar.setPreferredSize(new Dimension(18, 42));
			maiinToolBar.add(getNewObjectTreeButton());
			maiinToolBar.add(getEditOptionsButton());
		}
		return maiinToolBar;
	}

	/**
	 * This method initializes fileMenu	
	 * 	
	 * @return javax.swing.JMenu	
	 */
	private JMenu getFileMenu() {
		if (fileMenu == null) {
			fileMenu = new JMenu();
			fileMenu.setText("File");
			fileMenu.add(getNewMenuItem());
		}
		return fileMenu;
	}

	/**
	 * This method initializes newMenuItem	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */
	private JMenuItem getNewMenuItem() {
		if (newMenuItem == null) {
			newMenuItem = new JMenuItem();
			newMenuItem.setText("new object tree");
			newMenuItem.setIcon(new ImageIcon(getClass().getResource("/resources/images/document-new.png")));
			newMenuItem.addActionListener(new java.awt.event.ActionListener() {   
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					getNewObjectTreeButton().doClick();
				}
			
			});
		}
		return newMenuItem;
	}

	/**
	 * This method initializes newObjectTreeButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getNewObjectTreeButton() {
		if (newObjectTreeButton == null) {
			newObjectTreeButton = new JButton();
			newObjectTreeButton.setIcon(new ImageIcon(getClass().getResource("/resources/images/document-new.png")));
			newObjectTreeButton.setToolTipText("new object tree");
			newObjectTreeButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					if (!model.isEmpty() && model.hasChanges()) {
						int n = JOptionPane.showConfirmDialog(
							    model.getView(),
							    model.getRoot().getName() + " has unsaved changes.\n" +
							    "Do you want to save " + model.getRoot().getName(),
							    "save changes?",
							    JOptionPane.YES_NO_OPTION);
						if (n==JOptionPane.YES_OPTION) {
							model.save();
						}
					}
					ImagePlus image = WindowManager.getCurrentImage();
					if (image==null) return;
					model.createNewObjectTree(image);
				}
			});
		}
		return newObjectTreeButton;
	}

	public void update(Observable sender, Object aspect) {
		if (aspect.equals("object tree")) this.handleObjectTreeChanged();
	}

	private void handleObjectTreeChanged() {
		treeModel.setRoot(model.getRootNode());
	}

	public void treeNodesChanged(TreeModelEvent arg0) {
		System.out.println("tree nodes changed");
		
	}

	public void treeNodesInserted(TreeModelEvent arg0) {
		System.out.println("tree nodes inserted");
	}

	public void treeNodesRemoved(TreeModelEvent arg0) {
		System.out.println("tree nodes removed");
	}

	public void treeStructureChanged(TreeModelEvent arg0) {
		System.out.println("tree structure changed");
	}

	/**
	 * This method initializes subobjectsFromMaskMenuItem	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */
	private JMenuItem getSubobjectsFromMaskMenuItem() {
		if (subobjectsFromMaskMenuItem == null) {
			subobjectsFromMaskMenuItem = new JMenuItem();
			subobjectsFromMaskMenuItem.setText("create sub-objects from mask");
			subobjectsFromMaskMenuItem.setIcon(new ImageIcon(getClass().getResource("/resources/images/selection-break.png")));
			subobjectsFromMaskMenuItem
					.addActionListener(new java.awt.event.ActionListener() {
						@SuppressWarnings("unchecked")
						public void actionPerformed(java.awt.event.ActionEvent e) {
							ImagePlus image = WindowManager.getCurrentImage();
							if (image==null) return;
							IJ.run("Convert to Mask");
							Node<ImageObject> selection = (Node<ImageObject>) getImageObjectTree()
																				.getSelectionPath()
																				.getLastPathComponent();
							model.createSubObjectsFromMask(image, selection.getContent());
							treeModel.nodeStructureChanged(selection); 
						}
					});
		}
		return subobjectsFromMaskMenuItem;
	}

	/**
	 * This method initializes treePopupMenu	
	 * 	
	 * @return javax.swing.JPopupMenu	
	 */
	private JPopupMenu getTreePopupMenu() {
		if (treePopupMenu == null) {
			treePopupMenu = new JPopupMenu();
			treePopupMenu.add(getSubobjectsFromMaskMenuItem());
			treePopupMenu.add(getFindNeighborsMenuItem());
			treePopupMenu.add(getMeasureMenuItem());
			treePopupMenu.add(getSelectMenuItem());
			treePopupMenu.add(getSortMenuItem());
		}
		return treePopupMenu;
	}

	/**
	 * This method initializes findNeighborsMenuItem	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */
	private JMenuItem getFindNeighborsMenuItem() {
		if (findNeighborsMenuItem == null) {
			findNeighborsMenuItem = new JMenuItem();
			findNeighborsMenuItem.setText("find neighbors...");
			findNeighborsMenuItem.setIcon(new ImageIcon(getClass().getResource("/resources/images/find neighbors.png")));
			findNeighborsMenuItem.addActionListener(new java.awt.event.ActionListener() {
				@SuppressWarnings("unchecked")
				public void actionPerformed(java.awt.event.ActionEvent e) {
					String answer = JOptionPane.showInputDialog(model.getView(), 
												null, 
												"max. distance", 
												JOptionPane.QUESTION_MESSAGE);
					if (answer==null) return;
					int maxDist = Integer.parseInt(answer);
					TreePath[] selection = getImageObjectTree().getSelectionPaths();
					ArrayList<Node<ImageObject>> selectedNodes = new ArrayList<Node<ImageObject>>();
					for (int i=0; i<selection.length; i++) {
						selectedNodes.add((Node<ImageObject>) selection[i].getLastPathComponent());
					}
					model.findNeighbors(selectedNodes, maxDist);
				}
			});
		}
		return findNeighborsMenuItem;
	}

	/**
	 * This method initializes measureMenuItem	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */
	private JMenuItem getMeasureMenuItem() {
		if (measureMenuItem == null) {
			measureMenuItem = new JMenuItem();
			measureMenuItem.setText("measure children");
			measureMenuItem.setIcon(new ImageIcon(getClass().getResource("/resources/images/measure.png")));
			measureMenuItem.addActionListener(new java.awt.event.ActionListener() {
				@SuppressWarnings("unchecked")
				public void actionPerformed(java.awt.event.ActionEvent e) {
					TreePath[] selection = getImageObjectTree().getSelectionPaths();
					for (int i=0; i<selection.length; i++) {
						Node<ImageObject> node = 
							(Node<ImageObject>) selection[i].getLastPathComponent();
						model.measureChildren(node);
					}
				}
			});
		}
		return measureMenuItem;
	}

	/**
	 * This method initializes selectMenuItem	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */
	private JMenuItem getSelectMenuItem() {
		if (selectMenuItem == null) {
			selectMenuItem = new JMenuItem();
			selectMenuItem.setText("select");
			selectMenuItem.setIcon(new ImageIcon(getClass().getResource("/resources/images/select-ellipse.png")));
			selectMenuItem.addActionListener(new java.awt.event.ActionListener() {
				@SuppressWarnings("unchecked")
				public void actionPerformed(java.awt.event.ActionEvent e) {
					TreePath[] selection = getImageObjectTree().getSelectionPaths();
					ArrayList<Node<ImageObject>> nodes =new ArrayList<Node<ImageObject>>();
					for (int i=0; i<selection.length; i++) {
						nodes.add( 
							(Node<ImageObject>) selection[i].getLastPathComponent());
					}
					model.selectObjects(nodes);
				}
			});
		}
		return selectMenuItem;
	}

	/**
	 * This method initializes sortMenuItem	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */
	private JMenuItem getSortMenuItem() {
		if (sortMenuItem == null) {
			sortMenuItem = new JMenuItem();
			sortMenuItem.setText("sort children");
			sortMenuItem.setIcon(new ImageIcon(getClass().getResource("/resources/images/sort.gif")));
			sortMenuItem.addActionListener(new java.awt.event.ActionListener() {
				@SuppressWarnings("unchecked")
				public void actionPerformed(java.awt.event.ActionEvent e) {
					TreePath[] selection = getImageObjectTree().getSelectionPaths();
					for (int i=0; i<selection.length; i++) {
						Node<ImageObject> node = 
							(Node<ImageObject>) selection[i].getLastPathComponent();
						model.sortChildren(node);
					}
				}
			});
		}
		return sortMenuItem;
	}

	/**
	 * This method initializes OptionsMenu	
	 * 	
	 * @return javax.swing.JMenu	
	 */
	private JMenu getOptionsMenu() {
		if (OptionsMenu == null) {
			OptionsMenu = new JMenu();
			OptionsMenu.setText("Options");
			OptionsMenu.add(getEditOptionsMenuItem());
		}
		return OptionsMenu;
	}

	/**
	 * This method initializes editOptionsMenuItem	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */
	private JMenuItem getEditOptionsMenuItem() {
		if (editOptionsMenuItem == null) {
			editOptionsMenuItem = new JMenuItem();
			editOptionsMenuItem.setText("edit");
			editOptionsMenuItem.setIcon(new ImageIcon(getClass().getResource("/resources/images/preferences-system.png")));
			editOptionsMenuItem.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					getEditOptionsButton().doClick();
				}
			});
		}
		return editOptionsMenuItem;
	}

	/**
	 * This method initializes editOptionsButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getEditOptionsButton() {
		if (editOptionsButton == null) {
			editOptionsButton = new JButton();
			editOptionsButton.setIcon(new ImageIcon(getClass().getResource("/resources/images/preferences-system.png")));
			editOptionsButton.setMaximumSize(new Dimension(44, 44));
			editOptionsButton.setMinimumSize(new Dimension(44, 44));
			editOptionsButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					model.getOptions().show();
				}
			});
		}
		return editOptionsButton;
	}

}  //  @jve:decl-index=0:visual-constraint="10,10"
