package br.com.abeatrizdev.account_service.utils;

import java.util.stream.Stream;

public class UtilsTest {

    public static Stream<String> invalidUuidProvider() {
        return Stream.of(
                "10",
                "null",
                "invalid-uuid-format-test",
                "123e4567-e89b-12d3-a456",
                "123e4567-e89b-12d3-a456-426614174000-extra",
                "123e4567e89b12d3a456426614174000",
                "123e4567e89b-12d3-a456-426614174000",
                "123g4567-e89h-12d3-a456-426614174000",
                "123e4567 e89b 12d3 a456 426614174000",
                "123e4567-e89b-12d3-a456-42661417400@",
                "123e4567-e89b-12d3-a456-426614174😂!#",
                "550e8400-e29b-41d4-a716-44665544000g",
                "550e8400-e29b-41d4-a716-44665544000z",
                "gggggggg-gggg-gggg-gggg-gggggggggggg",
                "550e8400-e29b-41d4-a716-44665544000#",
                "550e8400-e29b-41d4-a716-44665544000$"
        );
    }
}
