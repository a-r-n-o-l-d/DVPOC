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

import fht.FHT3D;
import fht.SwapQuadrants3D;
import ij.IJ;
import org.apache.commons.math3.random.GaussianRandomGenerator;
import org.apache.commons.math3.random.MersenneTwister;
import registration.GrubbsTest;
import volume.Dimensions;
import volume.Point3D;
import volume.VolumeFloatZz;

/**
 * 
 *
 * @author Arnold Fertin
 */
public final class PhaseCorrelation3D
{

    public static final double DBL_EPSILON = 2.2204460492503131E-16;

    private final VolumeFloatZz correlation;

    private final Dimensions dims;

    private Displacement displacement;

    private boolean edge;
//    private boolean spurious;

    public PhaseCorrelation3D(final Dimensions dim)
    {
        dims = dim;
        correlation = new VolumeFloatZz(dims);
    }

    public Displacement correlateTest(final VolumeFloatZz p,
                                      final VolumeFloatZz q)
    {
        // Si déjà spurious pas besoin de faire le calcul
        edge = false;
        FHT3D.transform(p);
        FHT3D.transform(q);
        final Point3D pos = new Point3D();
        final Point3D posSym = new Point3D();

        final GaussianRandomGenerator gauss = new GaussianRandomGenerator(new MersenneTwister());
        final SwapQuadrants3D sw = new SwapQuadrants3D(dims);
        double u = 0, v = 0, w = 0;
        final int nite = 500;
        for (int i = 0; i < nite; i++)
        {
            for (pos.setZ(0); pos.getZ() < dims.dimZ; pos.incZ())
            {
                posSym.setZ(dims.dimZ - pos.getZ());
                if (posSym.getZ() > dims.dimZ - 1)
                {
                    posSym.setZ(0);
                }
                for (pos.setY(0); pos.getY() < dims.dimY; pos.incY())
                {
                    posSym.setY(dims.dimY - pos.getY());
                    if (posSym.getY() > dims.dimY - 1)
                    {
                        posSym.setY(0);
                    }
                    for (pos.setX(0); pos.getX() < dims.dimX; pos.incX())
                    {
                        posSym.setX(dims.dimX - pos.getX());
                        if (posSym.getX() > dims.dimX - 1)
                        {
                            posSym.setX(0);
                        }
                        final double p1 = p.getVoxel(pos) + gauss.nextNormalizedDouble() / 10;    // DHT[p](omega)
                        final double p2 = p.getVoxel(posSym) + gauss.nextNormalizedDouble() / 10; // DHT[p](-omega)
                        final double q1 = q.getVoxel(pos) + gauss.nextNormalizedDouble() / 10;    // DHT[q](omega)
                        final double q2 = q.getVoxel(posSym) + gauss.nextNormalizedDouble() / 10; // DHT[q](-omega)
                        double norm = Math.sqrt(p2 * p2 + p1 * p1) * Math.sqrt(q2 * q2 + q1 * q1);
                        if (norm == 0)
                        {
                            norm = DBL_EPSILON;
                        }
                        final double c = ((p2 - p1) * q2 + (p2 + p1) * q1) / norm;
                        correlation.setVoxel(pos, (float) c);
                    }
                }
            }
            FHT3D.transform(correlation);
            sw.swap(correlation);
            computeTranslation(true, 0);
            IJ.log("" + displacement.getU() + " " + displacement.getV() + " " + displacement.getW());
            u += displacement.getU();
            v += displacement.getV();
            w += displacement.getW();
        }
        displacement = new Displacement(u / nite, v / nite, w / nite);
        return displacement;
    }

    public Displacement correlate(final VolumeFloatZz p,
                                  final VolumeFloatZz q,
                                  final boolean subPixelEstimation,
                                  final double pValueLevel)
    {
        // Si déjà spurious pas besoin de faire le calcul
        edge = false;
        FHT3D.transform(p);
        FHT3D.transform(q);
        final Point3D pos = new Point3D();
        final Point3D posSym = new Point3D();

        for (pos.setZ(0); pos.getZ() < dims.dimZ; pos.incZ())
        {
            posSym.setZ(dims.dimZ - pos.getZ());
            if (posSym.getZ() > dims.dimZ - 1)
            {
                posSym.setZ(0);
            }
            for (pos.setY(0); pos.getY() < dims.dimY; pos.incY())
            {
                posSym.setY(dims.dimY - pos.getY());
                if (posSym.getY() > dims.dimY - 1)
                {
                    posSym.setY(0);
                }
                for (pos.setX(0); pos.getX() < dims.dimX; pos.incX())
                {
                    posSym.setX(dims.dimX - pos.getX());
                    if (posSym.getX() > dims.dimX - 1)
                    {
                        posSym.setX(0);
                    }
                    final double p1 = p.getVoxel(pos);    // DHT[p](omega)
                    final double p2 = p.getVoxel(posSym); // DHT[p](-omega)
                    final double q1 = q.getVoxel(pos);    // DHT[q](omega)
                    final double q2 = q.getVoxel(posSym); // DHT[q](-omega)
                    double norm = Math.sqrt(p2 * p2 + p1 * p1) * Math.sqrt(q2 * q2 + q1 * q1);
                    if (norm == 0)
                    {
                        norm = DBL_EPSILON;
                    }
                    final double c = ((p2 - p1) * q2 + (p2 + p1) * q1) / norm;
                    correlation.setVoxel(pos, (float) c);
                }
            }
        }
        FHT3D.transform(correlation);
        final SwapQuadrants3D sw = new SwapQuadrants3D(dims);
        sw.swap(correlation);
        computeTranslation(subPixelEstimation, pValueLevel);

        return displacement;
    }

    public VolumeFloatZz getCorrelation()
    {
        return correlation;
    }

