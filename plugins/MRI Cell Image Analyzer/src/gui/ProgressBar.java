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

import java.util.Observable;

/**
 * A progress bar that can be displayed in its own window. The client can set a text,
 * the min and max values and the current progress.
 *
 * @author	Volker Baecker
 **/
public class ProgressBar extends Observable {
	protected int min;
	protected int max;
	protected int progress;
	protected String text;
	protected boolean closeWhenFinished;
	protected ProgressBarView view;
	
	public ProgressBar(int min, int max, String text) {
		this.setMin(min);
		this.setMax(max);
		this.setProgress(min);
		this.setText(text);
	}

	public int getProgress() {
		return progress;
	}

	public void setProgress(int current) {
		this.progress = current;
		this.changed("progress");
	}

	public int getMax() {
		return max;
	}

	public void setMax(int max) {
		this.max = max;
		this.changed("max");
	}

	public int getMin() {
		return min;
	}

	public void setMin(int min) {
		this.min = min;
		this.changed("min");
	}
	
	public void changed(String string) {
		this.setChanged();
		this.notifyObservers(string);
		this.clearChanged();
	}

	public String getText() {
		return text;
	}
	
	public void setText(String aText) {
		this.text = aText;
		this.changed("text");
	}

	public boolean isCloseWhenFinished() {
		return closeWhenFinished;
	}

	public void setCloseWhenFinished(boolean closeWhenFinished) {
		this.closeWhenFinished = closeWhenFinished;
	}

	public ProgressBarView getView() {
		if (this.view==null) this.view  = new ProgressBarView(this);
		return view;
	}
}
