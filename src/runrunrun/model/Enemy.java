package runrunrun.model;

import br.com.davidbuzatto.jsge.animation.AnimationExecutionState;
import br.com.davidbuzatto.jsge.animation.AnimationUtils;
import br.com.davidbuzatto.jsge.animation.frame.FrameByFrameAnimation;
import br.com.davidbuzatto.jsge.animation.frame.SpriteMapAnimationFrame;
import br.com.davidbuzatto.jsge.collision.CollisionUtils;
import br.com.davidbuzatto.jsge.core.engine.EngineFrame;
import br.com.davidbuzatto.jsge.geom.Rectangle;
import br.com.davidbuzatto.jsge.image.Image;
import br.com.davidbuzatto.jsge.image.ImageUtils;
import br.com.davidbuzatto.jsge.math.MathUtils;
import br.com.davidbuzatto.jsge.math.Vector2;
import java.awt.Paint;
import runrunrun.GameWorld;

/**
 * Inimigo.
 * 
 * @author Prof. Dr. David Buzatto
 */
public class Enemy extends Entity {
    
    private Vector2 pos;
    private Vector2 dim;
    private Vector2 vel;
    private Paint paint;
    private Rectangle bb;
    
    private State state;
    private boolean lookingRight;
    
    private static final double MOVE_SPEED = 100;
    private static final double MAX_FALL_SPEED = 400;
    
    private Rectangle cpLeft;
    private Rectangle cpRight;
    private Rectangle cpBottom;
    
    private FrameByFrameAnimation<SpriteMapAnimationFrame> walkingAnimationRight;
    private FrameByFrameAnimation<SpriteMapAnimationFrame> walkingAnimationLeft;
    private FrameByFrameAnimation<SpriteMapAnimationFrame> dyingAnimationRight;
    private FrameByFrameAnimation<SpriteMapAnimationFrame> dyingAnimationLeft;
    
    private static final Image owletWalkingImage = ImageUtils.loadImage( "resources/images/sprites/owletMonsterWalking.png" );
    private static final Image owletDyingImage = ImageUtils.loadImage( "resources/images/sprites/owletMonsterDying.png" );
    private static final Image dudeWalkingImage = ImageUtils.loadImage( "resources/images/sprites/dudeMonsterWalking.png" );
    private static final Image dudeDyingImage = ImageUtils.loadImage( "resources/images/sprites/dudeMonsterDying.png" );
    
    private static enum CollisionType {
        LEFT,
        RIGHT,
        BOTTOM,
        NONE;
    }
    
    public static enum State {
        IDLE,
        ACTIVE,
        DYING;
    }

    public Enemy( Vector2 pos, Vector2 dim, Paint paint ) {
        
        this.pos = pos;
        this.dim = dim;
        this.vel = new Vector2( MOVE_SPEED, 0 );
        this.paint = paint;
        this.state = State.ACTIVE;
        
        this.cpLeft = new Rectangle( 0, 0, 10, 10 );
        this.cpRight = new Rectangle( 0, 0, 10, 10 );
        this.cpBottom = new Rectangle( 0, 0, 10, 10 );
        
        this.bb = new Rectangle( pos.x + 10, pos.y + 10, dim.x - 20, dim.y - 10 );
        
        Image walkingImage;
        Image dyingImage;
        
        if ( MathUtils.getRandomValue( 0, 1 ) == 0 ) {
            walkingImage = owletWalkingImage;
            dyingImage = owletDyingImage;
        } else {
            walkingImage = dudeWalkingImage;
            dyingImage = dudeDyingImage;
        }
        
        this.walkingAnimationRight = new FrameByFrameAnimation<>( 
            0.1, 
            AnimationUtils.getSpriteMapAnimationFrameList( 
                walkingImage, 
                6, 64, 64
            )
        );
        
        this.walkingAnimationLeft = new FrameByFrameAnimation<>( 
            0.1, 
            AnimationUtils.getSpriteMapAnimationFrameList( 
                walkingImage.copyFlipHorizontal(), 
                6, 64, 64, true
            )
        );
        
        this.dyingAnimationRight = new FrameByFrameAnimation<>( 
            0.1, 
            AnimationUtils.getSpriteMapAnimationFrameList( 
                dyingImage, 
                8, 64, 64
            ),
            false
        );
        
        this.dyingAnimationLeft = new FrameByFrameAnimation<>( 
            0.1, 
            AnimationUtils.getSpriteMapAnimationFrameList( 
                dyingImage.copyFlipHorizontal(), 
                8, 64, 64, true
            ),
            false
        );
        
    }
    
