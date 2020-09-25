package tok;

import vis.TokenVisitor;

public interface Token {

    public void accept(TokenVisitor visitor);
    
}
