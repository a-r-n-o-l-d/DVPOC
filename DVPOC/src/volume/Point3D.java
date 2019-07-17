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
 * Faire une méthode reset qui remet au point de départ. Méthode pour fixer le point de départ.
 *
 * @author Arnold Fertin
 */
public final class Point3D
{
    private int x;

    private int y;

    private int z;

    public Point3D()
    {
        x = 0;
        y = 0;
        z = 0;
    }

    public Point3D(final int xPos,
                   final int yPos,
                   final int zPos)
    {
        x = xPos;
        y = yPos;
        z = zPos;
    }

    public Point3D(final Point3D point,
                   final int dx,
                   final int dy,
                   final int dz)
    {
        x = point.x + dx;
        y = point.y + dy;
        z = point.z + dz;
    }

    public Point3D(final Point3D point)
    {
        x = point.x;
        y = point.y;
        z = point.z;
    }

    public int getX()
    {
        return x;
    }

    public void setX(final int xValue)
    {
        x = xValue;
    }

    public int getY()
    {
        return y;
    }

    public void setY(final int yValue)
    {
        y = yValue;
    }

    public int getZ()
    {
        return z;
    }

    public void setZ(final int zValue)
    {
        z = zValue;
    }

    public void incX()
    {
        ++x;
    }

    public void incY()
    {
        ++y;
    }

    public void incZ()
    {
        ++z;
    }

    public void set(final Point3D point)
    {
        x = point.x;
        y = point.y;
        z = point.z;
    }

    public void set(final int xX,
                    final int yY,
                    final int zZ)
    {
        x = xX;
        y = yY;
        z = zZ;
    }

    public boolean isAtPosition(final Point3D point)
    {
        return x == point.x && y == point.y && z == point.z;
    }

    public boolean inRange(final int xmin,
                           final int xmax,
                           final int ymin,
                           final int ymax,
                           final int zmin,
                           final int zmax)
    {
        return (x >= xmin && x < xmax) && (y >= ymin && y < ymax) && (z >= zmin && z < zmax);
    }

    public boolean inRange(final int min,
                           final int max)
    {
        return (x >= min && x < max) && (y >= min && y < max) && (z >= min && z < max);
    }
}
