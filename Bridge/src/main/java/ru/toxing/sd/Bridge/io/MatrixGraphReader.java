package ru.toxing.sd.Bridge.io;

import java.util.List;

import ru.toxing.sd.Bridge.graph.Graph;

public class MatrixGraphReader extends AbsGraphReader {

    private int row = 1;
    
    @Override
    public void readUnknownLine (Graph graph, List <String> values) {
        for (int i = 0; i < values.size (); i++) {
            if ("1".equals (values.get (i)) && i + 1 != row) {
                graph.addEdge (row, i + 1);
            }
        }
        
        row += 1;
    }

}
