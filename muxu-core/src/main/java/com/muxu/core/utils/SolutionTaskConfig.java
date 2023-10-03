package com.muxu.core.utils;

import com.fasterxml.jackson.core.JsonPointer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.muxu.core.constants.TypeReferenceHolder;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nullable;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * 比较通用的json config的结构, 基于Jackson将json解析为NodeTree结构, 支持对NodeTree中任意节点进行get, set, remove, add等操作<br>
 * 可以通过对`BUILD_NODE_FUNCTION_MAP`以及`GET_OBJECT_FUNCTION_LIST`进行扩展, 以达到对某些类进行特殊处理的目的<br>
 * 正常情况下, SolutionTaskConfig不会抛出任何异常
 *
 * @author fangpeiyu
 * @see SolutionTaskConfigPointer
 */
@SuppressWarnings("unused")
public final class SolutionTaskConfig {

    private static final Map<Class<?>, Function<Object, JsonNode>> BUILD_NODE_FUNCTION_MAP;
    private static final List<Pair<Predicate<JsonNode>, Function<JsonNode, Object>>> GET_OBJECT_FUNCTION_LIST;

    static {
        ImmutableMap.Builder<Class<?>, Function<Object, JsonNode>> builderNodeFunctionBuilder = ImmutableMap.builder();
        builderNodeFunctionBuilder.put(Boolean.class, object -> JsonNodeFactory.instance.booleanNode((Boolean) object));
        builderNodeFunctionBuilder.put(Integer.class, object -> JsonNodeFactory.instance.numberNode((Integer) object));
        builderNodeFunctionBuilder.put(Long.class, object -> JsonNodeFactory.instance.numberNode((Long) object));
        builderNodeFunctionBuilder.put(Short.class, object -> JsonNodeFactory.instance.numberNode((Short) object));
        builderNodeFunctionBuilder.put(Float.class, object -> JsonNodeFactory.instance.numberNode((Float) object));
        builderNodeFunctionBuilder.put(Double.class, object -> JsonNodeFactory.instance.numberNode((Double) object));
        builderNodeFunctionBuilder.put(BigInteger.class, object -> JsonNodeFactory.instance.numberNode((BigInteger) object));
        builderNodeFunctionBuilder.put(BigDecimal.class, object -> JsonNodeFactory.instance.numberNode((BigDecimal) object));
        builderNodeFunctionBuilder.put(String.class, object -> JsonNodeFactory.instance.textNode((String) object));
        builderNodeFunctionBuilder.put(byte[].class, object -> JsonNodeFactory.instance.binaryNode((byte[]) object));
        builderNodeFunctionBuilder.put(Optional.class, object -> buildNode(((Optional<?>) object).orElse(null)));
        builderNodeFunctionBuilder.put(SolutionTaskConfig.class, object -> buildNode(((SolutionTaskConfig) object).root));
        BUILD_NODE_FUNCTION_MAP = builderNodeFunctionBuilder.build();

        ImmutableList.Builder<Pair<Predicate<JsonNode>, Function<JsonNode, Object>>> fromNodeFunctionBuilder = ImmutableList.builder();
        fromNodeFunctionBuilder.add(Pair.of(JsonNode::isNull, ignore -> null));
        fromNodeFunctionBuilder.add(Pair.of(JsonNode::isBoolean, JsonNode::booleanValue));
        fromNodeFunctionBuilder.add(Pair.of(JsonNode::isNumber, JsonNode::numberValue));
        fromNodeFunctionBuilder.add(Pair.of(JsonNode::isTextual, JsonNode::textValue));
        fromNodeFunctionBuilder.add(Pair.of(JsonNode::isMissingNode, ignore -> null));
        fromNodeFunctionBuilder.add(Pair.of(JsonNode::isBinary, node -> {
            try {
                return node.binaryValue();
            } catch (IOException ignore) {
                // 理论上这个异常不会发生, 可忽略
                return null;
            }
        }));
        GET_OBJECT_FUNCTION_LIST = fromNodeFunctionBuilder.build();
    }

    private final ObjectNode root;

    private SolutionTaskConfig(ObjectNode root) {
        this.root = root;
    }

    public static SolutionTaskConfig fromJson(String json) {
        ObjectNode jsonNode = JsonUtils.fromJson(json).withObject("");
        if (jsonNode == null) {
            throw new IllegalArgumentException("config json is not a object, json: " + json);
        }
        return new SolutionTaskConfig(jsonNode);
    }

