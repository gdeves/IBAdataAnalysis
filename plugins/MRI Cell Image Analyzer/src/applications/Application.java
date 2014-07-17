/* This file is part of MRI Cell Image Analyzer.
 * (c) 2005-2007 Volker B�cker
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
package applications;

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
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import javax.swing.SwingUtilities;
import operations.Operation;
import gui.CellImageAnalyzer;
import gui.Options;
import gui.Parameter;
import gui.options.Option;

/**
 *  This class represents visual scripting applications. It contains the 
 *  visual script interpreter as well as everything needed to build, save
 *  and load applications.
 *  
 * @author	Volker Baecker
 **/
public class Application extends Operation implements Serializable {

	private static final long serialVersionUID = -3098504249448555530L;
	
	protected ArrayList<Operation> operations;
	protected ApplicationView applicationView;
	protected Parameter parameter;
	static protected ArrayList<ApplicationView> openApplicationViews = new ArrayList<ApplicationView>();
	protected HashMap<Integer, ArrayList<int[]>> deleteAfterMap;
	protected int programCounter;
	protected boolean stopped;
	
	String path;
	
	public Application() {
		super();
	}

	public void initialize() {
		operations = new ArrayList<Operation>();
	}

	public void stop() {
		stopped = true;
		this.cleanUp();
	}
	
	protected void setupDeleteAfterOperation() {
		int[][] deleteAfterOperation;
		deleteAfterOperation = new int[operations.size()][];
		for (int i=0; i<operations.size(); i++) {
			Operation operation = (operations.get(i));
			int[] results = null;
			for(int j=0; j<operation.getNumberOfResults(); j++) {
				if (results==null) {
					results = new int[operation.getNumberOfResults()];
					deleteAfterOperation[i] = results;
				}
				for (int k=i+1; k<operations.size(); k++) {
					Operation target = (operations.get(k));
					int targetIndex = k;
					if (this.isInLoop(target)) {
						targetIndex = indexOfNextLoopEndAfter(target);
					}
					for (int l=0; l<target.getNumberOfParameter(); l++) {
						int[] map = target.getInputMapForParameter(l);
						if (map[0]==i && map[1]==j) {
							deleteAfterOperation[i][j] = targetIndex;
						}
					}
				}
			}
		}
		setupDeleteAfterMap(deleteAfterOperation);
	}

	private void setupDeleteAfterMap(int[][] deleteAfterOperation) {
		deleteAfterMap = new HashMap<Integer, ArrayList<int[]>>();
		for (int i=0; i<deleteAfterOperation.length; i++) {
			int[] list = deleteAfterOperation[i];
			if (list==null) continue;
			for (int j=0; j<list.length; j++) {
				int delAfter = list[j];
				int resultNr = j;
				int fromOp = i;
				Integer key = new Integer(delAfter);
				if (!deleteAfterMap.containsKey(key)) {
					deleteAfterMap.put(key, new ArrayList<int[]>());
				}
				int[] map = new int[2];
				map[0] = fromOp;
				map[1] = resultNr;
				deleteAfterMap.get(key).add(map);
			}
		}
	}

