/*
 * Copyright (C) 2017 Arnold Fertin
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
package volume_registration;

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
