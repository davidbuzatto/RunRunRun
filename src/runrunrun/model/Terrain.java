package runrunrun.model;

import br.com.davidbuzatto.jsge.core.engine.EngineFrame;
import br.com.davidbuzatto.jsge.geom.Rectangle;
import br.com.davidbuzatto.jsge.image.Image;
import br.com.davidbuzatto.jsge.image.ImageUtils;
import br.com.davidbuzatto.jsge.math.Vector2;
import java.awt.Color;
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
    private int columns;
    private int lines;
    
    private static final Image tile0 = ImageUtils.loadImage( "resources/images/tiles/tile0.png" );
    private static final Image tile1 = ImageUtils.loadImage( "resources/images/tiles/tile1.png" );
    private static final Image tile2 = ImageUtils.loadImage( "resources/images/tiles/tile2.png" );
    private static final Image tile3 = ImageUtils.loadImage( "resources/images/tiles/tile3.png" );
    private static final Image tile4 = ImageUtils.loadImage( "resources/images/tiles/tile4.png" );

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
        columns = (int) dim.x / 64;
        lines = (int) dim.y / 64;
    }
    
    public void update( double delta ) {
    }
    
    public void draw( EngineFrame e ) {
        
        e.fillRectangle( pos.x, pos.y, dim.x, dim.y, paint );
        
        for ( int i = 0; i < lines; i++ ) {
            if ( i == 0 ) {
                for ( int j = 0; j < columns; j++ ) {
                    if ( j == 0 ) {
                        e.drawImage( tile0, pos.x + j * 64, pos.y );
                    } else if ( j == columns - 1 ) {
                        e.drawImage( tile2, pos.x + j * 64, pos.y );
                    } else {
                        e.drawImage( tile1, pos.x + j * 64, pos.y );
                    }
                }
            } else {
                for ( int j = 0; j < columns; j++ ) {
                    if ( j == 0 ) {
                        e.drawImage( tile3, pos.x + j * 64, pos.y + i * 64 );
                    } else if ( j == columns - 1 ) {
                        e.drawImage( tile4, pos.x + j * 64, pos.y + i * 64 );
                    }
                }
            }
        }
        
    }
    
    public Rectangle getBB() {
        return bb;
    }

    public void makeReached() {
        this.reached = true;
    }
    
}
