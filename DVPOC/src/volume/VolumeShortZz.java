/*
 * Copyright (C) 2016 Arnold Fertin
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
import ij.ImageStack;
import ij.measure.Calibration;

/**
 *
 * @author Arnold Fertin
 */
public class VolumeShortZz extends AbstractVolumeZz
{
    private final short[][] voxels;

    public VolumeShortZz(final ImagePlus img)
    {
        super(img);
        if (img.getBitDepth() != 16)
        {
            throw new IllegalArgumentException("must be short");
        }
        image = img;
        dim = new Dimensions(img);
        final ImageStack stk = img.getStack();
        voxels = new short[dim.dimZ][];
        for (int z = 0; z < dim.dimZ; z++)
        {
            final short[] v = (short[]) stk.getImageArray()[z];
            voxels[z] = v;
        }
    }

    public VolumeShortZz(final Dimensions dims)
    {
        super();
        dim = dims;
        voxels = new short[dim.dimZ][dim.dimX * dim.dimY];
    }

    public int getVoxel(final Point3D pos)
    {
        return voxels[pos.getZ()][pos.getX() + pos.getY() * dim.dimX] & 0xffff;
    }

    public void setVoxel(final Point3D pos,
                         final int value)
    {
        voxels[pos.getZ()][pos.getX() + pos.getY() * dim.dimX] = crop(value);
    }

    public int getVoxel(final int x,
                        final int y,
                        final int z)
    {
        return voxels[z][x + y * dim.dimX] & 0xffff;
    }

    public void setVoxel(final int x,
                         final int y,
                         final int z,
                         final int value)
    {
        voxels[z][x + y * dim.dimX] = crop(value);
    }

    private short crop(final int value)
    {
        if (value > 65535)
        {
            return (short) 65535;
        }
        else if (value < 0)
        {
            return 0;
        }
        return (short) value;
    }

    @Override
    public AbstractVolumeZz duplicate()
    {
        final VolumeShortZz copy = new VolumeShortZz(dim);
        final int n = dim.dimX * dim.dimY;
        for (int z = 0; z < dim.dimZ; z++)
        {
            System.arraycopy(voxels[z], 0, copy.voxels[z], 0, n);
        }
        return copy;
    }

    @Override
    public ImagePlus getImagePlus(String title)
    {
        if (image == null)
        {
            final ImageStack stk = ImageStack.create(dim.dimX, dim.dimY, dim.dimZ, 16);
            for (int z = 0; z < dim.dimZ; z++)
            {
                stk.setPixels(voxels[z], z + 1);
            }
            image = new ImagePlus(title, stk);
            final Calibration calib = image.getCalibration();
            calib.pixelWidth = voxelDimensions.width;
            calib.pixelHeight = voxelDimensions.height;
            calib.pixelDepth = voxelDimensions.depth;
            calib.setUnit(voxelDimensions.unit);
        }
        return image;
    }

}
