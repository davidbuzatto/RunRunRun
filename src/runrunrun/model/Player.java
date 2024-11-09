package runrunrun.model;

import br.com.davidbuzatto.jsge.core.engine.EngineFrame;
import br.com.davidbuzatto.jsge.math.Vector2;
import java.awt.Paint;

/**
 * Jogador.
 * 
 * @author Prof. Dr. David Buzatto
 */
public class Player {
    
    public Vector2 pos;
    public Vector2 dim;
    public Vector2 vel;
    public Paint paint;

    public Player( Vector2 pos, Vector2 dim, Vector2 vel, Paint paint ) {
        this.pos = pos;
        this.dim = dim;
        this.vel = vel;
        this.paint = paint;
    }
    
    public void update( double delta ) {
        
        pos.x += vel.x * delta;
        pos.y += vel.y * delta;
        
    }
    
    public void draw( EngineFrame e ) {
        e.fillRectangle( pos.x, pos.y, dim.x, dim.y, paint );
    }
    
}
