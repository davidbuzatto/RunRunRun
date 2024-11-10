package runrunrun.model;

import br.com.davidbuzatto.jsge.animation.AnimationUtils;
import br.com.davidbuzatto.jsge.animation.frame.FrameByFrameAnimation;
import br.com.davidbuzatto.jsge.animation.frame.SpriteMapAnimationFrame;
import br.com.davidbuzatto.jsge.collision.CollisionUtils;
import br.com.davidbuzatto.jsge.core.engine.EngineFrame;
import br.com.davidbuzatto.jsge.geom.Rectangle;
import br.com.davidbuzatto.jsge.image.Image;
import br.com.davidbuzatto.jsge.image.ImageUtils;
import br.com.davidbuzatto.jsge.math.Vector2;
import java.awt.Color;
import java.awt.Paint;
import runrunrun.GameWorld;

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
    
    private State state;
    private int remainingJumps;
    
    private static final double MOVE_SPEED = 400;
    private static final double JUMP_SPEED = 400;
    private static final double MAX_FALL_SPEED = 400;
    
    private Rectangle cpLeft;
    private Rectangle cpRight;
    private Rectangle cpBottom;
    
    private int lastReachedTerrain;
    
    private FrameByFrameAnimation<SpriteMapAnimationFrame> idleAnimation;
    private FrameByFrameAnimation<SpriteMapAnimationFrame> runningAnimation;
    private FrameByFrameAnimation<SpriteMapAnimationFrame> jumpingAnimation;
    private FrameByFrameAnimation<SpriteMapAnimationFrame> dyingAnimation;
    
    private static enum CollisionType {
        LEFT,
        RIGHT,
        BOTTOM,
        NONE;
    }
    
    private static enum State {
        STARTING,
        IDLE,
        RUNNING,
        JUMPING,
        DYING;
    }

    public Player( Vector2 pos, Vector2 dim, Paint paint ) {
        
        this.pos = pos;
        this.dim = dim;
        this.vel = new Vector2( MOVE_SPEED, 0 );
        this.paint = paint;
        this.state = State.IDLE;
        
        this.cpLeft = new Rectangle( 0, 0, 10, 10 );
        this.cpRight = new Rectangle( 0, 0, 10, 10 );
        this.cpBottom = new Rectangle( 0, 0, 10, 10 );
        
        this.remainingJumps = 2;
        
        Image playerIdleImage = ImageUtils.loadImage( "resources/images/playerIdle.png" );
        Image playerRunningImage = ImageUtils.loadImage( "resources/images/playerRunning.png" );
        Image playerJumpingImage = ImageUtils.loadImage( "resources/images/playerJumping.png" );
        Image playerDyingImage = ImageUtils.loadImage( "resources/images/playerDying.png" );
        
        Image[] images = new Image[]{
            playerIdleImage,
            playerRunningImage,
            playerJumpingImage,
            playerDyingImage
        };
        
        Color[] fromColor = new Color[]{
            new Color( 244, 137, 246 ),
            new Color( 216, 64, 251 ),
            new Color( 120, 11, 247 )
        };
        
        Color[] toColor = new Color[]{
            new Color( 106, 156, 246 ),
            new Color( 15, 94, 238 ),
            new Color( 9, 56, 147 )
        };
        
        for ( int i = 0; i < fromColor.length; i++ ) {
            for ( Image img : images ) {
                img.colorReplace( fromColor[i], toColor[i] );
            }
        }
        
        this.idleAnimation = new FrameByFrameAnimation<>( 
            0.1, 
            AnimationUtils.newSpriteMapAnimationFrameList( 
                playerIdleImage, 
                4, 64, 64
            )
        );
        
        this.runningAnimation = new FrameByFrameAnimation<>( 
            0.05, 
            AnimationUtils.newSpriteMapAnimationFrameList( 
                playerRunningImage, 
                6, 64, 64
            )
        );
        
        this.jumpingAnimation = new FrameByFrameAnimation<>( 
            0.2, 
            AnimationUtils.newSpriteMapAnimationFrameList( 
                playerJumpingImage, 
                8, 64, 64
            )
        );
        
        this.dyingAnimation = new FrameByFrameAnimation<>( 
            0.1, 
            AnimationUtils.newSpriteMapAnimationFrameList( 
                playerDyingImage, 
                8, 64, 64
            )
        );
        
    }
    
    public void update( double delta, EngineFrame e ) {
        
        if ( e.isKeyPressed( EngineFrame.KEY_RIGHT ) ) {
            state = State.RUNNING;
            vel.x = MOVE_SPEED;
        }
        
        if ( e.isKeyPressed( EngineFrame.KEY_SPACE ) && remainingJumps > 0 && ( state == State.RUNNING || state == State.JUMPING ) ) {
            vel.y = -JUMP_SPEED;
            remainingJumps--;
            state = State.JUMPING;
        }
        
        if ( state == State.RUNNING || state == State.JUMPING ) {
            pos.x += vel.x * delta;
        }
        
        pos.y += vel.y * delta;
        
        vel.y += GameWorld.GRAVITY;
        
        if ( vel.y > MAX_FALL_SPEED ) {
            vel.y = MAX_FALL_SPEED;
        }
        
        switch ( state ) {
            case STARTING:
            case IDLE:
                idleAnimation.update( delta );
                break;
            case RUNNING:
                runningAnimation.update( delta );
                break;
            case JUMPING:
                jumpingAnimation.update( delta );
                break;
            case DYING:
                dyingAnimation.update( delta );
                break;
        }
        
        updateCPs();
        
    }
    
    public void draw( EngineFrame e ) {
        
        switch ( state ) {
            case STARTING:
            case IDLE:
                idleAnimation.getCurrentFrame().draw( e, pos.x, pos.y );
                break;
            case RUNNING:
                runningAnimation.getCurrentFrame().draw( e, pos.x, pos.y );
                break;
            case JUMPING:
                jumpingAnimation.getCurrentFrame().draw( e, pos.x, pos.y );
                break;
            case DYING:
                dyingAnimation.getCurrentFrame().draw( e, pos.x, pos.y );
                break;
        }
        
        /*cpLeft.fill( e, e.LIME );
        cpRight.fill( e, e.LIME );
        cpBottom.fill( e, e.LIME );*/
        
    }
    
    public void updateCPs() {
        cpLeft.x = pos.x + 15;
        cpLeft.y = pos.y + dim.y / 2 - cpLeft.height / 2;
        cpRight.x = pos.x + dim.x - cpRight.width - 12;
        cpRight.y = pos.y + dim.y / 2 - cpRight.height / 2;
        cpBottom.x = pos.x + dim.x / 2 - cpBottom.width / 2;
        cpBottom.y = pos.y + dim.y - cpBottom.height;
    }
    
    private CollisionType checkCollisionTerrain( Terrain t ) {
        
        if ( CollisionUtils.checkCollisionRectangles( cpBottom, t.getBB() ) ) {
            return CollisionType.BOTTOM;
        } else if ( CollisionUtils.checkCollisionRectangles( cpLeft, t.getBB() ) ) {
            return CollisionType.LEFT;
        } else if ( CollisionUtils.checkCollisionRectangles( cpRight, t.getBB() ) ) {
            return CollisionType.RIGHT;
        }
        
        return CollisionType.NONE;
        
    }
    
    public void resolveCollisionTerrain( Terrain[] terrains ) {
        
        for ( Terrain t : terrains ) {
            
            if ( t != null ) {

                if ( lastReachedTerrain < t.id && 
                     pos.x + dim.x >= t.pos.x ) {
                    lastReachedTerrain = t.id;
                }
                
                switch ( checkCollisionTerrain( t ) ) {
                    case BOTTOM:
                        pos.y = t.pos.y - dim.y;
                        vel.y = 0;
                        if ( state == State.JUMPING ) {
                            state = State.RUNNING;
                        }
                        remainingJumps = 2;
                        t.makeReached();
                        break;
                    case LEFT:
                        vel.x = 0;
                        pos.x = t.pos.x + t.dim.x;
                        t.makeReached();
                        state = State.DYING;
                        break;
                    case RIGHT:
                        vel.x = 0;
                        pos.x = t.pos.x - dim.x;
                        t.makeReached();
                        state = State.DYING;
                        break;
                }
                
                updateCPs();
                
            }
            
        }
        
    }

    public int getLastReachedTerrain() {
        return lastReachedTerrain;
    }
    
}
