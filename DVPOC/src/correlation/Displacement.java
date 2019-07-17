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
