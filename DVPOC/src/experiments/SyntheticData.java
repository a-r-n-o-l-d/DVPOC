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
package experiments;

import correlation.Displacement;
import org.apache.commons.math3.random.GaussianRandomGenerator;
import org.apache.commons.math3.random.MersenneTwister;
import volume.Dimensions;
import volume.Point3D;
import volume.VolumeFloatZz;

/**
 *
 * @author Arnold Fertin
 */
public final class SyntheticData
{
    private static final double SIZE_FACTOR = 1.5;

    private static final int PARTICULE_FACTOR = 2;
//

    private final MersenneTwister random;

    private final GaussianRandomGenerator gauss;

//    private final int nParticles;
    public SyntheticData()
    {
        random = new MersenneTwister();
        gauss = new GaussianRandomGenerator(random);
    }

    public static double[] createSeq(final double from,
                                     final double to,
                                     final double by)
    {
        final int length = (int) Math.round(Math.abs(to - from) / Math.abs(by));
        final double[] seq = new double[length];
//        seq[0] = from;
        for (int i = 0; i < length; i++)
        {
            seq[i] = from + ((double) i) * by; // seq[i - 1] + by;
        }
        return seq;
    }

    public static double computeError(final Displacement tr,
                                      final double delta)
    {
        return Math.sqrt(Math.pow(tr.getU() - delta, 2) + Math.pow(tr.getV() + delta, 2) + Math.
                pow(tr.getW() - delta, 2));
    }

    public static VolumeFloatZz createGaussData(final int size,
                                                final double delta,
                                                final double ka,
                                                final TransfoTypes ttype,
                                                final double sigma)
    {
        final Point3D point = new Point3D();
        final VolumeFloatZz data = new VolumeFloatZz(new Dimensions(size, size, size));

        for (int Z = 0; Z < size; Z++)
        {
            for (int Y = 0; Y < size; Y++)
            {
                for (int X = 0; X < size; X++)
                {
                    final int[] xyz = imageToCartesian(X, Y, Z, size);

                    // transfo inverse
                    double xt, yt, zt;
                    switch (ttype)
                    {
                        case NONE:
                            xt = xyz[0];
                            yt = xyz[1];
                            zt = xyz[2];
                            break;
                        case TRANSLATION_ONLY:
                            xt = xyz[0] - delta;
                            yt = xyz[1] - delta;
                            zt = xyz[2] - delta;
                            break;
                        case SHEARING:
                            xt = -ka * xyz[1] + xyz[0] - delta;
                            yt = xyz[1] - delta;
                            zt = xyz[2] - delta;
                            break;
                        case STRETCHING:
                            xt = xyz[0] / (ka + 1) - delta;
                            yt = xyz[1] / (1 - ka / 2) - delta;
                            zt = xyz[2] / (1 - ka / 2) - delta;
                            break;
                        default:
                            xt = xyz[0];
                            yt = xyz[1];
                            zt = xyz[2];
                            break;
                    }
                    final double r = distance(xt, yt, zt);
                    final double value = Math.exp(-r * r / (2 * sigma * sigma));
                    point.set(X, Y, Z);
                    data.setVoxel(point, (float) value);
                }
            }
        }

        return data;
    }

    public VolumeFloatZz[] createSineDatas(final int size,
                                           final double delta,
                                           final double ka,
                                           final TransfoTypes ttype)
    {
        final Point3D point = new Point3D();
        final VolumeFloatZz data1 = new VolumeFloatZz(new Dimensions(size, size, size));
        final VolumeFloatZz data2 = new VolumeFloatZz(new Dimensions(size, size, size));

        final double f = random.nextDouble() * (1d - 1d / 3d) + 1d / 3d;

        for (int Z = 0; Z < size; Z++)
        {
            for (int Y = 0; Y < size; Y++)
            {
                for (int X = 0; X < size; X++)
                {
                    final int[] xyz = imageToCartesian(X, Y, Z, size);
                    point.set(X, Y, Z);
                    data1.setVoxel(point, (float) radialSine(distance(xyz), f));

                    // transfo inverse
                    double xt, yt, zt;
                    switch (ttype)
                    {
                        case TRANSLATION_ONLY:
                            xt = xyz[0] - delta;
                            yt = xyz[1] - delta;
                            zt = xyz[2] - delta;
                            break;
                        case SHEARING:
                            xt = -ka * xyz[1] + xyz[0] - delta;
                            yt = xyz[1] - delta;
                            zt = xyz[2] - delta;
                            break;
                        case STRETCHING:
                            xt = xyz[0] / (ka + 1) - delta;
                            yt = xyz[1] / (1 - ka / 2) - delta;
                            zt = xyz[2] / (1 - ka / 2) - delta;
                            break;
                        default:
                            xt = xyz[0];
                            yt = xyz[1];
                            zt = xyz[2];
                            break;
                    }
                    data2.setVoxel(point, (float) radialSine(distance(xt, yt, zt), f));
                }
            }
        }
        final VolumeFloatZz[] res =
        {
            data1, data2
        };
        return res;
    }

