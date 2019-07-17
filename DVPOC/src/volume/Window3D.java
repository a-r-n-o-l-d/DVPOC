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
package volume;

import static java.lang.Math.PI;
import static java.lang.Math.cos;
import static java.lang.Math.sin;
import org.apache.commons.math3.random.MersenneTwister;

/**
 *
 * @author Arnold Fertin
 */
public final class Window3D
{
    private static final double BLACKMAN_A0 = 0.42;

    private static final double BLACKMAN_A1 = 0.5;

    private static final double BLACKMAN_A2 = 0.08;

    private static final double HAMMING_C1 = 0.53836;

    private static final double HAMMING_C2 = 0.46164;

    private static final double HALF = 0.5;

    private static final double TUKEY_ALPHA = 0.5;

    private final Dimensions dim;

    private final double[][] coef;

    private final MersenneTwister random;

    public Window3D(final Dimensions dims,
                    final WindowType type)
    {
        dim = dims;
        coef = new double[dims.dimZ][dims.dimX * dims.dimY];
        for (int z = 0; z < dim.dimZ; z++)
        {
            final double wz;
            wz = getWeight(z, dim.dimZ, type);
            for (int y = 0; y < dim.dimY; y++)
            {
                final double wy = getWeight(y, dim.dimY, type);
                for (int x = 0; x < dim.dimX; x++)
                {
                    coef[z][y * dim.dimX + x] = getWeight(x, dim.dimX, type) * wy * wz;
                }
            }
        }
        random = new MersenneTwister();
    }

    public VolumeFloatZz apodizeSubVolume(final Point3D pos,
                                          final VolumeFloatZz src,
                                          final double sd)
    {
        final VolumeFloatZz sub = new VolumeFloatZz(dim);
        final Point3D p1 = new Point3D();
        final int xi = pos.getX() - dim.dimX / 2;
        final int yi = pos.getY() - dim.dimY / 2;
        final int zi = pos.getZ() - dim.dimZ / 2;
        final Point3D p2 = new Point3D(xi, yi, zi);
        for (p1.setZ(0), p2.setZ(zi); p1.getZ() < dim.dimZ; p1.incZ(), p2.incZ())
        {
            for (p1.setY(0), p2.setY(yi); p1.getY() < dim.dimY; p1.incY(), p2.incY())
            {
                for (p1.setX(0), p2.setX(xi); p1.getX() < dim.dimX; p1.incX(), p2.incX())
                {
                    final Dimensions srcDim = src.getDimensions();
                    if (srcDim.checkPosition(p2))
                    {
                        final float v = src.getVoxel(p2) * getCoefficient(p1);
                        sub.setVoxel(p1, v);
                    }
                    else
                    {
                        final float v = (float) (random.nextGaussian() * sd) * getCoefficient(p1);
                        sub.setVoxel(p1, v);
                    }

                }
            }
        }
        return sub;
    }

    public VolumeFloatZz apodizeSubVolume(final Point3D pos,
                                          final VolumeFloatZz src)
    {
        final VolumeFloatZz sub = new VolumeFloatZz(dim);
        final Point3D p1 = new Point3D();
        final int xi = pos.getX() - dim.dimX / 2;
        final int yi = pos.getY() - dim.dimY / 2;
        final int zi = pos.getZ() - dim.dimZ / 2;
        final Point3D p2 = new Point3D(xi, yi, zi);
        for (p1.setZ(0), p2.setZ(zi); p1.getZ() < dim.dimZ; p1.incZ(), p2.incZ())
        {
            for (p1.setY(0), p2.setY(yi); p1.getY() < dim.dimY; p1.incY(), p2.incY())
            {
                for (p1.setX(0), p2.setX(xi); p1.getX() < dim.dimX; p1.incX(), p2.incX())
                {
                    final Dimensions srcDim = src.getDimensions();
                    if (srcDim.checkPosition(p2))
                    {
                        final float v = src.getVoxel(p2) * getCoefficient(p1);
                        sub.setVoxel(p1, v);
                    }
                }
            }
        }
        return sub;
    }

    public VolumeFloatZz apodize(final VolumeFloatZz src)
    {
        final VolumeFloatZz tgt = new VolumeFloatZz(dim);
        final Point3D p1 = new Point3D();
        for (p1.setZ(0); p1.getZ() < dim.dimZ; p1.incZ())
        {
            for (p1.setY(0); p1.getY() < dim.dimY; p1.incY())
            {
                for (p1.setX(0); p1.getX() < dim.dimX; p1.incX())
                {
                    final float v = src.getVoxel(p1) * getCoefficient(p1);
                    tgt.setVoxel(p1, v);
                }
            }
        }
        return tgt;
    }

    public float getCoefficient(final Point3D point)
    {
        return (float) coef[point.getZ()][point.getX() + point.getY() * dim.dimX];
    }

    private double getWeight(final int n,
                             final int size,
                             final WindowType type)
    {
        switch (type)
        {
            case NONE:
                return 1.0;
            case COSINE:
                return cosine(n, size);
            case HAMMING:
                return hamming(n, size);
            case HANN:
                return hann(n, size);
            case TUKEY:
                return tukey(n, size);
            case WELCH:
                return welch(n, size);
            case BLACKMAN:
                return blackman(n, size);
            default:
                return 0;
        }
    }

    private double cosine(final double n,
                          final double size)
    {
        return sin(PI * n / (size - 1));
    }

    private double hamming(final double n,
                           final double size)
    {
        return HAMMING_C1 - HAMMING_C2 * cos(2 * PI * n / (size - 1));
    }

    private double blackman(final double n,
                            final double size)
    {
        return BLACKMAN_A0 - BLACKMAN_A1 * cos(2 * PI * n / (size - 1)) + BLACKMAN_A2 * cos(4 * PI * n / (size - 1));
    }

    private double hann(final double n,
                        final double size)
    {
        return HALF * (1 - cos(2 * PI * n / (size - 1)));
    }

    private double tukey(final double n,
                         final double size)
    {
        final double alpha = TUKEY_ALPHA;
        if (n >= 0 && n <= alpha * (size - 1) / 2)
        {
            return HALF * (1 + cos(PI * (2 * n / (alpha * (size - 1)) - 1)));
        }
        if (n > alpha * (size - 1) / 2 && n <= (size - 1) * (1 - alpha / 2))
        {
            return 1;
        }
        if (n > (size - 1) * (1 - alpha / 2) && n <= (size - 1))
        {
            return HALF * (1 + cos(PI * (2 * n / (alpha * (size - 1)) - 2 / alpha + 1)));
        }
        return 0;
    }

    private double welch(final double n,
                         final double size)
    {
        final double v = (n - (size - 1) / 2) / ((size - 1) / 2);
        return 1 - v * v;
    }
}
