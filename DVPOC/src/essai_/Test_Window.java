/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package essai_;

import ij.ImagePlus;
import ij.plugin.PlugIn;
import util.UtilIJ;
import volume.VolumeFloatZz;
import volume.Window3D;
import volume.WindowType;

/**
 *
 * @author Arnold Fertin
 */
public class Test_Window implements PlugIn
{
    @Override
    public void run(String string)
    {
        final VolumeFloatZz img = new VolumeFloatZz(UtilIJ.imagePlusByName("test2"));
        Window3D win = new Window3D(img.getDimensions(), WindowType.HANN);
        win.apodize(img);
    }
}
