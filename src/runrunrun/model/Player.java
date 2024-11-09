package runrunrun.model;

import br.com.davidbuzatto.jsge.animation.frame.FrameByFrameAnimation;
import br.com.davidbuzatto.jsge.animation.frame.ImageAnimationFrame;
import br.com.davidbuzatto.jsge.collision.CollisionUtils;
import br.com.davidbuzatto.jsge.core.engine.EngineFrame;
import br.com.davidbuzatto.jsge.geom.Rectangle;
import br.com.davidbuzatto.jsge.math.Vector2;
import java.awt.Paint;
import java.util.List;
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
    
    private List<ImageAnimationFrame> idleFrames;
    private List<ImageAnimationFrame> runFrames;
    private List<ImageAnimationFrame> jumpFrames;
    private List<ImageAnimationFrame> deathFrames;
    
    private FrameByFrameAnimation<ImageAnimationFrame> idleAnimation;
    private FrameByFrameAnimation<ImageAnimationFrame> runAnimation;
    private FrameByFrameAnimation<ImageAnimationFrame> jumpAnimation;
    private FrameByFrameAnimation<ImageAnimationFrame> deathAnimation;
    
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

    public Player( Vector2 pos, Vector2 dim, Paint paint,
                   List<ImageAnimationFrame> idleFrames, 
                   List<ImageAnimationFrame> runFrames, 
                   List<ImageAnimationFrame> jumpFrames, 
                   List<ImageAnimationFrame> deathFrames ) {
        
        this.pos = pos;
        this.dim = dim;
        this.vel = new Vector2( MOVE_SPEED, 0 );
        this.paint = paint;
        this.idleFrames = idleFrames;
        this.runFrames = runFrames;
        this.jumpFrames = jumpFrames;
        this.deathFrames = deathFrames;
        this.state = State.IDLE;
        
        this.cpLeft = new Rectangle( 0, 0, 10, 10 );
        this.cpRight = new Rectangle( 0, 0, 10, 10 );
        this.cpBottom = new Rectangle( 0, 0, 10, 10 );
        
        this.remainingJumps = 2;
        
        this.idleAnimation = new FrameByFrameAnimation<>( 0.1, idleFrames );
        this.runAnimation = new FrameByFrameAnimation<>( 0.05, runFrames );
        this.jumpAnimation = new FrameByFrameAnimation<>( 0.2, jumpFrames );
        this.deathAnimation = new FrameByFrameAnimation<>( 0.1, deathFrames );
        
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
                runAnimation.update( delta );
                break;
            case JUMPING:
                jumpAnimation.update( delta );
                break;
            case DYING:
                deathAnimation.update( delta );
                break;
        }
        
        updateCPs();
        
    }
    
    public void draw( EngineFrame e ) {
        
        switch ( state ) {
            case STARTING:
            case IDLE:
                e.drawImage( idleAnimation.getCurrentFrame().image, pos.x, pos.y );
                break;
            case RUNNING:
                e.drawImage( runAnimation.getCurrentFrame().image, pos.x, pos.y );
                break;
            case JUMPING:
                e.drawImage( jumpAnimation.getCurrentFrame().image, pos.x, pos.y );
                break;
            case DYING:
                e.drawImage( deathAnimation.getCurrentFrame().image, pos.x, pos.y );
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
