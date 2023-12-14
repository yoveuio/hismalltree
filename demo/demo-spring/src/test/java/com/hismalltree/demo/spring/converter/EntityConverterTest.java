package com.hismalltree.demo.spring.converter;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.junit.Test;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
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

        @Mapping(target = "a", source = "a.a")
        @Mapping(target = "b", source = "b.a")
        EntityC toC(EntityA a, EntityB b);

        @Mapping(target = "c", expression = "java(toC(a, b))")
        EntityD toD(EntityA a, EntityB b);

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

        public Double b;

    }

    @Data
    public static class EntityC {

        public Integer a;

        public Double b;

    }

    @Data
    public static class EntityD {

        public EntityC c;

    }

}