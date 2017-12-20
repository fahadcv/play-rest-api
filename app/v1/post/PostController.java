package v1.post;

import com.fasterxml.jackson.databind.JsonNode;
import dispatcher.EmailService;
import play.libs.Json;
import play.libs.concurrent.HttpExecutionContext;
import play.mvc.*;

import javax.inject.Inject;
import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;

@With(PostAction.class)
public class PostController extends Controller {

    private HttpExecutionContext ec;
    private PostResourceHandler handler;
    private EmailService emailService;

    @Inject
    public PostController(HttpExecutionContext ec, PostResourceHandler handler, EmailService emailService) {
        this.ec = ec;
        this.handler = handler;
        this.emailService = emailService;
    }

    public CompletionStage<Result> list() {
        return handler.find().thenApplyAsync(posts -> {
            final List<PostResource> postList = posts.collect(Collectors.toList());
            return ok(Json.toJson(postList));
        }, ec.current());
    }

    public CompletionStage<Result> show(String id) {
        return handler.lookup(id).thenApplyAsync(optionalResource -> {
            return optionalResource.map(resource ->
                ok(Json.toJson(resource))
            ).orElseGet(() ->
                notFound()
            );
        }, ec.current());
    }

    public CompletionStage<Result> update(String id) {
        JsonNode json = request().body().asJson();
        PostResource resource = Json.fromJson(json, PostResource.class);
        return handler.update(id, resource).thenApplyAsync(optionalResource -> {
            return optionalResource.map(r ->
                    ok(Json.toJson(r))
            ).orElseGet(() ->
                    notFound()
            );
        }, ec.current());
    }

    public CompletionStage<Result> create() {
        JsonNode json = request().body().asJson();
        final PostResource resource = Json.fromJson(json, PostResource.class);
        return handler.create(resource).thenApplyAsync(savedResource -> {
            emailService.sendEmail("fahadcv@gmail.com", "fahadcv@gmail.com", resource.getTitle(), resource.getBody());
            return created(Json.toJson(savedResource));
        }, ec.current());
    }

    public Result upload() {
//        JsonNode json = request().body().asJson();
//        final PostResource resource = Json.fromJson(json, PostResource.class);
        Http.MultipartFormData<File> body = request().body().asMultipartFormData();
        Http.MultipartFormData.FilePart<File> picture = body.getFile("picture");
        if (picture != null) {
            Map<String, String[]> formParams = body.asFormUrlEncoded();
            System.out.println("formParams " + formParams);

            String fileName = picture.getFilename();
            String contentType = picture.getContentType();
            File file = picture.getFile();
//            final PostResource resource = Json.fromJson(json, PostResource.class);
            emailService.sendEmail("fahadcv@gmail.com", "fahadcv@gmail.com", formParams.get("title")[0], formParams.get("body")[0], file, fileName);
            return ok("File "+fileName+" uploaded");
        } else {
            flash("error", "Missing file");
            return badRequest();
        }
    }
}
