package site.pushy.schlaframework.webmvc.core;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoDatabase;
import eu.dozd.mongo.MongoMapper;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import site.pushy.schlaframework.webmvc.exception.ConfigPropertiesException;
import site.pushy.schlaframework.webmvc.util.StringUtil;

/**
 * @author Pushy
 * @since 2019/3/15 10:13
 */
public class MongoComponent {

    private ThreadLocal<MongoClient> local;

    private MongoClientOptions settings;

    private String databaseName;

    private String host = "127.0.0.1";

    private int port = 27017;

    public MongoComponent() {
        this(null);
    }

    public MongoComponent(String name) {
        this.databaseName = name;
        local = new ThreadLocal<>();
        initMethod();
    }

    private void initMethod() {
        if (StringUtil.isEmpty(databaseName)) {
            throw new ConfigPropertiesException("The mongo database name is not present.");
        }
        CodecRegistry codecRegistry = CodecRegistries.fromProviders(MongoMapper.getProviders());
        settings = MongoClientOptions.builder().codecRegistry(codecRegistry).build();
    }

    public MongoDatabase getDB() {
        if (local.get() == null) {
            MongoClient client = new MongoClient(new ServerAddress(host, port), settings);
            local.set(client);
            return client.getDatabase(databaseName);
        }
        MongoClient client = local.get();
        return client.getDatabase(databaseName);
    }
}
