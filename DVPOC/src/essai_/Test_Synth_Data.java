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
package essai_;

import correlation.Displacement;
import correlation.PhaseCorrelation3D;
import experiments.SyntheticData;
import experiments.TransfoTypes;
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
public class Test_Synth_Data implements PlugIn
{
//    public static final void test0()
//    {
//        final int size = 256;
//        final double delta = 0.0;
//        final double k = 0.05;
//
//        final Dimensions dims = new Dimensions(size, size, size);
//        final SyntheticData d = new SyntheticData();
//
//        final VolumeFloatZz[] vols = d.createData(size, delta, k, TransfoTypes.STRETCHING, 2, 3);
//
//        vols[0].getImagePlus("t1").show();
//        vols[1].getImagePlus("t2").show();
//        final Window3D win = new Window3D(dims, WindowType.HANN);
//        final VolumeFloatZz v1 = win.apodize(vols[0]);
//        final VolumeFloatZz v2 = win.apodize(vols[1]);
//
//        final PhaseCorrelation3D corr = new PhaseCorrelation3D(dims);
//        final Displacement tr = corr.correlate(v1, v2, true, 0.0);
//
//        IJ.log("" + tr.getU() + " " + tr.getV() + " " + tr.getW());
//        IJ.log("" + computeError(tr, delta));
//
//        corr.getCorrelation().getImagePlus().show();
//
//    }
//
//    public static final void test1()
//    {
//        final int size = 32;
//        final double delta = 0.0;
//        final double k = 0.1;
//        final int nrep = 10;
//
//        final Dimensions dims = new Dimensions(size, size, size);
//        final SyntheticData d = new SyntheticData();
//
//        for (int s = 1; s < 5; s++)
//        {
//            IJ.log("s = " + s);
//            for (int i = 0; i < nrep; i++)
//            {
//                final VolumeFloatZz[] vols = d.createData(size, delta, k, TransfoTypes.SHEARING, s);
//
//                final Window3D win = new Window3D(dims, WindowType.HANN);
//                final VolumeFloatZz v1 = win.apodize(vols[0]);
//                final VolumeFloatZz v2 = win.apodize(vols[1]);
//
//                final PhaseCorrelation3D corr = new PhaseCorrelation3D(dims);
//                final Displacement tr = corr.correlate(v1, v2, true, 0.0);
//
//                IJ.log("" + computeError(tr, delta));
//            }
//        }
//
//    }
//
//    public static final void premanip()
//    {
//        final int[] pfactors =
//        {
//            1, 2, 5, 10, 50
//        };
//        final int[] partypes =
//        {
//            0, 1, 2
//        };
//        final int nrep = 200;
//
//        final int size = 32;
//        final double delta = 0.0;
//        final double k = 0.1;
//
//        final Dimensions dims = new Dimensions(size, size, size);
//        final SyntheticData d = new SyntheticData();
//        final Window3D win = new Window3D(dims, WindowType.HAMMING);
//        final PhaseCorrelation3D corr = new PhaseCorrelation3D(dims);
//
//        IJ.log("particule_type" + " " + "particule_factor" + " " + "error");
//
//        int j = 0;
//        for (int pt : partypes)
//        {
//            for (int pf : pfactors)
//            {
//                for (int i = 0; i < nrep; i++)
//                {
//                    final VolumeFloatZz[] vols = d.createData(size, delta, k, TransfoTypes.SHEARING, pf, pt);
//                    final VolumeFloatZz v1 = win.apodize(vols[0]);
//                    final VolumeFloatZz v2 = win.apodize(vols[1]);
//                    final Displacement tr = corr.correlate(v1, v2, true, 0.0);
//                    IJ.log("" + j + " " + pt + " " + pf + " " + computeError(tr, delta));
//                    j++;
//                }
//            }
//        }
//    }

    public static final void test3()
    {
        final int size = 16;

        final double delta = 0.5;
        final double k = 0.2;
        final double[] freqs = SyntheticData.createSeq(0.5d, 1d, 0.01d);
        final double freq = 0.5;

        final Dimensions dims = new Dimensions(size, size, size);

        final VolumeFloatZz vol1 = SyntheticData.createGaussData(size, delta, k, TransfoTypes.NONE, 2d);

        final VolumeFloatZz vol2 = SyntheticData.createGaussData(size, delta, k, TransfoTypes.SHEARING, 2d);

        vol1.getImagePlus("t1").show();
        vol2.getImagePlus("t2").show();

        final Window3D win = new Window3D(dims, WindowType.HAMMING);
        final VolumeFloatZz v1 = win.apodize(vol1);
        final VolumeFloatZz v2 = win.apodize(vol2);

        final PhaseCorrelation3D corr = new PhaseCorrelation3D(dims);
        final Displacement tr = corr.correlate(v1, v2, true, 0.0);

        IJ.log("" + tr.getU() + " " + tr.getV() + " " + tr.getW());
        IJ.log("" + SyntheticData.computeError(tr, delta));

        corr.getCorrelation().getImagePlus().show();

    }

    public static final void test4()
    {
        final int size = 64;
        final SyntheticData simu = new SyntheticData();

        final double delta = 0.5;
        final double k = 0.2;

        final Dimensions dims = new Dimensions(size, size, size);
        final Window3D win = new Window3D(dims, WindowType.HAMMING);

        final VolumeFloatZz[] vols = simu.createSineDatas(size, delta, k, TransfoTypes.SHEARING);

        vols[0].getImagePlus("t1").show();
        vols[1].getImagePlus("t2").show();

        final VolumeFloatZz v1 = win.apodize(vols[0]);
        final VolumeFloatZz v2 = win.apodize(vols[1]);

        final PhaseCorrelation3D corr = new PhaseCorrelation3D(dims);
        final Displacement tr = corr.correlate(v1, v2, true, 0.0);

        IJ.log("" + tr.getU() + " " + tr.getV() + " " + tr.getW());
        IJ.log("" + SyntheticData.computeError(tr, delta));

        corr.getCorrelation().getImagePlus().show();

    }

    @Override
    public void run(String string)
    {
        test4();
    }
}
