/* Copyright © Erik Meijering. All rights reserved.
 *
 * Permission to copy and use the software and accompanying documentation provided 
 * on these webpages for educational, research, and not-for-profit purposes, without 
 * fee and without a signed licensing agreement, is hereby granted, provided that the 
 * above copyright notice, this paragraph and the following two paragraphs appear in all copies. 
 * The copyright holder is free to make upgraded or improved versions of the software available for a 
 * fee or commercially only.
 *
 * IN NO EVENT SHALL THE COPYRIGHT HOLDER BE LIABLE TO ANY PARTY FOR DIRECT, INDIRECT, SPECIAL, 
 * INCIDENTAL, OR CONSEQUENTIAL DAMAGES, OF ANY KIND WHATSOEVER, ARISING OUT OF THE USE OF THIS 
 * SOFTWARE AND ITS DOCUMENTATION, EVEN IF HE HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * THE COPYRIGHT HOLDER SPECIFICALLY DISCLAIMS ANY WARRANTIES, INCLUDING, BUT NOT LIMITED TO, 
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE SOFTWARE AND 
 * ACCOMPANYING DOCUMENTATION IS PROVIDED "AS IS". THE COPYRIGHT HOLDER HAS NO OBLIGATION TO PROVIDE 
 * MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS, OR MODIFICATIONS.
 * 
 */
package nj;

import java.awt.Point;

public class Dijkstra {
    private final int INFINITE = 2147483647;
    private final int PROCESSED = 2147483647;
    private final int FREE = 2147483646;

    private int[] ccost = null;
    private int[] istat = null;
    private byte[][] dirs = null;

