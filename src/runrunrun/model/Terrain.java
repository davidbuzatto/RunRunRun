package runrunrun.model;

import br.com.davidbuzatto.jsge.core.engine.EngineFrame;
import br.com.davidbuzatto.jsge.geom.Rectangle;
import br.com.davidbuzatto.jsge.image.Image;
import br.com.davidbuzatto.jsge.image.ImageUtils;
import br.com.davidbuzatto.jsge.math.Vector2;
import java.awt.Paint;
import runrunrun.GameWorld;

/**
 * Um pedaço de terreno.
 * 
 * @author Prof. Dr. David Buzatto
 */
public class Terrain {
    
    private static int idCount = 0;
    
    private int id;
    private Vector2 pos;
    private Vector2 dim;
    private int gap;
    private Paint paint;
    
    private Rectangle bb;
    private int columns;
    private int lines;
    
    private static final Image tile0 = ImageUtils.loadImage( "resources/images/tiles/grass/tile0.png" );
    private static final Image tile1 = ImageUtils.loadImage( "resources/images/tiles/grass/tile1.png" );
    private static final Image tile2 = ImageUtils.loadImage( "resources/images/tiles/grass/tile2.png" );
    private static final Image tile3 = ImageUtils.loadImage( "resources/images/tiles/grass/tile3.png" );
    private static final Image tile4 = ImageUtils.loadImage( "resources/images/tiles/grass/tile4.png" );
    private static final Image tile5 = ImageUtils.loadImage( "resources/images/tiles/grass/tile5.png" );
    
    private Enemy enemy;

    public Terrain( Vector2 pos, Vector2 dim, int gap, Paint paint, boolean createEnemy ) {
        this.id = idCount++;
        this.pos = pos;
        this.dim = dim;
        this.gap = gap;
        this.paint = paint;
        this.bb = new Rectangle( 0, 0, 0, 0 );
        this.bb.x = pos.x;
        this.bb.y = pos.y;
        this.bb.width = dim.x;
        this.bb.height = dim.y;
        this.columns = (int) dim.x / 64;
        this.lines = (int) dim.y / 64;
        if ( createEnemy && !GameWorld.TEST ) {
            this.enemy = new Enemy( 
                new Vector2( pos.x, pos.y - 64 ),
                new Vector2( 64, 64 ),
                EngineFrame.WHITE
            );
        }
    }
    
    public void update( double delta ) {
        if ( enemy != null ) {
            enemy.update( delta );
            enemy.resolveTerrainInteraction( this );
        }
    }
    
    public void draw( EngineFrame e ) {
        
        //e.fillRectangle( pos.x, pos.y, dim.x, dim.y, paint );
        
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
                        e.drawImage( tile5, pos.x + j * 64, pos.y + i * 64 );
                    } else {
                        e.drawImage( tile4, pos.x + j * 64, pos.y + i * 64 );
                    }
                }
            }
        }
        
        if ( enemy != null ) {
            enemy.draw( e );
        }
        
    }
    
    public Rectangle getBB() {
        return bb;
    }
    
    public static void resetIdCount() {
        idCount = 0;
    }

    public Vector2 getPos() {
        return pos;
    }

    public Vector2 getDim() {
        return dim;
    }

    public Enemy getEnemy() {
        return enemy;
    }

    public int getId() {
        return id;
    }

    public int getGap() {
        return gap;
    }
    
}
