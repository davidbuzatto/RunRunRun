package runrunrun.model;

/**
 *
 * @author Prof. Dr. David Buzatto
 */
public abstract class Entity implements Comparable<Entity> {
    
    private static int idCount;
    private int id;
    
    public Entity() {
        id = idCount++;
    }
    
    public int getId() {
        return id;
    }

    @Override
    public int compareTo( Entity o ) {
        return id - o.id;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 13 * hash + this.id;
        return hash;
    }

    @Override
    public boolean equals( Object obj ) {
        if ( this == obj ) {
            return true;
        }
        if ( obj == null ) {
            return false;
        }
        if ( getClass() != obj.getClass() ) {
            return false;
        }
        final Entity other = (Entity) obj;
        return this.id == other.id;
    }
    
}
