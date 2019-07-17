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
public final class RegistrationTwoPassAverage27
{
    private static final int NEIGHBORHOOD_SIZE = 27;

    private final RegistrationData data;

    private final DisplacementField field;

    private final Dimensions dim1;

    private final Dimensions dim2;

    private AtomicInteger count;

    private int nOperations;

    private final String savePath;

    public RegistrationTwoPassAverage27(final RegistrationData dat,
                                        final DisplacementField fld,
                                        final String path)
    {
        this.data = dat;
        this.field = fld;
        dim1 = data.windowDimStart;
        dim2 = data.windowDimEnd;
        savePath = path;
    }

    public void match()
    {
        count = new AtomicInteger();
        showProgress();

        // Initialisation du champs de déplacement
        initializeField();

        final long t1 = firstPass();
        IJ.saveString(field.toString(data, t1), savePath + "/pass_" + 0 + ".txt");

        count.set(0);
        final long t2 = secondPass();
        IJ.saveString(field.toString(data, t2), savePath + "/pass_" + 1 + ".txt");
    }

    private long firstPass()
    {
        nOperations = field.getSize();
        final int nthreads = Math.min(ConcurrencyUtils.getNumberOfThreads(), nOperations);
        final Future<?>[] futures = new Future<?>[nthreads];
        final int k = nOperations / nthreads;

        long t1 = System.nanoTime();
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
            futures[j] = ConcurrencyUtils.submit(new ProcessorPass1(first, last));
        }
        ConcurrencyUtils.waitForCompletion(futures);
        long t2 = System.nanoTime() - t1;

        return t2;
    }

    private long secondPass()
    {
        nOperations = field.getSize();
        final int nthreads = Math.min(ConcurrencyUtils.getNumberOfThreads(), nOperations);
        final Future<?>[] futures = new Future<?>[nthreads];
        final int k = nOperations / nthreads;

        long t1 = System.nanoTime();
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
            futures[j] = ConcurrencyUtils.submit(new ProcessorPass2(first, last));
        }
        ConcurrencyUtils.waitForCompletion(futures);

        long t2 = System.nanoTime() - t1;

        return t2;
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
        IJ.showProgress(count.incrementAndGet(), nOperations);
    }

    /**
     * Première passe : grande boîte
     */
    private class ProcessorPass1 implements Runnable
    {
        private final int from;

        private final int to;

        ProcessorPass1(final int p1,
                       final int p2)
        {
            from = p1;
            to = p2;
        }

        @Override
        public void run()
        {
            final Window3D win = new Window3D(dim1, data.windowType);
            final PhaseCorrelation3D corr = new PhaseCorrelation3D(dim1);
            for (int p = from; p < to; p++)
            {
                final Point3D point1 = field.getPosition(p);
                final VolumeFloatZz v1 = win.apodizeSubVolume(point1, data.volume1);
                final Point3D point2 = field.getTranslatedPosition(p);
                final VolumeFloatZz v2 = win.apodizeSubVolume(point2, data.volume2);
                final Displacement tr = corr.correlate(v1, v2, true, data.pValueLevel);
                field.update(p, tr);
                showProgress();
            }
        }
    }

    /**
     * Deuxième passe : boîte plus petite, moyenne sur les 26 voisins
     */
    private class ProcessorPass2 implements Runnable
    {
        private final int from;

        private final int to;

        ProcessorPass2(final int p1,
                       final int p2)
        {
            from = p1;
            to = p2;
        }

        @Override
        public void run()
        {
            final Window3D win = new Window3D(dim2, data.windowType);
            final PhaseCorrelation3D corr = new PhaseCorrelation3D(dim2);
            for (int p = from; p < to; p++)
            {
                if (!field.getDisplacement(p).isSpurious())
                {
                    double meanU = 0, meanV = 0, meanW = 0;
                    int l = 0;
                    double sw = 0;
                    for (int i = -1; i < 2; i++)
                    {
                        for (int j = -1; j < 2; j++)
                        {
                            for (int k = -1; k < 2; k++)
                            {
                                final Point3D point1 = new Point3D(field.getPosition(p), i, j, k);
                                final VolumeFloatZz v1 = win.apodizeSubVolume(point1, data.volume1);
                                final Point3D point2 = new Point3D(field.getTranslatedPosition(p), i, j, k); // point décalé
                                final VolumeFloatZz v2 = win.apodizeSubVolume(point2, data.volume2);
                                final Displacement tr = corr.correlate(v1, v2, true, data.pValueLevel);
                                if (!tr.isSpurious())
                                {
                                    double d = Math.sqrt(i * i + j * j + k * k);
                                    if (d == 0)
                                    {
                                        d = 1;
                                    }
                                    meanU += tr.getU();
                                    meanV += tr.getV();
                                    meanW += tr.getW();
                                    sw += (1 / d);
                                    l++;
                                }
                                if (p == 0)
                                {
                                    IJ.log("" + (tr.getU()) + " " + tr.isSpurious());
                                }
                            }
                        }
                    }
                    if (l > 0)
                    {
                        meanU /= l;
                        meanV /= l;
                        meanW /= l;
                    }
//                IJ.log("" + count);
                    field.update(p, meanU, meanV, meanW);
                }
                showProgress();
            }
        }
    }

    /**
     * Deuxième passe : boîte plus petite, moyenne sur les 26 voisins
     */
    private class ProcessorPass2_ implements Runnable
    {
        private final int from;

        private final int to;

        ProcessorPass2_(final int p1,
                        final int p2)
        {
            from = p1;
            to = p2;
        }

        @Override
        public void run()
        {
            final Window3D win = new Window3D(dim2, data.windowType);
            final PhaseCorrelation3D corr = new PhaseCorrelation3D(dim2);
            for (int p = from; p < to; p++)
            {
                final Point3D point1 = field.getPosition(p);
                final VolumeFloatZz v1 = win.apodizeSubVolume(point1, data.volume1);
                final Point3D pointr = field.getTranslatedPosition(p);

                double meanU = 0, meanV = 0, meanW = 0;
                int l = 0;
                for (int i = -1; i < 2; i++)
                {
                    for (int j = -1; j < 2; j++)
                    {
                        for (int k = -1; k < 2; k++)
                        {
                            final Point3D point2 = new Point3D(pointr, i, j, k); // point décalé
//                            IJ.log("" + point2.getX() + " " + point2.getY() + " " + point2.getZ());
                            final VolumeFloatZz v2 = win.apodizeSubVolume(point2, data.volume2);
//                            IJ.log("pouet");
                            final Displacement tr = corr.correlate(v1, v2, true, data.pValueLevel);
                            if (!tr.isSpurious())
                            {
                                meanU += (tr.getU());
                                meanV += (tr.getV());
                                meanW += (tr.getW());
                                l++;
                            }
                            if (p == 0)
                            {
                                IJ.log("" + (tr.getU() - i) + " " + tr.isSpurious());
                            }
                        }
                    }
                }
                if (l > 0)
                {
                    meanU /= l;
                    meanV /= l;
                    meanW /= l;
                }
//                IJ.log("" + count);
                field.update(p, meanU, meanV, meanW);
                showProgress();
            }
        }
    }
}