    public Displacement getDisplacement()
    {
        return displacement;
    }

    /**
     * Foroosh, H., Zerubia, J. B., & Berthod, M. (2002). Extension of phase correlation to subpixel
     * registration. Image Processing, IEEE Transactions on, 11(3), 188-200.
     *
     * @param subPixelEstimation
     */
    private void computeTranslation(final boolean subPixelEstimation,
                                    final double pValueLevel)
    {
        double maxCorr = Double.MIN_VALUE;
        final Point3D p = new Point3D();
        final Point3D pMax = new Point3D();
        for (p.setZ(0); p.getZ() < dims.dimZ; p.incZ())
        {
            for (p.setY(0); p.getY() < dims.dimY; p.incY())
            {
                for (p.setX(0); p.getX() < dims.dimX; p.incX())
                {
                    final double v = correlation.getVoxel(p);
                    if (v > maxCorr)
                    {
                        maxCorr = v;
                        pMax.set(p);
                    }
                }
            }
        }
        double u = pMax.getX() - dims.dimX / 2;
        double v = pMax.getY() - dims.dimY / 2;
        double w = pMax.getZ() - dims.dimZ / 2;
        if (subPixelEstimation)
        {
            u += getDeltaX(pMax, maxCorr);
            v += getDeltaY(pMax, maxCorr);
            w += getDeltaZ(pMax, maxCorr);
        }
        final GrubbsTest test = new GrubbsTest(correlation, maxCorr);

        boolean spurious = false;
        if (edge)
        {
            spurious = true;
        }

        displacement = new Displacement(maxCorr, u, v, w, test.getGrubbsStatistics(), test.getPValue(), spurious);
    }

    private double getDelta(final double cNeg,
                            final double cPos,
                            final double maxCorr)
    {
        if (cNeg > cPos)
        {
            return -cNeg / (cNeg + maxCorr); // -c1 / (c1 + c0)
        }
        if (cPos > cNeg)
        {
            return cPos / (cPos + maxCorr); // c1 / (c1 + c0)
        }
        return 0;
    }

    private double getDeltaX(final Point3D pMax,
                             final double maxCorr)
    {
        final Point3D p = new Point3D(pMax);
        p.setX(pMax.getX() - 1);
        if (!dims.checkPosition(p))
        {
            edge = true;
            return 0;
        }
        final double cX1 = correlation.getVoxel(p);
        p.setX(pMax.getX() + 1);
        if (!dims.checkPosition(p))
        {
            edge = true;
            return 0;
        }
        final double cX2 = correlation.getVoxel(p);
        return getDelta(cX1, cX2, maxCorr);
    }

    private double getDeltaY(final Point3D pMax,
                             final double maxCorr)
    {
        final Point3D p = new Point3D(pMax);
        p.setY(pMax.getY() - 1);
        if (!dims.checkPosition(p))
        {
            edge = true;
            return 0;
        }
        final double cY1 = correlation.getVoxel(p);
        p.setY(pMax.getY() + 1);
        if (!dims.checkPosition(p))
        {
            edge = true;
            return 0;
        }
        final double cY2 = correlation.getVoxel(p);
        return getDelta(cY1, cY2, maxCorr);
    }

    private double getDeltaZ(final Point3D pMax,
                             final double maxCorr)
    {
        final Point3D p = new Point3D(pMax);
        p.setZ(pMax.getZ() - 1);
        if (!dims.checkPosition(p))
        {
            edge = true;
            return 0;
        }
        final double cZ1 = correlation.getVoxel(p);
        p.setZ(pMax.getZ() + 1);
        if (!dims.checkPosition(p))
        {
            edge = true;
            return 0;
        }
        final double cZ2 = correlation.getVoxel(p);
        return getDelta(cZ1, cZ2, maxCorr);
    }
}

//        // test dans fourier
//        final VolumeFloatZz correlationTest = new VolumeFloatZz(dims);
//        for (p.setZ(0); p.getZ() < dims.dimZ; p.incZ())
//        {
//            pSym.setZ(dims.dimZ - p.getZ());
//            if (pSym.getZ() > dims.dimZ - 1)
//            {
//                pSym.setZ(0);
//            }
//            for (p.setY(0); p.getY() < dims.dimY; p.incY())
//            {
//                pSym.setY(dims.dimY - p.getY());
//                if (pSym.getY() > dims.dimY - 1)
//                {
//                    pSym.setY(0);
//                }
//                for (p.setX(0); p.getX() < dims.dimX; p.incX())
//                {
//                    pSym.setX(dims.dimX - p.getX());
//                    if (pSym.getX() > dims.dimX - 1)
//                    {
//                        pSym.setX(0);
//                    }
//                    final double p1 = v1.getVoxel(p);    // H(p1)(omega)
//                    final double p2 = v1.getVoxel(pSym); // H(p1)(-omega)
//                    final double q1 = v2.getVoxel(p);
//                    final double q2 = v2.getVoxel(pSym);
//
//                    final double pr = (p1 + p2) / 2;
//                    final double pi = (p2 - p1) / 2;
//                    final double qr = (q1 + q2) / 2;
//                    final double qi = (q2 - q1) / 2;
//
//                    double norm = Math.sqrt(pr * pr + pi * pi) * Math.sqrt(qr * qr + qi * qi);
//                    if (norm == 0)
//                    {
//                        norm = DBL_EPSILON;
//                    }
//                    final double cr = (pr * qr + pi * qi) / norm;
//                    final double ci = (pr * qi - pi * qr) / norm;
//
//                    final double c = cr - ci;
//                    correlationTest.setVoxel(p, (float) c);
//                }
//            }
//        }
//        FHT3D.transform(correlationTest);
//        sw.swap(correlationTest);
//        correlationTest.getImagePlus("fourier_test").show();
