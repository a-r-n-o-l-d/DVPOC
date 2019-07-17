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
