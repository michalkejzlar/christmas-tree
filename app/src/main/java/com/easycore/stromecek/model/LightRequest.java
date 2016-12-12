package com.easycore.stromecek.model;


import android.graphics.Color;
import android.support.annotation.StringDef;
import com.easycore.stromecek.Config;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Calendar;

public final class LightRequest {

    @Retention(RetentionPolicy.SOURCE)
    @StringDef({TYPE_DEFINED, TYPE_UNDEFINED})
    private @interface ColorType{}
    private static final String TYPE_DEFINED = "defined";
    private static final String TYPE_UNDEFINED = "undefined";

    private final String color;
    private final String colorType;
    private final String createdAt;
    private final String displayedAt;

    private LightRequest(String color, @ColorType String colorType, String createdAt, String displayedAt) {
        this.color = color;
        this.colorType = colorType;
        this.createdAt = createdAt;
        this.displayedAt = displayedAt;
    }

    public String getColor() {
        return color;
    }

    @ColorType
    public String getColorType() {
        return colorType;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public String getDisplayedAt() {
        return displayedAt;
    }

    /**
     * Creates new light request. Add color HEX value of Christmas tree color.
     * @param color Color you wish Christmas tree will light.
     * @param colorType Set TYPE_DEFINED for specific color. Set TYPE_UNDEFINED for mixed colors.
     * @return new Light request for tree
     */
    public static LightRequest create(final String color, @ColorType final String colorType) {

        // in case someone enter invalid HEX value, throw InvalidArgumentException
        int colorInt = Color.parseColor(color);

        final Calendar now = Calendar.getInstance();
        final String createdAt = Config.formatDate(now.getTime());
        now.setTimeInMillis(now.getTimeInMillis() + Config.LIGHT_SIGNAL_DELAY_IN_MILLIS);
        final String displayedAt = Config.formatDate(now.getTime());
        return new LightRequest(color, colorType, createdAt, displayedAt);
    }

    /**
     * Creates new light request with undefined color. When colorType set to undefined, Christmas tree will light randomly.
     * @return new LightRequest
     */
    public static LightRequest createUndefined() {
        final Calendar now = Calendar.getInstance();
        final String createdAt = Config.formatDate(now.getTime());
        now.setTimeInMillis(now.getTimeInMillis() + Config.LIGHT_SIGNAL_DELAY_IN_MILLIS);
        final String displayedAt = Config.formatDate(now.getTime());

        return new LightRequest("#fff", TYPE_UNDEFINED, createdAt, displayedAt);
    }
}
