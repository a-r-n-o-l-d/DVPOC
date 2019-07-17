/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
