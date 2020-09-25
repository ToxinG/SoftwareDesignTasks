package ru.toxing.sd.Bridge.io;

import java.io.IOException;

import ru.toxing.sd.Bridge.graph.Graph;

public interface GraphReader {

    public Graph read(String path) throws IOException;
    
}
