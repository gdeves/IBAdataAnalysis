run("Hyperstack...", "title=Fused type=32-bit display=Grayscale width=500 height=800 channels=1 slices=1 frames=10 label");
run("Insert...", "source=PIXE_03.tif destination=Fused x=75 y=0");
run("Insert...", "source=PIXE_02.tif destination=Fused x=75 y=250");
run("Insert...", "source=PIXE_01.tif destination=Fused x=75 y=500");
