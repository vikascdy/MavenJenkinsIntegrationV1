package com.edifecs.xboard.portal;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Collection;

import com.edifecs.epp.isc.json.JsonTypeAdapter;
import com.edifecs.epp.isc.json.Schema;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

public class MenuSerializer extends JsonTypeAdapter<DoormatMenu> {

    static final String
        ID               = "id",
        ACTIVE           = "active",
        NAME             = "name",
        TYPE             = "type",
        TASK_HEADING     = "taskheading",
        DEFAULT_LINK_URL = "defaultLinkUrl",
        ICON             = "icon",
        MENU             = "menu",
        COLUMN_ONE       = "columnOne",
        COLUMN_TWO       = "columnTwo",
        WEIGHT           = "weight",
        TASKS            = "tasks";

    public static final Schema SCHEMA =
        Schema.Object()
            .withRequiredProperty(ID, Schema.String())
            .withRequiredProperty(ACTIVE, Schema.Boolean())
            .withRequiredProperty(NAME, Schema.String())
            .withRequiredProperty(TYPE, Schema.String())
            .withRequiredProperty(TASK_HEADING, Schema.String())
            .withRequiredProperty(DEFAULT_LINK_URL, Schema.String())
            .withRequiredProperty(ICON, Schema.String())
            .withRequiredProperty(MENU, Schema.Array(Schema.AnyOf(
                Schema.Object()
                    .withRequiredProperty(COLUMN_ONE, Schema.Array(MenuEntrySerializer.SCHEMA)),
                Schema.Object()
                    .withRequiredProperty(COLUMN_TWO, Schema.Array(MenuEntrySerializer.SCHEMA)))))
            .withProperty(TASKS, Schema.Array(MenuEntrySerializer.SCHEMA));

    private static final MenuEntrySerializer entrySerializer = new MenuEntrySerializer();

    @Override
    public TypeToken<DoormatMenu> typeToken() {
        return TypeToken.get(DoormatMenu.class);
    }

    @Override
    public void write(Gson gson, JsonWriter out, DoormatMenu menu) throws IOException {

        final Collection<DoormatMenuEntry>
                column1 = menu.getEntries(DoormatMenu.Section.COLUMN1),
                column2 = menu.getEntries(DoormatMenu.Section.COLUMN2),
                tasks   = menu.getEntries(DoormatMenu.Section.TASKS);

        out.beginObject()
            .name(ID).value(menu.getId())
            .name(NAME).value(menu.getName())
            .name(ACTIVE).value(menu.isActive())
            .name(TYPE).value(menu.getType())
            .name(TASK_HEADING).value(menu.getTaskHeading())
            .name(DEFAULT_LINK_URL).value(menu.getDefaultLinkUrl())
            .name(ICON).value(menu.getIconUrl())
            .name(WEIGHT).value(menu.getWeight())
            .name(MENU);

        out.beginArray();
        if (!column1.isEmpty()) {
            out.beginObject().name(COLUMN_ONE);
            writeEntries(column1, gson, out);
            out.endObject();
        }
        if (!column2.isEmpty()) {
            out.beginObject().name(COLUMN_TWO);
            writeEntries(column2, gson, out);
            out.endObject();
        }
        out.endArray();

        if (!tasks.isEmpty()) {
            out.name(TASKS);
            writeEntries(tasks, gson, out);
        }
        out.endObject();
    }

    private void writeEntries(Iterable<DoormatMenuEntry> entries, Gson gson,
            JsonWriter out) throws IOException {
        String lastNamespace = null;
        out.beginArray();
        for (DoormatMenuEntry entry : entries) {
            String namespace = entry.getNamespace();
            if (lastNamespace != null && !lastNamespace.equals(namespace))
                out.beginObject().name(MenuEntrySerializer.TEXT).value("").endObject();
            lastNamespace = namespace;
            entrySerializer.write(gson, out, entry);
        }
        out.endArray();
    }

    @Override
    public DoormatMenu read(Gson gson, JsonReader in) throws IOException {
        throw new UnsupportedOperationException("Cannot deserialize a DoormatMenu.");
    }

    @Override
    public Schema getSchema(Type t) {
        return SCHEMA;
    }
}
