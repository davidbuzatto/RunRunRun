package runrunrun.model;

import br.com.davidbuzatto.jsge.core.engine.EngineFrame;
import br.com.davidbuzatto.jsge.core.utils.ColorUtils;
import br.com.davidbuzatto.jsge.math.Vector2;
import java.awt.Color;
import java.awt.Paint;

/**
 * Um pedaço de terreno.
 * 
 * @author Prof. Dr. David Buzatto
 */
public class Terrain {
    
    public Vector2 pos;
    public Vector2 dim;
    public int gap;
    public Paint paint;
    
    private static final Color GAP_COLOR = ColorUtils.fade( EngineFrame.LIGHTGRAY, 0.5 );

    public Terrain( Vector2 pos, Vector2 dim, int gap, Paint paint ) {
        this.pos = pos;
        this.dim = dim;
        this.gap = gap;
        this.paint = paint;
    }
    
    public void update( double delta ) {
        
    }
    
    public void draw( EngineFrame e ) {
        e.fillRectangle(pos.x + dim.x, pos.y, gap, dim.y, GAP_COLOR );
        e.fillRectangle( pos.x, pos.y, dim.x, dim.y, paint );
    }
    
    
}
