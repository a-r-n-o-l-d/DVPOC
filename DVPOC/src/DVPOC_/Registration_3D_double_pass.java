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
import ij.ImagePlus;
import ij.WindowManager;
import ij.gui.GenericDialog;
import ij.plugin.PlugIn;
import java.util.Locale;
import registration.DisplacementField;
import registration.RegistrationData;
import registration.RegistrationDoublePass;
import util.ConcurrencyUtils;
import util.UtilIJ;
import volume.Dimensions;
import volume.VolumeByteZz;
import volume.VolumeFloatZz;
import volume.WindowType;

/**
 *
 * @author Arnold Fertin
 */
public class Registration_3D_double_pass implements PlugIn
{
    private static final int DEF_WIN_SIZE = 3;

    private static final int S_B_DEPTH = 32;

    private static final int M_B_DEPTH = 8;

    private VolumeFloatZz volume1;

    private VolumeFloatZz volume2;

    private VolumeByteZz volumeMask;

    private WindowType type;

    private Dimensions dims;

    private Dimensions dime;

    private double pValueLevel;

    private String savePath = "";

    @Override
    public void run(final String string)
    {
        Locale.setDefault(new Locale("en", "US"));
        if (!doDialog())
        {
            return;
        }
        if (savePath.equals(""))
        {
            IJ.log("Path is not defined");
            return;
        }
        ConcurrencyUtils.setNumberOfThreads(Runtime.getRuntime().availableProcessors());
        final RegistrationData dat = new RegistrationData(volume1, volume2, type, dims, dime, pValueLevel);
        final DisplacementField field = new DisplacementField(volumeMask);
        final RegistrationDoublePass reg = new RegistrationDoublePass(dat, field);
        final long t1 = System.nanoTime();
        reg.match();
        final long t2 = System.nanoTime() - t1;
        IJ.saveString(field.toString(dat, t2), savePath);
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

        gd.addMessage("Starting_window_size:");
        gd.addChoice("Window_size_XY_start", winSize, winSize[DEF_WIN_SIZE]);
        gd.addChoice("Window_size_Z_start", winSize, winSize[DEF_WIN_SIZE]);

        gd.addMessage("Ending_window_size:");
        gd.addChoice("Window_size_end", winSize, winSize[1]);

        gd.addChoice("P_value_significance", pValueLevels, pValueLevels[DEF_WIN_SIZE]);
        gd.addStringField("Save_path_directory", "");
        gd.showDialog();

        if (gd.wasCanceled())
        {
            return false;
        }

        final ImagePlus img1 = WindowManager.getImage(gd.getNextChoice());
        final ImagePlus img2 = WindowManager.getImage(gd.getNextChoice());
        if (img1.getBitDepth() != S_B_DEPTH || img2.getBitDepth() != S_B_DEPTH)
        {
            UtilIJ.errorMessage("Stacks must be 32-bit float.");
        }
        if (!UtilIJ.checkDimensions(img1, img2))
        {
            UtilIJ.errorMessage("Stacks with different dimensions.");
        }
        volume1 = new VolumeFloatZz(img1);
        volume2 = new VolumeFloatZz(img2);
        final ImagePlus mask = WindowManager.getImage(gd.getNextChoice());
        if (!UtilIJ.checkDimensions(img1, mask))
        {
            UtilIJ.errorMessage("Mask must be of the same size.");
        }
        if (mask.getBitDepth() != M_B_DEPTH)
        {
            UtilIJ.errorMessage("Mask must be 8-bit binary.");
        }
        volumeMask = new VolumeByteZz(mask);

        type = WindowType.valueOf(gd.getNextChoice());

        final int sizexy = Integer.parseInt(gd.getNextChoice());
        final int sizez = Integer.parseInt(gd.getNextChoice());

        dims = new Dimensions(sizexy, sizexy, sizez);

        final int size = Integer.parseInt(gd.getNextChoice());
        if (size >= 4)
        {
            dime = new Dimensions(size, size, size);
        }
        else
        {
            dime = new Dimensions(sizexy, sizexy, sizez);
        }

        pValueLevel = Double.parseDouble(gd.getNextChoice());
        savePath = gd.getNextString();

        return true;
    }
}
