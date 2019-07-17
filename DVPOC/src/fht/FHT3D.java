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
package fht;

import volume.Dimensions;
import volume.VolumeFloatZz;

/**
 *
 * @author Arnold Fertin
 */
public class FHT3D
{
    private FHT3D()
    {
    }

    public static void transform(final VolumeFloatZz vol)
    {
        final Dimensions dim = vol.getDimensions();
        if (!powerOf2Size(dim.dimX) || !powerOf2Size(dim.dimY) || !powerOf2Size(dim.dimZ))
        {
            throw new IllegalArgumentException("must be power of two");
        }
        FHT3D(vol.getVoxels(), dim.dimX, dim.dimY, dim.dimZ);
    }

    public static void zeroPaddingInZ(final VolumeFloatZz vol)
    {
        final Dimensions dim = vol.getDimensions();
        if (!powerOf2Size(dim.dimZ))
        {
            final int dimZ = nextPowerOf2(dim.dimZ);
            vol.addSlices(dimZ - dim.dimZ);
        }
    }
    
    private static void FHT3D(float[][] data,
                              int w,
                              int h,
                              int d)
    {
        float[] sw = new float[w / 4];
        float[] cw = new float[w / 4];
        float[] sh = new float[h / 4];
        float[] ch = new float[h / 4];
        makeSinCosTables(w, sw, cw);
        makeSinCosTables(h, sh, ch);
        for (int i = 0; i < d; i++)
        {
            rc2DFHT(data[i], w, h, sw, cw, sh, ch);
        }
        float[] u = new float[d];

        float[] s = new float[d / 4];
        float[] c = new float[d / 4];
        makeSinCosTables(d, s, c);
        for (int k2 = 0; k2 < h; k2++)
        {
            for (int k1 = 0; k1 < w; k1++)
            {
                int ind = k1 + k2 * w;
                for (int k3 = 0; k3 < d; k3++)
                {
                    u[k3] = data[k3][ind];
                }
                dfht3(u, 0, d, s, c);
                for (int k3 = 0; k3 < d; k3++)
                {
                    data[k3][ind] = u[k3];
                }
            }
        }

        //Convert to actual Hartley transform
        float A, B, C, D, E, F, G, H;
        int k1C, k2C, k3C;
        for (int k3 = 0; k3 <= d / 2; k3++)
        {
            k3C = (d - k3) % d;
            for (int k2 = 0; k2 <= h / 2; k2++)
            {
                k2C = (h - k2) % h;
                for (int k1 = 0; k1 <= w / 2; k1++)
                {
                    k1C = (w - k1) % w;
                    A = data[k3][k1 + w * k2C];
                    B = data[k3][k1C + w * k2];
                    C = data[k3C][k1 + w * k2];
                    D = data[k3C][k1C + w * k2C];
                    E = data[k3C][k1 + w * k2C];
                    F = data[k3C][k1C + w * k2];
                    G = data[k3][k1 + w * k2];
                    H = data[k3][k1C + w * k2C];
                    data[k3][k1 + w * k2] = (A + B + C - D) / 2;
                    data[k3C][k1 + w * k2] = (E + F + G - H) / 2;
                    data[k3][k1 + w * k2C] = (G + H + E - F) / 2;
                    data[k3C][k1 + w * k2C] = (C + D + A - B) / 2;
                    data[k3][k1C + w * k2] = (H + G + F - E) / 2;
                    data[k3C][k1C + w * k2] = (D + C + B - A) / 2;
                    data[k3][k1C + w * k2C] = (B + A + D - C) / 2;
                    data[k3C][k1C + w * k2C] = (F + E + H - G) / 2;
                }
            }
        }
        //normalize
        float norm = (float) Math.sqrt(d * h * w);
        for (int k3 = 0; k3 < d; k3++)
        {
            for (int k2 = 0; k2 < h; k2++)
            {
                for (int k1 = 0; k1 < w; k1++)
                {
                    data[k3][k1 + w * k2] /= norm;
                }
            }
        }
    }
    
    private static void makeSinCosTables(int maxN,
                                         float[] s,
                                         float[] c)
    {
        int n = maxN / 4;
        double theta = 0.0;
        double dTheta = 2.0 * Math.PI / maxN;
        for (int i = 0; i < n; i++)
        {
            c[i] = (float) Math.cos(theta);
            s[i] = (float) Math.sin(theta);
            theta += dTheta;
        }
    }

    /**
     * Row-column Fast Hartley Transform
     */
    private static void rc2DFHT(float[] x,
                                int w,
                                int h,
                                float[] sw,
                                float[] cw,
                                float[] sh,
                                float[] ch)
    {
        for (int row = 0; row < h; row++)
        {
            dfht3(x, row * w, w, sw, cw);
        }
        float[] temp = new float[h];
        for (int col = 0; col < w; col++)
        {
            for (int row = 0; row < h; row++)
            {
                temp[row] = x[col + w * row];
            }
            dfht3(temp, 0, h, sh, ch);
            for (int row = 0; row < h; row++)
            {
                x[col + w * row] = temp[row];
            }
        }
    }
    /* An optimized real FHT */

