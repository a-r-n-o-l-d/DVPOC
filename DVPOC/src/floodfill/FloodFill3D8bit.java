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
package floodfill;

import java.util.Stack;
import volume.Dimensions;
import volume.Point3D;
import volume.VolumeByteZz;

/**
 *
 * @author Arnold Fertin
 */
public class FloodFill3D8bit
{
    private VolumeByteZz stk;

    private Dimensions dim;

    public FloodFill3D8bit(VolumeByteZz stk)
    {
        this.stk = stk;
        dim = stk.getDimensions();
    }

    private class Node
    {
        public int x, y, z;

        public Node(final int x,
                    final int y,
                    final int z)
        {
            this.x = x;
            this.y = y;
            this.z = z;
        }

        public int getValue()
        {
            if (x >= 0 && x < dim.dimX && y >= 0 && y < dim.dimY && z >= 0 && z < dim.dimZ)
            {
                return stk.getVoxel(x, y, z);
            }
            else
            {
                return -1;
            }
        }

        public void setValue(final int value)
        {
            if (x >= 0 && x < dim.dimX && y >= 0 && y < dim.dimY && z >= 0 && z < dim.dimZ)
            {
                stk.setVoxel(x, y, z, value);
            }
        }
    }

    public int floodFill3D(final Point3D point,
                           final int target,
                           final int replacement,
                           final int connexityRule)
    {
        if (target == replacement || !dim.checkPosition(point))
        {
            return 0;
        }
        int count = 0;
//        Node node;
        Stack<Node> lifo;

//        node = new Node(point.getX(), point.getY(), point.getZ());
        lifo = new Stack();
        lifo.push(new Node(point.getX(), point.getY(), point.getZ()));
        while (!lifo.isEmpty())
        {
            final Node current = lifo.pop();
            if (current.getValue() == target)
            {
                current.setValue(replacement);
                count++;
                lifo.push(new Node(current.x - 1, current.y, current.z)); // par défaut connexité 6
                lifo.push(new Node(current.x + 1, current.y, current.z));
                lifo.push(new Node(current.x, current.y - 1, current.z));
                lifo.push(new Node(current.x, current.y + 1, current.z));
                lifo.push(new Node(current.x, current.y, current.z - 1));
                lifo.push(new Node(current.x, current.y, current.z + 1));
                if (connexityRule >= 18)
                { //12 autres voisins, sans les coins
                    lifo.push(new Node(current.x - 1, current.y, current.z + 1));
                    lifo.push(new Node(current.x - 1, current.y, current.z - 1));
                    lifo.push(new Node(current.x - 1, current.y - 1, current.z));
                    lifo.push(new Node(current.x - 1, current.y + 1, current.z));
                    lifo.push(new Node(current.x + 1, current.y, current.z + 1));
                    lifo.push(new Node(current.x + 1, current.y, current.z - 1));
                    lifo.push(new Node(current.x + 1, current.y - 1, current.z));
                    lifo.push(new Node(current.x + 1, current.y + 1, current.z));
                    lifo.push(new Node(current.x, current.y - 1, current.z + 1));
                    lifo.push(new Node(current.x, current.y - 1, current.z - 1));
                    lifo.push(new Node(current.x, current.y + 1, current.z + 1));
                    lifo.push(new Node(current.x, current.y + 1, current.z - 1));
                    if (connexityRule == 26)
                    { //tous les voisins, on rajoute les 8 coins
                        lifo.push(new Node(current.x - 1, current.y - 1, current.z + 1));
                        lifo.push(new Node(current.x - 1, current.y + 1, current.z + 1));
                        lifo.push(new Node(current.x + 1, current.y - 1, current.z + 1));
                        lifo.push(new Node(current.x + 1, current.y + 1, current.z + 1));
                        lifo.push(new Node(current.x - 1, current.y - 1, current.z - 1));
                        lifo.push(new Node(current.x - 1, current.y + 1, current.z - 1));
                        lifo.push(new Node(current.x + 1, current.y - 1, current.z - 1));
                        lifo.push(new Node(current.x + 1, current.y + 1, current.z - 1));
                    }
                }
            }
        }
        return count;
    }
}