    public static SolutionTaskConfig fromObject(Object object) {
        ObjectNode jsonNode = buildNode(object).withObject("");
        if (jsonNode == null) {
            throw new IllegalArgumentException("The object to convert  is not a object, json: " + object);
        }
        return new SolutionTaskConfig(jsonNode);
    }

    public static SolutionTaskConfig newDefault() {
        return new SolutionTaskConfig(JsonNodeFactory.instance.objectNode());
    }

    /**
     * get方法目前的使用场景只有单测
     *
     * @param ptr 路径
     * @return String object, or null if not found.
     */
    public Optional<String> getStringOption(SolutionTaskConfigPointer ptr) {
        return Optional.ofNullable(get0(ptr))
            .map(Object::toString);
    }

    public Optional<Number> getNumberOption(SolutionTaskConfigPointer ptr) {
        Object o = get0(ptr);
        if (o instanceof Number number) {
            return Optional.of(number);
        } else if (o instanceof String string) {
            try {
                return Optional.of(NumberFormat.getInstance().parse(string));
            } catch (final ParseException e) {
                // failure means null is returned
            }
        }
        return Optional.empty();
    }

    public Optional<Integer> getIntegerOption(SolutionTaskConfigPointer ptr) {
        return getNumberOption(ptr)
            .map(Number::intValue);
    }

    public Optional<Long> getLongOption(SolutionTaskConfigPointer ptr) {
        return getNumberOption(ptr)
            .map(Number::longValue);
    }

    public Optional<Short> getShortOption(SolutionTaskConfigPointer ptr) {
        return getNumberOption(ptr)
            .map(Number::shortValue);
    }

    public Optional<Double> getDoubleOption(SolutionTaskConfigPointer ptr) {
        return getNumberOption(ptr)
            .map(Number::doubleValue);
    }

    public Optional<Boolean> getBooleanOption(SolutionTaskConfigPointer ptr) {
        Object o = get0(ptr);
        if (o instanceof Boolean bool) {
            return Optional.of(bool);
        }
        if (o instanceof String string) {
            return Optional.of(Boolean.valueOf(string));
        }
        if (o instanceof Number number) {
            return Optional.of(number.intValue() != 0 ? Boolean.TRUE : Boolean.FALSE);
        }
        return Optional.empty();
    }

    public Optional<BigInteger> getBigIntegerOption(SolutionTaskConfigPointer ptr) {
        Object o = get0(ptr);
        if (o instanceof BigInteger bigInteger) {
            return Optional.of(bigInteger);
        }
        if (o instanceof String string) {
            return Optional.of(new BigInteger(string));
        }
        if (o instanceof BigDecimal bigDecimal) {
            return Optional.of(bigDecimal.toBigInteger());
        }
        if (o instanceof Number number) {
            return Optional.of(BigInteger.valueOf(number.longValue()));
        }
        return Optional.empty();
    }

    public Optional<BigDecimal> getBigDecimalOption(SolutionTaskConfigPointer ptr) {
        Object o = get0(ptr);
        if (o instanceof BigDecimal bigDecimal) {
            return Optional.of(bigDecimal);
        }
        if (o instanceof String string) {
            return Optional.of(new BigDecimal(string));
        }
        if (o instanceof BigInteger bigInteger) {
            return Optional.of(new BigDecimal(bigInteger));
        }
        if (o instanceof Number number) {
            return Optional.of(BigDecimal.valueOf(number.longValue()));
        }
        return Optional.empty();
    }

    public Optional<byte[]> getBinaryOption(SolutionTaskConfigPointer ptr) {
        Object o = get0(ptr);
        if (o instanceof byte[] bytes) {
            return Optional.of(bytes);
        }
        if (o instanceof String string) {
            return Optional.of(string.getBytes());
        }
        return Optional.empty();
    }

    public <T> List<T> getList(SolutionTaskConfigPointer ptr, TypeReferenceHolder<List<T>> typeReference) {
        List<T> result = get0(ptr, typeReference);
        return result == null ? Collections.emptyList() : result;
    }

    @SuppressWarnings("unused")
    public <T> Optional<T> getOption(SolutionTaskConfigPointer ptr, TypeReferenceHolder<T> typeReference) {
        return Optional.ofNullable(get0(ptr, typeReference));
    }

    public Optional<Object> getOption(SolutionTaskConfigPointer ptr) {
        return Optional.ofNullable(get0(ptr));
    }

    public void set(String key, Object value) {
        set(SolutionTaskConfigPointer.compile(key), value);
    }

