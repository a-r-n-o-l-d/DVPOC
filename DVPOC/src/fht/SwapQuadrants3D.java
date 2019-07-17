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
