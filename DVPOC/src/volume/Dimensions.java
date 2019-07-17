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

/**
 *
 * @author Arnold Fertin
 */
public final class Dimensions
{
    public final int dimX;

    public final int dimY;

    public final int dimZ;

    public Dimensions(final int dimx,
                      final int dimy,
                      final int dimz)
    {
        dimX = dimx;
        dimY = dimy;
        dimZ = dimz;
    }
    
    public Dimensions(final ImagePlus img)
    {
        dimX = img.getWidth();
        dimY = img.getHeight();
        dimZ = img.getNSlices();
    }
    
    
    public boolean checkPosition(final Point3D pos)
    {
        return pos.getX() >= 0 && pos.getX() < dimX &&
               pos.getY() >= 0 && pos.getY() < dimY &&
               pos.getZ() >= 0 && pos.getZ() < dimZ;
    }
    
    @Override
    public String toString()
    {
        final StringBuffer buffer = new StringBuffer();
        buffer.append(dimX);
        buffer.append('x');
        buffer.append(dimY);
        buffer.append('x');
        buffer.append(dimZ);
        return buffer.toString();
    }
//    
//    public boolean checkPosition(final int x, final int y, final int z)
//    {
//        return x >= 0 && x < dimX &&
//               y >= 0 && y < dimY &&
//               z >= 0 && z < dimZ;
//    }
}
