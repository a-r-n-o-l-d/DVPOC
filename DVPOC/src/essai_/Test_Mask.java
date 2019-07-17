/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package essai_;

import ij.plugin.PlugIn;
import registration.DisplacementField;
import util.UtilIJ;
import volume.VolumeByteZz;

/**
 *
 * @author Arnold Fertin
 */
public class Test_Mask implements PlugIn
{

    @Override
    public void run(String string)
    {
        final VolumeByteZz mask = new VolumeByteZz(UtilIJ.imagePlusByName("Mask"));
        DisplacementField field = new DisplacementField(mask);
        
    }
    
}
