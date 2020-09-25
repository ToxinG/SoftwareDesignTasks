package ru.toxing.sd.Bridge;

import ru.toxing.sd.Bridge.gfx.AWTGraphRender;
import ru.toxing.sd.Bridge.gfx.GraphRender;
import ru.toxing.sd.Bridge.gfx.JavaFXGraphRender;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public enum RenderType {

    AWT    (() -> new AWTGraphRender(800, 600)),
    JAVAFX (() -> new JavaFXGraphRender(800, 600));

    private static final Map <String, RenderType> MATCHES = new HashMap <> ();

    public static RenderType matchOrDeafault (String input, RenderType type) {
        if (input == null || input.length () == 0) {
            return type;
        }

        if (MATCHES.isEmpty ()) {
            for (RenderType __ : RenderType.values ()) {
                MATCHES.put (__.name ().toLowerCase (), __);
            }
        }

        RenderType match = MATCHES.get (input.toLowerCase ().replace (' ', '_'));
        if (match != null) { return match; }

        return type;
    }

    private final Supplier <GraphRender> SUPPLIER;

    private RenderType (Supplier <GraphRender> supplier) {
        this.SUPPLIER = supplier;
    }

    @Override
    public String toString () {
        return name ().toLowerCase ().replace ('_', ' ');
    }

    public GraphRender getInstance () {
        return SUPPLIER.get ();
    }

}