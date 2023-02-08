package com.mgu.reactive.tutorial;

import com.mgu.reactive.tutorial.entity.User;

import java.util.List;
import java.util.stream.LongStream;

public class UserFixture {

    public static List<User> getDefaultUsers() {
        return LongStream.of(1, 2, 3)
                .mapToObj(i -> new User(i, "user-"+i))
                .toList();
    }

    public static String defaultUsersAsString = "[{\"id\":1,\"name\":\"user-1\"},{\"id\":2,\"name\":\"user-2\"},{\"id\":3,\"name\":\"user-3\"}]";
}
