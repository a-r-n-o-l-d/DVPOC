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
import floodfill.FloodFill3D16bit;
import ij.IJ;
import ij.ImagePlus;
import ij.gui.GenericDialog;
import ij.macro.ExtensionDescriptor;
import ij.macro.Functions;
import ij.macro.MacroExtension;
import ij.plugin.PlugIn;
import volume.Point3D;
import volume.VolumeShortZz;

/**
 *
 * @author Arnold Fertin
 */
public final class Flood_Fill_3D_16bit implements PlugIn, MacroExtension
{

    private int xPos, yPos, zPos;

    private int tgt, rep, con;

    @Override
    public ExtensionDescriptor[] getExtensionFunctions()
    {
        int[] args =
        {
            ARG_NUMBER, ARG_NUMBER, ARG_NUMBER, ARG_NUMBER, ARG_NUMBER, ARG_NUMBER
        };
        ExtensionDescriptor[] extensions =
        {
            ExtensionDescriptor.newDescriptor("floodFill3D16bit", this, args),
        };
        return extensions;
    }

    @Override
    public String handleExtension(String name,
                                  Object[] args)
    {
        if (name.equals("floodFill3D16bit"))
        {
            final ImagePlus imp = IJ.getImage();
            final VolumeShortZz vol = new VolumeShortZz(imp);
            final FloodFill3D16bit ff = new FloodFill3D16bit(vol);
            final int x = ((Double) args[0]).intValue();
            final int y = ((Double) args[1]).intValue();
            final int z = ((Double) args[2]).intValue();
            final int t = ((Double) args[3]).intValue();
            final int r = ((Double) args[4]).intValue();
            final int c = ((Double) args[5]).intValue();
            final int count = ff.floodFill3D(new Point3D(x, y, z), t, r, c);
            imp.updateAndDraw();
            return String.valueOf(count);
        }
        return null;
    }

    @Override
    public void run(String string)
    {
        IJ.register(Flood_Fill_3D_16bit.class);
        if (IJ.macroRunning())
        {
            Functions.registerExtensions(this);
        }
        else
        {
            if (!doDialog())
            {
                return;
            }
            final ImagePlus imp = IJ.getImage();
            final VolumeShortZz vol = new VolumeShortZz(imp);
            final FloodFill3D16bit ff = new FloodFill3D16bit(vol);
            final int count = ff.floodFill3D(new Point3D(xPos, yPos, zPos), tgt, rep, con);
            imp.updateAndDraw();
            IJ.log("Number of labelled voxels : " + count);
        }
    }

    private boolean doDialog()
    {
        final String[] connexity =
        {
            "6", "18", "26"
        };
        final GenericDialog gd = new GenericDialog("Flood fill 3D 16-bit", IJ.getInstance());
        gd.addNumericField("x_position", 0, 0);
        gd.addNumericField("y_position", 0, 0);
        gd.addNumericField("z_position", 0, 0);
        gd.addNumericField("target_value", 0, 0);
        gd.addNumericField("replacement_value", 0, 0);
        gd.addChoice("connexity", connexity, connexity[0]);
        gd.showDialog();
        if (gd.wasCanceled())
        {
            return false;
        }
        xPos = (int) gd.getNextNumber();
        yPos = (int) gd.getNextNumber();
        zPos = (int) gd.getNextNumber();
        tgt = (int) gd.getNextNumber();
        rep = (int) gd.getNextNumber();
        con = new Integer(gd.getNextChoice());

        return true;
    }
}
