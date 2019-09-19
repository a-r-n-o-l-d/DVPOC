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
package util;

import ij.IJ;
import ij.ImagePlus;
import ij.WindowManager;
import ij.gui.Line;
import ij.gui.Roi;
import ij.gui.WaitForUserDialog;
import ij.process.FloatPolygon;
import ij.process.FloatProcessor;
import java.awt.Polygon;

/**
 * V 0.2
 *
 * @author Arnold Fertin
 */
public final class UtilIJ
{
    private UtilIJ()
    {
    }

    public static final void errorMessage(final String label)
    {
        final WaitForUserDialog ms = new WaitForUserDialog("Error", label);
        ms.show();
    }

    public static final boolean checkDimensions(final ImagePlus img1,
                                                final ImagePlus img2)
    {
        final int[] dim1 = img1.getDimensions();
        final int[] dim2 = img2.getDimensions();
        if (dim1.length != dim2.length)
        {
            return false;
        }
        for (int i = 0; i < dim1.length; i++)
        {
            if (dim1[i] != dim2[i])
            {
                return false;
            }
        }
        return true;
    }

    public static final ImagePlus averageProjection(final ImagePlus src)
    {
        ImagePlus proj = IJ.createImage("Proj", "32-bit black", src.getWidth(), src.getHeight(), 1);
        for (int i = 1; i <= src.getNSlices(); i++)
        {
            src.setSliceWithoutUpdate(i);
            for (int y = 0; y < src.getHeight(); y++)
            {
                for (int x = 0; x < src.getWidth(); x++)
                {
                    final float v = proj.getProcessor().getf(x, y)
                                    + src.getProcessor().getf(x, y) / src.getNSlices();
                    proj.getProcessor().setf(x, y, v);
                }
            }
        }
        return proj;
    }

    public static final ImagePlus imagePlusByName(final String name)
    {
        if (name.equals("none"))
        {
            return null;
        }
        return WindowManager.getImage(name);
    }

    public static final String[] getImageTitles(final boolean withnone) // mettre un filtre sur le type
    {
        int i, j;
        final int[] ids;
        final String[] impTitles;
        ImagePlus img;
        final boolean[] isStack;

        ids = WindowManager.getIDList();
        if (ids == null)
        {
            String[] tmp =
            {
                "none"
            };
            return tmp;
        }
        isStack = new boolean[ids.length];
        for (i = 0, j = 0; i < ids.length; i++)
        {
            img = WindowManager.getImage(ids[i]);
            if (img.getNSlices() > 1 && img.getNChannels() < 2 && img.getNFrames() < 2)
            {
                isStack[i] = true;
            }
            else
            {
                isStack[i] = false;
                j++;
            }
        }
        if (j == 0)
        {
            String[] tmp =
            {
                "none"
            };
            return tmp;
        }
        // rajoute ou non "none" à la liste
        if (withnone)
        {
            impTitles = new String[j + 1];
            impTitles[0] = "none";
            j = 1;
        }
        else
        {
            impTitles = new String[j];
            j = 0;
        }
        for (i = 0; i < ids.length; i++)
        {
            if (!isStack[i])
            {
                impTitles[j] = WindowManager.getImage(ids[i]).getTitle();
                j++;
            }
        }

        return impTitles;
    }

    public static final String[] getStackTitles(final boolean withnone) // mettre un filtre sur le type
    {
        int i, j;
        final int[] ids;
        final String[] stkTitles;
        ImagePlus img;
        final boolean[] isStack;

        ids = WindowManager.getIDList();
        if (ids == null)
        {
            String[] tmp =
            {
                "none"
            };
            return tmp;
        }
        isStack = new boolean[ids.length];
        for (i = 0, j = 0; i < ids.length; i++)
        {
            img = WindowManager.getImage(ids[i]);
            if (img.getNSlices() > 1 && img.getNChannels() < 2 && img.getNFrames() < 2)
            {
                isStack[i] = true;
                j++;
            }
            else
            {
                isStack[i] = false;
            }
        }
        if (j == 0)
        {
            String[] tmp =
            {
                "none"
            };
            return tmp;
        }
        // rajoute ou non "none" à la liste
        if (withnone)
        {
            stkTitles = new String[j + 1];
            stkTitles[0] = "none";
            j = 1;
        }
        else
        {
            stkTitles = new String[j];
            j = 0;
        }
        for (i = 0; i < ids.length; i++)
        {
            if (isStack[i])
            {
                stkTitles[j] = WindowManager.getImage(ids[i]).getTitle();
                j++;
            }
        }

        return stkTitles;
    }

    //dst doit être +petit ou égal à src, si +petit crop
    public static final boolean blitter(final FloatProcessor src,
                                        final FloatProcessor dst,
                                        final int x,
                                        final int y)
    {
        int i, j;
        final int start, stride;
        final float[] srcarray, dstarray;
        final int size, srcwidth, dstwidth;

        srcarray = (float[]) src.getPixels();
        dstarray = (float[]) dst.getPixels();
        if (src.getWidth() == dst.getWidth() && src.getHeight() == dst.getHeight())//copy
        {
            System.arraycopy(srcarray, 0, dstarray, 0, srcarray.length);
            return true;
        }
        if (src.getWidth() >= dst.getWidth() && src.getHeight() >= dst.getHeight())//crop
        {
            srcwidth = src.getWidth();
            dstwidth = dst.getWidth();
            size = dst.getWidth() * dst.getHeight();
            start = x + y * src.getWidth();
            stride = src.getWidth();
            for (i = 0, j = start; i < size; i += dstwidth, j += srcwidth)
            {
                System.arraycopy(srcarray, j, dstarray, i, dstwidth);
            }
            return true;
        }
        return false;
    }

    // rempli un carré de size x size avec value au point (x,y)
    // marche pas out of bound
    public static final boolean blitter(final FloatProcessor dst,
                                        final int x,
                                        final int y,
                                        final int size,
                                        final float value)
    {
        int i, j;
        final int start, end;
        final float[] srcarray, dstarray;

        dstarray = (float[]) dst.getPixels();
        srcarray = new float[size];
        for (i = 0; i < size; i++)
        {
            srcarray[i] = value;
        }
        start = x + y * dst.getWidth();
        end = (x + size) + (y + size) * dst.getWidth();
        for (i = start; i < end; i += size)
        {
            System.arraycopy(srcarray, 0, dstarray, i, size);
        }

        return true;
    }

    public static final float[][] getCoordinates(final Roi roi)
    {
        final float[][] xy = new float[2][];
        if (roi.getType() == Roi.LINE)
        {
            xy[0] = new float[2];
            xy[1] = new float[2];
            final Line line = (Line) roi;
            xy[0][0] = (float) line.x1d;
            xy[1][0] = (float) line.y1d;
            xy[0][1] = (float) line.x2d;
            xy[1][1] = (float) line.y2d;
        }
        else
        {
            final FloatPolygon fp = roi.getFloatPolygon();
            if (fp == null)
            {
                final Polygon p = roi.getPolygon();
                xy[0] = new float[p.npoints];
                xy[1] = new float[p.npoints];
                System.arraycopy(p.xpoints, 0, xy[0], 0, p.npoints);
                System.arraycopy(p.ypoints, 0, xy[1], 0, p.npoints);
            }
            else
            {
                xy[0] = new float[fp.npoints];
                xy[1] = new float[fp.npoints];
                System.arraycopy(fp.xpoints, 0, xy[0], 0, fp.npoints);
                System.arraycopy(fp.ypoints, 0, xy[1], 0, fp.npoints);
            }
        }

        return xy;
    }
}
