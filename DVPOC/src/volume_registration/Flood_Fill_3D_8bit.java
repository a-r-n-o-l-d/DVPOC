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
import floodfill.FloodFill3D8bit;
import ij.IJ;
import ij.ImagePlus;
import ij.gui.GenericDialog;
import ij.macro.ExtensionDescriptor;
import ij.macro.Functions;
import ij.macro.MacroExtension;
import ij.plugin.PlugIn;
import volume.Point3D;
import volume.VolumeByteZz;

/**
 *
 * @author Arnold Fertin
 */
public final class Flood_Fill_3D_8bit implements PlugIn, MacroExtension
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
            ExtensionDescriptor.newDescriptor("floodFill3D8bit", this, args),
        };
        return extensions;
    }

    @Override
    public String handleExtension(String name,
                                  Object[] args)
    {
        if (name.equals("floodFill3D8bit"))
        {
            final ImagePlus imp = IJ.getImage();
            final VolumeByteZz vol = new VolumeByteZz(imp);
            final FloodFill3D8bit ff = new FloodFill3D8bit(vol);
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
        IJ.register(Flood_Fill_3D_8bit.class);
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
            final VolumeByteZz vol = new VolumeByteZz(imp);
            final FloodFill3D8bit ff = new FloodFill3D8bit(vol);
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
        final GenericDialog gd = new GenericDialog("Flood fill 3D 8-bit", IJ.getInstance());
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
