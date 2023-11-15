package com.hismalltree.demo.spring.converter;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.junit.Test;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class EntityConverterTest {

    @Test
    public void test() {
        EntityB b = Converter.MAPPER.toB(EntityA.builder().a("123").b(1.0).build());
        assertNotNull(b);
    }

    @Mapper(
        typeConversionPolicy = ReportingPolicy.WARN
    )
    public interface Converter {

        Converter MAPPER = Mappers.getMapper(Converter.class);

        EntityB toB(final EntityA a);

    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class EntityA {

        public String a;

        public Double b;

    }

    @Data
    public static class EntityB {

        public Integer a;

        public Integer b;

    }

}