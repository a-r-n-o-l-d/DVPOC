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

import org.apache.commons.math3.distribution.TDistribution;
import volume.Dimensions;
import volume.Point3D;
import volume.VolumeFloatZz;

/**
 * A partir du package "outliers" de R. seulement one-sided avec test si max est outlier
 * <p>
 * @author Arnold Fertin
 */
public final class GrubbsTest
{
    /**
     * Maximum value of data set.
     */
    private final double maxValue;

    /**
     * Sample size.
     */
    private final int sampleSize;

    /**
     * P value of the one-sided Grubbs test.
     */
    private double pValue;

    /**
     * Grubbs statistics = |Vmax - Vmean| / sd .
     */
    private double grubbsStatistics;

    /**
     * Constructor.
     * <p>
     * @param vol voxels data set
     * @param max maximum value of voxels
     */
    public GrubbsTest(final VolumeFloatZz vol,
                      final double max)
    {
        sampleSize = vol.getSize();
        maxValue = max;
        compute(vol);
    }

    /**
     * Get the Grubbs test p-value.
     * <p>
     * @return p-value
     */
    public double getPValue()
    {
        return pValue;
    }

    /**
     * Get the Grubbs statistics.
     * <p>
     * @return g
     */
    public double getGrubbsStatistics()
    {
        return grubbsStatistics;
    }

    public boolean isOutlier(final double significance)
    {
        return pValue <= significance;
    }

    /**
     * Compute pValue and grubbs statistics for one-sided Grubbs test.
     * H0 : there is no outlier in the data set
     * Ha : the maximum value is an outlier
     *
     * @param vol
     */
    private void compute(final VolumeFloatZz vol)
    {
        final Dimensions dims = vol.getDimensions();
        final Point3D p1 = new Point3D();
        double sum1 = 0;
        for (p1.setZ(0); p1.getZ() < dims.dimZ; p1.incZ())
        {
            for (p1.setY(0); p1.getY() < dims.dimY; p1.incY())
            {
                for (p1.setX(0); p1.getX() < dims.dimX; p1.incX())
                {
                    sum1 += vol.getVoxel(p1);
                }
            }
        }

        // T.F.Chan, G.H. Golub and R.J. LeVeque (1983). ""Algorithms for computing the sample variance: Analysis and
        // recommendations", The American Statistician, 37"
        // K algorithm de la page wikipedia
        final double mean = sum1 / sampleSize;
        double sum2 = 0;
        double sum3 = 0;
        for (p1.setZ(0); p1.getZ() < dims.dimZ; p1.incZ())
        {
            for (p1.setY(0); p1.getY() < dims.dimY; p1.incY())
            {
                for (p1.setX(0); p1.getX() < dims.dimX; p1.incX())
                {
                    final double v = vol.getVoxel(p1) - mean;
                    sum2 += v * v;
                    sum3 += v;
                }
            }
        }
        final double stdDev = Math.sqrt((sum2 - sum3 * sum3 / sampleSize) / (sampleSize - 1));
        grubbsStatistics = Math.abs(maxValue - mean) / stdDev;
        pValue = 1 - pgrubbs(grubbsStatistics, sampleSize);
//        IJ.log("size " + sampleSize);
//        IJ.log("mean " + mean);
//        IJ.log("sd " + stdDev);
//        IJ.log("grubbs " + grubbsStatistics);
//        IJ.log("pvalue " + pValue);
    }

    /**
     * R function.
     * <p>
     * @param p grubbs statistics
     * @param n sample size
     * <p>
     * @return 1 - pvalue
     */
    private static double pgrubbs(final double p,
                                  final double n)
    {
        final double p2 = p * p;
        final double s = (p2 * n * (2 - n)) / (p2 * n - (n - 1) * (n - 1));
        final double t = Math.sqrt(s);
        double res;
        if (Double.isNaN(t))
        {
            res = 0;
        }
        else
        {
            res = n * (1 - pt(t, n - 2)); // si res > 1 res=1
        }
        return 1 - res;
    }

    /*
     *
     * s <- (p^2 * n * (2 - n))/(p^2 * n - (n - 1)^2)
     * t <- sqrt(s)
     * if (is.nan(t)) {
     * res <- 0
     * }
     * else {
     * res <- n * (1 - pt(t, n - 2))
     * res[res > 1] <- 1
     * }
     * return(1 - res)
     *
     */
    /**
     * Distribution function of the Student t distribution.
     * <p>
     * @param q  quantile
     * @param df degrees of freedom
     * <p>
     * @return Student t distribution value at q and df
     */
    private static double pt(final double q,
                             final double df)
    {
        return new TDistribution(df).cumulativeProbability(q);
    }
}
