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
import ij.IJ;
import java.util.ArrayList;
import volume.Dimensions;
import volume.Point3D;
import volume.VolumeByteZz;
import volume.VolumeFloatZz;
import volume.VoxelSize;

/**
 *
 * @author Arnold Fertin
 */
public final class DisplacementField
{
    private final Dimensions dims;

    private final VoxelSize voxelDimensions;

    private Displacement globalDisplacement;

    private final Displacement[] displacements;

    private final Point3D[] positions;

    private final int size;

    public DisplacementField(final VolumeByteZz mask)
    {
        dims = mask.getDimensions();
        voxelDimensions = mask.getVoxelDimensions();
        final ArrayList<Point3D> pList = new ArrayList<>();
        final Point3D p = new Point3D();
        for (p.setZ(0); p.getZ() < dims.dimZ; p.incZ())
        {
            for (p.setY(0); p.getY() < dims.dimY; p.incY())
            {
                for (p.setX(0); p.getX() < dims.dimX; p.incX())
                {
                    if (mask.getVoxel(p) == 255)
                    {
                        pList.add(new Point3D(p));
                    }
                }
            }
        }
        size = pList.size();
        positions = new Point3D[size];
        pList.toArray(positions);
        displacements = new Displacement[size];
    }

    public Dimensions getDimensions()
    {
        return dims;
    }

    public Displacement getDisplacement(final int idx)
    {
        return displacements[idx];
    }

    public void initialize(final Displacement global)
    {
        globalDisplacement = global;
        for (int i = 0; i < size; i++)
        {
            displacements[i] = new Displacement(0, 0, 0);
        }
    }

    public Point3D getPosition(final int idx)
    {
        return positions[idx];
    }

    public int getSize()
    {
        return size;
    }

    public Point3D getTranslatedPosition(final int idx)
    {
        final Point3D p = new Point3D();
        p.setX(positions[idx].getX() + (int) Math.rint(displacements[idx].getU()));
        p.setY(positions[idx].getY() + (int) Math.rint(displacements[idx].getV()));
        p.setZ(positions[idx].getZ() + (int) Math.rint(displacements[idx].getW()));
        return p;
    }

    public VolumeFloatZz getVolumeU()
    {
        final VolumeFloatZz uVol = new VolumeFloatZz(dims);
        uVol.setVoxelDimensions(voxelDimensions);
        uVol.fill(Float.NaN);
        for (int i = 0; i < size; i++)
        {
            final Displacement d = displacements[i];
            if (!d.isSpurious())
            {
                final double u = (d.getU() - globalDisplacement.getU()) * voxelDimensions.width;
                uVol.setVoxel(positions[i], (float) u);
            }
        }
        return uVol;
    }

    public VolumeFloatZz getVolumeV()
    {
        final VolumeFloatZz vVol = new VolumeFloatZz(dims);
        vVol.setVoxelDimensions(voxelDimensions);
        vVol.fill(Float.NaN);
        for (int i = 0; i < size; i++)
        {
            final Displacement d = displacements[i];
            if (!d.isSpurious())
            {
                final double v = (d.getV() - globalDisplacement.getV()) * voxelDimensions.height;
                vVol.setVoxel(positions[i], (float) v);
            }
        }
        return vVol;
    }

    public VolumeFloatZz getVolumeW()
    {
        final VolumeFloatZz wVol = new VolumeFloatZz(dims);
        wVol.setVoxelDimensions(voxelDimensions);
        wVol.fill(Float.NaN);
        for (int i = 0; i < size; i++)
        {
            final Displacement d = displacements[i];
            if (!d.isSpurious())
            {
                final double w = (d.getW() - globalDisplacement.getW()) * voxelDimensions.depth;
                wVol.setVoxel(positions[i], (float) w);
            }
        }
        return wVol;
    }

