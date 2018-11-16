/*
 * Copyright 2018 Heiko Scherrer
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.openwms.common.units;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.type.StandardBasicTypes;
import org.hibernate.type.Type;
import org.hibernate.usertype.CompositeUserType;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * A PieceUserType.
 * 
 * @author <a href="mailto:scherrer@openwms.org">Heiko Scherrer</a>
 * @deprecated Use UnitUserType instead
 */
@Deprecated
public class PieceUserType implements CompositeUserType {

    /**
     * {@inheritDoc}
     */
    @Override
    public String[] getPropertyNames() {
        return new String[] { "unitType", "amount" };
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Type[] getPropertyTypes() {
        return new Type[] { StandardBasicTypes.STRING, StandardBasicTypes.BIG_DECIMAL };
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object getPropertyValue(Object component, int property) {
        Piece piece = (Piece) component;
        return property == 0 ? piece.getUnitType() : piece.getMagnitude();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setPropertyValue(Object component, int property, Object value) {
        throw new UnsupportedOperationException("Unit types are immutable");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Class returnedClass() {
        return Piece.class;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object x, Object y) {
        if (x == y) {
            return true;
        }
        if (x == null || y == null) {
            return false;
        }
        return x.equals(y);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode(Object x) {
        return x.hashCode();
    }

    /**
     * {@inheritDoc}
     *
     * @throws SQLException
     *             in case of database errors
     */
    @Override
    public Object nullSafeGet(ResultSet resultSet, String[] strings, SessionImplementor sessionImplementor, Object o) throws HibernateException, SQLException {
        String unitType = resultSet.getString(strings[0]);
        if (resultSet.wasNull()) {
            return null;
        }
        int amount = resultSet.getInt(strings[1]);
        return new Piece(amount, PieceUnit.valueOf(unitType));
    }

    /**
     * {@inheritDoc}
     *
     * @throws SQLException
     *             in case of database errors
     */
    @Override
    public void nullSafeSet(PreparedStatement preparedStatement, Object o, int i, SessionImplementor sessionImplementor) throws HibernateException, SQLException {
        if (o == null) {
            preparedStatement.setNull(i, StandardBasicTypes.STRING.sqlType());
            preparedStatement.setNull(i + 1, StandardBasicTypes.BIG_DECIMAL.sqlType());
        } else {
            Piece piece = (Piece) o;
            String unitType = piece.getUnitType().toString();
            preparedStatement.setString(i, unitType);
            preparedStatement.setBigDecimal(i + 1, piece.getMagnitude());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object deepCopy(Object value) {
        return value;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isMutable() {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Serializable disassemble(Object o, SessionImplementor sessionImplementor) throws HibernateException {
        return (Serializable) o;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object assemble(Serializable serializable, SessionImplementor sessionImplementor, Object o) throws HibernateException {
        return o;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object replace(Object o, Object o1, SessionImplementor sessionImplementor, Object o2) throws HibernateException {
        return o;
    }
}