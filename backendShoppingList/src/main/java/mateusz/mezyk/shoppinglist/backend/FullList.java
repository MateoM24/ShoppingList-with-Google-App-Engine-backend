package mateusz.mezyk.shoppinglist.backend;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

import java.util.Map;

/**
 * Created by Mateusz on 2016-11-23.
 */

@Entity
public class FullList {
    @Id
    private Long id;
    private Map<String,Boolean> products;

    public FullList(){}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Map<String, Boolean> getProducts() {
        return products;
    }

    public void setProducts(Map<String, Boolean> products) {
        this.products = products;
    }
}
