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