    /**
     * 往NodeTree中插入一个新node, 根据被插入对象的不同, 有以下几种处理: <br>
     * <ul>
     *     <li>值对象将作为valueNode直接插入</li>
     *     <li>Collection类型的对象将作为ListNode直接插入, 其集合内的所有元素递归插入</li>
     *     <li>Map类型的对象将作为ObjectNode直接插入, 其集合内的所有元素递归插入</li>
     *     <li>其他POJO对象会先转化为值对象, collection对象, map对象, 然后插入到NodeTree</li>
     *     <li>某些类型的对象会进行一些特殊处理(比如Optional, SolutionTaskConfig等), 可以注意一下</li>
     * </ul>
     * 如果路径上的节点不符合预期, 则直接覆盖
     *
     * @param ptr config ptr
     * @param value object对象, 作为一个新的node对象插入到node tree中
     * @param overrideIfArray 如果路径上有array节点的处理方式: <br>
     *         true: 使用object节点覆盖原array节点; 如 a: [{b: 1}, {c: 1}] 插入一条 a.{d: 1} => a.{d: 1}<br>
     *         false: 插入的value对每一个array节点中的object节点都生效; 如 a: [{b: 1}, {c: 1}] 插入一条 a.{d: 1} => a: [{b: 1, d: 1}, {c: 1, d: 1}]
     */
    public void set(SolutionTaskConfigPointer ptr, Object value, boolean overrideIfArray) {
        set0(this.root, ptr.getJsonPointer(), value, true, overrideIfArray);
    }

    public void set(SolutionTaskConfigPointer ptr, Object value) {
        set(ptr, value, true);
    }

    public @Nullable Object replace(SolutionTaskConfigPointer ptr, Object value) {
        Object oldValue = get0(ptr);
        set0(this.root, ptr.getJsonPointer(), value, true, true);
        return oldValue;
    }

    /**
     * 类似于set方法, 但是与set方法不同的是add方法对于指针终点处的属性采用merge策略, 行为如下(a.b为ptr, ()中为插入的值): <br>
     * <ul>
     *     <li>a.b(1) + a.b(2) = a.b([1, 2]) --- 都是值对象则合并成数组</li>
     *     <li>a.b({x: 1}) + a.b.({y: 2}) = a.b({x: 1, y: 2}) --- 都是map对象, 则合并items</li>
     *     <li>a.b([1, 2]) + a.b.(3) = a.b([1, 2, 3]) --- 值对象和list对象合并成list对象</li>
     *     <li>a.b([1, 2]) + a.b.({x: 1}) = a.b({x: 1}) --- 如果是map对象和非map对象合并, 则新对象覆盖旧对象</li>
     *     <li>a.b({x: 1}) + a.b.(1) = a.b(1)</li>
     * </ul>
     *
     * @param ptr config ptr
     * @param value object对象, 作为一个新的node对象插入到node tree中
     */
    public void add(SolutionTaskConfigPointer ptr, Object value) {
        set0(this.root, ptr.getJsonPointer(), value, false, false);
    }

    /**
     * 不存在则插入
     */
    public void putIfAbsent(SolutionTaskConfigPointer ptr, Object value) {
        Object oldValue = get0(ptr);
        if (oldValue == null) {
            set0(this.root, ptr.getJsonPointer(), value, true, true);
        }
    }

    /**
     * 移除指定的一个配置
     *
     * @param ptr ptr
     * @return 当ptr指向空时返回empty, 否则返回被移除的对象
     */
    public Optional<Object> remove(SolutionTaskConfigPointer ptr) {
        return remove0(ptr);
    }

    public void replacePlaceholder(SolutionTaskConfigPointer startPtr, Placeholder placeholder) {
        JsonPointer ptr = startPtr.getJsonPointer();
        if (ptr == null || ptr.tail() == null) {
            return;
        }
        ObjectNode objectNode = this.root;
        StringBuilder prefix = new StringBuilder();
        while (!ptr.tail().matches()) {
            String key = ptr.getMatchingProperty();
            JsonNode currentNode = objectNode.get(ptr.getMatchingProperty());
            if (currentNode == null || !currentNode.isObject()) {
                return;
            }
            objectNode = (ObjectNode) currentNode;
            prefix.append(JsonPointer.SEPARATOR).append(key);
            ptr = ptr.tail();
        }
        String key = ptr.getMatchingProperty();
        replacePlaceholder(this.root, objectNode, objectNode.get(key), key, placeholder, prefix.toString());
    }

