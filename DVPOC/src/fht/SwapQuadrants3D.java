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
package fht;

import volume.Dimensions;
import volume.Point3D;
import volume.VolumeFloatZz;

/**
 * Ne marche pas pour des dimensions impaires.
 * @author Arnold Fertin
 */
public class SwapQuadrants3D
{
    private final Dimensions dims;
    private final int sizeX;
    private final int offsetX;
    private final int sizeY;
    private final int offsetY;
    private final int sizeZ;
    private final int offsetZ;
    
    public SwapQuadrants3D(final Dimensions dim)
    {
        if ((dim.dimX & 1) != 0 || (dim.dimY & 1) != 0 || (dim.dimZ & 1) != 0)
        {
            throw new IllegalArgumentException("must have even dimensions");
        }
        dims = dim;
        sizeX = dims.dimX / 2;
        offsetX = (dims.dimX + 1) / 2;
        sizeY = dims.dimY / 2;
        offsetY = (dims.dimY + 1) / 2;
        sizeZ = dims.dimZ / 2;
        offsetZ = (dims.dimZ + 1) / 2;
    }
    
    public void swap(final VolumeFloatZz vol)
    {
        // Point du premier Qudrant
        final Point3D p1 = new Point3D();
        // Point complémentaire
        final Point3D p2 = new Point3D();
        // Swap du premier quadrant
        for (p1.setZ(0), p2.setZ(offsetZ); p1.getZ() < sizeZ; p1.incZ(), p2.incZ())
        {
            for (p1.setY(0), p2.setY(offsetY); p1.getY() < sizeY; p1.incY(), p2.incY())
            {
                for (p1.setX(0), p2.setX(offsetX); p1.getX() < sizeX; p1.incX(), p2.incX())
                {
                    final float v1 = vol.getVoxel(p1);
                    final float v2 = vol.getVoxel(p2);
                    vol.setVoxel(p1, v2);
                    vol.setVoxel(p2, v1);
                }
            }
        }
        // Swap du second quadrant
        for (p1.setZ(0), p2.setZ(offsetZ); p1.getZ() < sizeZ; p1.incZ(), p2.incZ())
        {
            for (p1.setY(0), p2.setY(offsetY); p1.getY() < sizeY; p1.incY(), p2.incY())
            {
                for (p1.setX(offsetX), p2.setX(0); p1.getX() < dims.dimX; p1.incX(), p2.incX())
                {
                    final float v1 = vol.getVoxel(p1);
                    final float v2 = vol.getVoxel(p2);
                    vol.setVoxel(p1, v2);
                    vol.setVoxel(p2, v1);
                }
            }
        }
        // Swap du troisieme quadrant
        for (p1.setZ(offsetZ), p2.setZ(0); p1.getZ() < dims.dimZ; p1.incZ(), p2.incZ())
        {
            for (p1.setY(0), p2.setY(offsetY); p1.getY() < sizeY; p1.incY(), p2.incY())
            {
                for (p1.setX(0), p2.setX(offsetX); p1.getX() < sizeX; p1.incX(), p2.incX())
                {
                    final float v1 = vol.getVoxel(p1);
                    final float v2 = vol.getVoxel(p2);
                    vol.setVoxel(p1, v2);
                    vol.setVoxel(p2, v1);
                }
            }
        }
        // Swap du quatrième quadrant
        for (p1.setZ(offsetZ), p2.setZ(0); p1.getZ() < dims.dimZ; p1.incZ(), p2.incZ())
        {
            for (p1.setY(0), p2.setY(offsetY); p1.getY() < sizeY; p1.incY(), p2.incY())
            {
                for (p1.setX(offsetX), p2.setX(0); p1.getX() < dims.dimX; p1.incX(), p2.incX())
                {
                    final float v1 = vol.getVoxel(p1);
                    final float v2 = vol.getVoxel(p2);
                    vol.setVoxel(p1, v2);
                    vol.setVoxel(p2, v1);
                }
            }
        }
    }
}
