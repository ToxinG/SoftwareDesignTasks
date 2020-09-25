package ru.toxing.sd.Bridge.io;

public enum Parameter {
    
    FORMAT      ("-f",  true),
    DRAWING_API ("-da", true),
    GRAPH_FILE  ("-gf", false, "test.gf");
    
    public final boolean IS_REQUIRED;
    public final String KEY, DEFAULT;
    
    private Parameter (String key, boolean req) {
        this (key, req, null);
    }
    
    private Parameter (String key, boolean req, String def) {
        ParametersData.PARAM_BY_KEY.put (key, this);
        this.IS_REQUIRED = def == null || req;
        this.DEFAULT = def;
        this.KEY = key;
    }
    
    @Override
    public String toString () {
        return name ().toLowerCase ().replace ('_', ' ');
    }
    
    public String get (ParametersData parameters) {
        String res = parameters.VALUES.get (this.KEY);
        if (res != null) { return res; }
        
        return DEFAULT;
    }
    
}