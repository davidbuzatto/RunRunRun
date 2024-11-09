package runrunrun;

import br.com.davidbuzatto.jsge.animation.frame.ImageAnimationFrame;
import br.com.davidbuzatto.jsge.core.Camera2D;
import br.com.davidbuzatto.jsge.core.engine.EngineFrame;
import br.com.davidbuzatto.jsge.math.MathUtils;
import br.com.davidbuzatto.jsge.math.Vector2;
import java.util.ArrayList;
import java.util.List;
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
    
    public static final double GRAVITY = 20;
    
    private int lastReachedTerrain;
    private int terrainQuantity;
    private int end;
    
    public GameWorld() {
        super ( 800, 450, "RunRunRun!", 60, true );
    }
    
    @Override
    public void create() {
        
        List<ImageAnimationFrame> idleFrames = new ArrayList<>();
        idleFrames.add( new ImageAnimationFrame( loadImage( "resources/images/playerIdle_00000.png" ) ) );
        idleFrames.add( new ImageAnimationFrame( loadImage( "resources/images/playerIdle_00001.png" ) ) );
        idleFrames.add( new ImageAnimationFrame( loadImage( "resources/images/playerIdle_00002.png" ) ) );
        idleFrames.add( new ImageAnimationFrame( loadImage( "resources/images/playerIdle_00003.png" ) ) );
        
        List<ImageAnimationFrame> runFrames = new ArrayList<>();
        runFrames.add( new ImageAnimationFrame( loadImage( "resources/images/playerRun_00000.png" ) ) );
        runFrames.add( new ImageAnimationFrame( loadImage( "resources/images/playerRun_00001.png" ) ) );
        runFrames.add( new ImageAnimationFrame( loadImage( "resources/images/playerRun_00002.png" ) ) );
        runFrames.add( new ImageAnimationFrame( loadImage( "resources/images/playerRun_00003.png" ) ) );
        runFrames.add( new ImageAnimationFrame( loadImage( "resources/images/playerRun_00004.png" ) ) );
        runFrames.add( new ImageAnimationFrame( loadImage( "resources/images/playerRun_00005.png" ) ) );
        
        List<ImageAnimationFrame> jumpFrames = new ArrayList<>();
        jumpFrames.add( new ImageAnimationFrame( loadImage( "resources/images/playerJump_00000.png" ) ) );
        jumpFrames.add( new ImageAnimationFrame( loadImage( "resources/images/playerJump_00001.png" ) ) );
        jumpFrames.add( new ImageAnimationFrame( loadImage( "resources/images/playerJump_00002.png" ) ) );
        jumpFrames.add( new ImageAnimationFrame( loadImage( "resources/images/playerJump_00003.png" ) ) );
        jumpFrames.add( new ImageAnimationFrame( loadImage( "resources/images/playerJump_00004.png" ) ) );
        jumpFrames.add( new ImageAnimationFrame( loadImage( "resources/images/playerJump_00005.png" ) ) );
        jumpFrames.add( new ImageAnimationFrame( loadImage( "resources/images/playerJump_00006.png" ) ) );
        jumpFrames.add( new ImageAnimationFrame( loadImage( "resources/images/playerJump_00007.png" ) ) );
        
        List<ImageAnimationFrame> deathFrames = new ArrayList<>();
        deathFrames.add( new ImageAnimationFrame( loadImage( "resources/images/playerDeath_00000.png" ) ) );
        deathFrames.add( new ImageAnimationFrame( loadImage( "resources/images/playerDeath_00001.png" ) ) );
        deathFrames.add( new ImageAnimationFrame( loadImage( "resources/images/playerDeath_00002.png" ) ) );
        deathFrames.add( new ImageAnimationFrame( loadImage( "resources/images/playerDeath_00003.png" ) ) );
        deathFrames.add( new ImageAnimationFrame( loadImage( "resources/images/playerDeath_00004.png" ) ) );
        deathFrames.add( new ImageAnimationFrame( loadImage( "resources/images/playerDeath_00005.png" ) ) );
        deathFrames.add( new ImageAnimationFrame( loadImage( "resources/images/playerDeath_00006.png" ) ) );
        deathFrames.add( new ImageAnimationFrame( loadImage( "resources/images/playerDeath_00007.png" ) ) );
        
        player = new Player( 
            new Vector2( 50, 50 ), 
            new Vector2( 64, 64 ),
            BLUE,
            idleFrames,
            runFrames,
            jumpFrames,
            deathFrames
        );
        
        end = -1;
        createTerrains( 10 );
        
        try {
            camera = new Camera2D( 
                (Vector2) player.pos.clone(), 
                new Vector2( getScreenWidth() / 2, getScreenHeight() / 2 ), 
                0, 1
            );
        } catch ( CloneNotSupportedException exc ) {
        }
        
    }
    
    @Override
    public void update( double delta ) {
        
        player.update( delta, this );
        
        for ( int i = 0; i < terrainQuantity; i++ ) {
            terrains[i].update( delta );
        }
        player.resolveCollisionTerrain( terrains );
        
        if ( lastReachedTerrain != player.getLastReachedTerrain() && lastReachedTerrain > 1 ) {
            addTerrain();
        }
        lastReachedTerrain = player.getLastReachedTerrain();
        
        updateCamera();
        
    }
    
    @Override
    public void draw() {
        
        clearBackground( SKYBLUE );
        beginMode2D( camera );
        
        for ( int i = 0; i < terrainQuantity; i++ ) {
            terrains[i].draw( this );
        }
        
        player.draw( this );
        
        endMode2D();
        drawFPS( 10, 10 );
    
    }
    
    private void createTerrains( int quantity ) {
        terrains = new Terrain[quantity];
        for ( int i = 0; i < quantity; i++ ) {
            addTerrain();
        }
    }
    
    private void addTerrain() {
        
        int x = 0;
        int y = getScreenHeight() - 150;
        
        int width = MathUtils.getRandomValue( 20, 40 ) * 10;
        int height = 200;
        int gap = MathUtils.getRandomValue( 3, 8 ) * 20;
        
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
            gap, ORANGE
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
