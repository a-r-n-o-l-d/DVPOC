/*
 * Copyright (C) 2015 Arnold Fertin
 *
 * Centre National de la Recherche Scientifique
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU General Public License as published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program. If
 * not, see <http://www.gnu.org/licenses/>.
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
