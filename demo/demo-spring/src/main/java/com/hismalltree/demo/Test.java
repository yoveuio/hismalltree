package com.hismalltree.demo;

import com.google.common.collect.Sets;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;

/**
 * @author fangpeiyu.py
 */
public class Test {

    private Set<Character> OPERATORS = Sets.newHashSet('+', '-', '*', '/');
    private Set<Character> NUMS = Sets.newHashSet('0', '1', '2', '3', '4', '5', '6', '7', '8', '9');

    public Pair<Integer, Integer> getNextInt(String str) {
        int i = 0;
        while (i < str.length()) {
            if (str.charAt(i) == ' ') {
                break;
            }
            i++;
        }
        return Pair.of(Integer.parseInt(str.substring(0, i)), i);
    }

    public Integer calculate(String str) {
        int length = str.length();
        List<Character> operators = new ArrayList<>();
        List<Integer> nums = new ArrayList<>();
        for (int i = 0; i < length; i++) {
            if (OPERATORS.contains(str.charAt(i))) {
                operators.add(str.charAt(i));
            }
            if (NUMS.contains(str.charAt(i))) {
                Pair<Integer, Integer> pair = getNextInt(str.substring(i));
                nums.add(pair.getLeft());
                i = pair.getRight();
            }
        }
        return dfs(operators, nums, 0);
    }

    int dfs(List<Character> operators, List<Integer> nums, int i) {
        if (i >= nums.size()) {
            return 0;
        }
        if (i == nums.size() - 1) {
            return nums.get(i);
        }
        Integer num = nums.get(i);
        Integer nextNum = nums.get(i + 1);
        Character operator = operators.get(i);
        // 1 + 2 - 3
        switch (operator) {
            case '+':
                return num + dfs(operators, nums, i + 1);
            case '-':
                return num - dfs(operators, nums, i + 1);
            case '*':
                return num * nextNum + dfs(operators, nums, i + 2);
            case '/':
                return num / nextNum + dfs(operators, nums, i + 2);
            default:
                throw new UnsupportedOperationException();
        }
    }

    public static void main(String[] args) {
        Test test = new Test();
        System.out.println(test.calculate("1 + 2"));
    }

}


