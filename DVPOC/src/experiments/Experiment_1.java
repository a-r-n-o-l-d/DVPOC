/*
 * Copyright (C) 2018 Arnold Fertin
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
package experiments;

import correlation.Displacement;
import correlation.PhaseCorrelation3D;
import ij.IJ;
import ij.plugin.PlugIn;
import volume.Dimensions;
import volume.VolumeFloatZz;
import volume.Window3D;
import volume.WindowType;

/**
 *
 * @author Arnold Fertin
 */
public class Experiment_1 implements PlugIn
{
    @Override
    public void run(String string)
    {
        essai4();
    }

    private void essai1()
    {
        final double pvalue = 1e-6;
//        final double freq = 0.5;
        final double[] freqs =
        {
            1d
        };//SyntheticData.createSeq(1d / 3d, 1d, 0.01d);
        final int[] sizes =
        {
            /*
             * 8,
             */ 16/*
         * , 32/*
         * , 64
         */
        };
        final double delta = 0.44;
        final double[] ka = SyntheticData.createSeq(0.001d, 0.3d, 0.001d);

        IJ.log(" " + "vol_size" + " " + "window" + " " + "shearing" + " " + "error" + " " + "MaxCorr"
               + " " + "GrubbStat" + " " + "p_value" + " " + "spurious");
        int i = 0;
        for (int s : sizes)
        {
            final Dimensions dims = new Dimensions(s, s, s);
            final VolumeFloatZz vol1 = SyntheticData.createSineData(s, freqs);
            final PhaseCorrelation3D corr = new PhaseCorrelation3D(dims);

            for (WindowType w : WindowType.values())
            {
                final Window3D win = new Window3D(dims, w);

                for (double k : ka)
                {
                    final VolumeFloatZz vol2 = SyntheticData.createSineDataTransform(s, delta, k, TransfoTypes.SHEARING,
                                                                                     freqs);
                    final VolumeFloatZz v1 = win.apodize(vol1);
                    final VolumeFloatZz v2 = win.apodize(vol2);
                    final Displacement tr = corr.correlate(v1, v2, true, pvalue);
                    IJ.log("" + i + " " + s + " " + w + " " + k + " "
                           + SyntheticData.computeError(tr, delta) + " "
                           + tr.getMaximumOfCorrelation() + " "
                           + tr.getGrubbsStatistics() + " "
                           + tr.getPValue() + " "
                           + tr.isSpurious());
                    i++;
                }
            }
        }
    }

    private void essai2()
    {
        final double pvalue = 1e-6;
        final int nrep = 1000;
        final int[] sizes =
        {
            16
        };
        final double delta = 0.5d;
        final double[] ka = SyntheticData.createSeq(0.01d, 0.3d, 0.01d);
        final SyntheticData simu = new SyntheticData();

        IJ.log(" " + "vol_size" + " " + "window" + " " + "shearing" + " " + "error");
        int i = 0;
        for (int s : sizes)
        {
            final Dimensions dims = new Dimensions(s, s, s);
//            final VolumeFloatZz vol1 = SyntheticData.createSineData(s, freqs);
            final PhaseCorrelation3D corr = new PhaseCorrelation3D(dims);

//            for (WindowType w : WindowType.values())
//            {
            final Window3D win = new Window3D(dims, WindowType.HAMMING);

            for (double k : ka)
            {
                double meanError = 0;
                for (int j = 0; j < nrep; j++)
                {
                    final VolumeFloatZz[] vols = simu.createSineDatas(s, delta, k, TransfoTypes.SHEARING);
                    final VolumeFloatZz v1 = win.apodize(vols[0]);
                    final VolumeFloatZz v2 = win.apodize(vols[1]);
                    final Displacement tr = corr.correlate(v1, v2, true, pvalue);
                    meanError += SyntheticData.computeError(tr, delta);
                }
                meanError /= nrep;
                IJ.log("" + i + " " + s + " " + WindowType.HAMMING + " " + k + " " + meanError);
                i++;
            }
//            }
        }
    }

    private void essai3()
    {
        final double pvalue = 1e-6;
        final int nrep = 100;
        final int[] sizes =
        {
            16
        };
        final double delta = 0.45;
        final double[] ka = SyntheticData.createSeq(0.01d, 0.3d, 0.01d);
        final SyntheticData simu = new SyntheticData();

        IJ.log(" " + "vol_size" + " " + "window" + " " + "shearing" + " " + "error");
        int i = 0;
        for (int s : sizes)
        {
            final Dimensions dims = new Dimensions(s, s, s);
//            final VolumeFloatZz vol1 = SyntheticData.createSineData(s, freqs);
            final PhaseCorrelation3D corr = new PhaseCorrelation3D(dims);

//            for (WindowType w : WindowType.values())
//            {
            final Window3D win = new Window3D(dims, WindowType.HAMMING);

            for (double k : ka)
            {
                double meanError = 0;
                for (int j = 0; j < nrep; j++)
                {
                    final VolumeFloatZz[] vols = simu.createRandomData(s, delta, k, TransfoTypes.SHEARING);
                    final VolumeFloatZz v1 = win.apodize(vols[0]);
                    final VolumeFloatZz v2 = win.apodize(vols[1]);
                    final Displacement tr = corr.correlate(v1, v2, true, pvalue);
                    meanError += SyntheticData.computeError(tr, delta);
                }
                meanError /= nrep;
                IJ.log("" + i + " " + s + " " + WindowType.HAMMING + " " + k + " " + meanError);
                i++;
            }
//            }
        }
    }

    private void essai4()
    {
        final double pvalue = 1e-6;
        final int[] sizes =
        {
            /*
             * 8,
             */ 16/*
         * , 32/*
         * , 64
         */
        };
        final double delta = 0.5;
        final double[] ka = SyntheticData.createSeq(0.001d, 0.5d, 0.001d);

        IJ.log(" " + "vol_size" + " " + "window" + " " + "shearing" + " " + "error" + " " + "MaxCorr"
               + " " + "GrubbStat" + " " + "p_value" + " " + "spurious");
        int i = 0;
        for (int s : sizes)
        {
            final Dimensions dims = new Dimensions(s, s, s);
            final VolumeFloatZz vol1 = SyntheticData.createGaussData(s, delta, 0, TransfoTypes.NONE, 0.5d);
            final PhaseCorrelation3D corr = new PhaseCorrelation3D(dims);

            for (WindowType w : WindowType.values())
            {
                final Window3D win = new Window3D(dims, w);

                for (double k : ka)
                {
                    final VolumeFloatZz vol2 = SyntheticData.createGaussData(s, delta, k, TransfoTypes.STRETCHING, 0.5d);
                    final VolumeFloatZz v1 = win.apodize(vol1);
                    final VolumeFloatZz v2 = win.apodize(vol2);
                    final Displacement tr = corr.correlate(v1, v2, true, pvalue);
                    IJ.log("" + i + " " + s + " " + w + " " + k + " "
                           + SyntheticData.computeError(tr, delta) + " "
                           + tr.getMaximumOfCorrelation() + " "
                           + tr.getGrubbsStatistics() + " "
                           + tr.getPValue() + " "
                           + tr.isSpurious());
                    i++;
                }
            }
        }
    }
}