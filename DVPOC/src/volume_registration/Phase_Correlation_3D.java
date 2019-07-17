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
import correlation.PhaseCorrelation3D;
import correlation.Displacement;
import fht.FHT3D;
import ij.IJ;
import ij.ImagePlus;
import ij.WindowManager;
import ij.gui.GenericDialog;
import ij.plugin.Duplicator;
import ij.plugin.PlugIn;
import util.UtilIJ;
import volume.Dimensions;
import volume.Point3D;
import volume.VolumeFloatZz;
import volume.Window3D;
import volume.WindowType;

/**
 *
 * @author Arnold Fertin
 */
public class Phase_Correlation_3D implements PlugIn
{
    private VolumeFloatZz vol1;

    private VolumeFloatZz vol2;

    private WindowType wintype;

    private double pValueLevel;

    @Override
    public void run(String string)
    {
        if (!doDialog())
        {
            return;
        }
//        vol1.getImagePlus("vol1").show();
//        vol2.getImagePlus("vol2").show();
        FHT3D.zeroPaddingInZ(vol1);
        FHT3D.zeroPaddingInZ(vol2);
        final PhaseCorrelation3D co = new PhaseCorrelation3D(vol1.getDimensions());
        final Displacement tr = co.correlate(vol1, vol2, true, pValueLevel);
        co.getCorrelation().getImagePlus("Correlation").show();
//        final Displacement tr = co.getDisplacement();
        IJ.log("Window=" + wintype);
        IJ.log("U=" + tr.getU());
        IJ.log("V=" + tr.getV());
        IJ.log("W=" + tr.getW());
        IJ.log("MaxCorr=" + tr.getMaximumOfCorrelation());
        IJ.log("GrubbsStat=" + tr.getGrubbsStatistics());
        IJ.log("p-value=" + tr.getPValue());
        IJ.log("Spurious=" + tr.isSpurious());
    }

    private boolean doDialog()
    {
        final String[] pValueLevels =
        {
            "0.05", "0.01", "0.001", "0.000001"
        };
        final String[] stkTitles = UtilIJ.getStackTitles(false);
        final GenericDialog gd = new GenericDialog("Volume Phase Correlation", IJ.getInstance());
        gd.addChoice("Stack_1", stkTitles, stkTitles[0]);
        gd.addChoice("Stack_2", stkTitles, stkTitles[1]);
        gd.addChoice("Window_type", WindowType.NAMES, WindowType.NAMES[0]);
        gd.addChoice("P_value_significance", pValueLevels, pValueLevels[2]);
        gd.showDialog();
        final ImagePlus im1 = new Duplicator().run(WindowManager.getImage(gd.getNextChoice()));
        final ImagePlus im2 = new Duplicator().run(WindowManager.getImage(gd.getNextChoice()));
        if (gd.wasCanceled())
        {
            return false;
        }
        if (!UtilIJ.checkDimensions(im1, im2))
        {
            UtilIJ.errorMessage("Stacks with different dimensions");
            return false;
        }
        final VolumeFloatZz v1 = new VolumeFloatZz(im1);
        final VolumeFloatZz v2 = new VolumeFloatZz(im2);
        wintype = WindowType.valueOf(gd.getNextChoice());
        final Dimensions dim = v1.getDimensions();
        final Window3D win = new Window3D(dim, wintype);
        vol1 = win.apodize(v1);
        vol2 = win.apodize(v2);
        pValueLevel = Double.parseDouble(gd.getNextChoice());
        return true;
    }

    public void test(VolumeFloatZz src)
    {
        final Dimensions dim = src.getDimensions();
        final Point3D p1 = new Point3D();
        for (p1.setZ(0); p1.getZ() < dim.dimZ; p1.incZ())
        {
            for (p1.setY(0); p1.getY() < dim.dimY; p1.incY())
            {
                for (p1.setX(0); p1.getX() < dim.dimX; p1.incX())
                {
                    src.setVoxel(p1, 0f);
                }
            }
        }
    }
}
