/* This file is part of MRI Cell Image Analyzer.
 * (c) 2005-2009 INSERM 
 * MRI Cell Image Analyzer has been created at Montpellier RIO Imaging
 * (www.mri.cnrs.fr) by Volker Bäcker
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
package tools.moleculeLengthAnalyzer.progress;

/**
 * A default implementation of the ProgressIndicator interface.
 * 
 * @author baecker
 *
 */
public class DefaultProgressIndicator implements ProgressIndicator {
	private int progress;
	private int maxProgress;

	@Override
	public int getProgress() {
		return progress;
	}

	@Override
	public void setProgress(int progress) {
		this.progress = progress;
	}

	@Override
	public void setMaxProgress(int max) {
		this.maxProgress = max;
	}

	@Override
	public int getMaxProgress() {
		return maxProgress;
	}

	@Override
	public void stopWithError() {
		this.setProgress(maxProgress);
	}

}
