/* 
 * Copyright or © or Copr. Arnold Fertin 2019
 *
 * This software is a computer program whose purpose is to perform image processing.
 *
 * This software is governed by the CeCILL-C license under French law and abiding by the rules of distribution of free
 * software. You can use, modify and/ or redistribute the software under the terms of the CeCILL-C license as circulated
 * by CEA, CNRS and INRIA at the following URL "http://www.cecill.info".
 *
 * As a counterpart to the access to the source code and rights to copy, modify and redistribute granted by the license,
 * users are provided only with a limited warranty and the software's author, the holder of the economic rights, and the
 * successive licensors have only limited liability.
 *
 * In this respect, the user's attention is drawn to the risks associated with loading, using, modifying and/or
 * developing or reproducing the software by the user in light of its specific status of free software, that may mean
 * that it is complicated to manipulate, and that also therefore means that it is reserved for developers and
 * experienced professionals having in-depth computer knowledge. Users are therefore encouraged to load and test the
 * software's suitability as regards their requirements in conditions enabling the security of their systems and/or data
 * to be ensured and, more generally, to use and operate it in the same conditions as regards security.
 *
 * The fact that you are presently reading this means that you have had knowledge of the CeCILL-C license and that you
 * accept its terms.
 */
package registration;

import correlation.Displacement;
import java.text.DecimalFormat;
import volume.Dimensions;

/**
 *
 * @author Arnold Fertin
 */
public final class DisplacementFieldFormatter
{
    private final DecimalFormat numPointFormatter;
    private final DecimalFormat coordFormatterI;
    private final DecimalFormat coordFormatterD;
    private final DecimalFormat maxCorrFormatter;
            
    public DisplacementFieldFormatter(final DisplacementField field)
    {
        // Formatage du numéro de point
        final int n = field.getSize();
        numPointFormatter = new DecimalFormat(getNumberOfZero(n));
        
        // Formatage coordonnées entières
        final Dimensions dims = field.getDimensions();
        final int mdim = Math.max(Math.max(dims.dimX, dims.dimY), dims.dimZ);
        coordFormatterI = new DecimalFormat(getNumberOfZero(mdim));
        
        // Formatage coordonnées à virgules flottante +00.00000;-#
        double maxDisp = Double.MIN_VALUE;
        double maxMaxCorr = Double.MIN_VALUE;
        for (int i = 0; i < n; i++)
        {
            final Displacement tr = field.getDisplacement(i);
            if (tr.getU() > maxDisp)
            {
                maxDisp = tr.getU();
            }
            if (tr.getV() > maxDisp)
            {
                maxDisp = tr.getV();
            }
            if (tr.getW() > maxDisp)
            {
                maxDisp = tr.getW();
            }
            if (tr.getMaximumOfCorrelation() > maxMaxCorr)
            {
                maxMaxCorr = tr.getMaximumOfCorrelation();
            }
        }
        coordFormatterD = new DecimalFormat("+" + getNumberOfZero(maxDisp) + ".00000;-#");
        
        maxCorrFormatter = new DecimalFormat(getNumberOfZero(maxMaxCorr) + ".00000");
    }
    
    public String formatNumPoint(final int n)
    {
        return numPointFormatter.format(n);
    }
    
    public String formatCoordinate(final int coord)
    {
        return coordFormatterI.format(coord);
    }
    
    public String formatCoordinate(final double coord)
    {
        return coordFormatterD.format(coord);
    }
    
    public String formatMaxCorr(final double coord)
    {
        return maxCorrFormatter.format(coord);
    }
    
    private String getNumberOfZero(final int val)
    {
        final StringBuffer nZero = new StringBuffer(""); // Nbre de zéros pour la mise en forme
        int n = 1;
        while (n < val)
        {
            n *= 10;
            nZero.append("0");
        }
        return nZero.toString();
    }
    
    private String getNumberOfZero(final double val)
    {
        final StringBuffer nZero = new StringBuffer(""); // Nbre de zéros pour la mise en forme
        double n = 1;
        while (n < val)
        {
            n *= 10;
            nZero.append("0");
        }
        return nZero.toString();
    }
}
