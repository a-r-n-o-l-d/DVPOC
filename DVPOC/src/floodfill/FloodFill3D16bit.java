/*
 * Copyright (C) 2016 Arnold Fertin
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
package floodfill;

import java.util.Stack;
import volume.Dimensions;
import volume.Point3D;
import volume.VolumeShortZz;

/**
 *
 * @author Arnold Fertin
 */
public class FloodFill3D16bit
{
    private VolumeShortZz stk;

    private Dimensions dim;

    public FloodFill3D16bit(VolumeShortZz stk)
    {
        this.stk = stk;
        dim = stk.getDimensions();
    }

    private class Node
    {
        public int x, y, z;

        Node(final int x,
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
        Stack<Node> lifo;
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
