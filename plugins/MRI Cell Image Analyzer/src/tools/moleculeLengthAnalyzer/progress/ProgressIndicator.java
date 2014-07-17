
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
 * Objects can report the progress of a task to an object implementing the
 * ProgressIndicator interface. 
 * 
 * @author baecker
 *
 */
public interface ProgressIndicator {
	abstract public int getProgress();
	abstract public void setProgress(int progress);
	public abstract void setMaxProgress(int max);
	public abstract int getMaxProgress();
	public abstract void stopWithError();
}
