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
import ij.ImagePlus;
import ij.plugin.PlugIn;
import registration.GrubbsTest;
import util.UtilIJ;
import volume.Dimensions;
import volume.Point3D;
import volume.VolumeFloatZz;

/**
 *
 * @author afertin
 */
public class Test_Grubbs implements PlugIn
{

    @Override
    public void run(String string)
    {
        final ImagePlus img = UtilIJ.imagePlusByName("test");
        final VolumeFloatZz vol = new VolumeFloatZz(img);
        final Dimensions dims = vol.getDimensions();
        final Point3D p1 = new Point3D();
        double max = Double.MIN_VALUE;
        for (p1.setZ(0); p1.getZ() < dims.dimZ; p1.incZ())
        {
            for (p1.setY(0); p1.getY() < dims.dimY; p1.incY())
            {
                for (p1.setX(0); p1.getX() < dims.dimX; p1.incX())
                {
                    if(vol.getVoxel(p1) > max)
                    {
                        max = vol.getVoxel(p1);
                    }
                }
            }
        }
        IJ.log("max " + max);
        final GrubbsTest test = new GrubbsTest(vol, max);
    }
     
}
