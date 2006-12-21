/*
 *
 * Enginuity Open-Source Tuning, Logging and Reflashing
 * Copyright (C) 2006 Enginuity.org
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 */

package enginuity.logger.definition;

import static enginuity.util.ByteUtil.asInt;
import static enginuity.util.JEPUtil.evaluate;
import static enginuity.util.ParamChecker.checkNotNullOrEmpty;

import java.text.DecimalFormat;

public final class EcuParameterConvertorImpl implements EcuDataConvertor {
    private final String units;
    private final String expression;
    private final DecimalFormat format;

    public EcuParameterConvertorImpl(String units, String expression, String format) {
        checkNotNullOrEmpty(units, "units");
        checkNotNullOrEmpty(expression, "expression");
        checkNotNullOrEmpty(format, "format");
        this.units = units;
        this.expression = expression;
        this.format = new DecimalFormat(format);
    }

    public double convert(byte[] bytes) {
        double value = (double) asInt(bytes);
        double result = evaluate(expression, value);
        return Double.isNaN(result) || Double.isInfinite(result) ? 0.0 : result;
    }

    public String getUnits() {
        return units;
    }

    public String format(double value) {
        return format.format(value);
    }

    public String toString() {
        return getUnits();
    }

}
