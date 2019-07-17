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

import ij.ImagePlus;
import ij.measure.Calibration;

/**
 *
 * @author Arnold Fertin
 */
public abstract class AbstractVolumeZz
{

    protected Dimensions dim;

    protected VoxelSize voxelDimensions;

    protected ImagePlus image;

    public AbstractVolumeZz()
    {
        voxelDimensions = new VoxelSize();
    }

    public AbstractVolumeZz(final ImagePlus img)
    {
        if (img.getNDimensions() != 3 && img.getNSlices() > 1)
        {
            final String msg = "ImagePlus must have three dimensions to build a Volume";
            throw new IllegalArgumentException(msg);
        }
        Calibration calib = img.getCalibration();
        voxelDimensions = new VoxelSize(calib.pixelHeight, calib.pixelHeight, calib.pixelDepth, calib.getUnit());
    }

    public abstract AbstractVolumeZz duplicate();

//    public abstract double getVoxelDouble(Point3D pos);
//
//    public abstract void setVoxel(Point3D pos,
//                                  double value);
    public Dimensions getDimensions()
    {
        return dim;
    }

    public abstract ImagePlus getImagePlus(final String title);

    public final ImagePlus getImagePlus()
    {
        return getImagePlus("");
    }

    public VoxelSize getVoxelDimensions()
    {
        return voxelDimensions;
    }

    public void setVoxelDimensions(final VoxelSize voxdim)
    {
        voxelDimensions = voxdim;
    }

    public int getSize()
    {
        return dim.dimX * dim.dimY * dim.dimZ;
    }
}
