package com.muxu.demo.debezium;

import com.hismalltree.core.exception.HismalltreeException;
import io.debezium.config.CommonConnectorConfig;
import io.debezium.connector.mysql.MySqlConnector;
import io.debezium.connector.mysql.MySqlConnectorConfig;
import io.debezium.embedded.EmbeddedEngine;
import io.debezium.engine.ChangeEvent;
import io.debezium.engine.DebeziumEngine;
import io.debezium.engine.format.Json;
import io.debezium.relational.HistorizedRelationalDatabaseConnectorConfig;
import io.debezium.relational.RelationalDatabaseConnectorConfig;
import io.debezium.storage.file.history.FileSchemaHistory;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.connect.storage.FileOffsetBackingStore;

import java.io.Closeable;
import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author fangpeiyu.py
 */
@Slf4j
public class CdcEngine implements Closeable {

    private final DebeziumEngine<ChangeEvent<String, String>> engine;

    public CdcEngine(Properties properties) {
        engine = DebeziumEngine.create(Json.class)
            .using(properties)
            .notifying(r -> log.info("Engine receive record: {}", r))
            .build();
    }

    public void run() {
        try (ExecutorService executorService = Executors.newSingleThreadExecutor()) {
            executorService.execute(engine);
        } catch (Exception e) {
            throw new HismalltreeException("Cdc engine failed cause by " + e.getMessage(), e);
        }
    }

    @Override
    public void close() throws IOException {
        engine.close();
    }

    public static void main(String[] args) throws IOException {
        final Properties props = new Properties();
        props.setProperty(EmbeddedEngine.ENGINE_NAME.name(), "engine");
        props.setProperty(EmbeddedEngine.CONNECTOR_CLASS.name(), MySqlConnector.class.getName());
        props.setProperty(EmbeddedEngine.OFFSET_STORAGE.name(), FileOffsetBackingStore.class.getName());
        props.setProperty(EmbeddedEngine.OFFSET_STORAGE_FILE_FILENAME.name(), "a.json");
        props.setProperty(RelationalDatabaseConnectorConfig.HOSTNAME.name(), "10.37.40.84");
        props.setProperty(RelationalDatabaseConnectorConfig.PORT.name(), "3306");
        props.setProperty(RelationalDatabaseConnectorConfig.USER.name(), "root");
        props.setProperty(RelationalDatabaseConnectorConfig.PASSWORD.name(), "debezium");
        props.setProperty(RelationalDatabaseConnectorConfig.INCLUDE_SCHEMA_CHANGES.name(), "true");
        props.setProperty(MySqlConnectorConfig.SERVER_ID.name(), "12312");
        props.setProperty(CommonConnectorConfig.TOPIC_PREFIX.name(), "a");
        props.setProperty(HistorizedRelationalDatabaseConnectorConfig.SCHEMA_HISTORY.name(), FileSchemaHistory.class.getName());
        props.setProperty(FileSchemaHistory.FILE_PATH.name(), FileSchemaHistory.class.getName());

        try (CdcEngine cdcEngine = new CdcEngine(props)) {
            cdcEngine.run();
        }
    }

}
