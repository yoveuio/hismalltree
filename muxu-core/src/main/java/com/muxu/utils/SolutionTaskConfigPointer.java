package com.muxu.utils;

import com.fasterxml.jackson.core.JsonPointer;

/**
 * 一般作为SolutionTaskConfig的入参, 用于快速查找指定路径下的配置(以`/`作为路径的分隔符)<br>
 * 其实就是对Jackson JsonPointer的封装, 从解耦角度上可以考虑单独实现一套, 不依赖Jackson<br>
 *
 * @author fangpeiyu
 * @see SolutionTaskConfig
 */
public class SolutionTaskConfigPointer {

    private final JsonPointer ptr;

    private final static String SYMBOL_SLASH = "/";
    private SolutionTaskConfigPointer(JsonPointer ptr) {
        this.ptr = ptr;
    }

    public static SolutionTaskConfigPointer compile(String path) {
        if (!path.startsWith(SYMBOL_SLASH)) {
            // 兜底逻辑
            path = SYMBOL_SLASH + path;
        }
        return new SolutionTaskConfigPointer(JsonPointer.compile(path));
    }

    public SolutionTaskConfigPointer append(String childPath) {
        return new SolutionTaskConfigPointer(ptr.appendProperty(childPath));
    }

    public JsonPointer getJsonPointer() {
        return this.ptr;
    }
}