    public void mergeObject(SolutionTaskConfigPointer ptr, Object value) {
        ObjectNode oldNode = this.root.withObject(ptr.getJsonPointer());
        JsonNode newNode = buildNode(value);
        if (oldNode == null || !newNode.isObject()) {
            return;
        }
        oldNode.setAll((ObjectNode) newNode);
    }

    public SolutionTaskConfig deepCopy() {
        return new SolutionTaskConfig(this.root.deepCopy());
    }

    public String toJson() {
        return toString();
    }

    @Override
    public String toString() {
        return this.root.toString();
    }

    private Optional<Object> remove0(SolutionTaskConfigPointer configPointer) {
        JsonPointer ptr = configPointer.getJsonPointer();
        if (ptr == null || ptr.tail() == null) {
            return Optional.empty();
        }
        JsonPointer currentPtr = ptr;
        ObjectNode parentNode = this.root;

        while (!currentPtr.tail().matches()) {
            JsonNode currentNode = parentNode.get(currentPtr.getMatchingProperty());
            if (currentNode == null || !currentNode.isObject()) {
                return Optional.empty();
            }
            parentNode = (ObjectNode) currentNode;
            currentPtr = currentPtr.tail();
        }

        return Optional.ofNullable(getFromNode(parentNode.remove(currentPtr.getMatchingProperty())));
    }

    private <T> T get0(SolutionTaskConfigPointer configPointer, TypeReferenceHolder<T> typeReference) {
        return JsonUtils.convertValue(get0(configPointer), typeReference);
    }

    private Object get0(SolutionTaskConfigPointer configPointer) {
        return getFromNode(this.root.at(configPointer.getJsonPointer()));
    }

    private void set0(
        ObjectNode node,
        JsonPointer ptr,
        Object object,
        boolean override,
        boolean overrideIfArray
    ) {
        if (ptr == null || ptr.tail() == null) {
            return;
        }
        JsonPointer currentPtr = ptr;
        while (!currentPtr.tail().matches()) {
            JsonNode currentNode = node.get(currentPtr.getMatchingProperty());
            if (!overrideIfArray && currentNode != null && currentNode.isArray()) {
                // 如果路径上的节点是array形式, 且set模式是override, 递归地尝试赋值给array所有子节点
                JsonPointer finalCurrentPtr = currentPtr;
                currentNode.forEach(childNode -> {
                    if (childNode.isObject()) {
                        set0((ObjectNode) childNode, finalCurrentPtr.tail(), object, override, false);
                    }
                });
                return;
            }
            if (currentNode == null || !currentNode.isObject()) {
                // 其他情况下, 如果路径上的节点不为object节点, 直接覆盖
                currentNode = JsonNodeFactory.instance.objectNode();
                node.set(currentPtr.getMatchingProperty(), currentNode);
            }
            node = (ObjectNode) currentNode;
            currentPtr = currentPtr.tail();
        }

        setObject(node, currentPtr.getMatchingProperty(), object, override);
    }

    private void replacePlaceholder(
        ObjectNode contextNode,
        ObjectNode parentNode,
        JsonNode currentNode,
        String currentKey,
        Placeholder placeholder,
        String prefix
    ) {
        if (currentNode == null) {
            return;
        }
        if (currentNode.isArray()) {
            JsonPointer prefixPtr = JsonPointer.compile(prefix);
            List<JsonNode> actualNodes = new ArrayList<>();
            Iterator<JsonNode> iterator = currentNode.elements();
            while (iterator.hasNext()) {
                JsonNode childNode = iterator.next();
                if (childNode.isObject()) {
                    ObjectNode copiedRootNode = contextNode.deepCopy();
                    ObjectNode prefixNode = copiedRootNode.withObject(prefixPtr);
                    prefixNode.set(currentKey, childNode);
                    replacePlaceholder(copiedRootNode, parentNode, childNode, currentKey, placeholder, prefix);
                } else {
                    actualNodes.add(buildNode(placeholder.replacePlaceholder(contextNode, getFromNode(childNode))));
                    iterator.remove();
                }
            }
            ((ArrayNode) currentNode).addAll(actualNodes);
        } else if (currentNode.isObject()) {
            currentNode.fields().forEachRemaining(entry -> replacePlaceholder(contextNode, (ObjectNode) currentNode, entry.getValue(), entry.getKey(), placeholder, prefix + JsonPointer.SEPARATOR + currentKey));
        } else {
            Object actualObject = placeholder.replacePlaceholder(contextNode, getFromNode(currentNode));
            setObject(parentNode, currentKey, actualObject, true);
        }
    }

