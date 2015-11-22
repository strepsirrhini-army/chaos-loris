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

import org.hibernate.type.descriptor.WrapperOptions;
import org.hibernate.type.descriptor.java.AbstractTypeDescriptor;
import org.hibernate.type.descriptor.java.ImmutableMutabilityPlan;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.GregorianCalendar;

// TODO: Remove with Hibernate 5
class InstantJavaDescriptor extends AbstractTypeDescriptor<Instant> {

    /**
     * Singleton access
     */
    static final InstantJavaDescriptor INSTANCE = new InstantJavaDescriptor();

    @SuppressWarnings("unchecked")
    public InstantJavaDescriptor() {
        super( Instant.class, ImmutableMutabilityPlan.INSTANCE );
    }

    @Override
    public String toString(Instant value) {
        return InstantType.FORMATTER.format( ZonedDateTime.ofInstant( value, ZoneId.of( "UTC" ) ) );
    }

    @Override
    public Instant fromString(String string) {
        return (Instant) InstantType.FORMATTER.parse( string );
    }

    @Override
    @SuppressWarnings("unchecked")
    public <X> X unwrap(Instant instant, Class<X> type, WrapperOptions options) {
        if ( instant == null ) {
            return null;
        }

        if ( Instant.class.isAssignableFrom( type ) ) {
            return (X) instant;
        }

        if ( Calendar.class.isAssignableFrom( type ) ) {
            final ZoneId zoneId = ZoneId.ofOffset( "UTC", ZoneOffset.UTC );
            return (X) GregorianCalendar.from( instant.atZone( zoneId ) );
        }

        if ( java.sql.Timestamp.class.isAssignableFrom( type ) ) {
            return (X) Timestamp.from( instant );
        }

        if ( java.sql.Date.class.isAssignableFrom( type ) ) {
            return (X) java.sql.Date.from( instant );
        }

        if ( java.sql.Time.class.isAssignableFrom( type ) ) {
            return (X) java.sql.Time.from( instant );
        }

        if ( java.util.Date.class.isAssignableFrom( type ) ) {
            return (X) java.util.Date.from( instant );
        }

        if ( Long.class.isAssignableFrom( type ) ) {
            return (X) Long.valueOf( instant.toEpochMilli() );
        }

        throw unknownUnwrap( type );
    }

    @Override
    public <X> Instant wrap(X value, WrapperOptions options) {
        if ( value == null ) {
            return null;
        }

        if ( Instant.class.isInstance( value ) ) {
            return (Instant) value;
        }

        if ( Timestamp.class.isInstance( value ) ) {
            final Timestamp ts = (Timestamp) value;
            return ts.toInstant();
        }

        if ( Long.class.isInstance( value ) ) {
            return Instant.ofEpochMilli( (Long) value );
        }

        if ( Calendar.class.isInstance( value ) ) {
            final Calendar calendar = (Calendar) value;
            return ZonedDateTime.ofInstant( calendar.toInstant(), calendar.getTimeZone().toZoneId() ).toInstant();
        }

        if ( java.util.Date.class.isInstance( value ) ) {
            return ( (java.util.Date) value ).toInstant();
        }

        throw unknownWrap( value.getClass() );
    }

}
