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
import ij.plugin.PlugIn;
import volume.VolumeByteZz;

/**
 *
 * @author Arnold Fertin
 */
public class Keep_Largest_Particle_3D implements PlugIn
{

    @Override
    public void run(String string)
    {
        final VolumeByteZz vol = new VolumeByteZz(IJ.getImage());
    }

}
