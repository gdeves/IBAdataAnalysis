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


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Vector;

/**
 * An option of the visual scripting framework with the possible values 
 * taken from a list of choices.
 * 
 * @author	Volker Baecker
 **/
public class ChoiceOption extends Option {
	private static final long serialVersionUID = -2713111385429233321L;
	protected String[] choices;
	
	public ChoiceOption() {
		super();
		choices = null;
	}
	
	public ChoiceOption(String[] choices) {
		super();
		this.choices = choices;
	}
	
	public ChoiceOption(Vector<String> choices) {
		super();
		this.choices = new String[choices.size()];
		int index = 0;
		for (String choice : choices) {
			this.choices[index] = choice;
			index++;
		}
	}
	
	public String[] getChoices() {
		return choices;
	}
	
	public boolean isChoice() {
		return true;
	}
	
	public void printOn(BufferedWriter out) throws IOException {
			super.printOn(out);
			for(int i=0; i<this.choices.length;i++) {
				String element = choices[i];
				out.write(element + "\n");
			}
			out.write("[end choices]\n");
	}

	public void setupFrom(BufferedReader in) throws IOException {
		super.setupFrom(in);
		String line;
		Vector<String> theChoices = new Vector<String>();
		while((line = in.readLine())!=null) {
			if (line.contains("[end choices]")) {
				break;
			}
			theChoices.add(line);
		}
		choices = new String[theChoices.size()];
		int index = 0;
		for(String element : theChoices) {
			choices[index] = element;
			index++;
		}
	}
}
