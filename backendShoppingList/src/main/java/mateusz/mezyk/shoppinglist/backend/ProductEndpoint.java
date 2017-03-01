package mateusz.mezyk.shoppinglist.backend;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.api.server.spi.response.CollectionResponse;
import com.google.api.server.spi.response.NotFoundException;
import com.google.appengine.api.datastore.Cursor;
import com.google.appengine.api.datastore.QueryResultIterator;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.cmd.Query;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.annotation.Nullable;
import javax.inject.Named;

import static com.googlecode.objectify.ObjectifyService.ofy;

/**
 * WARNING: This generated code is intended as a sample or starting point for using a
 * Google Cloud Endpoints RESTful API with an Objectify entity. It provides no data access
 * restrictions and no data validation.
 * <p>
 * DO NOT deploy this code unchanged as part of a real application to real users.
 */
@Api(
        name = "productApi",
        version = "v1",
        resource = "product",
        namespace = @ApiNamespace(
                ownerDomain = "backend.shoppinglist.mezyk.mateusz",
                ownerName = "backend.shoppinglist.mezyk.mateusz",
                packagePath = ""
        )
)
public class ProductEndpoint {

    private static final Logger logger = Logger.getLogger(ProductEndpoint.class.getName());

    private static final int DEFAULT_LIST_LIMIT = 20;

    static {
        // Typically you would register this inside an OfyServive wrapper. See: https://code.google.com/p/objectify-appengine/wiki/BestPractices
        ObjectifyService.register(Product.class);
    }

    /**
     * Returns the {@link Product} with the corresponding ID.
     *
     * @param id the ID of the entity to be retrieved
     * @return the entity with the corresponding ID
     * @throws NotFoundException if there is no {@code Product} with the provided ID.
     */
    @ApiMethod(
            name = "get",
            path = "product/{id}",
            httpMethod = ApiMethod.HttpMethod.GET)
    public Product get(@Named("id") Long id) throws NotFoundException {
        logger.info("Getting Product with ID: " + id);
        Product product = ofy().load().type(Product.class).id(id).now();
        if (product == null) {
            throw new NotFoundException("Could not find Product with ID: " + id);
        }
        return product;
    }

    /**
     * Inserts a new {@code Product}.
     */
    @ApiMethod(
            name = "insert",
            path = "product",
            httpMethod = ApiMethod.HttpMethod.POST)
    public Product insert(Product product) {
        // Typically in a RESTful API a POST does not have a known ID (assuming the ID is used in the resource path).
        // You should validate that product.id has not been set. If the ID type is not supported by the
        // Objectify ID generator, e.g. long or String, then you should generate the unique ID yourself prior to saving.
        //
        // If your client provides the ID then you should probably use PUT instead.
        ofy().save().entity(product).now();
        logger.info("Created Product with ID: " + product.getId());

        return ofy().load().entity(product).now();
    }

    /**
     * Updates an existing {@code Product}.
     *
     * @param id      the ID of the entity to be updated
     * @param product the desired state of the entity
     * @return the updated version of the entity
     * @throws NotFoundException if the {@code id} does not correspond to an existing
     *                           {@code Product}
     */
    @ApiMethod(
            name = "update",
            path = "product/{id}",
            httpMethod = ApiMethod.HttpMethod.PUT)
    public Product update(@Named("id") Long id, Product product) throws NotFoundException {
        // TODO: You should validate your ID parameter against your resource's ID here.
        checkExists(id);
        ofy().save().entity(product).now();
        logger.info("Updated Product: " + product);
        return ofy().load().entity(product).now();
    }

    /**
     * Deletes the specified {@code Product}.
     *
     * @param id the ID of the entity to delete
     * @throws NotFoundException if the {@code id} does not correspond to an existing
     *                           {@code Product}
     */
    @ApiMethod(
            name = "remove",
            path = "product/{id}",
            httpMethod = ApiMethod.HttpMethod.DELETE)
    public void remove(@Named("id") Long id) throws NotFoundException {
        checkExists(id);
        ofy().delete().type(Product.class).id(id).now();
        logger.info("Deleted Product with ID: " + id);
    }

    /**
     * List all entities.
     *
     * @param cursor used for pagination to determine which page to return
     * @param limit  the maximum number of entries to return
     * @return a response that encapsulates the result list and the next page token/cursor
     */
    @ApiMethod(
            name = "list",
            path = "product",
            httpMethod = ApiMethod.HttpMethod.GET)
    public CollectionResponse<Product> list(@Nullable @Named("cursor") String cursor, @Nullable @Named("limit") Integer limit) {
        limit = limit == null ? DEFAULT_LIST_LIMIT : limit;
        Query<Product> query = ofy().load().type(Product.class).limit(limit);
        if (cursor != null) {
            query = query.startAt(Cursor.fromWebSafeString(cursor));
        }
        QueryResultIterator<Product> queryIterator = query.iterator();
        List<Product> productList = new ArrayList<Product>(limit);
        while (queryIterator.hasNext()) {
            productList.add(queryIterator.next());
        }
        return CollectionResponse.<Product>builder().setItems(productList).setNextPageToken(queryIterator.getCursor().toWebSafeString()).build();
    }

    private void checkExists(Long id) throws NotFoundException {
        try {
            ofy().load().type(Product.class).id(id).safe();
        } catch (com.googlecode.objectify.NotFoundException e) {
            throw new NotFoundException("Could not find Product with ID: " + id);
        }
    }
}