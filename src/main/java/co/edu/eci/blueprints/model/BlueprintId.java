package co.edu.eci.blueprints.model;

import java.io.Serializable;
import java.util.Objects;

public class BlueprintId implements Serializable {

    private String author;
    private String name;

    public BlueprintId() {
    }

    public BlueprintId(String author, String name) {
        this.author = author;
        this.name = name;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (!(object instanceof BlueprintId that)) return false;
        return Objects.equals(author, that.author) && Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(author, name);
    }
}