    // Computes the shortest path based on the given cost values and
    // vectors. The first index is the image index: element 0 contains
    // the cost image, element 1 the x-component of the vector field,
    // and element 2 the y-component of the vector field. The second and
    // third index correspond to, respectively, the y- and x-coordinate.
    // Pixels with a value of 255 in the cost image are treated as if
    // they had "infinite" cost and therefore will never be part of the
    // shortest paths.
    //
    // The returned image contains for every pixel the direction to the
    // predecessing pixel along the shortest path. The first index is
    // the y-coordinate and the second the x-coordinate. Note that if in
    // a series of calls to this method the cost image keeps the same
    // dimensions, the returned handle will be the same for every
    // call. That is to say, the directions image is reallocated only
    // when the cost image changes dimensions. Otherwise it is reused in
    // order to gain speed. The direction values should be interpreted
    // as follows:
    //
    // 0 = go directly to starting point
    // 1 = go one up, one left
    // 2 = go one up
    // 3 = go one up, one right
    // 4 = go one left
    // 5 = go one right
    // 6 = go one down, one left
    // 7 = go one down
    // 8 = go one down, one right
    //
    public byte[][] run(final float[][][] costvector, final Point startpoint) {
		
	// Initialize variables and handles:
	final float[][] costimage = costvector[0];
	final float[][] costfieldx = costvector[1];
	final float[][] costfieldy = costvector[2];

	final int iYSize = costimage.length;
	final int iXSize = costimage[0].length;
	final int iYSizem1 = iYSize - 1;
	final int iXSizem1 = iXSize - 1;
	
	final int iStartY = startpoint.y;
	final int iStartX = startpoint.x;
	if (iStartY <= 0 || iStartY >= iYSizem1 || iStartX <= 0 || iStartX >= iXSizem1)
	    throw new IllegalArgumentException("Starting point on or outside border of image");
	final int vstart = iStartY*iXSize + iStartX;
		
	final int iNrPixels = iYSize*iXSize;
	if (dirs == null || dirs.length != iYSize || dirs[0].length != iXSize) {
	    dirs = new byte[iYSize][iXSize];
	    ccost = new int[iNrPixels];
	    istat = new int[iNrPixels];
	}
		
	// Mask border pixels and pixels outside window:
	final int iXSizem2 = iXSize - 2;
	final int iYSizem2 = iYSize - 2;
	int iLX = 1; int iLY = 1;
	int iHX = iXSizem2; int iHY = iYSizem2;
	final int iHalfWinSize = NJ.dijkrange/2;
	if (NJ.dijkrange < iXSizem2) {
	    iLX = iStartX - iHalfWinSize;
	    iHX = iStartX + iHalfWinSize;
	    if (iLX < 1) { iLX = 1; iHX = NJ.dijkrange; }
	    if (iHX > iXSizem2) { iHX = iXSizem2; iLX = iXSizem1 - NJ.dijkrange; }
	}
	if (NJ.dijkrange < iYSizem2) {
	    iLY = iStartY - iHalfWinSize;
	    iHY = iStartY + iHalfWinSize;
	    if (iLY < 1) { iLY = 1; iHY = NJ.dijkrange; }
	    if (iHY > iYSizem2) { iHY = iYSizem2; iLY = iYSizem1 - NJ.dijkrange; }
	}
	for (int y=0, i=0; y<iLY; ++y)
	    for (int x=0; x<iXSize; ++x, ++i)
		{ istat[i] = PROCESSED; dirs[y][x] = 0; }
	for (int y=iHY+1, i=(iHY+1)*iXSize; y<iYSize; ++y)
	    for (int x=0; x<iXSize; ++x, ++i)
		{ istat[i] = PROCESSED; dirs[y][x] = 0; }
	for (int y=iLY, i=iLY*iXSize; y<=iHY; ++y, i+=iXSize) {
	    for (int x=0, j=i; x<iLX; ++x, ++j)
		{ istat[j] = PROCESSED; dirs[y][x] = 0; }
	    for (int x=iHX+1, j=i+iHX+1; x<iXSize; ++x, ++j)
		{ istat[j] = PROCESSED; dirs[y][x] = 0; }
	}

	// Initialize arrays within window:
	for (int y=iLY, i=iLY*iXSize; y<=iHY; ++y, i+=iXSize)
	    for (int x=iLX, j=i+iLX; x<=iHX; ++x, ++j) {
		dirs[y][x] = 0;
		ccost[j] = INFINITE;
		istat[j] = (costimage[y][x] == 255) ? PROCESSED : FREE;
	    }

	// Initialize queue:
	final QueueElement[] queue = new QueueElement[256];
	for (int i=0; i<256; ++i) queue[i] = new QueueElement();
	
	// Define relative positions of neighboring points:
	final int[] rpos = new int[9];
	rpos[8] = -iXSize - 1;
	rpos[7] = -iXSize;
	rpos[6] = -iXSize + 1;
	rpos[5] = -1;
	rpos[4] = 1;
	rpos[3] = iXSize - 1;
	rpos[2] = iXSize;
	rpos[1] = iXSize + 1;
	rpos[0] = 0;
		
	// The following lines implement the shortest path algorithm as
	// proposed by E. W. Dijkstra, A Note on Two Problems in
	// Connexion with Graphs, Numerische Mathematik, vol. 1, 1959,
	// pp. 269-271. Note, however, that this is a special
	// implementation for discrete costs based on a circular queue.
	
	// Initialization:
	ccost[vstart] = 0;
	int pindex = -1;
	int cindex = 0;
	queue[cindex].add(vstart);
	boolean bQueue = true;

	final float gamma = NJ.gamma;
	final float invgamma = 1.0f - gamma;
			
	// Path searching:
	while (bQueue) {

	    final int vcurrent = queue[cindex].remove();
	    istat[vcurrent] = PROCESSED;
	    final int iCY = vcurrent/iXSize;
	    final int iCX = vcurrent%iXSize;

	    for (int i=1; i<9; ++i) {
		final int vneighbor = vcurrent + rpos[i];
		if (istat[vneighbor] != PROCESSED) {
		    final int iNY = vneighbor/iXSize;
		    final int iNX = vneighbor%iXSize;
		    float fDY = iNY - iCY;
		    float fDX = iNX - iCX;
		    final float fLen = (float)Math.sqrt(fDY*fDY + fDX*fDX);
		    fDY /= fLen; fDX /= fLen;
		    final int iCurCCost = ccost[vneighbor];
		    final int iNewCCost = ccost[vcurrent] +
			(int)(gamma*costimage[iNY][iNX] +
			      invgamma*127f*(float)(Math.sqrt(1.0f - Math.abs(costfieldy[iCY][iCX]*fDY + costfieldx[iCY][iCX]*fDX)) +
						    Math.sqrt(1.0f - Math.abs(costfieldy[iNY][iNX]*fDY + costfieldx[iNY][iNX]*fDX))));
		    if (iNewCCost < iCurCCost) {
			ccost[vneighbor] = iNewCCost;
			dirs[iNY][iNX] = (byte)i;
			if (istat[vneighbor] == FREE)
			    istat[vneighbor] = queue[iNewCCost & 255].add(vneighbor);
			else {
			    final int iVIndex = iCurCCost & 255;
			    final int iEIndex = istat[vneighbor];
			    queue[iVIndex].remove(iEIndex);
			    istat[queue[iVIndex].element(iEIndex)] = iEIndex;
			    istat[vneighbor] = queue[iNewCCost & 255].add(vneighbor);
			}
		    }
		}
	    }

	    pindex = cindex;
	    while (queue[cindex].size() == 0) {
		++cindex; cindex &= 255;
		if (cindex == pindex) { bQueue = false; break; }
	    }
	}
		
	return dirs;
    }
}