    public VolumeFloatZz getVolumeMaxCorr()
    {
        final VolumeFloatZz corrVol = new VolumeFloatZz(dims);
        corrVol.setVoxelDimensions(voxelDimensions);
        corrVol.fill(Float.NaN);
        for (int i = 0; i < size; i++)
        {
            final Displacement d = displacements[i];
            if (!d.isSpurious())
            {
                final double c = d.getMaximumOfCorrelation();
                corrVol.setVoxel(positions[i], (float) c);
            }
        }
        return corrVol;
    }

    /**
     * A right-handed coordinate system is assumed for transformation, where the origin is taken precisely in the center
     * of the volume.
     *
     * @param dat
     * @param execTime
     *
     * @return
     */
    public String toString(final RegistrationData dat,
                           final long execTime)
    {
        final StringBuffer buffer = new StringBuffer();
        final DisplacementFieldFormatter2 fmt = new DisplacementFieldFormatter2(this);
        final String sep = "\t";

        buffer.append("# Execution time (seconds): ");
        buffer.append(execTime * 1e-9);
        buffer.append('\n');

        buffer.append(dat.summary());

        buffer.append("# Number of points: ");
        buffer.append(positions.length);
        buffer.append('\n');

        buffer.append("# Number of spurious points: ");
        buffer.append(getNumberOfSpuriousPoints());
        buffer.append('\n');

        buffer.append("# Unit: ");
        buffer.append(voxelDimensions.unit);
        buffer.append('\n');

        buffer.append("# Cartesian coordinates system");
        buffer.append('\n');

        buffer.append("# Global displacement: ");
        buffer.append('\n');
        final String ug = fmt.formatTranslation(globalDisplacement.getU() * voxelDimensions.width);
        buffer.append("#    U = ");
        buffer.append(ug);
        buffer.append('\n');
        final String vg = fmt.formatTranslation(globalDisplacement.getV() * voxelDimensions.height);
        buffer.append("#    V = ");
        buffer.append(vg);
        buffer.append('\n');
        final String wg = fmt.formatTranslation(globalDisplacement.getW() * voxelDimensions.depth);
        buffer.append("#    W = ");
        buffer.append(wg);
        buffer.append('\n');

        buffer.append("\tX\tY\tZ\tU\tV\tW\tMaxCorr\tGrubbsStat\tPvalue\tSpurious");
        buffer.append('\n');

        for (int i = 0; i < size; i++)
        {
            final Point3D point = positions[i];
            final int j = i + 1;
            buffer.append(j);
            buffer.append(sep);

            final double x = image2cartesianX(point) * voxelDimensions.width; // * voxelDimensions.width
            buffer.append(fmt.formatCoordinate(x));
            buffer.append(sep);

            final double y = image2cartesianY(point) * voxelDimensions.height;
            buffer.append(fmt.formatCoordinate(y));
            buffer.append(sep);

            final double z = image2cartesianZ(point) * voxelDimensions.depth;
            buffer.append(fmt.formatCoordinate(z));
            buffer.append(sep);

            final Displacement d = displacements[i];

            final double u = d.getU() * voxelDimensions.width; // - globalDisplacement.getU()
            buffer.append(fmt.formatTranslation(u));
            buffer.append(sep);

            final double v = d.getV() * voxelDimensions.height; //( moins : coordonnées cartésiennes), non
            buffer.append(fmt.formatTranslation(v));
            buffer.append(sep);

            final double w = d.getW() * voxelDimensions.depth;
            buffer.append(fmt.formatTranslation(w));
            buffer.append(sep);

            buffer.append(fmt.formatMaxCorr(d.getMaximumOfCorrelation()));
            buffer.append(sep);

            buffer.append(fmt.formatGrubbs(d.getGrubbsStatistics()));
            buffer.append(sep);

            buffer.append(fmt.formatPvalue(d.getPValue()));
            buffer.append(sep);

            if (d.isSpurious())
            {
                buffer.append("TRUE");
            }
            else
            {
                buffer.append("FALSE");
            }

            buffer.append('\n');
        }

        return buffer.toString();
    }

