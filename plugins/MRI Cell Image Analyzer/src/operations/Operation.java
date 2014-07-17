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
package operations;

import java.awt.geom.Line2D;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.Observable;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import applications.Application;
import gui.ListEditor;
import gui.Options;
import gui.options.Option;
import help.HelpSystem;
import ij.IJ;
import ij.ImagePlus;
import ij.ImageStack;
import ij.WindowManager;
import ij.macro.Interpreter;
import ij.measure.ResultsTable;
import ij.plugin.Duplicator;
import ij.plugin.filter.Analyzer;
import ij.plugin.frame.Recorder;
import ij.process.ImageProcessor;
import imagejProxies.MRIInterpreter;

/**
 * An operation of the visual scripting system. Operations might call ImageJ code or have their own
 * implementation. Operations can be run directly and they can be used in applications. To create a 
 * new operation subclass this class and override the following methods:<br>
 * <li>intialize<br>
 * declare the parameter types and names, the option names and the result types and names
 * <li>setupOptions<br>
 * to set choices, min and max values, the default value and the short help text
 * <li>connectOptions<br> 
 * if you want to access the options directly via an instance variables of the operation
 * <li>doIt<br>
 * to implement the action of the operation, in case it works on the image as a whole
 * <li>doItForSlice<br>
 * to implement the action of the operation, in case it works on the image sclice by slice
 * <li>showResult<br>
 * if the result to be shown is not the image stored in the instance variable result
 * <li>cleanUpInput<br>
 * to remove references to input parameters other than the input image stored in the instance variable inputImage<br>
 * <br>You must add instance variables and create getters and setters for all but the standard input parameters and results.
 * The declared parameter and result names must begin with an uppercase letter and correspond to the name in the getter and setter.
 * Once the operation is created it has to be added to the method setupOperationClasses to be available in the list
 * of all operations.
 * 
 * @author	Volker Baecker
 **/
public class Operation extends Observable implements Cloneable, Serializable, Runnable {
	private static final long serialVersionUID = -5729953658920751639L;
	protected Class<?>[] parameterTypes;
	protected Class<?>[] resultTypes;
	protected String[] parameterNames;
	protected String[] humanReadableParameterNames;
	protected String[] resultNames;
	protected String[] optionsNames;
	protected ArrayList<int[]> inputMap;
	protected Options options;
	protected boolean keepSource;
	protected OperationView view;
	protected ImagePlus inputImage;
	protected ImagePlus result;
	static public ArrayList<Class<?>> operationClasses;
	private static boolean record = true;
	boolean showResult;
	protected String name;
	protected int progressMax;
	protected int progress;
	protected boolean isRunning;
	protected Thread task;
	protected Application application;
	protected MRIInterpreter interpreter;
	protected boolean isInteractive;
	