    public void update( double delta ) {
        
        if ( lookingRight ) {
            vel.x = MOVE_SPEED;
        } else {
            vel.x = -MOVE_SPEED;
        }
        
        if ( state == State.ACTIVE ) {
            pos.x += vel.x * delta;
            pos.y += vel.y * delta;
        }
        
        vel.y += GameWorld.GRAVITY;
        
        if ( vel.y > MAX_FALL_SPEED ) {
            vel.y = MAX_FALL_SPEED;
        }
        
        switch ( state ) {
            case ACTIVE:
                walkingAnimationRight.update( delta );
                walkingAnimationLeft.update( delta );
                break;
            case DYING:
                dyingAnimationRight.update( delta );
                dyingAnimationLeft.update( delta );
                break;
        }
        
        updateCPAndBB();
        
    }
    
    public void draw( EngineFrame e ) {
        
        switch ( state ) {
            case ACTIVE:
                if ( lookingRight ) {
                    walkingAnimationRight.getCurrentFrame().draw( e, pos.x, pos.y );
                } else {
                    walkingAnimationLeft.getCurrentFrame().draw( e, pos.x, pos.y );
                }
                break;
            case DYING:
                if ( lookingRight ) {
                    if ( dyingAnimationRight.getState() != AnimationExecutionState.FINISHED ) {
                        dyingAnimationRight.getCurrentFrame().draw( e, pos.x, pos.y );
                    }
                } else {
                    if ( dyingAnimationLeft.getState() != AnimationExecutionState.FINISHED ) {
                        dyingAnimationLeft.getCurrentFrame().draw( e, pos.x, pos.y );
                    }
                }
                break;
        }
        
        /*cpLeft.fill( e, e.RED );
        cpRight.fill( e, e.RED );
        cpBottom.fill( e, e.RED );
        bb.fill( e, ColorUtils.fade( e.RED, 0.2 ) );*/
        
    }
    
    public void updateCPAndBB() {
        cpLeft.x = pos.x + 15;
        cpLeft.y = pos.y + dim.y / 2 - cpLeft.height / 2;
        cpRight.x = pos.x + dim.x - cpRight.width - 12;
        cpRight.y = pos.y + dim.y / 2 - cpRight.height / 2;
        cpBottom.x = pos.x + dim.x / 2 - cpBottom.width / 2;
        cpBottom.y = pos.y + dim.y - cpBottom.height;
        bb.x = pos.x + 10;
        bb.y = pos.y + 10;
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
    
    public void resolveTerrainInteraction( Terrain t ) {
        
        switch ( checkCollisionTerrain( t ) ) {
            case BOTTOM:
                pos.y = t.getPos().y - dim.y;
                vel.y = 0;
                break;
        }
        
        updateCPAndBB();
        
        if ( pos.x < t.getPos().x ) {
            pos.x = t.getPos().x;
            lookingRight = true;
        } else if ( pos.x + dim.x > t.getPos().x + t.getDim().x ) {
            pos.x = t.getPos().x + t.getDim().x - dim.x;
            lookingRight = false;
        }
        
    }

    public Rectangle getBB() {
        return bb;
    }

    public Vector2 getPos() {
        return pos;
    }

    public Vector2 getDim() {
        return dim;
    }

    public Vector2 getVel() {
        return vel;
    }

    public State getState() {
        return state;
    }

    public void setState( State state ) {
        this.state = state;
    }
    
}
