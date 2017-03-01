package mateusz.mezyk.shoppinglist.backend;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

/**
 * Created by Mateusz on 2016-12-02.
 */
@Entity
public class Product {
    @Id
    private Long id;
    private String name;
    private Boolean done=false;

    public Product() {}

    public Boolean getDone() {
        return done;
    }

    public void setDone(Boolean done) {
        this.done = done;
    }

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
}
