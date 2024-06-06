package nl.svsticky.crazy88.http.routes;

import java.util.Optional;

public record Either<L, R>(Optional<L> left, Optional<R> right) {
    public static <L, R> Either<L, R> left(L left) {
        return new Either<>(Optional.of(left), Optional.empty());
    }

    public static <L, R> Either<L, R> right(R right) {
        return new Either<>(Optional.empty(), Optional.of(right));
    }
}
