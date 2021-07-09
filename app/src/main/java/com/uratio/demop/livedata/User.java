package com.uratio.demop.livedata;

/**
 * @author lang
 * @data 2021/6/29
 */
public class User {
    public String id;
    public String name;
    public String lastName;

    public User(String id) {
        this.id = id;
    }

    public User(String name, String lastName) {
        this.name = name;
        this.lastName = lastName;
    }

    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", lastName='" + lastName + '\'' +
                '}';
    }
}
