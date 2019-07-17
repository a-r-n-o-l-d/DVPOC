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
