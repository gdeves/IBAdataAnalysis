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

import gui.options.Option;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import operations.Operation;

/**
 * A container for the options of an operation.
 *
 * @author	Volker Baecker
 **/
public class Options implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2743359575928877857L;
	protected ArrayList<Option> options;
	protected OptionsView view;
	protected String name;
	protected Operation operation;
	protected OptionsDialogView dialogView;

	public String getName() {
		if (name == null) name = "options";
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	public Options() {
		options = new ArrayList<Option>();
	}
	
	public void add(Option option) {
		options.add(option);
	}

	public ArrayList<Option> getOptions() {
		return options;
	}
	
	public OptionsView view() {
		if (view == null) view = new OptionsView(this);
		return view;
	}
	
	public OptionsDialogView dialogView() {
		if (dialogView == null) dialogView = new OptionsDialogView(this);
		dialogView.setModal(true);
		return dialogView;
	}
	
	public void printOn(BufferedWriter out) throws IOException {
		Iterator<Option> it = this.options.iterator();
		while(it.hasNext()) {
			Option anOption = it.next();
			out.write("[option]\n");
			anOption.printOn(out);
		}
		out.write("[end options]\n");
	}
	
	public void setupFrom(BufferedReader in) throws IOException {
		int index = 0;
		String line = null;
		while((line = in.readLine()) != null) {
			if (line.equals("[end options]")) {
				break;
			}
			Option anOption = Option.readFrom(in); 
			this.getOptions().set(index, anOption);
			index++;
		}
	}

	public Operation getOperation() {
		return operation;
	}

	public void setOperation(Operation operation) {
		this.operation = operation;
	}
}
