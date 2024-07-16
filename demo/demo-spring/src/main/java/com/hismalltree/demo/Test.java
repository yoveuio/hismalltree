package com.hismalltree.demo;

import com.google.common.collect.Sets;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;
import java.util.regex.Pattern;

/**
 * @author fangpeiyu.py
 */
public class Test {


    public static void main(String[] args) {
        Pattern compile = Pattern.compile("^abc");
        System.out.println(compile.matcher("abcd").find());
    }
}