    public static VolumeFloatZz createSineData(final int size,
                                               final double... freqs)
    {
        final Point3D point = new Point3D();
        final VolumeFloatZz data = new VolumeFloatZz(new Dimensions(size, size, size));

        for (int Z = 0; Z < size; Z++)
        {
            for (int Y = 0; Y < size; Y++)
            {
                for (int X = 0; X < size; X++)
                {
                    final int[] xyz = imageToCartesian(X, Y, Z, size);
                    double value = 0;
                    for (double f : freqs)
                    {
                        value += radialSine(distance(xyz), f);
                    }
                    point.set(X, Y, Z);
                    data.setVoxel(point, (float) value);
                }
            }
        }

        return data;
    }

    public static VolumeFloatZz createSineDataTransform(final int size,
                                                        final double delta,
                                                        final double... freqs)
    {
        return createSineDataTransform(size, delta, 0, TransfoTypes.TRANSLATION_ONLY, freqs);
    }

    public static VolumeFloatZz createSineDataTransform(final int size,
                                                        final double delta,
                                                        final double ka,
                                                        final TransfoTypes ttype,
                                                        final double... freqs)
    {
        final Point3D point = new Point3D();
        final VolumeFloatZz data = new VolumeFloatZz(new Dimensions(size, size, size));

        for (int Z = 0; Z < size; Z++)
        {
            for (int Y = 0; Y < size; Y++)
            {
                for (int X = 0; X < size; X++)
                {
                    final int[] xyz = imageToCartesian(X, Y, Z, size);

                    // transfo inverse
                    double xt, yt, zt;
                    switch (ttype)
                    {
                        case TRANSLATION_ONLY:
                            xt = xyz[0] - delta;
                            yt = xyz[1] - delta;
                            zt = xyz[2] - delta;
                            break;
                        case SHEARING:
                            xt = -ka * xyz[1] + xyz[0] - delta;
                            yt = xyz[1] - delta;
                            zt = xyz[2] - delta;
                            break;
                        case STRETCHING:
                            xt = xyz[0] / (ka + 1) - delta;
                            yt = xyz[1] / (1 - ka / 2) - delta;
                            zt = xyz[2] / (1 - ka / 2) - delta;
                            break;
                        default:
                            xt = xyz[0];
                            yt = xyz[1];
                            zt = xyz[2];
                            break;
                    }
                    double value = 0;
                    for (double f : freqs)
                    {
                        value += radialSine(distance(xt, yt, zt), f);
                    }
                    point.set(X, Y, Z);
                    data.setVoxel(point, (float) value);
                }
            }
        }

        return data;
    }

//
//    public VolumeFloatZz[] createData(final int size,
//                                      double delta,
//                                      final double ka,
//                                      final TransfoTypes ttype,
//                                      final int scale)
//    {
//        final int nParticles = PARTICULE_FACTOR * (int) Math.pow(SIZE_FACTOR * size * scale, 3);
//        final int halfSize = scale * size / 2;
//        final Point3D point = new Point3D();
//        final VolumeFloatZz[] datas = new VolumeFloatZz[2];
//        datas[0] = new VolumeFloatZz(new Dimensions(size, size, size));
//        datas[1] = new VolumeFloatZz(new Dimensions(size, size, size));
//        delta *= scale;
//
//        int i = 0;
//        while (i < nParticles)
//        {
//            final double x1 = (random.nextDouble() * SIZE_FACTOR * size * scale - SIZE_FACTOR * halfSize);
//            final double y1 = (random.nextDouble() * SIZE_FACTOR * size * scale - SIZE_FACTOR * halfSize);
//            final double z1 = (random.nextDouble() * SIZE_FACTOR * size * scale - SIZE_FACTOR * halfSize);
//
//            double x2, y2, z2;
//            switch (ttype)
//            {
//                case TRANSLATION_ONLY:
//                    x2 = x1 + delta;
//                    y2 = y1 + delta;
//                    z2 = z1 + delta;
//                    break;
//                case SHEARING:
//                    x2 = ka * (y1 + delta) + x1 + delta;
//                    y2 = y1 + delta;
//                    z2 = z1 + delta;
//                    break;
//                case STRETCHING:
//                    x2 = (ka + 1) * (x1 + delta);
//                    y2 = (1 - ka / 2) * (y1 + delta);
//                    z2 = (1 - ka / 2) * (z1 + delta);
//                    break;
//                default:
//                    x2 = x1;
//                    y2 = y1;
//                    z2 = z1;
//                    break;
//            }
//
//            boolean flag = false;
//
////            point.set((int) Math.round(x1), (int) Math.round(y1), (int) Math.round(z1));
//            float w = 1;
////            if (p.inRange(-halfSize / 2, halfSize / 2)) // test uniquement : dessine un carré
////            {
////                w = 4;
////            }
//            point.setX((int) Math.floor((Math.round(x1) + halfSize) / scale));
//            point.setY((int) Math.floor((halfSize - Math.round(y1)) / scale));
//            point.setZ((int) Math.floor((Math.round(z1) + halfSize) / scale));
//            if (point.inRange(0, size, 0, size, 0, size))
//            {
//                final float v = datas[0].getVoxel(point);
//                datas[0].setVoxel(point, v + w);
//                flag = true;
//            }
//
//            point.setX((int) Math.floor((Math.round(x2) + halfSize) / scale));
//            point.setY((int) Math.floor((halfSize - Math.round(y2)) / scale));
//            point.setZ((int) Math.floor((Math.round(z2) + halfSize) / scale));
//            if (point.inRange(0, size, 0, size, 0, size))
//            {
//                final float v = datas[1].getVoxel(point);
//                datas[1].setVoxel(point, v + w);
//                flag = true;
//            }
//
//            if (flag)
//            {
//                i++;
//            }
//        }
//
//        return datas;
//    }
    public VolumeFloatZz[] createRandomData(final int size,
                                            final double delta,
                                            final double ka,
                                            final TransfoTypes ttype)
    {
        final int nParticles = PARTICULE_FACTOR * (int) Math.pow(SIZE_FACTOR * size, 3);
        final int halfSize = size / 2;
        final Point3D p = new Point3D();
        final VolumeFloatZz[] datas = new VolumeFloatZz[2];
        datas[0] = new VolumeFloatZz(new Dimensions(size, size, size));
        datas[1] = new VolumeFloatZz(new Dimensions(size, size, size));

        int i = 0;
        while (i < nParticles)
        {
            final double x1 = random.nextDouble() * SIZE_FACTOR * size - SIZE_FACTOR * halfSize;
            final double y1 = random.nextDouble() * SIZE_FACTOR * size - SIZE_FACTOR * halfSize;
            final double z1 = random.nextDouble() * SIZE_FACTOR * size - SIZE_FACTOR * halfSize;

            double x2, y2, z2;
            switch (ttype)
            {
                case TRANSLATION_ONLY:
                    x2 = x1 + delta;
                    y2 = y1 + delta;
                    z2 = z1 + delta;
                    break;
                case SHEARING:
                    x2 = ka * (y1 + delta) + x1 + delta;
                    y2 = y1 + delta;
                    z2 = z1 + delta;
                    break;
                case STRETCHING:
                    x2 = (ka + 1) * (x1 + delta);
                    y2 = (1 - ka / 2) * (y1 + delta);
                    z2 = (1 - ka / 2) * (z1 + delta);
                    break;
                default:
                    x2 = x1;
                    y2 = y1;
                    z2 = z1;
                    break;
            }

            boolean flag = false;

            p.set((int) Math.round(x1), (int) Math.round(y1), (int) Math.round(z1));
            float w = 1;
//            if (p.inRange(-halfSize / 2, halfSize / 2)) // test uniquement : dessine un carré
//            {
//                w = 4;
//            }
            if (p.inRange(-halfSize, halfSize, -halfSize + 1, halfSize + 1, -halfSize, halfSize))
            {
                p.setX(p.getX() + halfSize);
                p.setY(halfSize - p.getY());
                p.setZ(p.getZ() + halfSize);
                final float v = datas[0].getVoxel(p);
                datas[0].setVoxel(p, v + w);
                flag = true;
            }

            p.set((int) Math.round(x2), (int) Math.round(y2), (int) Math.round(z2));
            if (p.inRange(-halfSize, halfSize, -halfSize + 1, halfSize + 1, -halfSize, halfSize))
            {
                p.setX(p.getX() + halfSize);
                p.setY(halfSize - p.getY());
                p.setZ(p.getZ() + halfSize);
                final float v = datas[1].getVoxel(p);
                datas[1].setVoxel(p, v + w);
                flag = true;
            }

            if (flag)
            {
                i++;
            }
        }

        return datas;
    }

//    public VolumeFloatZz[] createData(final int size,
//                                      final double delta,
//                                      final double ka,
//                                      final TransfoTypes ttype,
//                                      final int pfactor,
//                                      final int parttype)
//    {
//        final int nParticles = pfactor * (int) Math.pow(SIZE_FACTOR * size, 3);
//        final int halfSize = size / 2;
//        final Point3D p = new Point3D();
//        final VolumeFloatZz[] datas = new VolumeFloatZz[2];
//        datas[0] = new VolumeFloatZz(new Dimensions(size, size, size));
//        datas[1] = new VolumeFloatZz(new Dimensions(size, size, size));
//
//        int i = 0;
//        while (i < nParticles)
//        {
//            final double x1 = random.nextDouble() * SIZE_FACTOR * size - SIZE_FACTOR * halfSize;
//            final double y1 = random.nextDouble() * SIZE_FACTOR * size - SIZE_FACTOR * halfSize;
//            final double z1 = random.nextDouble() * SIZE_FACTOR * size - SIZE_FACTOR * halfSize;
//
//            double x2, y2, z2;
//            switch (ttype)
//            {
//                case TRANSLATION_ONLY:
//                    x2 = x1 + delta;
//                    y2 = y1 + delta;
//                    z2 = z1 + delta;
//                    break;
//                case SHEARING:
//                    x2 = ka * (y1 + delta) + x1 + delta;
//                    y2 = y1 + delta;
//                    z2 = z1 + delta;
//                    break;
//                case STRETCHING:
//                    x2 = (ka + 1) * (x1 + delta);
//                    y2 = (1 - ka / 2) * (y1 + delta);
//                    z2 = (1 - ka / 2) * (z1 + delta);
//                    break;
//                default:
//                    x2 = x1;
//                    y2 = y1;
//                    z2 = z1;
//                    break;
//            }
//
//            boolean flag = false;
//
//            p.set((int) Math.round(x1), (int) Math.round(y1), (int) Math.round(z1));
//
//            float w;
//            switch (parttype)
//            {
//                case 0:
//                    w = 1;
//                    break;
//                case 1:
//                    w = random.nextFloat();
//                    break;
//                case 2:
//                    w = (float) gauss.nextNormalizedDouble();
//                    break;
//                case 3:
//                    w = 1;
//                    if (p.inRange(-halfSize / 2, halfSize / 2)) // test uniquement : dessine un carré
//                    {
//                        w = 4;
//                    }
//                    break;
//                default:
//                    w = 1;
//                    break;
//            }
//
//            if (p.inRange(-halfSize, halfSize, -halfSize + 1, halfSize + 1, -halfSize, halfSize))
//            {
//                p.setX(p.getX() + halfSize);
//                p.setY(halfSize - p.getY());
//                p.setZ(p.getZ() + halfSize);
//                final float v = datas[0].getVoxel(p);
//                datas[0].setVoxel(p, v + w);
//                flag = true;
//            }
//
//            p.set((int) Math.round(x2), (int) Math.round(y2), (int) Math.round(z2));
//            if (p.inRange(-halfSize, halfSize, -halfSize + 1, halfSize + 1, -halfSize, halfSize))
//            {
//                p.setX(p.getX() + halfSize);
//                p.setY(halfSize - p.getY());
//                p.setZ(p.getZ() + halfSize);
//                final float v = datas[1].getVoxel(p);
//                datas[1].setVoxel(p, v + w);
//                flag = true;
//            }
//
//            if (flag)
//            {
//                i++;
//            }
//        }
//
//        return datas;
//    }
    private static int[] cartesiantoImage(final int x,
                                          final int y,
                                          final int z,
                                          final int size)
    {
        final int[] XYZ = new int[3];
        XYZ[0] = x + size / 2;
        XYZ[1] = size / 2 - y;
        XYZ[2] = z + size / 2;
        return XYZ;
    }

