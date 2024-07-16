package com.hismalltree.core.utils;

import com.google.common.cache.*;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author fangpeiyu.py
 */
public class ExpsUtils {

    private ExpsUtils() {

    }

    @SuppressWarnings("all")
    private static final @NonNull LoadingCache<Pair<String, Integer>, Pattern> PATTERN_CACHE = CacheBuilder.newBuilder()
            .maximumSize(1000)
            .recordStats()
            .build(CacheLoader.from(pair -> Pattern.compile(pair.getLeft(), pair.getRight())));

    @CanIgnoreReturnValue
    public static Pattern getPattern(String pattern) {
        return Objects.requireNonNull(PATTERN_CACHE.getUnchecked(Pair.of(pattern, 0)));
    }

    public static Pattern getPattern(String pattern, Integer flags) {
        return Objects.requireNonNull(PATTERN_CACHE.getUnchecked(Pair.of(pattern, flags)));
    }

    public static boolean match(String pattern, String value) {
        if (StringUtils.isEmpty(pattern)) {
            return true;
        }
        return Objects.requireNonNull(getPattern(pattern)).matcher(value).matches();
    }

    public static boolean match(String pattern, Integer flags, String value) {
        if (StringUtils.isEmpty(pattern)) {
            return true;
        }
        return Objects.requireNonNull(getPattern(pattern, flags)).matcher(value).matches();
    }

    public static void main(String[] args) {
        String regex = "^(.*)[:：](.*)$";
        Pattern compile = Pattern.compile(regex);
        String s = "public_param_template：公共属性模版关联属性\n" +
                "schema_model_conf: 场景属性\n" +
                "schema_model：唯一性配置关联属性\n" +
                "custom：自定义属性";
        System.out.println(match(regex, 8, s));
        System.out.println(compile.matcher(s).find());
    }

}

