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
package DVPOC_;

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
