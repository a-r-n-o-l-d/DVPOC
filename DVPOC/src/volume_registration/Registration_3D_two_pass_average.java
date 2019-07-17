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
import ij.WindowManager;
import ij.gui.GenericDialog;
import ij.plugin.PlugIn;
import java.util.Locale;
import registration.DisplacementField;
import registration.RegistrationData;
import registration.RegistrationTwoPassAverage27;
import util.UtilIJ;
import volume.Dimensions;
import volume.VolumeByteZz;
import volume.VolumeFloatZz;
import volume.WindowType;

/**
 *
 * @author Arnold Fertin
 */
public class Registration_3D_two_pass_average implements PlugIn
{
    private VolumeFloatZz volume1;

    private VolumeFloatZz volume2;

    private VolumeByteZz volumeMask;

    private WindowType type;

    private Dimensions dims;

    private double pValueLevel;

    private String savePath = "";

    private static final int MIN_SIZE = 8;

    @Override
    public void run(String string)
    {
        Locale.setDefault(new Locale("en", "US"));
        if (!doDialog())
        {
            return;
        }
        if (savePath.equals(""))
        {
            return;
        }
        final RegistrationData dat = new RegistrationData(volume1, volume2, type, dims,
                                                          new Dimensions(MIN_SIZE, MIN_SIZE, MIN_SIZE), pValueLevel);
        final DisplacementField field = new DisplacementField(volumeMask);
        final RegistrationTwoPassAverage27 reg = new RegistrationTwoPassAverage27(dat, field, savePath);
        reg.match();
    }

    private boolean doDialog()
    {
        final String[] winSize =
        {
            "4", "8", "16", "32", "64", "128"
        };
        final String[] pValueLevels =
        {
            "0.05", "0.01", "0.001", "0.000001"
        };
        final String[] stkTitles = UtilIJ.getStackTitles(false);
        final GenericDialog gd = new GenericDialog("Volume Registration", IJ.getInstance());
        gd.addChoice("Stack_1", stkTitles, stkTitles[0]);
        gd.addChoice("Stack_2", stkTitles, stkTitles[0]);
        gd.addChoice("Mask", stkTitles, stkTitles[0]);
        gd.addChoice("Window_type", WindowType.NAMES, WindowType.NAMES[2]);
        gd.addChoice("Window_size_XY", winSize, winSize[3]);
        gd.addChoice("Window_size_Z", winSize, winSize[3]);
        gd.addChoice("P_value_significance", pValueLevels, pValueLevels[3]);
        gd.addStringField("Save_path_directory", "");
        gd.showDialog();
        if (gd.wasCanceled())
        {
            return false;
        }
        final ImagePlus img1 = WindowManager.getImage(gd.getNextChoice());
        final ImagePlus img2 = WindowManager.getImage(gd.getNextChoice());
        if (img1.getBitDepth() != 32 || img1.getBitDepth() != 32)
        {
            UtilIJ.errorMessage("Stacks must be 32-bit float.");
        }
        if (!UtilIJ.checkDimensions(img1, img2))
        {
            UtilIJ.errorMessage("Stacks with different dimensions.");
        }
        final ImagePlus mask = WindowManager.getImage(gd.getNextChoice());
        if (!UtilIJ.checkDimensions(img1, mask))
        {
            UtilIJ.errorMessage("Mask must be of the same size.");
        }
        volume1 = new VolumeFloatZz(img1);
        volume2 = new VolumeFloatZz(img2);
        volumeMask = new VolumeByteZz(mask);
        type = WindowType.valueOf(gd.getNextChoice());
        final int sizexy = Integer.parseInt(gd.getNextChoice());
        final int sizez = Integer.parseInt(gd.getNextChoice());
        dims = new Dimensions(sizexy, sizexy, sizez);
        pValueLevel = Double.parseDouble(gd.getNextChoice());
        savePath = gd.getNextString();

        return true;
    }
}
