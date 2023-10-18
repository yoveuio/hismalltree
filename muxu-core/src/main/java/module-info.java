module hismalltree.muxu.core.main {
    opens com.muxu.core;
    opens com.muxu.core.utils;

    requires com.fasterxml.jackson.core;
    requires com.fasterxml.jackson.databind;
    requires com.google.common;
    requires org.apache.commons.lang3;
    requires jsr305;
    requires org.slf4j;
    requires kotlin.stdlib;
    requires typesafe.config;
    requires org.yaml.snakeyaml;
    requires java.sql;


}