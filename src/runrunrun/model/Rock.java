package runrunrun.model;

import br.com.davidbuzatto.jsge.core.engine.EngineFrame;
import br.com.davidbuzatto.jsge.image.Image;
import br.com.davidbuzatto.jsge.image.ImageUtils;
import br.com.davidbuzatto.jsge.math.Vector2;

/**
 *
 * @author Prof. Dr. David Buzatto
 */
public class Rock extends Entity {
    
    private static Image img = ImageUtils.loadImage( "resources/images/sprites/rock1.png" );
    
    private Vector2 pos;
    private Vector2 vel;
    private double lifeSpan;
    private double rotationAngle;
    private boolean active;

    public Rock( Vector2 pos, Vector2 vel ) {
        this.pos = pos;
        this.vel = vel;
        this.lifeSpan = 5;
        this.active = true;
    }
    
    public void update( double delta ) {
        if ( active ) {
            lifeSpan -= delta;
            rotationAngle += 10 * delta;
            pos.x += vel.x * delta;
            pos.y += vel.y * delta;
        }
        if ( lifeSpan <= 0.0 ) {
            active = false;
        }
    }
    
    public void draw( EngineFrame e ) {
        if ( active ) {
            e.drawImage( img, pos.x, pos.y, rotationAngle );
        }
    }

    public boolean isActive() {
        return active;
    }
    
}
