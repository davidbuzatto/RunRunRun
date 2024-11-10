package runrunrun;

import br.com.davidbuzatto.jsge.core.Camera2D;
import br.com.davidbuzatto.jsge.core.engine.EngineFrame;
import br.com.davidbuzatto.jsge.image.Image;
import br.com.davidbuzatto.jsge.image.ImageUtils;
import br.com.davidbuzatto.jsge.math.MathUtils;
import br.com.davidbuzatto.jsge.math.Vector2;
import java.awt.Color;
import runrunrun.model.Player;
import runrunrun.model.Terrain;

/**
 * RunRunRun!
 * 
 * @author Prof. Dr. David Buzatto
 */
public class GameWorld extends EngineFrame {
    
    private Player player;
    private Terrain[] terrains;
    private Parallax parallax;
    private Camera2D camera;
    private Color bgColor;
    private Color terrainColor;
    private double terrainY;
    
    public static final double GRAVITY = 20;
    
    private int lastReachedTerrain;
    private int terrainQuantity;
    private int end;
    
    private class Parallax {
        
        Image[] images = new Image[]{
            ImageUtils.loadImage( "resources/images/background/1.png" ),
            ImageUtils.loadImage( "resources/images/background/2.png" ),
            ImageUtils.loadImage( "resources/images/background/3.png" ),
            ImageUtils.loadImage( "resources/images/background/4.png" ),
            ImageUtils.loadImage( "resources/images/background/5.png" ),
            ImageUtils.loadImage( "resources/images/background/6.png" ),
            ImageUtils.loadImage( "resources/images/background/7.png" )
        };
        
        double[] vel = { 0.05, 0.1, 0.5, 1, 5, 10, 50 };
        double[] x = new double[vel.length];
        
        void update( double delta ) {
            for ( int i = 0; i < x.length; i++ ) {
                x[i] -= vel[i] * delta;
            }
        }
        
        void draw() {
            for ( int i = 0; i < images.length; i++ ) {
                for ( int j = 0; j < 2; j++ ) {
                    drawImage( images[i], x[i] + j * images[i].getWidth(), getScreenHeight() - images[i].getHeight() );
                }
                if ( x[i] <= -getScreenWidth() ) {
                    x[i] = 0;
                }
            }
        }
        
    }
    
    public GameWorld() {
        super ( 800, 450, "RunRunRun!", 60, false );
    }
    
    @Override
    public void create() {
        
        terrainY = getScreenHeight() - 100;
        prepare();
        bgColor = new Color( 127, 126, 196 );
        
        try {
            camera = new Camera2D( 
                (Vector2) player.pos.clone(), 
                new Vector2( getScreenWidth() / 2, getScreenHeight() / 2 ), 
                0, 1
            );
        } catch ( CloneNotSupportedException exc ) {
        }
        
        setWindowIcon( loadImage( "resources/images/icon.png" ));
        
    }
    
    private void prepare() {
        
        player = new Player( 
            new Vector2( 50, terrainY - 64 ), 
            new Vector2( 64, 64 ),
            BLUE
        );
        
        Terrain.resetIdCount();
        lastReachedTerrain = 0;
        end = -1;
        terrainColor = new Color( 56, 0, 44 );
        createTerrains( 10, terrainY );
        
        parallax = new Parallax();
        
    }
    
    @Override
    public void update( double delta ) {
        
        if ( player.state == Player.State.DYING ) {
            if ( isKeyPressed( KEY_ENTER ) ) {
                prepare();
            }
        }
        
        player.update( delta, this );
        
        for ( int i = 0; i < terrainQuantity; i++ ) {
            terrains[i].update( delta );
        }
        player.resolveCollisionTerrainsAndEnemies( terrains );
        
        if ( lastReachedTerrain != player.getLastReachedTerrain() && lastReachedTerrain > 5 ) {
            addTerrain( terrainY, true );
        }
        lastReachedTerrain = player.getLastReachedTerrain();
        
        if ( player.pos.x > getScreenWidth() / 2 && player.state != Player.State.DYING ) {
            parallax.update( delta );
        }
        
        updateCamera();
        
    }
    
    @Override
    public void draw() {
        
        clearBackground( bgColor );
        parallax.draw();
        
        beginMode2D( camera );
        
        for ( int i = 0; i < terrainQuantity; i++ ) {
            terrains[i].draw( this );
        } 
        
        player.draw( this );
        
        endMode2D();
        //drawFPS( 10, 10 );
    
    }
    
    private void createTerrains( int quantity, double y ) {
        terrains = new Terrain[quantity];
        for ( int i = 0; i < quantity; i++ ) {
            addTerrain( y, i != 0 );
        }
    }
    
    private void addTerrain( double y, boolean createEnemy ) {
        
        int x = 0;
        
        int width = MathUtils.getRandomValue( 3, 6 ) * 64;
        int height = 200;
        int gap = MathUtils.getRandomValue( 1, 3 ) * 64;
        //int gap = 0;
        
        end++;
        int pos = end % terrains.length;
        
        if ( end > 0 ) {
            
            int prevPos = pos-1;
            if ( prevPos < 0 ) {
                prevPos = terrains.length-1;
            }
            
            Terrain lastTerrain = terrains[prevPos];
            x += lastTerrain.pos.x + lastTerrain.dim.x + lastTerrain.gap;
            
        }
        
        terrains[pos] = new Terrain( 
            new Vector2( x, y ), 
            new Vector2( width, height ), 
            gap, terrainColor, createEnemy
        );
        
        if ( terrainQuantity < terrains.length ) {
            terrainQuantity++;
        }
        
    }
    
    private void updateCamera() {
        
        if ( player.pos.x <= getScreenWidth() / 2 ) {
            camera.target.x = getScreenWidth() / 2;
        } else {
            camera.target.x = player.pos.x;
        }
        
        if ( player.pos.y <= getScreenHeight() / 2 ) {
            camera.target.y = getScreenHeight() / 2;
        } else if ( player.pos.y > getScreenHeight() / 2 ) {
            camera.target.y = getScreenHeight() / 2;
        } else {
            camera.target.y = player.pos.y;
        }
        
    }
    
    public static void main( String[] args ) {
        new GameWorld();
    }
    
}
