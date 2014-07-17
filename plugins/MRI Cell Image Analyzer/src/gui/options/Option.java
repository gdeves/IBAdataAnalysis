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
package gui.options;

import gui.ListEditor;
import help.HelpSystem;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.Observable;
import java.util.Vector;
import operations.Operation;

/**
 * An option of the visual scripting framework. Option can be used to represent
 * options of type String or options of a numerical type. It is the superclass of 
 * other option classes representing options of other types. Generally an option 
 * has a name and a value. It can have  a minimal and a maximal allowed value and 
 * a help text.
 * 
 * @author	Volker Baecker
 **/
public class Option extends Observable implements Serializable {
	private static final long serialVersionUID = -3283260319080715983L;
	protected OptionView view;
	protected String name;
	protected Double min;
	protected Double max;
	protected String value;
	protected String shortHelpText;
	private boolean isForFilename = false;
	
	public Option() {
		this.initialize();
	}
	
	protected void initialize() {
		name = "unnamed option";
	}
	
	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
		this.changed("value");
	}
	
	public OptionView newView() {
		OptionView newView = new OptionView(this);
		this.addObserver(newView);
		return newView;
	}
	
	public double getDoubleValue() {
		double doubleValue = (Double.parseDouble(this.getValue()));
		return doubleValue;
	}
	
	public float getFloatValue() {
		float floatValue = (Float.parseFloat(this.getValue()));
		return floatValue;
	}
	
	public void setFloatValue(float value) {
		this.setValue(Float.toString(value));
	}
	
	public void setDoubleValue(double value) {
		this.setValue(Double.toString(value));
	}
	
	public void setIntegerValue(int value) {
		this.setValue(Integer.toString(value));
	}
	
	public void setBooleanValue(boolean value) {
		this.setValue(Boolean.toString(value));
	}
	
	public boolean getBooleanValue() {
		return false;
	}
	
	public float[] getMatrixValue() {
		float[] result = {};
		return result;
	}
	
	public Vector<File> getListValue() {
		Vector<File> result = new Vector<File>();
		return result;
	}
	
	public int getIntegerValue() {
		int integerValue = (Integer.parseInt(this.getValue()));
		return integerValue;
	}

	public Double getMax() {
		return max;
	}

	public void setMax(double max) {
		this.max = new Double(max);
	}

	public Double getMin() {
		return min;
	}

	public void setMin(double min) {
		this.min = new Double(min);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
		this.changed("name");
	}

	private void changed(String string) {
		this.setChanged();
		this.notifyObservers(string);
		this.clearChanged();
	}

	public OptionView getView() {
		if (view==null) {
			view = new OptionView(this);
			this.addObserver(view);
		}
		return view;
	}

	public void setView(OptionView view) {
		this.view = view;
	}

	public String getRangeString() {
		if (this.getMin()==null && this.getMax()==null) return "";
		String min = this.getMin() + "";
		if (this.getMin()==null) {
			min = "-";
		}
		String max = this.getMax() + "";
		if (this.getMax()==null) {
			max = "-";
		}
		String result = "[" + min + ", " + max + "]";
		return result;
	}

	public String getShortHelpText() {
		if (this.shortHelpText==null) {
			this.shortHelpText = "";
		}
		return shortHelpText;
	}
	
	public void setShortHelpText(String shortHelpText) {
		this.shortHelpText = shortHelpText;
	}

	public boolean isBoolean() {
		return false;
	}
	
	public boolean isChoice() {
		return false;
	}
	
	public String[] getChoices() {
		return null;
	}

	public boolean isMatrix() {
		return false;
	}
	
	public boolean isList() {
		return false;
	}
	
	public void printOn(BufferedWriter out) throws IOException {
			out.write(this.getClass().getName() + "\n");
			out.write(this.getName() + "\n" );
			out.write(this.getValue()+"\n");
			out.write(this.getMin() + "\n");
			out.write(this.getMax() + "\n");
			out.write(this.getShortHelpText() + "\n");
	}
	
	public static Option readFrom(BufferedReader in) throws IOException {
		IOException error = new IOException();
		String className = in.readLine();
		Option option = null;
		try {
			String[] nameComponents = className.split("\\.");
			String lastNameComponent = nameComponents[nameComponents.length-1]; 
			String name = "gui.options." + lastNameComponent;
			option = (Option)Class.forName(name).newInstance();
			option.setupFrom(in);
		} catch (InstantiationException e) {
			error.setStackTrace(e.getStackTrace());
			throw error;
		} catch (IllegalAccessException e) {
			error.setStackTrace(e.getStackTrace());
			throw error;
		} catch (ClassNotFoundException e) {
			error.setStackTrace(e.getStackTrace());
			throw error;
		}
		return option;
	}
	
	public void setupFrom(BufferedReader in) throws IOException {
		String optionName = in.readLine();
		String value = in.readLine();
		String input = in.readLine();
		Double min; 
		if (input.equals("null")) {
			min = null;
		} else {
			min = Double.valueOf(input);
		}
		input = in.readLine();
		Double max; 
		if (input.equals("null")) {
			max = null;
		} else {
			max = Double.valueOf(input);
		}
		String shortHelpText = in.readLine();
		this.setName(optionName);
		this.setValue(value);
		if (min!=null) {
			this.setMin(min.doubleValue());
		}
		if (max!=null) {
			this.setMax(max.doubleValue());
		}
		this.setShortHelpText(shortHelpText);
	}

	public void openHelpFor(String operationName) {
		HelpSystem help = HelpSystem.getCurrent();
		help.openHelpFor(operationName, this.getName());
		
	}

	public ListEditor openEditor() {
		return null;		
	}

	public void browse(Operation operation) {
		operation.browseFileForOption(this);
	}

	public boolean isForFilename() {
		return isForFilename;
	}
	
	public void beForFilename() {
		isForFilename = true;
	}
}
