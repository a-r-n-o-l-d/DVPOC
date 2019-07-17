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


import fht.SwapQuadrants3D;
import ij.IJ;
import ij.ImagePlus;
import ij.plugin.filter.PlugInFilter;
import static ij.plugin.filter.PlugInFilter.DOES_32;
import ij.process.ImageProcessor;
import volume.VolumeFloatZz;

/**
 *
 * @author Arnold Fertin
 */
public class Swap_Quadrants_3D implements PlugInFilter
{
    private ImagePlus imp;
    
    @Override
    public void run(ImageProcessor ip)
    {
        final VolumeFloatZz vol = new VolumeFloatZz(imp);
        final SwapQuadrants3D sw = new SwapQuadrants3D(vol.getDimensions());
        sw.swap(vol);
    }

    @Override
    public int setup(String string,
                     ImagePlus ip)
    {
        IJ.register(Swap_Quadrants_3D.class);
        this.imp = ip;
        return DOES_32;
    }
}
