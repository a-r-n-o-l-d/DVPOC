package volume_registration;

/*
 * Copyright (C) 2015 Arnold Fertin (Centre National de la Recherche Scientifique)
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU General Public License as published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program.  If
 * not, see <http://www.gnu.org/licenses/>.
 */


import ij.IJ;
import ij.ImagePlus;
import ij.gui.GenericDialog;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;
import volume.Dimensions;
import volume.Point3D;
import volume.VolumeFloatZz;
import volume.Window3D;
import volume.WindowType;

/**
 *
 * @author Arnold Fertin
 */
public class Test_Window3D  implements PlugInFilter
{
    private ImagePlus imp;
    private int dimX, dimY, dimZ;
    private int xPos, yPos, zPos;
    private WindowType wintype;
    
    @Override
    public void run(ImageProcessor ip)
    {
        if (!doDialog())
        {
            return;
        }
        final VolumeFloatZz vol = new VolumeFloatZz(imp);
        final Window3D win = new Window3D(new Dimensions(dimX, dimY, dimZ), wintype);
        final VolumeFloatZz sub = win.apodizeSubVolume(new Point3D(xPos, yPos, zPos), vol);
        sub.getImagePlus().show();
   }

    @Override
    public int setup(String string,
                     ImagePlus ip)
    {
        IJ.register(Test_Window3D.class);
        this.imp = ip;
        return DOES_32;
    }
    
    private boolean doDialog()
    {
        final GenericDialog gd = new GenericDialog("Window 3D", IJ.getInstance());
        gd.addNumericField("x_position", 0, 0);
        gd.addNumericField("y_position", 0, 0);
        gd.addNumericField("z_position", 0, 0);
        gd.addNumericField("dim_x", 32, 0);
        gd.addNumericField("dim_y", 32, 0);
        gd.addNumericField("dim_z", 16, 0);
        gd.addChoice("Window_type", WindowType.NAMES, WindowType.NAMES[0]);
        gd.showDialog();

        xPos = (int) gd.getNextNumber();
        yPos = (int) gd.getNextNumber();
        zPos = (int) gd.getNextNumber();
        dimX = (int) gd.getNextNumber();
        dimY = (int) gd.getNextNumber();
        dimZ = (int) gd.getNextNumber();
        wintype = WindowType.valueOf(gd.getNextChoice());
        
        return true;
    }
}
