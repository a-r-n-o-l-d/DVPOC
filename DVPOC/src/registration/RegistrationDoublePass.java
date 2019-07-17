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
import correlation.PhaseCorrelation3D;
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
public final class RegistrationDoublePass
{
    private final RegistrationData data;

    private final DisplacementField field;

    private Dimensions dim;

    private final Dimensions dim1;

    private final Dimensions dim2;

    private AtomicInteger count;

    private int nOperations;

    public RegistrationDoublePass(final RegistrationData dat,
                                  final DisplacementField fld)
    {
        this.data = dat;
        this.field = fld;
        dim1 = data.windowDimStart;
        dim2 = data.windowDimEnd;
    }

    public void match()
    {
        count = new AtomicInteger();
        showProgress();

        // Initialisation du champs de déplacement
        initializeField();

        nOperations = field.getSize();
        final int nthreads = Math.min(ConcurrencyUtils.getNumberOfThreads(), nOperations);
        final Future<?>[] futures = new Future<?>[nthreads];
        final int k = nOperations / nthreads;
        for (int i = 0; i < 2; i++)
        {
            if (i == 0)
            {
                dim = dim1;
            }
            else
            {
                dim = dim2;
            }
            boolean subpix = false;
            if (i == 1)
            {
                subpix = true;
            }
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
                futures[j] = ConcurrencyUtils.submit(new Processor(first, last, subpix));
            }
            ConcurrencyUtils.waitForCompletion(futures);
        }
    }

    private void initializeField()
    {
        final Dimensions d = data.volume1.getDimensions();
        final Window3D win = new Window3D(d, data.windowType);
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
        IJ.showProgress(count.incrementAndGet(), nOperations * 2);
    }

    private class Processor implements Runnable
    {
        private final int from;

        private final int to;

        private final boolean sb;

        Processor(final int p1,
                  final int p2,
                  final boolean subpix)
        {
            from = p1;
            to = p2;
            sb = subpix;
        }

        @Override
        public void run()
        {
            final Window3D win = new Window3D(dim, data.windowType);
            final PhaseCorrelation3D corr = new PhaseCorrelation3D(dim);
            for (int j = from; j < to; j++)
            {
                final Point3D p1 = field.getPosition(j);
                final VolumeFloatZz v1 = win.apodizeSubVolume(p1, data.volume1);
                final Point3D p2 = field.getTranslatedPosition(j);
                final VolumeFloatZz v2 = win.apodizeSubVolume(p2, data.volume2);
                final Displacement tr = corr.correlate(v1, v2, sb, data.pValueLevel);
                field.update(j, tr);
                showProgress();
            }
        }
    }
}
