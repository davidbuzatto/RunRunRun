package runrunrun.model;

import br.com.davidbuzatto.jsge.core.engine.EngineFrame;
import br.com.davidbuzatto.jsge.geom.Rectangle;
import br.com.davidbuzatto.jsge.math.Vector2;
import java.awt.Paint;

/**
 * Um pedaço de terreno.
 * 
 * @author Prof. Dr. David Buzatto
 */
public class Terrain {
    
    private static int idCount = 0;
    
    public int id;
    public Vector2 pos;
    public Vector2 dim;
    public int gap;
    public Paint paint;
    
    private boolean reached;
    private Rectangle bb;

    public Terrain( Vector2 pos, Vector2 dim, int gap, Paint paint ) {
        this.id = idCount++;
        this.pos = pos;
        this.dim = dim;
        this.gap = gap;
        this.paint = paint;
        bb = new Rectangle( 0, 0, 0, 0 );
        bb.x = pos.x;
        bb.y = pos.y;
        bb.width = dim.x;
        bb.height = dim.y;
    }
    
    public void update( double delta ) {
    }
    
    public void draw( EngineFrame e ) {
        e.fillRectangle( pos.x, pos.y, dim.x, dim.y, reached ? EngineFrame.LIME : paint );
    }
    
    public Rectangle getBB() {
        return bb;
    }

    public void makeReached() {
        this.reached = true;
    }
    
}
