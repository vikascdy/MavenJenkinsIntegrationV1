package com.edifecs.xboard.portal;

import java.io.IOException;
import java.lang.reflect.Type;

import com.edifecs.epp.isc.json.JsonTypeAdapter;
import com.edifecs.epp.isc.json.Schema;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

public class AppBarSerializer extends JsonTypeAdapter<AppBar> {

    static final String
        ID = "id",
        TYPE = "type",
        WEIGHT="weight",
        TEXT = "text",
        ICON = "icon",
        LINK_URL = "linkUrl",
        HIDDEN = "hidden",
        DESCRIPTION = "description",
        SUB_MENU = "subMenu",
        NAVIGATION = "navigation";

    public static final Schema SCHEMA =
        Schema.Object().withRequiredProperty(NAVIGATION, Schema.Array(Schema.Object()
            .withRequiredProperty(ID, Schema.String())
            .withProperty(TYPE, Schema.String())
            .withProperty(TEXT, Schema.String())
            .withProperty(ICON, Schema.String())
            .withProperty(WEIGHT, Schema.Number())
            .withProperty(LINK_URL, Schema.String())
            .withRequiredProperty(HIDDEN, Schema.Boolean())
            .withProperty(SUB_MENU, Schema.Array(Schema.Object()
                .withRequiredProperty(TEXT, Schema.String())
                .withProperty(LINK_URL, Schema.String())
                .withProperty(WEIGHT, Schema.Number())
                .withProperty(DESCRIPTION, Schema.String())))))
        .withNoAdditionalProperties();

    @Override
    public TypeToken<AppBar> typeToken() {
        return TypeToken.get(AppBar.class);
    }

    @Override
    public void write(Gson gson, JsonWriter out, AppBar appBar) throws IOException {
        out.beginObject().name(NAVIGATION).beginArray();
        for (AppBarButton button : appBar.getButtons()) {
            out.beginObject().name(ID).value(button.getId());
            if (button.getType() != null)
                out.name(TYPE).value(button.getType());
            if (button.getText() != null)
                out.name(TEXT).value(button.getText());
            if(button.getWeight() != 0)
                out.name(WEIGHT).value(button.getWeight());
            if (button.getIconUrl() != null)
                out.name(ICON).value(button.getIconUrl());
            if (button.getLinkUrl() != null)
                out.name(LINK_URL).value(button.getLinkUrl());
            out.name(HIDDEN).value(button.isHidden());
            if (button.hasSubMenu()) {
                out.name(SUB_MENU).beginArray();
                for (AppBarMenuEntry entry : button.getSubMenu()) {
                    out.beginObject().name(TEXT).value(entry.getText());
                    final String url = entry.getLinkUrl();
                    if (url != null) out.name(LINK_URL).value(url);
                    final String desc = entry.getDescription();
                    final int weight=entry.getWeight();
                    if(weight != 0) out.name(WEIGHT).value(weight);
                    if (desc != null) out.name(DESCRIPTION).value(desc);
                    out.endObject();
                }
                out.endArray();
            }
            out.endObject();
        }
        out.endArray().endObject();
    }

    @Override
    public AppBar read(Gson gson, JsonReader in) {
        throw new UnsupportedOperationException("Cannot deserialize an AppBar.");
    }

    @Override
    public Schema getSchema(Type t) {
        return SCHEMA;
    }
}
