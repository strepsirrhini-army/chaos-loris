/*
 * Copyright 2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.pivotal.strepsirrhini.chaosloris.data;

import org.hibernate.dialect.Dialect;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.internal.util.compare.ComparableComparator;
import org.hibernate.type.AbstractSingleColumnStandardBasicType;
import org.hibernate.type.LiteralType;
import org.hibernate.type.VersionType;
import org.hibernate.type.descriptor.sql.TimestampTypeDescriptor;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.Locale;

// TODO: Remove with Hibernate 5
public final class InstantType extends AbstractSingleColumnStandardBasicType<Instant> implements VersionType<Instant>, LiteralType<Instant> {

    static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S 'Z'", Locale.ENGLISH);

    public InstantType() {
        super(TimestampTypeDescriptor.INSTANCE, InstantJavaDescriptor.INSTANCE);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Comparator<Instant> getComparator() {
        return ComparableComparator.INSTANCE;
    }

    @Override
    public String getName() {
        return Instant.class.getSimpleName();
    }

    @Override
    public Instant next(Instant current, SessionImplementor session) {
        return Instant.now();
    }

    @Override
    public String objectToSQLString(Instant value, Dialect dialect) throws Exception {
        return "{ts '" + FORMATTER.format(ZonedDateTime.ofInstant(value, ZoneId.of("UTC"))) + "'}";
    }

    @Override
    public Instant seed(SessionImplementor session) {
        return Instant.now();
    }

    @Override
    protected boolean registerUnderJavaType() {
        return true;
    }

}
