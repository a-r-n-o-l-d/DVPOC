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

import ij.IJ;
import ij.ImagePlus;
import ij.ImageStack;
import ij.measure.Calibration;
import java.util.Arrays;

/**
 *
 * @author Arnold Fertin
 */
public final class VolumeFloatZz extends AbstractVolumeZz
{

    private float[][] voxels;

    public VolumeFloatZz(final ImagePlus img)
    {
        super(img);
        if (img.getBitDepth() != 32)
        {
            throw new IllegalArgumentException("must be float");
        }
        image = img;
        dim = new Dimensions(img);
        final ImageStack stk = img.getStack();
        voxels = new float[dim.dimZ][]; //dim.dimX * dim.dimY
        for (int z = 0; z < dim.dimZ; z++)
        {
            final float[] v = (float[]) stk.getImageArray()[z];
            voxels[z] = v;
        }
    }

    public VolumeFloatZz(final Dimensions dims)
    {
        super();
        dim = dims;
        voxels = new float[dim.dimZ][dim.dimX * dim.dimY];
    }

    public float getVoxel(final Point3D pos)
    {
        return voxels[pos.getZ()][pos.getX() + pos.getY() * dim.dimX];
    }

    public void setVoxel(final Point3D pos,
                         final float value)
    {
        voxels[pos.getZ()][pos.getX() + pos.getY() * dim.dimX] = value;
    }

    @Override
    public ImagePlus getImagePlus(final String title)
    {
        if (image == null)
        {
            final ImageStack stk = ImageStack.create(dim.dimX, dim.dimY, dim.dimZ, 32);
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

    public float[][] getVoxels()
    {
        final float[][] tmp = new float[dim.dimZ][];
        System.arraycopy(voxels, 0, tmp, 0, dim.dimZ);
        return tmp;
    }
    
    //arraycopy(Object src, int srcPos, Object dest, int destPos, int length)
//    public void setVoxels(final float[][] vox)
//    {
//        System.arraycopy(vox, 0, voxels, 0, dim.dimZ);
//    }
    
    @Override
    public AbstractVolumeZz duplicate()
    {
        final VolumeFloatZz copy = new VolumeFloatZz(dim);
        final int n = dim.dimX * dim.dimY;
        for (int z = 0; z < dim.dimZ; z++)
        {
            System.arraycopy(voxels[z], 0, copy.voxels[z], 0, n);
        }
        return copy;
    }
    
    public void addSlices(final int n)
    {
        final float[][] tmp = new float[dim.dimZ + n][];
        System.arraycopy(voxels, 0, tmp, 0, dim.dimZ);
        for (int z = dim.dimZ; z < dim.dimZ + n; z++)
        {
            tmp[z] = new float[dim.dimX * dim.dimY];
        }
        voxels = tmp;
        dim = new Dimensions(dim.dimX, dim.dimY, dim.dimZ + n);
    }
    
    public void fill(final float value)
    {
        for (int z = 0; z < dim.dimZ; ++z)
        {
            Arrays.fill(voxels[z], value);
        }
    }
}
