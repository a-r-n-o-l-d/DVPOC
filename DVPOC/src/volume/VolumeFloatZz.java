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
