package vis;

import tok.BraceToken;
import tok.NumberToken;
import tok.OpToken;

public interface TokenVisitor {

    public void visit(BraceToken token);
    
    public void visit(NumberToken token);
    
    public void visit(OpToken token);
    
    
}
