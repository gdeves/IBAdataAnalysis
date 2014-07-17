/* This file is part of MRI Cell Image Analyzer.
 * (c) 2005-2007 Volker Bäcker
 * MRI Cell Image Analyzer has been created at Montpellier RIO Imaging
 * www.mri.cnrs.fr
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 * 
 */
package gui;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import javax.swing.JButton;
import javax.swing.SwingUtilities;
import operations.Operation;
import operations.OperationView;

/**
 * An operations box can be used to create a collection of operations that can
 * be saved and opened from the operations menu. 
 *
 * @author	Volker Baecker
 **/
public class OperationsBox {

	protected String title;
	protected ArrayList<Operation> operations;
	protected OperationsBoxView view;
	static protected ArrayList<OperationsBoxView> openOperationsBoxViews = new ArrayList<OperationsBoxView>();
	private String path;
	
	public OperationsBox() {
		this.initialize();
	}
	
	private void initialize() {
		operations = new ArrayList<Operation>();
		view = new OperationsBoxView();
		view.setModel(this);
	}

	public void setTitle(String string) {
		title = string;
	}

	public String getTitle() {
		return title;
	}

	public void add(Operation operation) {
		operations.add(operation);
	}
	
	public void show() {
		view.setTitle(this.getTitle());
		double xSize = 0;
		double ySize = 0;
		int maxButtonWidth = 0;
		int buttonWidth;
		for (int i=0; i<operations.size(); i++) {
			Operation operation = operations.get(i);
			this.view.getJPanel().add(operation.view().getJPanel());
		}
		for (int i=0; i<operations.size(); i++) {
			Operation anOperation = operations.get(i);
			OperationView view = anOperation.view();
			buttonWidth = view.getRunOperationButton().getText().length();
			if (buttonWidth > maxButtonWidth) maxButtonWidth = buttonWidth;
			if (view.getSize().getWidth()>xSize) xSize = view.getSize().getWidth();
			ySize += view.getSize().getHeight()+1;
		}
		for (int i=0; i<operations.size(); i++) {
			Operation anOperation = operations.get(i);
			OperationView view = anOperation.view();
			JButton button = view.getRunOperationButton(); 
			String text = button.getText();
			if (text.length()<maxButtonWidth) {
				while(text.length()<maxButtonWidth) {
					text += " ";
				}
			}
			button.setText(text);
		}
		this.view.setSize((int)xSize+16, Math.min((int)ySize, 600));
		this.view.setVisible(true);
		openOperationsBoxViews.add(view);
	}

	public void operationsBoxViewClosed(OperationsBoxView aView) {
		openOperationsBoxViews.remove(aView);
	}

	public void moveToFront(OperationsBoxView aView) {
		openOperationsBoxViews.remove(view);
		openOperationsBoxViews.add(0, aView);
	}

	public static ArrayList<OperationsBoxView> getOpenOperationsBoxViews() {
		return openOperationsBoxViews;
	}

	public static void checkForDropAction(Operation operation, MouseEvent e) {
		Iterator<OperationsBoxView> it = openOperationsBoxViews.iterator();
		while (it.hasNext()) {
			OperationsBoxView boxView = it.next();
			if (boxView.getState()!=OperationsBoxView.NORMAL || !boxView.isVisible()) continue;
			Rectangle viewBounds = boxView.getBounds();
			Point point = e.getPoint(); 
			SwingUtilities.convertPointToScreen(point,
					e.getComponent());
			if (viewBounds.contains(point)) {
				Point localPoint = SwingUtilities.convertPoint(e.getComponent(), e.getPoint(), boxView.getJPanel());
				boxView.operationDropedAt(operation, localPoint);
				break;
			}
		}
	}
		
		
	public ArrayList<Operation> getOperations() {
		return operations;
	}

	public void addOperationAfterIndex(Operation operation, int addAfterIndex) {
		this.getOperations().add(addAfterIndex+1, operation);
		operation.view().dispose();
		this.rebuildViews();
	}

	private void rebuildViews() {
		Rectangle bounds = this.getView().getBounds();
		this.getView().dispose();
		this.show();
		this.getView().setBounds(bounds);
		this.view.validate();
	}

	public OperationsBoxView getView() {
		return view;
	}

	public static String fileExtension() {
		return ".cio";
	}

	public void save(String path) throws IOException {
		FileWriter fileWriter;
		BufferedWriter out = null;
		try {
			fileWriter = new FileWriter(path);
			out = new BufferedWriter(fileWriter);
			this.printOn(out);
			this.setupPathAndTitle(path);
		} catch (IOException e) {
			if (out!=null) {
				try {
					out.close();
				} catch (IOException e1) {
					// ignore
				}
			}
			throw e;
		} finally {
			if (out!=null) {
				try {
					out.close();
				} catch (IOException e1) {
					// ignore
				}
			}
		}
		CellImageAnalyzer app = CellImageAnalyzer.getCurrent();
		app.rebuildOperationMenu();
	}
	
	public static OperationsBox load(String path) {
		FileReader fileReader;
		BufferedReader in = null;
		OperationsBox box = null;
		try {
			fileReader = new FileReader(path);
			in = new BufferedReader(fileReader);
			box = (OperationsBox) OperationsBox.readFrom(in);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (in!=null) {
				try {
					in.close();
				} catch (IOException e1) {
					// ignore
				}
			}
		}
		box.setupPathAndTitle(path);
		return box;
	}
	
	private void setupPathAndTitle(String path) {
		this.setPath(path);
		File aFile = new File(path);
		String name = aFile.getName();
		name = name.substring(0, name.length()-4);
		this.setTitle(name);
	}

	private static OperationsBox readFrom(BufferedReader in) throws IOException {
		OperationsBox box = new OperationsBox();
		Operation op;
		try {
			while((op = (Operation)Operation.readFrom(in))!=null) {
				box.add(op);
				op.setShowResult(true);
			}
			
		} catch (IOException e) {
			e.printStackTrace();
			box = null;
			throw e;
		}
		return box;
	}

	private void printOn(BufferedWriter out) throws IOException {
		Iterator<Operation> it = operations.iterator();
		while(it.hasNext()) {
			Operation anOperation = it.next();
			anOperation.printOn(out);
		}
	}
	
	public String getPath() {
		if (path==null) {
			File file = new File("." + File.separator + baseFolder() + File.separator + this.getTitle());
			path = file.getAbsolutePath() + fileExtension();
		}
		return path;
	}

	public static String baseFolder() {
		return "_operations";
	}

	public void setPath(String path) {
		this.path = path;
	}

	public void removeOperation(Operation operation) {
		operations.remove(operation);
	}
}
