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

import ij.ImagePlus;
import ij.process.ByteProcessor;
import imagescience.features.Differentiator;
import imagescience.images.ByteImage;
import imagescience.images.Dimensions;
import imagescience.images.FloatImage;
import imagescience.utilities.Progressor;

public class Costs {

//	 ***************************************************************************
	    
	    // Returns a cost image and vector field computed from the
	    // eigenvalues and eigenvectors of the Hessian of the input
	    // image. The second and third index correspond, respectively, to
	    // the y- and x-coordinate. The first index selects the image:
	    // element 0 contains the cost image, element 1 the x-component of
	    // the vector field, and element 2 the y-component of the vector
	    // field. The gray-value at any point in the cost image is computed
	    // from the eigenvalues of the Hessian matrix at that
	    // point. Specifically, the method computes both (adjusted)
	    // eigenvalues and selects the one with the largest magnitude. Since
	    // in the NeuronJ application we are interested in bright structures
	    // on a dark background, the method stores this absolute eigenvalue
	    // only if the actual eigenvalue is negative. Otherwise it stores a
	    // zero. The eventual largest-eigenvalue image is inverted and
	    // scaled to gray-value range [0,254]. A gray-value of 255 in the
	    // input image is retained. Effectively this amounts to masking
	    // regions outside the field of view. The vector at any point in the
	    // vector field is simply the eigenvector corresponding to the
	    // largest absolute eigenvalue at that point.
	    //
	    public float[][][] run(final ByteProcessor image, final float scale) {
		
	//	NJ.write("Cost image and vector field from Hessian at scale "+scale+" ...");

		// Compute Hessian components:
		final ByteImage inImage = new ByteImage((new ImagePlus("",image)).getStack());
		final Differentiator differ = new Differentiator();
		final FloatImage Hxx = differ.gauss(inImage,scale,2,0,0);
		final FloatImage Hxy = differ.gauss(inImage,scale,1,1,0);
		final FloatImage Hyy = differ.gauss(inImage,scale,0,2,0);

		// Compute and select adjusted eigenvalues and eigenvectors:
		final Progressor pgs = new Progressor("Computing eigenimages");
		final Dimensions dims = inImage.dimensions();
		pgs.steps(dims.y);
		final float[] ahxx = (float[])Hxx.stack().getPixels(1);
		final float[] ahxy = (float[])Hxy.stack().getPixels(1);
		final float[] ahyy = (float[])Hyy.stack().getPixels(1);
		final float[][][] evv = new float[3][dims.y][dims.x];
		final float[][] value = evv[0];
		final float[][] vectx = evv[1];
		final float[][] vecty = evv[2];
		for (int y=0, i=0; y<dims.y; ++y) {
		    for (int x=0; x<dims.x; ++x, ++i) {
			final float b1 = ahxx[i] + ahyy[i];
			final float b2 = ahxx[i] - ahyy[i];
			final float d = (float)Math.sqrt(4.0*ahxy[i]*ahxy[i] + b2*b2);
			final float L1 = (b1 + 2.0f*d)/3.0f;
			final float L2 = (b1 - 2.0f*d)/3.0f;
			final float absL1 = Math.abs(L1);
			final float absL2 = Math.abs(L2);
			if (absL1 > absL2) {
			    if (L1 > 0.0f) value[y][x] = 0.0f;
			    else value[y][x] = absL1;
			    vectx[y][x] = b2 - d;
			} else {
			    if (L2 > 0.0f) value[y][x] = 0.0f;
			    else value[y][x] = absL2;
			    vectx[y][x] = b2 + d;
			}
			vecty[y][x] = 2.0f*ahxy[i];
		    }
		    pgs.step();
		}
		pgs.end();

		// Convert eigenvalues to costs and normalize eigenvectors:
		float minval = value[0][0];
		float maxval = minval;
		for (int y=0; y<dims.y; ++y)
		    for (int x=0; x<dims.x; ++x)
			if (value[y][x] > maxval) maxval = value[y][x];
			else if (value[y][x] < minval) minval = value[y][x];
		final float roof = 254.0f;
		final float offset = 0.0f;
		final float factor = (roof - offset)/(maxval - minval);
		final byte[] pixels = (byte[])image.getPixels();
		for (int y=0, i=0; y<dims.y; ++y)
		    for (int x=0; x<dims.x; ++x, ++i) {
			value[y][x] = (pixels[i] == -1) ? 255.0f : (roof - (value[y][x] - minval)*factor);
			final float vectlen = (float)Math.sqrt(vectx[y][x]*vectx[y][x] + vecty[y][x]*vecty[y][x]);
			if (vectlen > 0.0f) { vectx[y][x] /= vectlen; vecty[y][x] /= vectlen; }
		    }
		
		return evv;
	    }
	    
	}

