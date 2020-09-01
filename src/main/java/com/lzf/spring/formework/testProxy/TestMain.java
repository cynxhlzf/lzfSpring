package com.lzf.spring.formework.testProxy;

public class TestMain {
    public static void main(String[] args) {
        Person obj = (Person)new JDKMeipo().getInstance(new Customer());
        obj.findLove();
    }
}