    public void print(final RegistrationData dat,
                      final long execTime)
    {
        IJ.log("\\Clear");
        IJ.log(toString(dat, execTime));
//        final DisplacementFieldFormatter1 fmt = new DisplacementFieldFormatter1(this);
//        final String sep = "\t";
//        IJ.log("\\Clear");
//        IJ.log("# Execution time (seconds): " + execTime * 1e-9);
//        IJ.log(dat.summary());
//        IJ.log("# Number of points: " + positions.length);
//        IJ.log("# Number of spurious points: " + getNumberOfSpuriousPoints());
//        IJ.log("# Unit: " + voxelDimensions.unit);
//        IJ.log("# Cartesian coordinates system");
//        IJ.log("# Global displacement: ");
//        final String ug = fmt.formatTranslation(globalDisplacement.getU() * voxelDimensions.width);
//        IJ.log("#    U = " + ug);
//        final String vg = fmt.formatTranslation(globalDisplacement.getV() * voxelDimensions.height);
//        IJ.log("#    V = " + vg);
//        final String wg = fmt.formatTranslation(globalDisplacement.getW() * voxelDimensions.depth);
//        IJ.log("#    W = " + wg);
//        IJ.log("\tX\tY\tZ\tU\tV\tW\tMaxCorr\tSpurious");
//        for (int i = 0; i < size; i++)
//        {
//            final StringBuffer buffer = new StringBuffer();
//            final Point3D point = positions[i];
//            final int j = i + 1;
//            buffer.append(j);
//            buffer.append(sep);
//
//            final double x = image2cartesianX(point) * voxelDimensions.width; // * voxelDimensions.width
//            buffer.append(fmt.formatCoordinate(x));
//            buffer.append(sep);
//
//            final double y = image2cartesianY(point) * voxelDimensions.height;
//            buffer.append(fmt.formatCoordinate(y));
//            buffer.append(sep);
//
//            final double z = image2cartesianZ(point) * voxelDimensions.depth;
//            buffer.append(fmt.formatCoordinate(z));
//            buffer.append(sep);
//
//            final Displacement d = displacements[i];
//
//            final double u = d.getU() * voxelDimensions.width; // - globalDisplacement.getU()
//            buffer.append(fmt.formatTranslation(u));
//            buffer.append(sep);
//
//            final double v = -d.getV() * voxelDimensions.height; // moins : coordonnées cartésiennes
//            buffer.append(fmt.formatTranslation(v));
//            buffer.append(sep);
//
//            final double w = d.getW() * voxelDimensions.depth;
//            buffer.append(fmt.formatTranslation(w));
//            buffer.append(sep);
//
//            buffer.append(fmt.formatMaxCorr(d.getMaximumOfCorrelation()));
//            buffer.append(sep);
//            if (d.isSpurious())
//            {
//                buffer.append("TRUE");
//            }
//            else
//            {
//                buffer.append("FALSE");
//            }
//            IJ.log(buffer.toString());
//        }
    }

    private double image2cartesianX(final Point3D point)
    {
        return (double) point.getX() - ((double) dims.dimX) / 2d;
    }

    private double image2cartesianY(final Point3D point)
    {
        return (double) point.getY() - ((double) dims.dimY) / 2d; // bug : * -1
    }

    private double image2cartesianZ(final Point3D point)
    {
        return (double) point.getZ() - ((double) dims.dimZ) / 2d;
    }

    private int getNumberOfSpuriousPoints()
    {
        int count = 0;
        for (int i = 0; i < size; i++)
        {
            final Displacement d = displacements[i];
            if (d.isSpurious())
            {
                ++count;
            }
        }
        return count;
    }

    public void setDisplacement(final int idx,
                                final Displacement tr)
    {
        displacements[idx] = tr;
    }

    public void update(final int idx,
                       final Displacement tr)
    {
        displacements[idx].add(tr);
    }

    public void update(final int idx,
                       final double u,
                       final double v,
                       final double w)
    {
        displacements[idx].add(u, v, w);
    }

    public VoxelSize getVoxelDimensions()
    {
        return voxelDimensions;
    }

    public Displacement getGlobalDisplacement()
    {
        return globalDisplacement;
    }
}