    private static void dfht3(float[] x,
                              int base,
                              int maxN,
                              float[] s,
                              float[] c)
    {
        int i, stage, gpNum, gpIndex, gpSize, numGps, Nlog2;
        int bfNum, numBfs;
        int Ad0, Ad1, Ad2, Ad3, Ad4, CSAd;
        float rt1, rt2, rt3, rt4;

        Nlog2 = log2(maxN);
        BitRevRArr(x, base, Nlog2, maxN);	//bitReverse the input array
        gpSize = 2;     //first & second stages - do radix 4 butterflies once thru
        numGps = maxN / 4;
        for (gpNum = 0; gpNum < numGps; gpNum++)
        {
            Ad1 = gpNum * 4;
            Ad2 = Ad1 + 1;
            Ad3 = Ad1 + gpSize;
            Ad4 = Ad2 + gpSize;
            rt1 = x[base + Ad1] + x[base + Ad2];   // a + b
            rt2 = x[base + Ad1] - x[base + Ad2];   // a - b
            rt3 = x[base + Ad3] + x[base + Ad4];   // c + d
            rt4 = x[base + Ad3] - x[base + Ad4];   // c - d
            x[base + Ad1] = rt1 + rt3;      // a + b + (c + d)
            x[base + Ad2] = rt2 + rt4;      // a - b + (c - d)
            x[base + Ad3] = rt1 - rt3;      // a + b - (c + d)
            x[base + Ad4] = rt2 - rt4;      // a - b - (c - d)
        }
        if (Nlog2 > 2)
        {
            // third + stages computed here
            gpSize = 4;
            numBfs = 2;
            numGps = numGps / 2;
            //IJ.write("FFT: dfht3 "+Nlog2+" "+numGps+" "+numBfs);
            for (stage = 2; stage < Nlog2; stage++)
            {
                for (gpNum = 0; gpNum < numGps; gpNum++)
                {
                    Ad0 = gpNum * gpSize * 2;
                    Ad1 = Ad0;     // 1st butterfly is different from others - no mults needed
                    Ad2 = Ad1 + gpSize;
                    Ad3 = Ad1 + gpSize / 2;
                    Ad4 = Ad3 + gpSize;
                    rt1 = x[base + Ad1];
                    x[base + Ad1] = x[base + Ad1] + x[base + Ad2];
                    x[base + Ad2] = rt1 - x[base + Ad2];
                    rt1 = x[base + Ad3];
                    x[base + Ad3] = x[base + Ad3] + x[base + Ad4];
                    x[base + Ad4] = rt1 - x[base + Ad4];
                    for (bfNum = 1; bfNum < numBfs; bfNum++)
                    {
                        // subsequent BF's dealt with together
                        Ad1 = bfNum + Ad0;
                        Ad2 = Ad1 + gpSize;
                        Ad3 = gpSize - bfNum + Ad0;
                        Ad4 = Ad3 + gpSize;

                        CSAd = bfNum * numGps;
                        rt1 = x[base + Ad2] * c[CSAd] + x[base + Ad4] * s[CSAd];
                        rt2 = x[base + Ad4] * c[CSAd] - x[base + Ad2] * s[CSAd];

                        x[base + Ad2] = x[base + Ad1] - rt1;
                        x[base + Ad1] = x[base + Ad1] + rt1;
                        x[base + Ad4] = x[base + Ad3] + rt2;
                        x[base + Ad3] = x[base + Ad3] - rt2;

                    } /* end bfNum loop */

                } /* end gpNum loop */

                gpSize *= 2;
                numBfs *= 2;
                numGps = numGps / 2;
            } /* end for all stages */

        } /* end if Nlog2 > 2 */

    }

    private static int log2(int x)
    {
        int count = 15;
        while (!btst(x, count))
        {
            count--;
        }
        return count;
    }

    private static boolean btst(int x,
                                int bit)
    {
        //int mask = 1;
        return ((x & (1 << bit)) != 0);
    }

    private static boolean powerOf2Size(int w)
    {
        int i = 2;
        while (i < w)
        {
            i *= 2;
        }
        return i == w;
    }

    private static int nextPowerOf2(final int w)
    {
        int i = 2;
        while (i < w)
        {
            i *= 2;
        }
        return i;
    }
    
    private static void BitRevRArr(float[] x,
                                   int base,
                                   int bitlen,
                                   int maxN)
    {
        int l;
        float[] tempArr = new float[maxN];
        for (int i = 0; i < maxN; i++)
        {
            l = BitRevX(i, bitlen);  //i=1, l=32767, bitlen=15
            tempArr[i] = x[base + l];
        }
        System.arraycopy(tempArr, 0, x, base, maxN);
    }

    private static int BitRevX(int x,
                               int bitlen)
    {
        int temp = 0;
        for (int i = 0; i <= bitlen; i++)
        {
            if ((x & (1 << i)) != 0)
            {
                temp |= (1 << (bitlen - i - 1));
            }
        }
        return temp & 0x0000ffff;
    }

}
