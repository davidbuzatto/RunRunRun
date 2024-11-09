package runrunrun;

import br.com.davidbuzatto.jsge.core.Camera2D;
import br.com.davidbuzatto.jsge.core.engine.EngineFrame;
import br.com.davidbuzatto.jsge.math.MathUtils;
import br.com.davidbuzatto.jsge.math.Vector2;
import runrunrun.model.Player;
import runrunrun.model.Terrain;

/**
 * RunRunRun!!!
 * 
 * @author Prof. Dr. David Buzatto
 */
public class GameWorld extends EngineFrame {
    
    private Player player;
    private Terrain[] terrains;
    private Camera2D camera;
    
    public GameWorld() {
        super ( 800, 450, "RunRunRun!", 60, true );
    }
    
    @Override
    public void create() {
        
        player = new Player( 
            new Vector2( 50, 50 ), 
            new Vector2( 32, 32 ),
            new Vector2( 0, 0 ),
            BLUE
        );
        
        terrains = new Terrain[10];
        createTerrains();
        
        camera = new Camera2D( 
            player.pos, 
            new Vector2( getScreenWidth() / 2, getScreenHeight() / 2 ), 
            0, 1
        );
        
    }
    
    @Override
    public void update( double delta ) {
        
        player.update( delta );
        
        for ( int i = 0; i < terrains.length; i++ ) {
            terrains[i].update( delta );
        }
        
        updateCamera();
        
    }
    
    @Override
    public void draw() {
        
        clearBackground( WHITE );
        beginMode2D( camera );
        
        for ( int i = 0; i < terrains.length; i++ ) {
            terrains[i].draw( this );
        }
        
        player.draw( this );
        
        endMode2D();
        drawFPS( 10, 10 );
    
    }
    
    private void createTerrains() {
        
        int x = 0;
        int y = getScreenHeight() - 150;
        
        Terrain terrainBefore = null;
        
        for ( int i = 0; i < terrains.length; i++ ) {
            
            int width = MathUtils.getRandomValue( 10, 20 ) * 10;
            int height = 200;
            int gap = MathUtils.getRandomValue( 3, 8 ) * 20;
            
            if ( terrainBefore != null ) {
                x += terrainBefore.dim.x + terrainBefore.gap;
            }
            
            terrains[i] = new Terrain( 
                new Vector2( x, y ), 
                new Vector2( width, height ), 
                gap, ORANGE
            );
            
            terrainBefore = terrains[i];
            
        }
        
    }
    
    private void updateCamera() {
        
    }
    
    public static void main( String[] args ) {
        new GameWorld();
    }
    
}