	public Operation() {
		try {
			this.initialize();
			this.setupOptions();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	static public ArrayList<Class<?>> operationClasses() {
		if (operationClasses == null) {
			setupOperationClasses();
		}
		return operationClasses;
		
	}
	
	public String[] getHumanReadableParameterNames() {
		if (humanReadableParameterNames==null) {
			humanReadableParameterNames = new String[parameterNames.length];
			for (int i=0; i<parameterNames.length; i++) {
				humanReadableParameterNames[i] = Operation.humanReadableName(parameterNames[i]);
			}
		}
		return humanReadableParameterNames;
	}
	
	private static void setupOperationClasses() {
		operationClasses = new ArrayList<Class<?>>();
		try {
			operationClasses.add(Class.forName("operations.image.AbsOperation"));
			operationClasses.add(Class.forName("operations.control.AcceptOrSkipOrExitOperation"));
			operationClasses.add(Class.forName("operations.processing.AdaptiveErodeOperation"));
			operationClasses.add(Class.forName("operations.image.AddOperation"));
			operationClasses.add(Class.forName("operations.analysis.AnalyzeParticlesOperation"));
			operationClasses.add(Class.forName("operations.image.AndOperation"));
			operationClasses.add(Class.forName("operations.image.ApplyLutOperation"));
			operationClasses.add(Class.forName("operations.segmentation.ApplyRegionalThresholdsOperation"));
			operationClasses.add(Class.forName("operations.stack.AutoAlignImagesOperation"));
			operationClasses.add(Class.forName("operations.stack.AutoAlignSlicesOperation"));
			operationClasses.add(Class.forName("operations.segmentation.AutoThresholdOperation"));
			operationClasses.add(Class.forName("operations.morphology.BinaryCloseOperation"));			
			operationClasses.add(Class.forName("operations.morphology.BinaryDilateOperation"));
			operationClasses.add(Class.forName("operations.morphology.BinaryErodeOperation"));
			operationClasses.add(Class.forName("operations.segmentation.BinaryFillOperation"));
			operationClasses.add(Class.forName("operations.image.BinaryLiveOrDieOperation"));				
			operationClasses.add(Class.forName("operations.morphology.BinaryOpenOperation"));			
			operationClasses.add(Class.forName("operations.morphology.BottomHatGrayOperation"));
			operationClasses.add(Class.forName("objectModelingWorkbench.CalculateAlignmentTranslationsOperation"));
			operationClasses.add(Class.forName("operations.image.ClearImageOperation"));
			operationClasses.add(Class.forName("operations.roi.ClearOperation"));
			operationClasses.add(Class.forName("operations.file.CloseImageOperation"));
			operationClasses.add(Class.forName("operations.control.CloseSessionOperation"));
			operationClasses.add(Class.forName("operations.segmentation.ColorThresholdOperation"));	
			operationClasses.add(Class.forName("operations.image.CombineImagesOperation"));
			operationClasses.add(Class.forName("operations.analysis.ComputeDifferenceOperation"));
			operationClasses.add(Class.forName("operations.analysis.ComputeMomentsOperation"));
			operationClasses.add(Class.forName("operations.morphology.CloseGrayOperation"));
			operationClasses.add(Class.forName("operations.image.ConvertImageTypeOperation"));
			operationClasses.add(Class.forName("operations.processing.ConvolveOperation"));
			operationClasses.add(Class.forName("operations.image.CopyImageOperation"));
			operationClasses.add(Class.forName("operations.channel.CreateCompositeOperation"));
			operationClasses.add(Class.forName("operations.roi.CreateSelectionOperation"));
			operationClasses.add(Class.forName("operations.roi.CropOperation"));
			operationClasses.add(Class.forName("operations.lines.CutLinesToSizeOperation"));
			operationClasses.add(Class.forName("operations.stack.DeltaFUpOperation"));
			operationClasses.add(Class.forName("operations.morphology.DilateGrayOperation"));
			operationClasses.add(Class.forName("operations.processing.DilateOperation"));			
			operationClasses.add(Class.forName("operations.image.DivideOperation"));
			operationClasses.add(Class.forName("operations.image.DrawCubeOperation"));
			operationClasses.add(Class.forName("operations.image.DrawOperation"));
			operationClasses.add(Class.forName("operations.stack.DuplicateSliceOperation"));
			operationClasses.add(Class.forName("operations.processing.EnhanceContrastOperation"));
			operationClasses.add(Class.forName("operations.processing.EnhanceSpotsOperation"));
			operationClasses.add(Class.forName("operations.roi.EnlargeSelectionOperation"));
			operationClasses.add(Class.forName("operations.segmentation.EntropyThresholdOperation"));
			operationClasses.add(Class.forName("operations.morphology.ErodeGrayOperation"));
			operationClasses.add(Class.forName("operations.processing.ErodeOperation"));
			operationClasses.add(Class.forName("operations.image.ExpOperation"));
			operationClasses.add(Class.forName("operations.segmentation.FcmThresholdOperation"));
			operationClasses.add(Class.forName("operations.processing.FilterHorizontalLinesOperation"));
			operationClasses.add(Class.forName("operations.analysis.FilterLongObjectsOperation"));
			operationClasses.add(Class.forName("operations.analysis.FilterMeasurementsOperation"));
			operationClasses.add(Class.forName("operations.image.FillImageOperation"));
			operationClasses.add(Class.forName("operations.roi.FillOperation"));
			operationClasses.add(Class.forName("operations.analysis.Find3dObjectsOperation"));
			operationClasses.add(Class.forName("operations.processing.FindAndSubtractBackgroundOperation"));
			operationClasses.add(Class.forName("operations.processing.FindEdgesOperation"));
			operationClasses.add(Class.forName("operations.analysis.FindEndsOperation"));
			operationClasses.add(Class.forName("operations.analysis.FindObjectsOperation"));
			operationClasses.add(Class.forName("operations.analysis.FindObjectsFloodFillOperation"));
			operationClasses.add(Class.forName("operations.analysis.FindObjectsRedirectingOperation"));
			operationClasses.add(Class.forName("operations.tracing.FindSeedPointsOperation"));
			operationClasses.add(Class.forName("operations.tracing.FindSkeletonBranchingPointsOperation"));
			operationClasses.add(Class.forName("operations.tracing.FindSkeletonEndPointsOperation"));
			operationClasses.add(Class.forName("operations.control.ForeachImageDoOperation"));
			operationClasses.add(Class.forName("operations.control.ForeachImageEndOperation"));
			operationClasses.add(Class.forName("operations.control.ForeachImageInListDoOperation"));
			operationClasses.add(Class.forName("operations.control.ForeachObjectDoOperation"));
			operationClasses.add(Class.forName("operations.control.ForeachObjectEndOperation"));	
			operationClasses.add(Class.forName("operations.control.ForeachPairOfImagesInListDoOperation"));
			operationClasses.add(Class.forName("operations.analysis.FractalBoxCountOperation"));
			operationClasses.add(Class.forName("operations.clustering.FuzzyCMeansClusteringOperation"));
			operationClasses.add(Class.forName("operations.image.GammaAdjustOperation"));
			operationClasses.add(Class.forName("operations.processing.GaussianBlurOperation"));
			operationClasses.add(Class.forName("operations.file.GetCurrentImageOperation"));	
			operationClasses.add(Class.forName("operations.analysis.GetImageFromHessianOperation"));
			operationClasses.add(Class.forName("operations.control.GetImageListOperation"));
			operationClasses.add(Class.forName("operations.control.GetImageNameOperation"));
			operationClasses.add(Class.forName("operations.analysis.GetVectorImageFromHessianOperation"));
			operationClasses.add(Class.forName("operations.segmentation.GreyscaleWatershedOperation"));
			operationClasses.add(Class.forName("operations.processing.GridMaximaOperation"));
			operationClasses.add(Class.forName("operations.analysis.HeightOfProfileMaximumOperation"));	
			operationClasses.add(Class.forName("operations.analysis.HessianOperation"));
			operationClasses.add(Class.forName("operations.file.HideImageOperation"));
			operationClasses.add(Class.forName("operations.control.IfFalseSkipOperation"));
			operationClasses.add(Class.forName("operations.image.ImageCalculationOperation"));
			operationClasses.add(Class.forName("operations.segmentation.IntensityPortionThresholdOperation"));
			operationClasses.add(Class.forName("operations.image.InvertImageOperation"));
			operationClasses.add(Class.forName("operations.roi.InverseSelectionOperation"));
			operationClasses.add(Class.forName("operations.reporting.JoinResultTablesOperation"));
			operationClasses.add(Class.forName("operations.lines.LineHoughTransformOperation"));			
			operationClasses.add(Class.forName("operations.analysis.LocalSnrOperation"));		
			operationClasses.add(Class.forName("operations.segmentation.LocalThresholdOperation"));
			operationClasses.add(Class.forName("operations.image.LogOperation"));
			operationClasses.add(Class.forName("operations.MacroOperation"));
			operationClasses.add(Class.forName("operations.roi.MakeBandSelectionOperation"));
			operationClasses.add(Class.forName("operations.image.MaxOperation"));
			operationClasses.add(Class.forName("operations.processing.MaximumFilterOperation"));
			operationClasses.add(Class.forName("operations.processing.MeanFilterOperation"));
			operationClasses.add(Class.forName("operations.segmentation.MeanThresholdOperation"));
			operationClasses.add(Class.forName("operations.analysis.MeasureOperation"));
			operationClasses.add(Class.forName("operations.analysis.MeasureAllSlicesOperation"));	
			operationClasses.add(Class.forName("operations.analysis.MeasureEntropyOperation"));			
			operationClasses.add(Class.forName("operations.analysis.MeasureIntensityValuesOperation"));
			operationClasses.add(Class.forName("operations.analysis.MeasureMaskAreaOperation"));
			operationClasses.add(Class.forName("operations.analysis.MeasureMaxCubeOperation"));
			operationClasses.add(Class.forName("operations.tracing.MeasureMeanDiameterOperation"));
			operationClasses.add(Class.forName("operations.analysis.MeasureMinimumDiameterOperation"));		
			operationClasses.add(Class.forName("operations.tracing.MeasureSkeletonLengthOperation"));
			operationClasses.add(Class.forName("operations.processing.MedianFilterOperation"));
			operationClasses.add(Class.forName("operations.segmentation.MedianThresholdOperation"));
			operationClasses.add(Class.forName("operations.channel.MergeChannelsOperation"));
			operationClasses.add(Class.forName("operations.stack.MergeStacksOperation"));
			operationClasses.add(Class.forName("operations.image.MinOperation"));
			operationClasses.add(Class.forName("operations.processing.MinimumFilterOperation"));
			operationClasses.add(Class.forName("operations.morphology.MorphoContrastEnhanceOperation"));
			operationClasses.add(Class.forName("operations.image.MultiplyOperation"));
			operationClasses.add(Class.forName("operations.image.NanBackgroundOperation"));
			operationClasses.add(Class.forName("operations.roi.NormalizeRoiRotationOperation"));			
			operationClasses.add(Class.forName("operations.control.NTimesRepeatEndOperation"));
			operationClasses.add(Class.forName("operations.control.NTimesRepeatOperation"));
			operationClasses.add(Class.forName("operations.roi.ObjectsToPointSelectionOperation"));
			operationClasses.add(Class.forName("operations.morphology.OpenGrayOperation"));
			operationClasses.add(Class.forName("operations.file.OpenImageOperation"));
			operationClasses.add(Class.forName("operations.file.OpenSeriesAsStackOperation"));
			operationClasses.add(Class.forName("operations.image.OrOperation"));
			operationClasses.add(Class.forName("operations.segmentation.OtsuThresholdOperation"));
			operationClasses.add(Class.forName("operations.image.PasteImageOperation"));		
			operationClasses.add(Class.forName("operations.image.ReciprocalOperation"));			
			operationClasses.add(Class.forName("operations.stack.RegisterSeriesSlicesOperation"));
			operationClasses.add(Class.forName("operations.stack.RegisterStackSlicesOperation"));
			operationClasses.add(Class.forName("operations.roi.RemoveSelectionOperation"));
			operationClasses.add(Class.forName("operations.control.ReplaceNullWithEmptyImageOperation"));
			operationClasses.add(Class.forName("operations.reporting.ReportCalibratedAreaMeasurementOperation"));
			operationClasses.add(Class.forName("operations.reporting.ReportCountComparisonOperation"));
			operationClasses.add(Class.forName("operations.reporting.ReportIntensityClassesOperation"));
			operationClasses.add(Class.forName("operations.reporting.ReportIntensityComparisonOperation"));
			operationClasses.add(Class.forName("operations.reporting.ReportMeasurementsOperation"));
			operationClasses.add(Class.forName("operations.reporting.ReportObjectNumberRatioOperation"));
			operationClasses.add(Class.forName("operations.reporting.ReportPointMeasurementsOperation"));
			operationClasses.add(Class.forName("operations.reporting.ReportSegmentsOperation"));
			operationClasses.add(Class.forName("operations.reporting.ReportSpotsOnNucleiOperation"));
			operationClasses.add(Class.forName("operations.reporting.ReportStainCombinationsOperation"));
			operationClasses.add(Class.forName("operations.reporting.ReportTwoCountComparisonOperation"));	
			operationClasses.add(Class.forName("operations.image.ResizeToRotateOperation"));			
			operationClasses.add(Class.forName("operations.roi.RotateRoiOperation"));
			operationClasses.add(Class.forName("operations.file.SaveAsTiffSequenceOperation"));
			operationClasses.add(Class.forName("operations.file.SaveImageOperation"));
			operationClasses.add(Class.forName("operations.file.SaveImageAsOperation"));
			operationClasses.add(Class.forName("operations.roi.SaveSelectionOperation"));
			operationClasses.add(Class.forName("operations.image.ScaleImageOperation"));
			operationClasses.add(Class.forName("operations.lines.ScanForStraightLinesOperation"));
			operationClasses.add(Class.forName("operations.segmentation.SkeletonizeOperation"));
			operationClasses.add(Class.forName("operations.roi.SelectAllObjectsOperation"));
			operationClasses.add(Class.forName("operations.roi.SelectObjectOperation"));
			operationClasses.add(Class.forName("operations.roi.SelectWhitePixelOperation"));
			operationClasses.add(Class.forName("operations.image.SetMinAndMaxDisplayOperation"));		
			operationClasses.add(Class.forName("operations.analysis.SetScaleOperation"));						
			operationClasses.add(Class.forName("operations.analysis.SetScaleFromMeasurementOperation"));
			operationClasses.add(Class.forName("operations.gui.SetWindowPositionOperation"));
			operationClasses.add(Class.forName("operations.file.ShowImageOperation"));
			operationClasses.add(Class.forName("operations.file.ShowResultsTableOperation"));
			operationClasses.add(Class.forName("operations.file.ShowTextOperation"));
			operationClasses.add(Class.forName("operations.analysis.ShortestPathsOperation"));
			operationClasses.add(Class.forName("operations.control.SkipImageNotLoadedOperation"));
			operationClasses.add(Class.forName("operations.control.SkipNoObjectsFoundOperation"));
			operationClasses.add(Class.forName("operations.control.SkipSaturatedOperation"));
			operationClasses.add(Class.forName("operations.segmentation.SnrThresholdOperation"));
			operationClasses.add(Class.forName("operations.analysis.SplitAndCountOperation"));
			operationClasses.add(Class.forName("operations.channel.SplitChannelsOperation"));		
			operationClasses.add(Class.forName("operations.image.SquareOperation"));
			operationClasses.add(Class.forName("operations.image.SquareRootOperation"));
			operationClasses.add(Class.forName("operations.annotation.StampScaleBarOperation"));
			operationClasses.add(Class.forName("operations.segmentation.StdDevAroundMeanThresholdOperation"));
			operationClasses.add(Class.forName("operations.segmentation.StdDevAroundMedianThresholdOperation"));
			operationClasses.add(Class.forName("operations.image.SubtractOperation"));
			operationClasses.add(Class.forName("operations.processing.SubtractBackgroundOperation"));
			operationClasses.add(Class.forName("operations.processing.SubtractMeanOperation"));
			operationClasses.add(Class.forName("operations.processing.SubtractBaselineOperation"));
			operationClasses.add(Class.forName("operations.roi.SubtractSelectionOperation"));
			operationClasses.add(Class.forName("operations.stack.SubtractSlicesBackgroundOperation"));
			operationClasses.add(Class.forName("operations.analysis.TextureAnalysisOperation"));
			operationClasses.add(Class.forName("operations.segmentation.TextureThresholdOperation"));
			operationClasses.add(Class.forName("operations.segmentation.ThresholdOperation"));
			operationClasses.add(Class.forName("operations.morphology.TopHatGrayOperation"));
			operationClasses.add(Class.forName("operations.analysis.TraceLinesOperation"));			
			operationClasses.add(Class.forName("operations.analysis.TrackParticlesOperation"));
			operationClasses.add(Class.forName("operations.roi.TransferSelectionOperation"));
			operationClasses.add(Class.forName("operations.processing.TranslateImageOperation"));
			operationClasses.add(Class.forName("operations.processing.VarianceFilterOperation"));
			operationClasses.add(Class.forName("operations.control.WaitForUserOperation"));
			operationClasses.add(Class.forName("operations.segmentation.WatershedOperation"));
			operationClasses.add(Class.forName("operations.image.XorOperation"));
			operationClasses.add(Class.forName("operations.gui.ZoomViewOperation"));
			operationClasses.add(Class.forName("operations.stack.ZProjectionOperation"));
			Collections.sort(operationClasses, new ClassComparator());
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	protected void initialize() throws ClassNotFoundException {	
		keepSource = true;
		showResult = true;
		isInteractive = true;
	}

	public void execute() {
		this.task = new Thread(this);
		task.start();
	}
	
	protected void isRunning(boolean b) {
		isRunning = b;
		this.changed("isRunning");
	}

	protected void doIt() {
		// noop
	}

	public Class<?>[] getParameterTypes() {
		return parameterTypes;
	}
	
	public void setParameterTypes(Class<?>[] parameterTypes) {
		this.parameterTypes = parameterTypes;
	}
	
	public Class<?>[] getResultTypes() {
		return resultTypes;
	}
	
	public void setResultTypes(Class<?>[] resultType) {
		this.resultTypes = resultType;
	}
	
	public int getNumberOfParameter() {
		return parameterTypes.length;
	}
	
	public int getNumberOfResults() {
		return resultTypes.length;
	}

	public String[] getParameterNames() {
		return parameterNames;
	}

	public void setParameterNames(String[] parameterNames) {
		this.parameterNames = parameterNames;
	}

	public String[] getResultNames() {
		return resultNames;
	}

	public void setResultNames(String[] resultNames) {
		this.resultNames = resultNames;
	}

	public boolean isKeepSource() {
		return keepSource;
	}

	public void setKeepSource(boolean keepSource) {
		this.keepSource = keepSource;
	}

	public ImagePlus getInputImage() {
		if (inputImage==null) {
			inputImage = WindowManager.getCurrentImage();
			if (this.getApplication()!=null) {
				System.out.println("input image was null in " + this.name());
				if (inputImage!=null) System.out.println("using image" + inputImage.getTitle());
			}
		}
		return inputImage;
	}

	public void setInputImage(ImagePlus inputImage) {
		this.inputImage = inputImage;
	}

	public ImagePlus getResult() {
		return result;
	}

	public void setResult(ImagePlus anImage) {
		result = anImage;
	}
	
	public void show() {
		this.view().setVisible(true);
	}

	public OperationView view() {
		if (this.view == null) {
			this.view = new OperationView(this);
		}
		return this.view;
	}

	public String name() {
		if (name==null) {	
			name = this.getClass().getSimpleName();
			int index = name.lastIndexOf("Operation");
			name = name.substring(0, index);
			name = humanReadableName(name);
		}
		return name;
	}
	
	public void setName(String name) {
		this.name = name; 
	}

	public static String humanReadableName(String name) {
		char[] chars = name.toCharArray();
		String result = "";
		for (int i=0; i<chars.length; i++) {
			char current = chars[i];
			if (i>0 && Character.isUpperCase(current) || Character.isDigit(current)) {
				result = result + " ";
			}
			result = result + Character.toString(Character.toLowerCase(current));
		}
		return result;
	}
	
	public Object clone() {
		Operation clone = null;
		try {
			clone = this.getClass().newInstance();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return clone;
	}

	public boolean getShowResult() {
		return showResult;
	}

	public void setShowResult(boolean showResult) {
		this.showResult = showResult;
	}

	public String[] getOptionsNames() {
		if (optionsNames == null) optionsNames = new String[0];
		return optionsNames;
	}

	public void setOptionsNames(String[] optionsNames) {
		this.optionsNames = optionsNames;
	}
	
	public Options getOptions() {
		return options;
	}

	protected void setupOptions() {
		options = new Options(); 
		options.setName(this.name() + " options");
		String[] optionsNames = this.getOptionsNames();
		for (int i=0; i<optionsNames.length; i++) {
			Option option = new Option();
			option.setName(optionsNames[i]);
			options.add(option);
		}
		this.connectOptions();
	}

	public boolean isApplication() {
		return false;
	}
	
	public void showApplicationView() {
		// Do nothing
		// overwritten in Application
	}

	public ArrayList<int[]> getInputMap() {
		if (inputMap==null) {
			inputMap = new ArrayList<int[]>();
			for (int i=0; i<this.getNumberOfParameter();i++) {
				int[] map = new int[2];
				map[0] = -1;
				map[1] = -1;
				inputMap.add(map);
			}
		}
		return inputMap;
	}
	
	public void resetInputMap() {
		inputMap = null;
		this.changed("input map");
	}
	
	public int[] getInputMapForParameter(int i) {
		return this.getInputMap().get(i);
	}
	
	public void setInputForParameterTo(int parameter, int operation, int result) {
		int[] map =this.getInputMap().get(parameter);
		map[0] = operation;
		map[1] = result;
		this.changed("input map");
	}

	public void printOn(BufferedWriter out) throws IOException {
		out.write(this.getClass().getName() + "\n");
		String parameterAddition = "";
		if (!this.isKeepSource()) parameterAddition = "/keepSource=false";
		out.write(this.name() + parameterAddition +  "\n");
		this.options.printOn(out);
		Iterator<int[]> it = this.getInputMap().iterator();
		while (it.hasNext()) {
			int[] anInputMap = it.next();
			out.write(anInputMap[0] + "," + anInputMap[1] + "\n");
		}
		out.write("[end input maps]\n");
	}
	
	public static Operation readFrom(BufferedReader in) throws IOException {
		IOException error = new IOException();
		String className = in.readLine();
		className = Operation.replaceOldName(className);
		if (className==null) {
			return null;
		}
		Operation operation = null;
		try {
			operation = (Operation)Class.forName(className).newInstance();
			operation.setShowResult(false);
			operation.setupFrom(in);
			operation.connectOptions();
		} catch (InstantiationException e) {
			error.setStackTrace(e.getStackTrace());
			throw error;
		} catch (IllegalAccessException e) {
			error.setStackTrace(e.getStackTrace());
			throw error;
		} catch (ClassNotFoundException e) {
			if (className.contains("Substract")) className = className.replaceAll("Substract", "Subtract");
			try {
				operation = (Operation)Class.forName(className).newInstance();
			} catch (InstantiationException e1) {
				e1.printStackTrace();
				throw error;
			} catch (IllegalAccessException e1) {
				e1.printStackTrace();
				throw error;
			} catch (ClassNotFoundException e1) {
				e1.printStackTrace();
				throw error;
			}
			operation.setShowResult(false);
			operation.setupFrom(in);
			operation.setName(null);
			operation.connectOptions();
			error.setStackTrace(e.getStackTrace());
		}
		return operation;
	}

	protected static String replaceOldName(String className) {
		if (className==null) return className;
		String[] components = className.split("\\.");
		if (components.length>2 || !components[0].contains("Operation")) return className;
		String result = "operations.";
		String[] packageComponents = components[0].split("[A-Z]"); 
		result = result + packageComponents[0] + "." + components[1]; 
		return result;
	}

	public void connectOptions() {
		// noop
	}
	
	public void setupFrom(BufferedReader in) throws IOException {
		String operationName = in.readLine();
		String[] data = operationName.split("/");
		if (data.length>1 && data[1].equals("keepSource=false")) this.setKeepSource(false);
		this.setName(data[0]);
		this.setupOptions();
		options.setupFrom(in);
		String line = null;
		this.inputMap = new ArrayList<int[]>();
		while((line = in.readLine())!=null) {
			if (line.equals("[end input maps]")) {
				break;
			} 
			data = line.split(",");
			int[] anInputMap = new int[2];
			anInputMap[0] = Integer.parseInt(data[0]);
			anInputMap[1] = Integer.parseInt(data[1]);
			inputMap.add(anInputMap);
		}
	}

	public String getFilename() {
		return null;
	}
	
	public void changed(String anAspect) {
		this.setChanged();
		this.notifyObservers(anAspect);
	}

	public boolean isConnected() {
		boolean result = true;
		if (this.numberOfParameters()==0) return result;
		Iterator<int[]> it = this.getInputMap().iterator();
		while (it.hasNext()) {
			int[] aMap = it.next();
			if (aMap[0]==-1 || aMap[1]==-1) result = false;
		}
		return result;
	}

	private int numberOfParameters() {
		return this.getParameterNames().length;
	}

	public int getProgress() {
		return progress;
	}

	public void setProgress(int progress) {
		this.progress = progress;
		this.changed("progress");
	}

	public int getProgressMax() {
		return progressMax;
	}

	public void setProgressMax(int progressMax) {
		this.progressMax = progressMax;
		this.changed("progressMax");
	}

	public void run() {
		boolean wasBatchMode = Interpreter.isBatchMode();
		try {
			this.isRunning(true);
			if (this.isBatchOperation()) {
				this.getInterpreter().setBatchMode(true);
			} else {
				this.getInterpreter().setBatchMode(false);
			}
			if (Operation.record && Recorder.record) {
 				Recorder.record("call", "operations.Operation.run", this.name(), this.getOptionsString());
 			}
			this.doIt();
			if (Operation.record && Recorder.record) {
 				Recorder.saveCommand();
 			}
			if (this.getShowResult()) {
				showResult();
			}
		} catch (Exception e) {
			String message = "An error occured in " + this.name() + "\n";
			if (e.getMessage()!=null) message = message + e.getMessage();
			else message += e.toString();
			
			JOptionPane.showConfirmDialog(this.view(), message,
					"operation error", JOptionPane.CLOSED_OPTION, JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
			this.stopApplication();
		} finally {
			cleanUpInput();
			System.gc();
			this.isRunning(false);
			this.getInterpreter().setBatchMode(wasBatchMode);
		}
	}

	protected void cleanUpInput() {
		this.setInputImage(null);
	}

	protected void showResult() {
		if (this.getResult()==null) return;
		this.getResult().show();
	}

	public Thread getTask() {
		return task;
	}

	public void openHelp() {
		HelpSystem help = HelpSystem.getCurrent();
		help.openHelpFor(this.name());
	}

	static public void drawLines(ArrayList<Line2D> lines, ImageProcessor ip) {
		for (int i=0; i<lines.size();i++) {
			Line2D aLine = (Line2D) lines.get(i);
			ip.drawLine((int)Math.round(aLine.getX1()), 
										   (int)Math.round(aLine.getY1()), 
										   (int)Math.round(aLine.getX2()), 
										   (int)Math.round(aLine.getY2()));
		}
	}

	public void setInputMap(ArrayList<int[]> inputMap) {
		this.inputMap = inputMap;
	}

	public void setApplication(Application application) {
		this.application = application;
	}
	
	public Application getApplication() {
		return application;
	}

	public boolean isLoop() {
		return false;
	}

	public Operation getEndLoopOperation() {
		return null;
	}
	
	public boolean isLoopEnd() {
		return false;
	}
	
	public void stopApplication() {
		if (this.application!=null) {
			this.application.stop();
		}
	}
	
	public int browseFileForOption(Option anOption) {
		JFileChooser fileChooser = new JFileChooser();
		if (IJ.getDirectory("image")!=null) {
			fileChooser.setCurrentDirectory(new File(IJ.getDirectory("image")));
		}
		fileChooser.setMultiSelectionEnabled(false);
		fileChooser.addChoosableFileFilter(ListEditor.getImageFileFilter());
		fileChooser.addChoosableFileFilter(ListEditor.getTiffFileFilter());
		int returnVal = fileChooser.showOpenDialog(null);
		if (returnVal != JFileChooser.APPROVE_OPTION) return -1;
		anOption.setValue(fileChooser.getSelectedFile().getAbsolutePath());
		return returnVal;
	}

	public void resetPathOptions() {
		// by default do nothing, overwrite if action needed.		
	}
	
	public void setPathOptionsFromUser() {
		// by default do nothing, overwrite if action needed.
	}

	public void reset() {
		// by default do nothing, overwrite if action needed.
	}
	
	public int getCurrentIndex() {
		return -1;
	}

	protected void doItForSlice(int sliceNumber, ImageStack stack) {
	}

	protected void processSlices() {
		ImageStack stack = result.getStack();
		for (int i=1; i<=result.getStackSize(); i++) {
			IJ.showStatus("processing slice " + i + " of " + result.getStackSize());
			this.doItForSlice(i, stack);
			Thread.yield();
		}
		result.setStack(inputImage.getTitle() + " - " + this.name(), stack);
	}
	
	protected boolean isBatchOperation() {
		return !this.getShowResult();
	}
	
	public MRIInterpreter getInterpreter() {
		if (interpreter==null) {
			interpreter = new MRIInterpreter();
		}
		return interpreter;
	}

	protected ImagePlus getCopyOfOrReferenceTo(ImagePlus inputImage, String title) {
		ImagePlus result = inputImage;
		if (keepSource) {
			result = this.copyImage(inputImage, title);
		}
		return result;
	}
	
	protected ImagePlus copyImage(ImagePlus anImage, String title) {
		ImagePlus result = null;
		Duplicator copyAction = new Duplicator();
		result = copyAction.run(anImage);
		result.setTitle(title);
		return result;
	}

	public boolean isRunning() {
		return isRunning;
	}
	
	public static String run(String name, String optionsString) {
		String[] theOptions = new String[0];
		if (!optionsString.trim().equals("")) theOptions = optionsString.split(";");
		Class<?> operationClass = Operation.getClassFor(name);
		if (operationClass==null) return "error: operation " + name + " not found";
		try {
			Operation operation = (Operation) operationClass.newInstance();
			ArrayList<Option> options = operation.getOptions().getOptions();
 			for (int i=0; i<theOptions.length; i++) {
 				((Option)options.get(i)).setValue(theOptions[i].trim());
 			}
 			operation.setKeepSource(false);
 			operation.setShowResult(true);
 			setRecord(false);
 			operation.run();
 			setRecord(true);
		} catch (InstantiationException e) {
			e.printStackTrace();
			return "error: operation " + name + " could not be instantiated";
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			return "error: operation " + name + " could not be accessed";
		} 
		return "executed operation " + name;
	}

	private static void setRecord(boolean b) {
		record = b;
	}

	protected static Class<?> getClassFor(String aName) {
		Class<?> result = null;
		String[] nameComponents = aName.split(" ");
		String completeName = "";
		for (int i=0; i<nameComponents.length; i++) {
			completeName = completeName + nameComponents[i];
		}
		completeName = completeName + "operation";
		ArrayList<Class<?>> allClasses = Operation.operationClasses();
		Iterator<Class<?>> it = allClasses.iterator();
		while (it.hasNext()) {
			Class<?> currentClass = it.next();
			String[] currentNameComponents = currentClass.getName().split("\\.");
			String currentShortName = currentNameComponents[currentNameComponents.length-1];
			if (currentShortName.toLowerCase().equals(completeName)) return currentClass;
		}
		return result;
	}
	
	public String getOptionsString() {
		String result = "";
		ArrayList<Option> options = this.getOptions().getOptions();
		int count = 0;
		for(Option option : options) {
			result = result + option.getValue();
			if (count < this.getOptions().getOptions().size()-1) result = result + "; ";
			count++;	
		}
		return result;
	}

	public boolean isInteractive() {
		return isInteractive;
	}

	public void setInteractive(boolean isInteractive) {
		this.isInteractive = isInteractive;
	}
	
	public boolean isMacroOperation() {
		return false;
	}

	public String macroName() {
		return null;
	}
	

	
	public static ResultsTable copySystemResultsTable() {
		ResultsTable result = new ResultsTable();
		copyResultsTable(Analyzer.getResultsTable(), result);
		return result;
	}
	
	public static void copyResultsTable(ResultsTable source, ResultsTable destination) {
		destination.reset();
		String[] headings = source.getColumnHeadings().split("\t");
		for (int i=0; i<source.getCounter(); i++) {
			destination.incrementCounter();
			for (int j=1; j<headings.length; j++) {
				destination.setValue(headings[j], i, source.getValue(headings[j], i));
			}
		}
	}
	
	public String toString() {
		return name();
	}
}
