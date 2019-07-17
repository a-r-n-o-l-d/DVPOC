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
import ij.gui.GenericDialog;
import ij.plugin.PlugIn;
import volume.Dimensions;
import volume.Point3D;
import volume.VolumeByteZz;
import volume.VolumeFloatZz;

/**
 *
 * @author Arnold Fertin
 */
public class Find_Maxima_3D implements PlugIn
{

    private VolumeFloatZz vol;

    private float threshold;

    private int count;

    @Override
    public void run(String string)
    {
        if (!doDialog())
        {
            return;
        }
        vol = new VolumeFloatZz(IJ.getImage());
        final Dimensions dims = vol.getDimensions();
        final VolumeByteZz volMax = new VolumeByteZz(dims);
        volMax.setVoxelDimensions(vol.getVoxelDimensions());
        final Point3D p = new Point3D();
        count = 0;
        for (p.setZ(1); p.getZ() < dims.dimZ - 1; p.incZ())
        {
            for (p.setY(1); p.getY() < dims.dimY - 1; p.incY())
            {
                for (p.setX(1); p.getX() < dims.dimX - 1; p.incX())
                {
                    volMax.setVoxel(p, isMaxima(p));
                }
            }
        }
        volMax.getImagePlus("Maxima").show();
        IJ.log("Number of Maxima: " + count);
    }

    private int isMaxima(final Point3D pos)
    {
        final float value = vol.getVoxel(pos);
        if (value < threshold)
        {
            return 0;
        }
        final Point3D p = new Point3D();
        for (p.setZ(pos.getZ() - 1); p.getZ() <= pos.getZ() + 1; p.incZ())
        {
            for (p.setY(pos.getY() - 1); p.getY() <= pos.getY() + 1; p.incY())
            {
                for (p.setX(pos.getX() - 1); p.getX() <= pos.getX() + 1; p.incX())
                {
                    if (!p.isAtPosition(pos))
                    {
                        final float v = vol.getVoxel(p);
                        if (v >= value)
                        {
                            return 0;
                        }
                    }
                }
            }
        }
        count++;
        return 255;
    }

    private boolean doDialog()
    {
        final GenericDialog gd = new GenericDialog("3D Maxima Finder", IJ.getInstance());
        gd.addNumericField("Threshold", 0, 0);
        gd.showDialog();
        if (gd.wasCanceled())
        {
            return false;
        }
        threshold = (float) gd.getNextNumber();
        return true;
    }
}
