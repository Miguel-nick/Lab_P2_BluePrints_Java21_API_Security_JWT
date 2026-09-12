package co.edu.eci.blueprints.model;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OrderColumn;
import jakarta.persistence.Table;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "blueprints")
@IdClass(BlueprintId.class)
public class Blueprint {

    @Id
    @Column(name = "author", nullable = false)
    private String author;

    @Id
    @Column(name = "name", nullable = false)
    private String name;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "blueprint_points",
            joinColumns = {
                    @JoinColumn(name = "blueprint_author", referencedColumnName = "author"),
                    @JoinColumn(name = "blueprint_name", referencedColumnName = "name")
            }
    )
    @OrderColumn(name = "point_order")
    private List<Point> points = new ArrayList<>();

    protected Blueprint() {
    }

    public Blueprint(String author, String name, List<Point> points) {
        this.author = author;
        this.name = name;
        if (points != null) {
            this.points.addAll(points);
        }
    }

    public String getAuthor() {
        return author;
    }

    public String getName() {
        return name;
    }

    public List<Point> getPoints() {
        return Collections.unmodifiableList(points);
    }

    public void addPoint(Point point) {
        points.add(point);
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (!(object instanceof Blueprint blueprint)) return false;
        return Objects.equals(author, blueprint.author) && Objects.equals(name, blueprint.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(author, name);
    }
}