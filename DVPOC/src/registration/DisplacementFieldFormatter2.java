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
package registration;

import java.text.DecimalFormat;

/**
 *
 * @author Arnold Fertin
 */
public final class DisplacementFieldFormatter2
{
    private final DecimalFormat formatter;

    public DisplacementFieldFormatter2(final DisplacementField field)
    {
        // Formatage coordonnées points
        formatter = new DecimalFormat("+0.#########E0;-#");
    }

    public String formatTranslation(final double uvw)
    {
        return formatter.format(uvw);
    }

    public String formatCoordinate(final double coord)
    {
        return formatter.format(coord);
    }

    public String formatMaxCorr(final double value)
    {
        return formatter.format(value);
    }

    public String formatGrubbs(final double value)
    {
        return formatter.format(value);
    }

    public String formatPvalue(final double value)
    {
        return formatter.format(value);
    }
}