    private static double distance(final int[] xyz)
    {
        return Math.sqrt(xyz[0] * xyz[0] + xyz[1] * xyz[1] + xyz[2] * xyz[2]);
    }

    private static double distance(final double x,
                                   final double y,
                                   final double z)
    {
        return Math.sqrt(x * x + y * y + z * z);
    }

    private static int[] imageToCartesian(final int X,
                                          final int Y,
                                          final int Z,
                                          final int size)
    {
        final int[] xyz = new int[3];
        xyz[0] = X - size / 2;
        xyz[1] = size / 2 - Y;
        xyz[2] = Z - size / 2;
        return xyz;
    }

    private static double radialSine(final double r,
                                     final double freq)
    {
        return Math.sin(Math.PI * freq * r);
    }

}

//    public VolumeFloatZz[] createSineData(final int size,
//                                          final double delta,
//                                          final double ka,
//                                          final TransfoTypes ttype,
//                                          final double freq)
//    {
//        final Point3D point = new Point3D();
//        final VolumeFloatZz[] datas = new VolumeFloatZz[2];
//        datas[0] = new VolumeFloatZz(new Dimensions(size, size, size));
//        datas[1] = new VolumeFloatZz(new Dimensions(size, size, size));
//
//        for (int Z = 0; Z < size; Z++)
//        {
//            for (int Y = 0; Y < size; Y++)
//            {
//                for (int X = 0; X < size; X++)
//                {
//                    final int[] xyz = imageToCartesian(X, Y, Z, size);
//                    point.set(X, Y, Z);
//                    datas[0].setVoxel(point, (float) radialSine(distance(xyz), freq));
//
//                    // transfo inverse
//                    double xt, yt, zt;
//                    switch (ttype)
//                    {
//                        case TRANSLATION_ONLY:
//                            xt = xyz[0] - delta;
//                            yt = xyz[1] - delta;
//                            zt = xyz[2] - delta;
//                            break;
//                        case SHEARING:
//                            xt = -ka * xyz[1] + xyz[0] - delta;
//                            yt = xyz[1] - delta;
//                            zt = xyz[2] - delta;
//                            break;
//                        case STRETCHING:
//                            xt = xyz[0] / (ka + 1) - delta;
//                            yt = xyz[1] / (1 - ka / 2) - delta;
//                            zt = xyz[2] / (1 - ka / 2) - delta;
//                            break;
//                        default:
//                            xt = xyz[0];
//                            yt = xyz[1];
//                            zt = xyz[2];
//                            break;
//                    }
//                    datas[1].setVoxel(point, (float) radialSine(distance(xt, yt, zt), freq));
//                }
//            }
//        }
//
//        return datas;
//    }
//    public VolumeFloatZz[] createProdSineData(final int size,
//                                              final double delta,
//                                              final double ka,
//                                              final TransfoTypes ttype,
//                                              final double freq)
//    {
//        final Point3D point = new Point3D();
//        final VolumeFloatZz[] datas = new VolumeFloatZz[2];
//        datas[0] = new VolumeFloatZz(new Dimensions(size, size, size));
//        datas[1] = new VolumeFloatZz(new Dimensions(size, size, size));
//
//        for (int Z = 0; Z < size; Z++)
//        {
//            for (int Y = 0; Y < size; Y++)
//            {
//                for (int X = 0; X < size; X++)
//                {
//                    final int[] xyz = imageToCartesian(X, Y, Z, size);
//                    final double r = Math.sin(Math.PI * freq * xyz[0]) * Math.sin(Math.PI * freq * xyz[1])
//                                     * Math.sin(Math.PI * freq * xyz[2]);
//                    point.set(X, Y, Z);
//                    datas[0].setVoxel(point, (float) r);
//
//                    // transfo inverse
//                    double xt, yt, zt;
//                    switch (ttype)
//                    {
//                        case TRANSLATION_ONLY:
//                            xt = xyz[0] - delta;
//                            yt = xyz[1] - delta;
//                            zt = xyz[2] - delta;
//                            break;
//                        case SHEARING:
//                            xt = -ka * xyz[1] + xyz[0] - delta;
//                            yt = xyz[1] - delta;
//                            zt = xyz[2] - delta;
//                            break;
//                        case STRETCHING:
//                            xt = xyz[0] / (ka + 1) - delta;
//                            yt = xyz[1] / (1 - ka / 2) - delta;
//                            zt = xyz[2] / (1 - ka / 2) - delta;
//                            break;
//                        default:
//                            xt = xyz[0];
//                            yt = xyz[1];
//                            zt = xyz[2];
//                            break;
//                    }
//                    final double rt = Math.sin(Math.PI * freq * xt) * Math.sin(Math.PI * freq * yt)
//                                      * Math.sin(Math.PI * freq * zt);
//                    datas[1].setVoxel(point, (float) rt);
//                }
//            }
//        }
//
//        return datas;
//    }
//
//    public VolumeFloatZz[] createSineCardinalData(final int size,
//                                                  final double delta,
//                                                  final double ka,
//                                                  final TransfoTypes ttype,
//                                                  final double freq)
//    {
//        final Point3D point = new Point3D();
//        final VolumeFloatZz[] datas = new VolumeFloatZz[2];
//        datas[0] = new VolumeFloatZz(new Dimensions(size, size, size));
//        datas[1] = new VolumeFloatZz(new Dimensions(size, size, size));
//
//        for (int Z = 0; Z < size; Z++)
//        {
//            for (int Y = 0; Y < size; Y++)
//            {
//                for (int X = 0; X < size; X++)
//                {
//                    final int[] xyz = imageToCartesian(X, Y, Z, size);
//                    final double r = Math.sqrt(xyz[0] * xyz[0] + xyz[1] * xyz[1] + xyz[2] * xyz[2]);
//                    point.set(X, Y, Z);
//                    if (r == 0)
//                    {
//                        datas[0].setVoxel(point, 1);
//                    }
//                    else
//                    {
//                        datas[0].setVoxel(point, (float) (Math.sin(Math.PI * freq * r) / (Math.PI * freq * r)));
//                    }
//
//                    // transfo inverse
//                    double xt, yt, zt;
//                    switch (ttype)
//                    {
//                        case TRANSLATION_ONLY:
//                            xt = xyz[0] - delta;
//                            yt = xyz[1] - delta;
//                            zt = xyz[2] - delta;
//                            break;
//                        case SHEARING:
//                            xt = -ka * xyz[1] + xyz[0] - delta;
//                            yt = xyz[1] - delta;
//                            zt = xyz[2] - delta;
//                            break;
//                        case STRETCHING:
//                            xt = xyz[0] / (ka + 1) - delta;
//                            yt = xyz[1] / (1 - ka / 2) - delta;
//                            zt = xyz[2] / (1 - ka / 2) - delta;
//                            break;
//                        default:
//                            xt = xyz[0];
//                            yt = xyz[1];
//                            zt = xyz[2];
//                            break;
//                    }
//                    final double rt = Math.sqrt(xt * xt + yt * yt + zt * zt);
//                    if (rt == 0)
//                    {
//                        datas[1].setVoxel(point, 1);
//                    }
//                    else
//                    {
//                        datas[1].setVoxel(point, (float) (Math.sin(Math.PI * freq * rt) / (Math.PI * freq * rt)));
//                    }
//                }
//            }
//        }
//
//        return datas;
//    }
