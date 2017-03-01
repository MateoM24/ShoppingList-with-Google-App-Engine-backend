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
        name = "fullListApi",
        version = "v1",
        resource = "fullList",
        namespace = @ApiNamespace(
                ownerDomain = "backend.shoppinglist.mezyk.mateusz",
                ownerName = "backend.shoppinglist.mezyk.mateusz",
                packagePath = ""
        )
)
public class FullListEndpoint {

    private static final Logger logger = Logger.getLogger(FullListEndpoint.class.getName());

    private static final int DEFAULT_LIST_LIMIT = 20;

    static {
        // Typically you would register this inside an OfyServive wrapper. See: https://code.google.com/p/objectify-appengine/wiki/BestPractices
        ObjectifyService.register(FullList.class);
    }

    /**
     * Returns the {@link FullList} with the corresponding ID.
     *
     * @param id the ID of the entity to be retrieved
     * @return the entity with the corresponding ID
     * @throws NotFoundException if there is no {@code FullList} with the provided ID.
     */
    @ApiMethod(
            name = "get",
            path = "fullList/{id}",
            httpMethod = ApiMethod.HttpMethod.GET)
    public FullList get(@Named("id") Long id) throws NotFoundException {
        logger.info("Getting FullList with ID: " + id);
        FullList fullList = ofy().load().type(FullList.class).id(id).now();
        if (fullList == null) {
            throw new NotFoundException("Could not find FullList with ID: " + id);
        }
        return fullList;
    }

    /**
     * Inserts a new {@code FullList}.
     */
    @ApiMethod(
            name = "insert",
            path = "fullList",
            httpMethod = ApiMethod.HttpMethod.POST)
    public FullList insert(FullList fullList) {
        // Typically in a RESTful API a POST does not have a known ID (assuming the ID is used in the resource path).
        // You should validate that fullList.id has not been set. If the ID type is not supported by the
        // Objectify ID generator, e.g. long or String, then you should generate the unique ID yourself prior to saving.
        //
        // If your client provides the ID then you should probably use PUT instead.
        ofy().save().entity(fullList).now();
        logger.info("Created FullList with ID: " + fullList.getId());

        return ofy().load().entity(fullList).now();
    }

    /**
     * Updates an existing {@code FullList}.
     *
     * @param id       the ID of the entity to be updated
     * @param fullList the desired state of the entity
     * @return the updated version of the entity
     * @throws NotFoundException if the {@code id} does not correspond to an existing
     *                           {@code FullList}
     */
    @ApiMethod(
            name = "update",
            path = "fullList/{id}",
            httpMethod = ApiMethod.HttpMethod.PUT)
    public FullList update(@Named("id") Long id, FullList fullList) throws NotFoundException {
        // TODO: You should validate your ID parameter against your resource's ID here.
        checkExists(id);
        ofy().save().entity(fullList).now();
        logger.info("Updated FullList: " + fullList);
        return ofy().load().entity(fullList).now();
    }

    /**
     * Deletes the specified {@code FullList}.
     *
     * @param id the ID of the entity to delete
     * @throws NotFoundException if the {@code id} does not correspond to an existing
     *                           {@code FullList}
     */
    @ApiMethod(
            name = "remove",
            path = "fullList/{id}",
            httpMethod = ApiMethod.HttpMethod.DELETE)
    public void remove(@Named("id") Long id) throws NotFoundException {
        checkExists(id);
        ofy().delete().type(FullList.class).id(id).now();
        logger.info("Deleted FullList with ID: " + id);
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
            path = "fullList",
            httpMethod = ApiMethod.HttpMethod.GET)
    public CollectionResponse<FullList> list(@Nullable @Named("cursor") String cursor, @Nullable @Named("limit") Integer limit) {
        limit = limit == null ? DEFAULT_LIST_LIMIT : limit;
        Query<FullList> query = ofy().load().type(FullList.class).limit(limit);
        if (cursor != null) {
            query = query.startAt(Cursor.fromWebSafeString(cursor));
        }
        QueryResultIterator<FullList> queryIterator = query.iterator();
        List<FullList> fullListList = new ArrayList<FullList>(limit);
        while (queryIterator.hasNext()) {
            fullListList.add(queryIterator.next());
        }
        return CollectionResponse.<FullList>builder().setItems(fullListList).setNextPageToken(queryIterator.getCursor().toWebSafeString()).build();
    }

    private void checkExists(Long id) throws NotFoundException {
        try {
            ofy().load().type(FullList.class).id(id).safe();
        } catch (com.googlecode.objectify.NotFoundException e) {
            throw new NotFoundException("Could not find FullList with ID: " + id);
        }
    }
}