package world.ultravanilla;

public class StaffAction {

    private long expires;
    private Type type;
    private String source;
    private String target;
    private String description;
    private long created;

    public StaffAction(Type type, String description, String source, String target) {
        this(type, description, source, target, System.currentTimeMillis(), -1);
    }

    public StaffAction(Type type, String description, String source, String target, long created, long expires) {
        this.type = type;
        this.description = description;
        this.source = source;
        this.target = target;
        this.created = created;
        this.expires = expires;
    }

    public long getExpires() {
        return expires;
    }

    public StaffAction(Type type, String description, String source, String target, long created) {
        this(type, description, source, target, created, -1);
    }

    public void setExpires(long expires) {
        this.expires = expires;
    }

    public long getCreated() {
        return created;
    }

    public void setCreated(long created) {
        this.created = created;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public enum Type {

        PERMA_BAN("permanently banned"),
        IP_BAN("IP banned"),
        BAN("banned"),
        TEMP_BAN("temporarily banned"),
        KICK("kicked"),
        WARN("warned"),
        PARDON("pardoned"),
        PARDON_IP("pardoned IP");

        String verb;

        Type(String verb) {
            this.verb = verb;
        }

        public String getVerb() {
            return verb;
        }
    }
}
