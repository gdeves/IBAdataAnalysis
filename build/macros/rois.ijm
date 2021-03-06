selectWindow("C:\Users\deves\OwnCloud\HOME-SYNC\2016\Aifira-Bergo-Dec16\Hela\0Gy-4�g\PIXE093.ADC17.pixe2");
run("16_color-bis");
saveAs("Tiff", "C:\\Users\\deves\\OwnCloud\\HOME-SYNC\\2016\\Aifira-Bergo-Dec16\\Hela\\0Gy-4�g\\PIXE093.ADC17.pixe2.tif");
//run("Brightness/Contrast...");
run("Enhance Contrast", "saturated=0.35");
run("Make Montage...", "columns=5 rows=2 scale=0.50 first=1 last=10 increment=1 border=0 font=12 label use");
saveAs("Tiff", "C:\\Users\\deves\\OwnCloud\\HOME-SYNC\\2016\\Aifira-Bergo-Dec16\\Hela\\0Gy-4�g\\Montage-93.tif");
close();
run("Duplicate...", "title=PIXE093.ADC17.pixe2-1.tif");
run("Duplicate...", "title=PIXE093.ADC17.pixe2-2.tif");
run("8-bit");
setAutoThreshold("Default");
//run("Threshold...");
setThreshold(0, 21);
run("Convert to Mask");
run("Invert");
run("Fill Holes");
run("Watershed");
run("Analyze Particles...", "size=300-Infinity circularity=0.00-1.00 show=Nothing display clear add");
