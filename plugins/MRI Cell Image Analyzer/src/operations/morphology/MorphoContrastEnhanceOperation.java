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

import ij.IJ;
import ij.ImagePlus;
import ij.WindowManager;
import operations.image.ImageCalculationOperation;
/**
 * Enhances the contrast of an image by adding the result of the top-hat filter and subtracting the result of the
 * bottom hat filter.
 * 
 * @author Volker Bäcker
 */
public class MorphoContrastEnhanceOperation extends MorphologyBaseOperation {

	private static final long serialVersionUID = 1L;

	public void runFilter() {
		if (this.getMorphologyType()=="8-bit" && result.getType() != ImagePlus.GRAY8 && result.getType() != ImagePlus.COLOR_256) IJ.run("8-bit");
		if (this.getMorphologyType()=="32-bit" && result.getType() != ImagePlus.GRAY32 && result.getType()!=ImagePlus.COLOR_RGB) IJ.run("32-bit");
		ImagePlus tmpImage = this.copyImage(WindowManager.getCurrentImage(), WindowManager.getCurrentImage().getTitle() + " tmp");

		TopHatGrayOperation top = new TopHatGrayOperation();
		top.setShowResult(false);
		top.setInputImage(result);
		top.setMorphologyType(this.getMorphologyType());
		top.setRadius(this.getRadius());
		top.setStructuringElement(this.getStructuringElement());
		top.setStructuringElementType(this.getStructuringElementType());
		top.run();
		
		BottomHatGrayOperation bottom = new BottomHatGrayOperation();
		bottom.setShowResult(false);
		bottom.setInputImage(result);
		bottom.setMorphologyType(this.getMorphologyType());
		bottom.setRadius(this.getRadius());
		bottom.setStructuringElement(this.getStructuringElement());
		bottom.setStructuringElementType(this.getStructuringElementType());
		bottom.run();
		
		boolean floatResult = (this.isFloatMorphology()) ? true : false; 
		ImageCalculationOperation operation = new ImageCalculationOperation();
		operation.setShowResult(false);
		operation.setOperator("Add");
		operation.setFloatResult(floatResult);
		operation.setInputImage(tmpImage);
		operation.setSecondInputImage(top.getResult());
		operation.run();
		
		result = operation.getResult();
		
		operation.setInputImage(result);
		operation.setSecondInputImage(bottom.getResult());
		operation.setOperator("Subtract");
		operation.setShowResult(false);
		operation.run();
		
		WindowManager.setTempCurrentImage(operation.getResult());
	}
	
	public String operator() {
		return "morpho-contrast-enhance";
	}

}
