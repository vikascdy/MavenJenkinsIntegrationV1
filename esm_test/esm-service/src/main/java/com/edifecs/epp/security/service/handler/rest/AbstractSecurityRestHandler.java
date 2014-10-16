package com.edifecs.epp.security.service.handler.rest;

import com.edifecs.epp.isc.core.command.Direction;
import com.edifecs.epp.isc.core.command.RestCommandHandler;
import com.edifecs.epp.isc.core.command.Sorter;
import com.edifecs.epp.isc.json.JsonTypeAdapter;
import com.edifecs.epp.security.remote.SecurityManager;
import com.edifecs.epp.security.service.SecurityContext;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.*;

abstract class AbstractSecurityRestHandler<T extends Serializable> extends
        RestCommandHandler<T> {

    protected final SecurityContext sc;

    public AbstractSecurityRestHandler(SecurityContext sc, Class<T> type) {
        super(JsonTypeAdapter.defaultForClass(type));
        this.sc = sc;
    }

    protected long idFromUrl(String url) {
        try {
            return Long.parseLong(url);
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("Invalid id: " + url);
        }
    }

    protected List<T> sort(Collection<T> items, List<Sorter> sorters) {
        final List<T> list = new ArrayList<>(items);
        if (sorters != null) {
            for (Sorter sorter : sorters) {
                try {
                    final Field field = typeAdapter().typeToken().getRawType().getDeclaredField(
                            sorter.property());
                    Collections.sort(list,
                            new FieldComparator(field, sorter.direction()));
                } catch (NoSuchFieldException ex) {
                    throw new IllegalArgumentException("Cannot sort on the"
                            + " property '" + sorter.property()
                            + "'; it is not" + " defined for this type.", ex);
                }
            }
        }
        return list;
    }

    @Override
    public scala.Option<SecurityManager> getReceivingSecurityManager() {
        return scala.Option.apply((SecurityManager) sc.manager());
    }

    private class FieldComparator implements Comparator<T> {
        private final Field field;
        private final int factor;

        FieldComparator(Field field, Direction direction) {
            this.field = field;
            field.setAccessible(true);
            this.factor = (direction.asc()) ? 1 : -1;
        }

        @SuppressWarnings({"rawtypes", "unchecked"})
        @Override
        public int compare(T a, T b) {
            try {
                final Comparable ca = (Comparable) field.get(a);
                final Comparable cb = (Comparable) field.get(b);
                if (null == ca || null == cb)
                    return -1;
                return ca.compareTo(cb) * factor;
            } catch (IllegalAccessException ex) {
                throw new RuntimeException(ex);
            } catch (ClassCastException ex) {
                throw new IllegalArgumentException(
                        "Cannot sort on the property" + " '" + field.getName()
                                + "'; it is not comparable.", ex);
            }
        }
    }
}
