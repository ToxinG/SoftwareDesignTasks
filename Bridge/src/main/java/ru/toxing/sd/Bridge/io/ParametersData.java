package ru.toxing.sd.Bridge.io;

import static ru.shemplo.snowball.utils.fp.StreamUtils.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;
import java.util.StringTokenizer;
import java.util.stream.Collectors;

import java.io.IOException;

public class ParametersData {
    
    public static ParametersData parse (String input) throws IOException {
        return new ParametersData (input);
    }
    
    public static ParametersData parse (String ... tokens) throws IOException {
        return new ParametersData (tokens);
    }
    
    static final Map <String, Parameter> 
        PARAM_BY_KEY = new HashMap <> ();
    static { Parameter.values (); }
    
    final Map <String, String> VALUES = new HashMap <> ();
    
    private ParametersData (String input) throws IOException {
        StringTokenizer st = new StringTokenizer (input);
        List <String> tokens = whilst (p -> p.hasMoreTokens (), f -> f.nextToken (), st)
                             . collect (Collectors.toList ());
        this._initByTokens (tokens);
    }
    
    private ParametersData (String ... tokens) throws IOException {
        this._initByTokens (Arrays.asList (tokens));
    }
    
    private final void _initByTokens (List <String> tokens) {
        StringJoiner sj = new StringJoiner (" ");
        Parameter current = null;
        
        for (int i = 0; i < tokens.size (); i++) {
            String token = tokens.get (i);
            if (token.charAt (0) == '-') {
                if (!PARAM_BY_KEY.containsKey (token) && i + 1 < tokens.size ()) {
                    do {
                        token = tokens.get (i + 1);
                        if (token.charAt (0) == '-') {
                            break;
                        }
                        
                        i += 1;
                    } while (i + 1 < tokens.size ());
                    
                    continue;
                }
                
                if (current != null && sj.length () > 0) {
                    VALUES.put (current.KEY, sj.toString ());
                }
                
                current = PARAM_BY_KEY.get (token);
                sj = new StringJoiner (" ");
            } else { sj.add (token); }
        }
        
        if (current != null && sj.length () > 0) {
            VALUES.put (current.KEY, sj.toString ());
        }
    }
    
    @Override
    public String toString () {
        return VALUES.toString ();
    }
    
    public int getSize () {
        return VALUES.size ();
    }
    
    public String getValue (Parameter parameter) {
        if (parameter == null) { return ""; }
        return parameter.get (this);
    }
    
}
