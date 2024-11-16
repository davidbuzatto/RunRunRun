package runrunrun;

import br.com.davidbuzatto.jsge.core.Camera2D;
import br.com.davidbuzatto.jsge.core.engine.EngineFrame;
import br.com.davidbuzatto.jsge.core.utils.ColorUtils;
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
    
    public static final double GRAVITY = 20;
    
    private Player player;
    private Terrain[] terrains;
    private Parallax parallax;
    private Camera2D camera;
    private Color bgColor;
    private Color terrainColor;
    private double terrainY;
    
    private int lastReachedTerrain;
    private int terrainQuantity;
    private int end;
    
    public static final boolean TEST = false;
    
    private class Parallax {
        
        Image[] images;
        double[] runVel;
        double[] walkVel;
        double[] x;

        public Parallax() {
            images = new Image[]{
                ImageUtils.loadImage( "resources/images/background/1.png" ),
                ImageUtils.loadImage( "resources/images/background/2.png" ),
                ImageUtils.loadImage( "resources/images/background/3.png" ),
                ImageUtils.loadImage( "resources/images/background/4.png" ),
                ImageUtils.loadImage( "resources/images/background/5.png" ),
                ImageUtils.loadImage( "resources/images/background/6.png" ),
                ImageUtils.loadImage( "resources/images/background/7.png" )
            };

            runVel = new double[]{ 0.05, 0.1, 0.5, 1, 5, 10, 50 };
            walkVel = new double[runVel.length];
            for ( int i = 0; i < runVel.length; i++ ) {
                walkVel[i] = runVel[i]/2;
            }
            x = new double[runVel.length];
            
        }
        
        void update( double delta, boolean playerRunning ) {
            for ( int i = 0; i < x.length; i++ ) {
                if ( playerRunning ) {
                    x[i] -= runVel[i] * delta;
                } else {
                    x[i] -= walkVel[i] * delta;
                }
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
        
        setDefaultFontSize( 20 );
        terrainY = getScreenHeight() - 100;
        bgColor = new Color( 127, 126, 196 );
        
        prepare();
        
        try {
            camera = new Camera2D( 
                (Vector2) player.getPos().clone(), 
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
        terrainColor = BLACK;
        createTerrains( 10, terrainY );
        
        parallax = new Parallax();
        
    }
    
    @Override
    public void update( double delta ) {
        
        if ( player.getState() == Player.State.IDLE ) {
            if ( isKeyPressed( KEY_ENTER ) || isGamepadButtonPressed( GAMEPAD_1, GAMEPAD_BUTTON_MIDDLE_RIGHT ) ) {
                player.start();
            }
        } else if ( player.getState() == Player.State.DYING ) {
            if ( isKeyPressed( KEY_ENTER ) || isGamepadButtonPressed( GAMEPAD_1, GAMEPAD_BUTTON_MIDDLE_RIGHT ) ) {
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
        
        if ( player.getPos().x > getScreenWidth() / 2 && player.getState() != Player.State.DYING ) {
            parallax.update( delta, player.isRunning() );
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
        
        player.drawHud( this );
        
        if ( player.getState() == Player.State.DYING ) {
            fillRectangle( 0, 0, getScreenWidth(), getScreenHeight(), ColorUtils.fade( BLACK, 0.4 ) );
            fillRectangle( 0, getScreenHeight() / 2 - 100, getScreenWidth(), 200, ColorUtils.fade( BLACK, 0.6 ) );
            int fontSize = 60;
            String text = "YOU DIED!";
            drawText( text, getScreenWidth() / 2 - measureText( text, fontSize ) / 2, getScreenHeight() / 2 - 30, fontSize, RED );
            fontSize = 20;
            text = "Press <ENTER> or <START> to play again!";
            drawText( text, getScreenWidth() / 2 - measureText( text, fontSize ) / 2, getScreenHeight() / 2 + 30, fontSize, RED );
        }
        
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
        
        if ( TEST ) {
            gap = 0;
        }
        
        end++;
        int pos = end % terrains.length;
        
        if ( end > 0 ) {
            
            int prevPos = pos-1;
            if ( prevPos < 0 ) {
                prevPos = terrains.length-1;
            }
            
            Terrain lastTerrain = terrains[prevPos];
            x += lastTerrain.getPos().x + lastTerrain.getDim().x + lastTerrain.getGap();
            
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
        
        if ( player.getPos().x <= getScreenWidth() / 2 ) {
            camera.target.x = getScreenWidth() / 2;
        } else {
            camera.target.x = player.getPos().x;
        }
        
        if ( player.getPos().y <= getScreenHeight() / 2 ) {
            camera.target.y = getScreenHeight() / 2;
        } else if ( player.getPos().y > getScreenHeight() / 2 ) {
            camera.target.y = getScreenHeight() / 2;
        } else {
            camera.target.y = player.getPos().y;
        }
        
    }
    
    public static void main( String[] args ) {
        new GameWorld();
    }
    
}
