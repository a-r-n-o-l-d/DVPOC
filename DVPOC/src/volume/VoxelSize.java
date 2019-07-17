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
package volume;

/**
 *
 * @author afertin
 */
public final class VoxelSize
{
    public final double width;

    public final double height;

    public final double depth;

    public final String unit;

    public VoxelSize()
    {
        width = 1;
        height = 1;
        depth = 1;
        unit = "voxel";
    }
    
    public VoxelSize(final double w,
                     final double h,
                     final double d,
                     final String u)
    {
        width = w;
        height = h;
        depth = d;
        unit = u;
    }
    
    public String print()
    {
        final StringBuffer buffer = new StringBuffer();
        buffer.append(width);
        buffer.append('x');
        buffer.append(height);
        buffer.append('x');
        buffer.append(depth);
        buffer.append(' ');
        buffer.append(unit);
        return buffer.toString();
    }
}
