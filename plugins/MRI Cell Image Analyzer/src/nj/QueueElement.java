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

public class QueueElement extends Dijkstra {
	  private int iCapacity = 40;
	    private final int iCapInc = 40;
	    private int iLast = -1;
	    private int[] iarray = new int[iCapacity];

	    int add(final int element) {
		if (++iLast == iCapacity) inccap();
		iarray[iLast] = element;
		return iLast;
	    }
		
	    private void inccap() {
		iCapacity += iCapInc;
		final int[] newarray = new int[iCapacity];
		for (int i=0; i<iLast; ++i) newarray[i] = iarray[i];
		iarray = newarray;
	    }
		
	    int element(final int index) { return iarray[index]; }
		
	    int remove() { return iarray[iLast--]; }
		
	    int remove(final int index) {
		final int i = iarray[index];
		iarray[index] = iarray[iLast--];
		return i;
	    }
		
	    int size() { return (iLast + 1); }
}