    public void setObject(ObjectNode parentNode, String key, Object value, boolean override) {
        JsonNode oldNode = parentNode.get(key);
        JsonNode newNode = buildNode(value);
        if (override || oldNode == null) {
            parentNode.set(key, newNode);
            return;
        }

        ArrayNode resultArrayNode = JsonNodeFactory.instance.arrayNode();
        if (oldNode.isArray()) {
            resultArrayNode.addAll((ArrayNode) oldNode);
        } else {
            resultArrayNode.add(oldNode);
        }

        if (newNode.isArray()) {
            resultArrayNode.addAll((ArrayNode) newNode);
        } else {
            resultArrayNode.add(newNode);
        }
        parentNode.set(key, resultArrayNode);
    }

    private static Object getFromNode(JsonNode node) {
        for (Pair<Predicate<JsonNode>, Function<JsonNode, Object>> functionPair : GET_OBJECT_FUNCTION_LIST) {
            if (functionPair.getLeft().test(node)) {
                return functionPair.getRight().apply(node);
            }
        }
        if (node.isArray()) {
            Iterator<JsonNode> elements = node.elements();
            List<Object> children = new ArrayList<>();
            while (elements.hasNext()) {
                children.add(getFromNode(elements.next()));
            }
            return children;
        } else if (node.isObject()) {
            Iterator<String> keyIterator = node.fieldNames();
            LinkedHashMap<String, Object> children = new LinkedHashMap<>();
            while (keyIterator.hasNext()) {
                String key = keyIterator.next();
                children.put(key, getFromNode(node.get(key)));
            }
            return children;
        }
        throw new IllegalArgumentException("illegal jsonNode, node: " + node);
    }

    private static JsonNode buildNode(Object object) {
        if (object instanceof JsonNode node) {
            return node;
        }
        if (object == null) {
            return JsonNodeFactory.instance.nullNode();
        } else if (object instanceof Collection) {
            List<JsonNode> childrenNode = new ArrayList<>((Collection<?>) object).stream().map(SolutionTaskConfig::buildNode).toList();
            return new ArrayNode(JsonNodeFactory.instance, childrenNode);
        } else if (object instanceof Map) {
            @SuppressWarnings("unchecked")
            Map<String, JsonNode> childrenNode = ((Map<String, ?>) object).entrySet().stream()
                .map(entry -> Pair.of(entry.getKey(), buildNode(entry.getValue())))
                .collect(Collectors.toMap(Pair::getLeft, Pair::getRight,
                    (u, v) -> {
                        throw new IllegalStateException(String.format("Duplicate key %s", u));
                    }, LinkedHashMap::new));
            return new ObjectNode(JsonNodeFactory.instance, childrenNode);
        } else {
            if (BUILD_NODE_FUNCTION_MAP.containsKey(object.getClass())) {
                return BUILD_NODE_FUNCTION_MAP.get(object.getClass()).apply(object);
            }
            return JsonUtils.fromJson(JsonUtils.toJson(object));
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SolutionTaskConfig that)) return false;
        return Objects.equals(root, that.root);
    }

    @Override
    public int hashCode() {
        return Objects.hash(root);
    }

    public static class Placeholder {

        private final Pattern pattern;

        private final SolutionTaskConfigPointer prefix;

        public Placeholder(Pattern pattern, SolutionTaskConfigPointer prefix) {
            this.pattern = pattern;
            this.prefix = prefix;
        }

        public static Placeholder of(Pattern pattern, SolutionTaskConfigPointer prefix) {
            return new Placeholder(pattern, prefix);
        }

        private Object replacePlaceholder(ObjectNode node, Object value) {
            Object result = value;
            if (value instanceof String str) {
                result = replaceDatasailVariables(node, str);
            }
            if (value instanceof List) {
                result = ((List<?>) value).stream()
                    .map(item -> {
                        Object replacedValue = item;
                        if (item instanceof String str) {
                            replacedValue = replaceDatasailVariables(node, str);
                        }
                        return replacedValue;
                    }).toList();
            }
            return result;
        }

        public String replaceDatasailVariables(ObjectNode node, String templateString) {
            Matcher matcher = this.pattern.matcher(templateString);
            StringBuilder builder = new StringBuilder();
            while (matcher.find()) {
                String key = matcher.group(1);
                String value = Optional.ofNullable(node.at(prefix.append(key).getJsonPointer())).map(SolutionTaskConfig::getFromNode).map(String::valueOf).orElse("");
                matcher.appendReplacement(builder, value);
            }
            matcher.appendTail(builder);
            String result = builder.toString();
            return StringUtils.isEmpty(result) ? templateString : result;
        }

    }
}
