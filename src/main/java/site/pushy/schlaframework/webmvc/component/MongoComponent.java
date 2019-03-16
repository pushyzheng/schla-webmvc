package site.pushy.schlaframework.webmvc.component;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import eu.dozd.mongo.MongoMapper;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import site.pushy.schlaframework.webmvc.annotation.MongoDocument;
import site.pushy.schlaframework.webmvc.exception.ConfigPropertiesException;
import site.pushy.schlaframework.webmvc.util.StringUtil;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author Pushy
 * @since 2019/3/15 10:13
 */
public class MongoComponent {

    private static final String ID = "_id";

    private MongoClientOptions settings;

    private String databaseName;

    private String host = "127.0.0.1";

    private int port = 27017;

    private MongoClient client;

    public MongoComponent() {
        this(null);
    }

    public MongoComponent(String name) {
        this.databaseName = name;
        initMethod();
    }

    private void initMethod() {
        if (StringUtil.isEmpty(databaseName)) {
            throw new ConfigPropertiesException("The mongo database name is not present.");
        }
        CodecRegistry codecRegistry = CodecRegistries.fromProviders(MongoMapper.getProviders());
        settings = MongoClientOptions.builder().codecRegistry(codecRegistry).build();
        client = new MongoClient(new ServerAddress(host, port), settings);
    }

    private MongoDatabase getDB() {
        return client.getDatabase(databaseName);
    }

    private void closeConn() {
        client.close();
    }

    private <T> MongoCollection<T> getCollection(Class<T> clazz) {
        String colName;
        MongoDocument document = clazz.getAnnotation(MongoDocument.class);
        if (document != null && !StringUtil.isEmpty(document.value()))
            colName = document.value();
        else
            colName = clazz.getSimpleName();
        return getDB().getCollection(colName, clazz);
    }

    public <T> void insertOne(T record, Class<T> clazz) {
        MongoCollection<T> collection = getCollection(clazz);
        collection.insertOne(record);
    }

    public <T> void insertMany(List<T> records, Class<T> clazz) {
        MongoCollection<T> collection = getCollection(clazz);
        collection.insertMany(records);
    }

    public <T> List<T> findAll(Class<T> clazz) {
        MongoCollection<T> collection = getCollection(clazz);
        Iterator<T> iterator = collection.find(clazz).iterator();
        List<T> res = new ArrayList<>();

        while (iterator.hasNext()) {
            res.add(iterator.next());
        }
        return res;
    }

    public <T> T findOneById(String id, Class<T> clazz) {
        return findOneByKey(ID, id, clazz);
    }

    public <T> T findOneByKey(String key, String value, Class<T> clazz) {
        MongoCollection<T> collection = getCollection(clazz);
        return collection.find(Filters.eq(key, value)).first();
    }

    public <T> List<T> findManyById(String id, Class<T> clazz) {
        return findManyByKey(ID, id, clazz);
    }

    public <T> List<T> findManyByKey(String key, String value, Class<T> clazz) {
        MongoCollection<T> collection = getCollection(clazz);
        Iterator<T> iterator = collection.find(Filters.eq(key, value)).iterator();

        List<T> res = new ArrayList<>();
        while (iterator.hasNext()) {
            res.add(iterator.next());
        }
        return res;
    }

    public <T> UpdateResult updateOneById(String id, T record, Class<T> clazz) {
        return updateOneByKey(ID, id, record, clazz);
    }

    public <T> UpdateResult updateOneByKey(String key, String value,
                                           T record, Class<T> clazz) {
        MongoCollection<T> collection = getCollection(clazz);
        UpdateResult res = null;
        return res;
    }

    public <T> DeleteResult deleteById(String id, Class<T> clazz) {
        return deleteByKey(ID, id, clazz);
    }

    public <T> DeleteResult deleteByKey(String key, String value, Class<T> clazz) {
        MongoCollection<T> collection = getCollection(clazz);
        return collection.deleteOne(Filters.eq(key, value));
    }

}
