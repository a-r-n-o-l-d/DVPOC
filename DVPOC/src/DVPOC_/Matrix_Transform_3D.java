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

import ij.IJ;
import ij.gui.GenericDialog;
import ij.plugin.PlugIn;
import imagescience.image.Axes;
import imagescience.transform.Transform;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

/**
 *
 * @author Arnold Fertin
 */
public class Matrix_Transform_3D implements PlugIn
{
    private Transform transform;

    private String savePath = "";

    @Override
    public void run(String string)
    {
        transform = new Transform();
        if (!doDialog())
        {
            return;
        }

        final BufferedWriter bw;
        try
        {
            bw = new BufferedWriter(new FileWriter(savePath));
            bw.write(string("", "\t", "\n"));
            bw.close();
        }
        catch (IOException ex)
        {
            IJ.log("pouet");
        }

    }

    private String string(final String prefix,
                          final String delim,
                          final String postfix)
    {

        final StringBuffer sb = new StringBuffer();

        for (int r = 0; r < 4; ++r)
        {
            sb.append(prefix);
            for (int c = 0; c < 4; ++c)
            {
                sb.append("" + transform.get(r, c));
                if (c < 3)
                {
                    sb.append(delim);
                }
            }
            sb.append(postfix);
        }
        return sb.toString();
    }

    private boolean doDialog()
    {
        final GenericDialog gd = new GenericDialog("3D matrix", IJ.getInstance());
        gd.addNumericField("t_x", 0, 12);
        gd.addNumericField("t_y", 0, 12);
        gd.addNumericField("t_z", 0, 12);
        gd.addNumericField("r_x", 0, 12);
        gd.addNumericField("r_y", 0, 12);
        gd.addNumericField("r_z", 0, 12);
        gd.addNumericField("s_xy", 0, 12);
        gd.addStringField("Save_path", "");
        gd.showDialog();
        if (gd.wasCanceled())
        {
            return false;
        }
        double v;

        v = gd.getNextNumber();
        transform.translate(v, Axes.X);
        v = gd.getNextNumber();
        transform.translate(v, Axes.Y);
        v = gd.getNextNumber();
        transform.translate(v, Axes.Z);
        v = gd.getNextNumber();
        transform.rotate(v, Axes.X);
        v = gd.getNextNumber();
        transform.rotate(v, Axes.Y);
        v = gd.getNextNumber();
        transform.rotate(v, Axes.Z);

        v = gd.getNextNumber();
        transform.shear(v, Axes.X, Axes.Y);

        savePath = gd.getNextString();

        return true;
    }

}
