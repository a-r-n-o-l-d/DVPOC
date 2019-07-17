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
