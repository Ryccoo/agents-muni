package backend;

/**
 * Created by richard on 22.2.2014.
 */
public class Mission {
    private Long id;
    private String name;
    private String destination;
    private String description;
    private boolean secret = false;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isSecret() {
        return secret;
    }

    public void setSecret(boolean secret) {
        this.secret = secret;
    }

    public String toString() {
        return "Mission {id="+id+", name=`"+name+",` destination=`"+destination+", description=`"+description+"`, secret="+secret+"}";
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Mission other = (Mission) obj;
        if (this.id != other.id && (this.id == null || !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 42;
        hash = 11 * hash + (this.id != null ? this.id.hashCode() : 0);
        return hash;
    }
}
