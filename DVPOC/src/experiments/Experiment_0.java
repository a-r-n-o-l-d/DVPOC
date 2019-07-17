/*
 * Copyright (C) 2018 Arnold Fertin
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
package experiments;

import correlation.Displacement;
import correlation.PhaseCorrelation3D;
import ij.IJ;
import ij.plugin.PlugIn;
import volume.Dimensions;
import volume.VolumeFloatZz;
import volume.Window3D;
import volume.WindowType;

/**
 * Translations seulement : exprimées en proportion
 *
 * @author Arnold Fertin
 */
public final class Experiment_0 implements PlugIn
{

    @Override
    public void run(final String string)
    {
        final double pvalue = 1e-3;
        final double freq = 1;
        final int[] sizes =
        {
            8, 16, 32, 64
        };
        final double[] props = SyntheticData.createSeq(0.001d, 0.5d, 0.001d);

        IJ.log(" " + "vol_size" + " " + "window" + " " + "prop_of_displacement" + " " + "error" + " " + "MaxCorr"
               + " " + "GrubbStat" + " " + "p_value" + " " + "spurious");
        int i = 0;
        for (int s : sizes)
        {
            final Dimensions dims = new Dimensions(s, s, s);
            final VolumeFloatZz vol1 = SyntheticData.createSineData(s, freq);
            final PhaseCorrelation3D corr = new PhaseCorrelation3D(dims);

//            for (WindowType w : WindowType.values())
//            {
            final Window3D win = new Window3D(dims, WindowType.HAMMING);

            for (double p : props)
            {
                final double delta = p * s;
                final VolumeFloatZz vol2 = SyntheticData.createSineDataTransform(s, delta, freq);
                final VolumeFloatZz v1 = win.apodize(vol1);
                final VolumeFloatZz v2 = win.apodize(vol2);
                final Displacement tr = corr.correlate(v1, v2, true, pvalue);
                IJ.log("" + i + " " + s + " " + WindowType.HAMMING + " " + p + " "
                       + SyntheticData.computeError(tr, delta) + " "
                       + tr.getMaximumOfCorrelation() + " "
                       + tr.getGrubbsStatistics() + " "
                       + tr.getPValue() + " "
                       + tr.isSpurious());
                i++;
            }
//            }
        }
    }

}
