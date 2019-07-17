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
import volume.VoxelSize;

/**
 *
 * @author Arnold Fertin
 */
public final class DisplacementFieldFormatter1
{
//    private final DecimalFormat numPointFormatter;
    private final DecimalFormat coordFormatter;

    private final DecimalFormat transFormatter;

    private final DecimalFormat maxCorrFormatter;

    private final DecimalFormat pValueFormatter;

    private final DecimalFormat grubbsFormatter;

    public DisplacementFieldFormatter1(final DisplacementField field)
    {
        // Formatage coordonnées points
        final Dimensions dims = field.getDimensions();
        final VoxelSize vs = field.getVoxelDimensions();
        double mdim = Math.max(dims.dimX * vs.width, dims.dimY * vs.height);
        mdim = Math.max(mdim, dims.dimZ * vs.depth);
        coordFormatter = new DecimalFormat("+" + getNumberOfZero(mdim) + ".000000000;-#");

        // Formatage coordonnées à virgules flottante +00.00000;-#
        final int n = field.getSize();
        final Displacement global = field.getGlobalDisplacement();
        double maxDisp = Double.MIN_VALUE;
        double maxMaxCorr = Double.MIN_VALUE;
        double maxMaxGrubs = Double.MIN_VALUE;
        double maxPvalue = Double.MIN_VALUE;
        for (int i = 0; i < n; i++)
        {
            final Displacement tr = field.getDisplacement(i);
            final double u = (tr.getU() - global.getU()) * vs.width;
            if (u > maxDisp)
            {
                maxDisp = tr.getU();
            }
            final double v = (tr.getV() - global.getV()) * vs.height;
            if (v > maxDisp)
            {
                maxDisp = tr.getV();
            }
            final double w = (tr.getW() - global.getW()) * vs.depth;
            if (w > maxDisp)
            {
                maxDisp = tr.getW();
            }
            if (tr.getMaximumOfCorrelation() > maxMaxCorr)
            {
                maxMaxCorr = tr.getMaximumOfCorrelation();
            }
            if (tr.getPValue() > maxMaxGrubs)
            {
                maxPvalue = tr.getPValue();
            }
            if (tr.getGrubbsStatistics() > maxMaxGrubs)
            {
                maxMaxGrubs = tr.getGrubbsStatistics();
            }
        }
        transFormatter = new DecimalFormat("+" + getNumberOfZero(maxDisp) + ".000000000;-#");
        maxCorrFormatter = new DecimalFormat("+" + getNumberOfZero(maxMaxCorr) + ".000000000;-#");
        pValueFormatter = new DecimalFormat("+" + getNumberOfZero(maxPvalue) + ".000000000;-#");
        grubbsFormatter = new DecimalFormat("+" + getNumberOfZero(maxMaxGrubs) + ".000000000;-#");
    }

//    public String formatNumPoint(final int n)
//    {
//        return numPointFormatter.format(n);
//    }
//
//    public String formatCoordinate(final int coord)
//    {
//        return coordFormatter.format(coord);
//    }
    public String formatTranslation(final double uvw)
    {
        return transFormatter.format(uvw);
    }

    public String formatCoordinate(final double coord)
    {
        return coordFormatter.format(coord);
    }

    public String formatMaxCorr(final double value)
    {
        return maxCorrFormatter.format(value);
    }

    public String formatGrubbs(final double value)
    {
        return grubbsFormatter.format(value);
    }

    public String formatPvalue(final double value)
    {
        return pValueFormatter.format(value);
    }

//    private String getNumberOfZero(final int val)
//    {
//        final StringBuffer nZero = new StringBuffer(""); // Nbre de zéros pour la mise en forme
//        int n = 1;
//        while (n < val)
//        {
//            n *= 10;
//            nZero.append("0");
//        }
//        return nZero.toString();
//    }
    private String getNumberOfZero(final double val)
    {
        final StringBuffer nZero = new StringBuffer("0"); // Nbre de zéros pour la mise en forme
        double n = 10;
        while (n < val)
        {
            n *= 10;
            nZero.append("0");
        }
        return nZero.toString();
    }
}
