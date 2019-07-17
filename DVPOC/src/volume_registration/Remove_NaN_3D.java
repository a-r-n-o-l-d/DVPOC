package volume_registration;

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


import ij.IJ;
import ij.plugin.PlugIn;
import volume.Dimensions;
import volume.Point3D;
import volume.VolumeByteZz;
import volume.VolumeFloatZz;

/**
 *
 * @author Arnold Fertin
 */
public class Remove_NaN_3D implements PlugIn
{
    private VolumeFloatZz vol;
    private int count;
    
    @Override
    public void run(String string)
    {
        vol = new VolumeFloatZz(IJ.getImage());
        count = 1;
        while (count > 0)
        {
            iterate();
            IJ.log("Count: " + count);
        }
    }
    
    private void iterate()
    {
        final VolumeFloatZz tmp = (VolumeFloatZz) vol.duplicate();
        final Dimensions dims = vol.getDimensions();
        final Point3D p1 = new Point3D();
        
        count = 0;
        for (p1.setZ(0); p1.getZ() < dims.dimZ; p1.incZ())
        {
            for (p1.setY(0); p1.getY() < dims.dimY; p1.incY())
            {
                for (p1.setX(0); p1.getX() < dims.dimX; p1.incX())
                {
                    final float v1 = tmp.getVoxel(p1);
                    if (!Float.isNaN(v1))
                    {
                        final Point3D p2 = new Point3D();
                        for (p2.setZ(p1.getZ() - 1); p2.getZ() <= p1.getZ() + 1; p2.incZ())
                        {
                            for (p2.setY(p1.getY() - 1); p2.getY() <= p1.getY() + 1; p2.incY())
                            {
                                for (p2.setX(p1.getX() - 1); p2.getX() <= p1.getX() + 1; p2.incX())
                                {
                                    if (!p2.isAtPosition(p1) && dims.checkPosition(p2))
                                    {
                                        final float v2 = tmp.getVoxel(p2);
                                        if (Float.isNaN(v2))
                                        {
                                            vol.setVoxel(p2, v1);
                                            ++count;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
//    
//    private float getNearestValue(final Point3D p1)
//    {
//        final Point3D p2 = new Point3D();
//        for (p2.setZ(p1.getZ() - 1); p2.getZ() <= p1.getZ() + 1; p2.incZ())
//        {
//            for (p2.setY(p1.getY() - 1); p2.getY() <= p1.getY() + 1; p2.incY())
//            {
//                for (p2.setX(p1.getX() - 1); p2.getX() <= p1.getX() + 1; p2.incX())
//                {
//                    if (!p2.isAtPosition(p1))
//                    {
//                        final float v = vol.getVoxel(p2);
//                        if (!Float.isNaN(v))
//                        {
//                            return v;
//                        }
//                    }
//                }
//            }
//        }
//        return Float.NaN;
//    }
}
