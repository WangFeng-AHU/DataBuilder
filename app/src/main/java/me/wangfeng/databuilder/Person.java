package me.wangfeng.databuilder;

import me.wangfeng.annotation.Builder;

/**
 * Created by wangfeng on 17/2/18.
 */
@Builder
public class Person {

    long id;
    String name;
    int age;

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }
}
