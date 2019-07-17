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

import ij.ImagePlus;
import ij.ImageStack;
import ij.measure.Calibration;

/**
 *
 * @author Arnold Fertin
 */
public class VolumeByteZz extends AbstractVolumeZz
{

    private final byte[][] voxels;

    public VolumeByteZz(final ImagePlus img)
    {
        super(img);
        if (img.getBitDepth() != 8)
        {
            throw new IllegalArgumentException("must be byte");
        }
        image = img;
        dim = new Dimensions(img);
        final ImageStack stk = img.getStack();
        voxels = new byte[dim.dimZ][]; //dim.dimX * dim.dimY
        for (int z = 0; z < dim.dimZ; z++)
        {
            final byte[] v = (byte[]) stk.getImageArray()[z];
            voxels[z] = v;
        }
    }

    public VolumeByteZz(final Dimensions dims)
    {
        super();
        dim = dims;
        voxels = new byte[dim.dimZ][dim.dimX * dim.dimY];
    }

    public int getVoxel(final Point3D pos)
    {
        return voxels[pos.getZ()][pos.getX() + pos.getY() * dim.dimX] & 0xff;
    }

//    @Override
//    public double getVoxelDouble(Point3D pos)
//    {
//        return (double) (voxels[pos.getZ()][pos.getX() + pos.getY() * dim.dimX] & 0xff);
//    }
    public void setVoxel(final Point3D pos,
                         final int value)
    {
        voxels[pos.getZ()][pos.getX() + pos.getY() * dim.dimX] = crop(value);
    }

    public int getVoxel(final int x,
                        final int y,
                        final int z)
    {
        return voxels[z][x + y * dim.dimX] & 0xff;
    }

    public void setVoxel(final int x,
                         final int y,
                         final int z,
                         final int value)
    {
        voxels[z][x + y * dim.dimX] = crop(value);
    }
//
//    @Override
//    public void setVoxel(final Point3D pos,
//                         final double value)
//    {
//        voxels[z][x + y * dim.dimX] = crop((int) value);
//    }

    private byte crop(final int value)
    {
        if (value > 255)
        {
            return (byte) 255;
        }
        else if (value < 0)
        {
            return 0;
        }
        return (byte) value;
    }

    @Override
    public AbstractVolumeZz duplicate()
    {
        final VolumeByteZz copy = new VolumeByteZz(dim);
        final int n = dim.dimX * dim.dimY;
        for (int z = 0; z < dim.dimZ; z++)
        {
            System.arraycopy(voxels[z], 0, copy.voxels[z], 0, n);
        }
        return copy;
    }

    @Override
    public ImagePlus getImagePlus(final String title)
    {
        if (image == null)
        {
            final ImageStack stk = ImageStack.create(dim.dimX, dim.dimY, dim.dimZ, 8);
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