	public void doIt() {
		this.cleanUp();
		this.resetPathOptions();
		if (stopped) {
			stopped = false;
			return;
		}
		this.setupDeleteAfterOperation();
		int inputIndex = 1;
		this.setProgressMax(operations.size());
		for (programCounter = 0; programCounter < operations.size(); programCounter++) {
			if (applicationView!=null) applicationView.repaint();
			this.setProgress(programCounter);
			Operation operation = (operations.get(programCounter));
			if (stopped) {
				stopped = false;
				return;
			}
			operation.execute();
			try {
				operation.getTask().join();
				if (!operation.isLoopEnd()) {
					this.cleanupResultsAfter(programCounter);
				}
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
			if (programCounter < operations.size() - 1) {
				Operation nextOperation = (operations
						.get(programCounter + 1));
				for (int j = 0; j < nextOperation.getNumberOfParameter(); j++) {
					int[] inputMap = nextOperation.getInputMapForParameter(j);
					Operation sourceOperation = operations
							.get(inputMap[0]);
					Class<? extends Operation> sourceClass = sourceOperation.getClass();
					Class<? extends Operation> destClass = nextOperation.getClass();
					Class<?>[] paramTypes = new Class[1];
					paramTypes[0] = nextOperation.getParameterTypes()[j];
					String getMethodName = sourceOperation.getResultNames()[inputMap[1]];
					Method getMethod;
					try {
						getMethod = sourceClass.getMethod("get" + getMethodName, (Class[])null);
						String setMethodName = nextOperation
								.getParameterNames()[j];
						Method setMethod = destClass.getMethod("set"
								+ setMethodName, paramTypes);
						Object[] input = new Object[1];
						input[0] = getMethod.invoke(sourceOperation,
								new Object[0]);
						setMethod.invoke(nextOperation, input);
						inputIndex++;
					} catch (SecurityException e) {
						e.printStackTrace();
					} catch (NoSuchMethodException e) {
						e.printStackTrace();
					} catch (IllegalArgumentException e) {
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					} catch (InvocationTargetException e) {
						e.printStackTrace();
					}
					Thread.yield();
				}
			}
		}
		this.setProgress(operations.size());
		if (applicationView!=null) {
			applicationView.repaint();
		}
	}
	
	private void cleanupResultsAfter(int i) {
		if (i==0) return;
		Integer key = new Integer(i);
		if (!deleteAfterMap.containsKey(key)) return;
		ArrayList<int[]> list = deleteAfterMap.get(key);
		for(int[] map : list) {
			int operationNr = map[0];
			int resultNr = map[1];
			resetResult(operationNr, resultNr);
		}
		System.gc();
	}

	private void resetResult(int operationNr, int resultNr) {
		Operation operation = (operations
				.get(operationNr));
		String[] resultNames = operation.getResultNames();
			Class<?>[] resultType = new Class[1];
			resultType[0] = operation.getResultTypes()[resultNr];
			Method setMethod;
				try {
					setMethod = operation.getClass().getMethod("set"
							+ resultNames[resultNr], resultType);
					Object[] param = new Object[1];
					param[0] = null;
					setMethod.invoke(operation, param);
				} catch (SecurityException e) {
					e.printStackTrace();
				} catch (NoSuchMethodException e) {
					e.printStackTrace();
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					e.printStackTrace();
				}
	}

	private void cleanUp() {
		for (int i = 0; i < operations.size(); i++) {
			Operation operation = (operations
					.get(i));
			if (operation.isLoop()) {
				operation.reset();
			}
			String[] resultNames = operation.getResultNames();
			for (int j=0; j<resultNames.length; j++) {
				resetResult(i, j);
			}
		}
		System.gc();
	}

	public void cleanupResultsAfter(Operation anOperation) {
		int startIndex = this.getOperations().indexOf(anOperation) + 1;
		for (int i = startIndex; i < operations.size(); i++) {
			Operation operation = (operations
					.get(i));
			String[] resultNames = operation.getResultNames();
			for (int j=0; j<resultNames.length; j++) {
				resetResult(i, j);
			}
		}
		System.gc();
	}
	
	protected void setupOptions() {
		options = new Options();
		options.setName(this.name() + " options");
		for (Operation op : operations){
			op.setApplication(this);
			ArrayList<Option> someOptions = op.getOptions().getOptions();
			for (Option option : someOptions) {
				options.add(option);
			}
		}
		this.optionsNames = new String[options.getOptions().size()];
		int index = 0;
		for (Option option : options.getOptions()) {
			this.getOptionsNames()[index] = option.getName();
			index++;
		}
	}
	
	public void showApplicationView() {
		ApplicationView view = this.getApplicationView();
		view.setVisible(true);
	}

	public ApplicationView getApplicationView() {
		if (applicationView==null) {
			applicationView = new ApplicationView(this);
			openApplicationViews.add(applicationView);
		}
		return applicationView;
	}
	
	public ArrayList<Operation> getOperations() {
		return operations;
	}
	
	public boolean isApplication() {
		return true;
	}

	public void showParameterViewForOperation(Operation anOperation) {
		parameter = new Parameter(anOperation, this);
		parameter.show();
	}
	
	public String name() {
		if (name==null) {
			name = "";
		}
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
		this.setupOptions();
	}

	public static void checkForDropAction(Operation operation, MouseEvent e) {
		for (int i=0; i<openApplicationViews.size(); i++) {
			ApplicationView appView = openApplicationViews.get(i);
			Rectangle viewBounds = appView.getBounds();
			Point point = e.getPoint(); 
			SwingUtilities.convertPointToScreen(point,
					e.getComponent());
			if (viewBounds.contains(point)) {
				Point localPoint = SwingUtilities.convertPoint(e.getComponent(), e.getPoint(), appView.getJPanel());
				appView.operationDropedAt(operation, localPoint);
				break;
			}
		}
		
	}

	public void applicationViewClosed(ApplicationView view) {
		openApplicationViews.remove(view);
	}

	public void moveToFront(ApplicationView view) {
		openApplicationViews.remove(view);
		openApplicationViews.add(0, view);
		
	}

	public void addOperationAfterIndex(Operation operation, int addAfterIndex) {
		operation.setApplication(this);
		this.getOperations().add(addAfterIndex+1, operation);
		adjustInputStartingAtIndex(addAfterIndex+1, 1);
		operation.view().dispose();
		operation.setShowResult(false);
		rebuildViews();
		operation.changed("input map");
	}

	private void adjustInputStartingAtIndex(int index, int delta) {
		for (int i=index; i<this.getOperations().size();i++) {
			Operation operation = this.getOperations().get(i);
			ArrayList<int[]> inputMap = operation.getInputMap();
			for (int j=0; j<inputMap.size(); j++) {
				int[] map = inputMap.get(j);
				if (map[0]==-1) continue;
				if (delta==-1 && map[0]==index) {
					map[0] = -1;
					map[1] = -1;
					inputMap.set(j, map);
					continue;
				}
				if (map[0]<index) continue;
				map[0] = map[0] + delta;
				inputMap.set(j, map);
			}
			operation.setInputMap(inputMap);
			operation.changed("input map");
		}
	}

	private void rebuildViews() {
		if (applicationView==null) return;
		Rectangle bounds = applicationView.getBounds();
		this.applicationView.dispose();
		applicationView = null;
		this.showApplicationView();
		this.getApplicationView().setBounds(bounds);
		this.applicationView.validate();
		this.setupOptions();
		bounds = this.view().getBounds();
		this.view().dispose();
		view = null;
		this.show();
		this.view().setBounds(bounds);
	}

	public void removeOperation(Operation operation) {
		int index = operations.indexOf(operation);
		operations.remove(operation);
		// this.resetInputStartingAtIndex(index);
		adjustInputStartingAtIndex(index, -1);
		operation.view().dispose();
		if (this.applicationView!=null) {
			this.rebuildViews();
		}
		operation.changed("input map");
	}
	
	public void printOn(BufferedWriter out) throws IOException {
		out.write(this.getClass().getName() + "\n");
		out.write(this.name() + "\n");
		Iterator<Operation> it = this.getOperations().iterator();
		while(it.hasNext()) {
			Operation anOperation = it.next();
			anOperation.printOn(out);
		}
	}
	
	public void setupFrom(BufferedReader in) throws IOException {
		String applicationName = in.readLine();
		this.setName(applicationName);
		this.operations = new ArrayList<Operation>();
		Operation op = null;
		while((op = Operation.readFrom(in))!=null) {
			this.operations.add(op);
		}
	}
	
	public void save(String path) throws IOException {
		FileWriter fileWriter;
		BufferedWriter out = null;
		try {
			fileWriter = new FileWriter(path);
			out = new BufferedWriter(fileWriter);
			this.printOn(out);
			this.setPath(path);
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
		app.rebuildApplicationMenu();
	}

	public static Application load(String path) {
		FileReader fileReader;
		BufferedReader in = null;
		Application app = null;
		try {
			fileReader = new FileReader(path);
			in = new BufferedReader(fileReader);
			app = (Application) Application.readFrom(in);
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
		app.setPath(path);
		app.setName(nameFromPath(path));
		app.setupOptions();
		return app;
	}
	
	public static String nameFromPath(String aPath) {
		String result;
		String separator = "/";
		if (aPath.contains("\\")) {
				separator = "\\";
		}
		int startIndex = aPath.lastIndexOf(separator) + 1;
		int endIndex = aPath.lastIndexOf(".");
		result = aPath.substring(startIndex, endIndex);
		return result;
	}

	public String getFilename() {
		return this.name() + fileExtension();
	}
	
	static public String fileExtension() {
		return ".cia";
	}

	public static String baseFolder() {
		return "_applications";
	}

	public String getPath() {
		if (path==null) {
			File file = new File("." + File.separator + baseFolder() + File.separator + this.name());
			path = file.getAbsolutePath() + fileExtension();
		}
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public int getProgramCounter() {
		return programCounter;
	}

	public void setProgramCounter(int programCounter) {
		this.programCounter = programCounter;
	}

	public void backToOperationNumber(int i) {
		this.programCounter = i;
		
	}

	public void resetPathOptions() {
		Iterator<Operation> it = this.operations.iterator();
		while (it.hasNext()) {
			Operation op = it.next();
			op.resetPathOptions();
			op.setPathOptionsFromUser();
			if (stopped) break;
		}
		
	}

	public int indexOfNextLoopEndAfter(Operation operation) {
		int opIndex = this.operations.indexOf(operation);
		for (int i = opIndex; i<this.operations.size(); i++) {
			Operation anOperation = this.operations.get(i);
			if (anOperation.isLoopEnd()) return i;
		}
		return operations.size()-1;
	}

	public int getCurrentIndexInLoopFor(Operation operation) {
		int level = 0;
		int result = -1;
		int opIndex = this.operations.indexOf(operation);
		for (int i = opIndex; i>-1; i--) {
			Operation anOperation = this.operations.get(i);
			if (anOperation.isLoop() && level==0) {
				result = anOperation.getCurrentIndex();
			}
			if (anOperation.isLoopEnd()) level++;
			if (anOperation.isLoop()) level--;
		}
		return result;
	}
	
	private boolean isInLoop(Operation target) {
		return this.getCurrentIndexInLoopFor(target) != -1;
	}
	
	public static String run(String name) {
		String applicationName = (String)CellImageAnalyzer.getCurrent().getApplicationFiles().get(name);
		Application app = Application.load(applicationName);
		app.run();
		return "executed application " + name;
	}
}