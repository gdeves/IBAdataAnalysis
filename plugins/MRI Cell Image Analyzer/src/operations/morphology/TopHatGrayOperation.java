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
package operations.morphology;

import operations.image.ImageCalculationOperation;
import ij.IJ;
import ij.ImagePlus;
import ij.WindowManager;
/**
 * Extract bright regions. Subtracts the result of the open operation from the input image.
 * 
 * @author Volker Bäcker
 */
public class TopHatGrayOperation extends MorphologyBaseOperation {
	private static final long serialVersionUID = 1L;

	public void runFilter() {
		if (this.getMorphologyType()=="8-bit" && result.getType() != ImagePlus.GRAY8 && result.getType() != ImagePlus.COLOR_256) IJ.run("8-bit");
		if (this.getMorphologyType()=="32-bit" && result.getType() != ImagePlus.GRAY32 && result.getType()!=ImagePlus.COLOR_RGB) IJ.run("32-bit");
		ImagePlus tmpImage = this.copyImage(WindowManager.getCurrentImage(), WindowManager.getCurrentImage().getTitle() + " tmp");
		OpenGrayOperation operation = new OpenGrayOperation();
		operation.setShowResult(false);
		operation.setInputImage(result);
		operation.setMorphologyType(this.getMorphologyType());
		operation.setRadius(this.getRadius());
		operation.setStructuringElement(this.getStructuringElement());
		operation.setStructuringElementType(this.getStructuringElementType());
		operation.run();
		this.result = operation.getResult();
		boolean floatResult = (this.isFloatMorphology()) ? true : false; 
		ImageCalculationOperation subtract = new ImageCalculationOperation();
		subtract.setShowResult(false);
		subtract.setOperator("Subtract");
		subtract.setFloatResult(floatResult);
		subtract.setInputImage(tmpImage);
		subtract.setSecondInputImage(this.result);
		subtract.run();
		WindowManager.setTempCurrentImage(subtract.getResult());
	}
	
	public String operator() {
		return "top-hat";
	}

}
