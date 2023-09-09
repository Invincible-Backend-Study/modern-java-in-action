package com.baeldung.modules.hello.impl;

import com.baeldung.modules.hello.HelloInterface;

public class HelloImpl implements HelloInterface {

    @Override
    public void sayHello() {
        System.out.println("Hello!");
    }
}
