package nl.svsticky.crazy88.http.routes.submissions;

import nl.svsticky.crazy88.App;
import nl.svsticky.crazy88.config.model.ConfigModel;
import nl.svsticky.crazy88.database.driver.Driver;
import nl.svsticky.crazy88.http.HttpRequest;
import nl.svsticky.crazy88.http.HttpResponse;
import nl.svsticky.crazy88.http.RequestHandler;
import nl.svsticky.crazy88.http.response.ByteHttpResponse;
import nl.svsticky.crazy88.http.response.EmptyHttpResponse;
import nl.svsticky.crazy88.http.response.StringHttpResponse;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class GetSubmissionRoute implements RequestHandler {
    private final ConfigModel configModel;
    private final Driver driver;

    public GetSubmissionRoute(ConfigModel configModel, Driver driver) {
        this.configModel = configModel;
        this.driver = driver;
    }

    private record Either<L, R>(Optional<L> left, Optional<R> right) {
        public static <L, R> Either<L, R> left(L left) {
            return new Either<>(Optional.of(left), Optional.empty());
        }

        public static <L, R> Either<L, R> right(R right) {
            return new Either<>(Optional.empty(), Optional.of(right));
        }
    }

    @Override
    public HttpResponse handle(HttpRequest request) throws IOException {
        Either<Integer, HttpResponse> mTeamId = getIntParameter(request, "teamId");
        Either<Integer, HttpResponse> mAssignmentId = getIntParameter(request, "assignmentId");

        if(mTeamId.right().isPresent()) {
            return mTeamId.right().get();
        }

        if(mAssignmentId.right().isPresent()) {
            return mAssignmentId.right().get();
        }

        //noinspection OptionalGetWithoutIsPresent
        int teamId = mTeamId.left().get();
        //noinspection OptionalGetWithoutIsPresent
        int assignmentId = mAssignmentId.left().get();

        File submissionDir = new File(configModel.submit.submissionDirectory);
        File[] children = submissionDir.listFiles();

        Optional<File> mSubmissionFile = Arrays.stream(children)
                .filter(file -> {
                    String[] nameParts = file.getName().split(Pattern.quote("."));
                    if(nameParts.length < 2) {
                        return false;
                    }

                    String name = String.join(".", Arrays.copyOfRange(nameParts, 0, nameParts.length - 1));
                    return name.equals(String.format("%d_%d", teamId, assignmentId));
                })
                .findFirst();

        if(mSubmissionFile.isEmpty()) {
            return new StringHttpResponse(request, 404, "No submission exist for the combination of parameters provided");
        }

        byte[] contents;
        try(FileInputStream fis = new FileInputStream(mSubmissionFile.get())) {
            contents = fis.readAllBytes();
        } catch (IOException e) {
            App.getLogger().error(e);
            return new EmptyHttpResponse(request, 500);
        }

        String ext = Arrays.stream(mSubmissionFile.get().getName().split(Pattern.quote("."))).toList().getLast();
        if(ext.equals("jpg")) ext = "jpeg";

        return new ByteHttpResponse(request, 200, contents, String.format("image/%s", ext));
    }

    private Either<Integer, HttpResponse> getIntParameter(HttpRequest request, String name) throws IOException {
        try {
            Optional<String> mTeamId = request.getQueryParameter(name);
            if(mTeamId.isEmpty()) {
                return Either.right(new StringHttpResponse(request, 400, String.format("Missing required parameter '%s'", name)));
            }

            return Either.left(Integer.parseInt(mTeamId.get()));
        } catch (NumberFormatException e) {
            return Either.right(new StringHttpResponse(request, 400, String.format("Invalid number for parameter '%s'", name)));
        } catch (IllegalArgumentException e) {
            return Either.right(new StringHttpResponse(request, 400, e.getMessage()));
        }
    }
}
