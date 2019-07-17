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
package registration;

import ij.ImagePlus;
import ij.io.FileInfo;
import volume.Dimensions;
import volume.VolumeFloatZz;
import volume.WindowType;

/**
 *
 * @author Arnold Fertin
 */
public final class RegistrationData
{
    public final WindowType windowType;

    public final Dimensions windowDimStart;

    public final Dimensions windowDimEnd;

    public final VolumeFloatZz volume1;

    public final VolumeFloatZz volume2;
    
    public final double pValueLevel;

    public RegistrationData(final VolumeFloatZz vol1,
                            final VolumeFloatZz vol2,
                            final WindowType winType,
                            final Dimensions winStart,
                            final Dimensions winEnd,
                            final double significance)
    {
        volume1 = vol1;
        volume2 = vol2;
        windowType = winType;
        windowDimStart = winStart;
        windowDimEnd = winEnd;
        pValueLevel = significance;
    }

    public String summary()
    {
        final StringBuffer buffer = new StringBuffer();
        final ImagePlus img1 = volume1.getImagePlus();
        buffer.append("# Volume 1: ");
        final FileInfo info1 = img1.getOriginalFileInfo();
        if (info1 != null)
        {
            buffer.append(info1.directory);
        }
        buffer.append(img1.getTitle());
        buffer.append('\n');
        final ImagePlus img2 = volume2.getImagePlus();
        buffer.append("# Volume 2: ");
        final FileInfo info2 = img2.getOriginalFileInfo();
        if (info2 != null)
        {
            buffer.append(info2.directory);
        }
        buffer.append(img2.getTitle());
        buffer.append('\n');
        buffer.append("# Voxel size:");
        buffer.append(volume1.getVoxelDimensions().print());
        buffer.append('\n');
        buffer.append("# Window apodization: ");
        buffer.append(windowType);
        buffer.append('\n');
        buffer.append("# Window starting size: ");
        buffer.append(windowDimStart.toString());
        buffer.append('\n');
        buffer.append("# Window ending size: ");
        buffer.append(windowDimEnd.toString());
        buffer.append('\n');
        buffer.append("# Grubbs test, p-value level: ");
        buffer.append(pValueLevel);
//        buffer.append("\n#\n");
        buffer.append('\n');
        return buffer.toString();
    }
}
