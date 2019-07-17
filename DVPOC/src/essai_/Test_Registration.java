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
package essai_;

import ij.IJ;
import ij.plugin.PlugIn;
import registration.DisplacementField;
import registration.Registration;
import registration.RegistrationData;
import util.UtilIJ;
import volume.Dimensions;
import volume.VolumeByteZz;
import volume.VolumeFloatZz;
import volume.WindowType;

/**
 *
 * @author Arnold Fertin
 */
public class Test_Registration implements PlugIn
{

    @Override
    public void run(String string)
    {
        final VolumeByteZz mask = new VolumeByteZz(UtilIJ.imagePlusByName("Mask"));
        final DisplacementField field = new DisplacementField(mask);
        final VolumeFloatZz v1 = new VolumeFloatZz(UtilIJ.imagePlusByName("t1"));
        final VolumeFloatZz v2 = new VolumeFloatZz(UtilIJ.imagePlusByName("t2"));
        final Dimensions dim1 = new Dimensions(64, 64, 16);
        final Dimensions dim2 = new Dimensions(8, 8, 8);
        final RegistrationData dat = new RegistrationData(v1, v2, WindowType.HANN, dim1, dim2, 0.001);
        final Registration reg = new Registration(dat, field);
        
        final long t1 = System.nanoTime();
        reg.match();
        final long t2 = System.nanoTime() - t1;
        
        final long t3 = System.nanoTime();
        field.print(dat, t2);
        final long t4 = System.nanoTime() - t3;
        
        IJ.log("Temps de calcul " + (t2 * 1e-9) + " secondes");
        IJ.log("Temps d'écriture " + (t4 * 1e-9) + " secondes");
        field.getVolumeU().getImagePlus("U").show();
        field.getVolumeV().getImagePlus("V").show();
        field.getVolumeW().getImagePlus("W").show();
    }
    
}
