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
package correlation;

/**
 *
 * @author Arnold Fertin
 */
public final class Displacement
{
    private double u;

    private double v;

    private double w;

    private double maximumOfCorrelation;

    private double grubbsStatistics;

    private double pValue;

    private boolean spurious;

    public Displacement(final double max,
                        final double uMax,
                        final double vMax,
                        final double wMax,
                        final double gs,
                        final double pv,
                        final boolean spurious)
    {
        u = uMax;
        v = vMax;
        w = wMax;
        maximumOfCorrelation = max;
        grubbsStatistics = gs;
        pValue = pv;
        this.spurious = spurious;
    }

    public Displacement(final double uMax,
                        final double vMax,
                        final double wMax)
    {
        u = uMax;
        v = vMax;
        w = wMax;
        maximumOfCorrelation = 0;
        grubbsStatistics = 0;
        pValue = 0;
        this.spurious = false;
    }

    public Displacement(final Displacement tr)
    {
        u = tr.u;
        v = tr.v;
        w = tr.w;
        maximumOfCorrelation = tr.maximumOfCorrelation;
    }

    public void add(final Displacement tr)
    {
        u += tr.u;
        v += tr.v;
        w += tr.w;
        maximumOfCorrelation = tr.maximumOfCorrelation;
        spurious = tr.spurious;
        grubbsStatistics = tr.grubbsStatistics;
        pValue = tr.pValue;
    }

    public void add(final double uu,
                    final double vv,
                    final double ww)
    {
        u += uu;
        v += vv;
        w += ww;
    }

    public double getU()
    {
        return u;
    }

    public double getV()
    {
        return v;
    }

    public double getW()
    {
        return w;
    }

    public double getMaximumOfCorrelation()
    {
        return maximumOfCorrelation;
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

    public boolean isSpurious()
    {
        return spurious;
    }

    public void setAsSpurious()
    {
        spurious = true;
    }
}
