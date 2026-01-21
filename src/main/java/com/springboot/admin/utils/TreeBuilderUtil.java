package com.springboot.admin.utils;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * 树形结构构建工具类（泛型）
 *
 * 功能：
 * 1️⃣ 将平铺列表构建为树
 * 2️⃣ 支持泛型节点，灵活指定 ID、ParentID、Children 字段
 * 3️⃣ 自动递归对子节点进行排序（可按任意字段）
 *
 * 使用场景：
 * - 系统菜单、组织架构、部门等树形数据
 *
 * @param <T> 节点类型
 * @param <K> ID 类型
 */
public class TreeBuilderUtil<T, K extends Comparable<K>> {

    /**
     * 构建树
     *
     * @param list        平铺节点列表
     * @param idGetter    获取节点 ID 的方法
     * @param parentIdGetter 获取节点父ID的方法
     * @param childrenGetter 获取节点子节点 List 的方法
     * @param childrenSetter 设置节点子节点的方法
     * @param rootParentId 根节点的 parentId
     * @param comparator  子节点排序规则
     * @return 树形结构列表
     */
    public static <T, K extends Comparable<K>> List<T> buildTree(
            List<T> list,
            Function<T, K> idGetter,
            Function<T, K> parentIdGetter,
            Function<T, List<T>> childrenGetter,
            BiConsumer<T, List<T>> childrenSetter,
            K rootParentId,
            Comparator<T> comparator
    ) {
        Map<K, T> nodeMap = new HashMap<>();
        List<T> tree = new ArrayList<>();

        // 先平铺映射 ID -> 节点
        for (T node : list) {
            nodeMap.put(idGetter.apply(node), node);
        }

        // 构建树结构
        for (T node : list) {
            K parentId = parentIdGetter.apply(node);
            if (parentId == null || parentId.equals(rootParentId)) {
                tree.add(node);
            } else {
                T parent = nodeMap.get(parentId);
                if (parent != null) {
                    List<T> children = childrenGetter.apply(parent);
                    if (children == null) {
                        children = new ArrayList<>();
                        childrenSetter.accept(parent, children);
                    }
                    children.add(node);
                }
            }
        }

        // 对树递归排序
        tree.sort(comparator);
        tree.forEach(n -> sortChildren(n, childrenGetter, comparator));

        return tree;
    }

    /**
     * 递归对子节点排序
     */
    private static <T> void sortChildren(T node, Function<T, List<T>> childrenGetter, Comparator<T> comparator) {
        List<T> children = childrenGetter.apply(node);
        if (children != null && !children.isEmpty()) {
            children.sort(comparator);
            children.forEach(child -> sortChildren(child, childrenGetter, comparator));
        }
    }
}
