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

import correlation.PhaseCorrelation3D;
import correlation.Displacement;
import fht.FHT3D;
import ij.IJ;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;
import util.ConcurrencyUtils;
import volume.Dimensions;
import volume.Point3D;
import volume.VolumeFloatZz;
import volume.Window3D;

/**
 *
 * @author Arnold Fertin
 */
public final class Registration
{
    private final RegistrationData data;

    private final DisplacementField field;

    private Dimensions[] dimSerie;

    private AtomicInteger count;

    private int nOperations;

    public Registration(final RegistrationData dat,
                        final DisplacementField field)
    {
        this.data = dat;
        this.field = field;
    }

    public void match()
    {
        count = new AtomicInteger();
        showProgress();

        // Initialisation du champs de déplacement
        initializeField();

        // Préparation des fenêtrages et corrélateurs
        final int n = getNumberOfIteration();
        dimSerie = new Dimensions[n];
        for (int i = 0, k = 1; i < n; i++, k *= 2)
        {
            final int dimX = Math.max(data.windowDimStart.dimX / k, data.windowDimEnd.dimX);
            final int dimY = Math.max(data.windowDimStart.dimY / k, data.windowDimEnd.dimY);
            final int dimZ = Math.max(data.windowDimStart.dimZ / k, data.windowDimEnd.dimZ);
            dimSerie[i] = new Dimensions(dimX, dimY, dimZ);
        }

        // Calculs
        nOperations = field.getSize() * n;
        final int nthreads = Math.min(ConcurrencyUtils.getNumberOfThreads(), field.getSize());
        final Future<?>[] futures = new Future<?>[nthreads];
        final int k = field.getSize() / nthreads;
        for (int j = 0; j < nthreads; j++)
        {
            final int first = j * k;
            final int last;
            if (j == nthreads - 1)
            {
                last = field.getSize();
            }
            else
            {
                last = first + k;
            }
            futures[j] = ConcurrencyUtils.submit(new Processor(first, last));
        }
        ConcurrencyUtils.waitForCompletion(futures);
//        new Processor(0, field.getSize()).run();
    }

    private int getNumberOfIteration()
    {
        int sizeX = data.windowDimStart.dimX;
        int countX = 1;
        while (sizeX > data.windowDimEnd.dimX)
        {
            sizeX /= 2;
            countX++;
        }
        int sizeY = data.windowDimStart.dimY;
        int countY = 1;
        while (sizeY > data.windowDimEnd.dimY)
        {
            sizeY /= 2;
            countY++;
        }
        int sizeZ = data.windowDimStart.dimZ;
        int countZ = 1;
        while (sizeZ > data.windowDimEnd.dimZ)
        {
            sizeZ /= 2;
            countZ++;
        }
        return Math.max(Math.max(countX, countY), countZ);
    }

    private void initializeField()
    {
        final Dimensions dim = data.volume1.getDimensions();
        final Window3D win = new Window3D(dim, data.windowType);
        final VolumeFloatZz v1 = win.apodize(data.volume1);
        final VolumeFloatZz v2 = win.apodize(data.volume2);
        FHT3D.zeroPaddingInZ(v1);
        FHT3D.zeroPaddingInZ(v2);
        final PhaseCorrelation3D co = new PhaseCorrelation3D(v1.getDimensions());
        final Displacement tr = co.correlate(v1, v2, true, data.pValueLevel);
        field.initialize(tr);
    }

    private synchronized void showProgress()
    {
        IJ.showProgress(count.incrementAndGet(), nOperations);
    }

    private class Processor implements Runnable
    {
        private final int from;

        private final int to;

        Processor(final int p1,
                  final int p2)
        {
            from = p1;
            to = p2;
        }

        @Override
        public void run()
        {
            final int nIte = dimSerie.length;
//            boolean subPix = false;
            for (int ite = 0; ite < nIte; ite++)
            {
                final Window3D win = new Window3D(dimSerie[ite], data.windowType);
                final PhaseCorrelation3D corr = new PhaseCorrelation3D(dimSerie[ite]);
//                if (ite == nIte - 1)
//                {
//                    subPix = true; // estimation subpixel pour la dernière itération
//                }
                for (int j = from; j < to; j++)
                {
                    final Point3D p1 = field.getPosition(j);
                    final VolumeFloatZz v1 = win.apodizeSubVolume(p1, data.volume1);
                    final Point3D p2 = field.getTranslatedPosition(j);
                    final VolumeFloatZz v2 = win.apodizeSubVolume(p2, data.volume2);
                    final Displacement tr = corr.correlate(v1, v2, true, data.pValueLevel);
                    field.update(j, tr);
                    showProgress();
//                    IJ.log("Iteration : " + ite + " Point : " + j);
//                    IJ.log("" + p1.getX() + " " + p1.getY() + " " + p1.getZ());
//                    IJ.log("Window=" + data.windowType);
//                    IJ.log("U=" + tr.getU());
//                    IJ.log("V=" + tr.getV());
//                    IJ.log("W=" + tr.getW());
//                    IJ.log("MaxCorr=" + tr.getMaximumOfCorrelation());
//                    IJ.log("Spurious=" + tr.isSpurious());
//                    IJ.log("");
                }
//                IJ.log("Iteration : " + ite);
//                IJ.log(field.print());
//                IJ.log("");
            }
        }
    }
}
