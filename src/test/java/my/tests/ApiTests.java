package my.tests;

import de.vandermeer.asciitable.AsciiTable;
import de.vandermeer.skb.interfaces.transformers.textformat.TextAlignment;
import dev.mccue.guava.collect.Ordering;
import my.tests.requests.HttpUtil;
import my.tests.responses.Comment;
import my.tests.responses.Post;
import my.tests.utils.Mapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ApiTests {

    private final Logger logger = LoggerFactory.getLogger(ApiTests.class);

    private final String baseURIPosts = "https://jsonplaceholder.typicode.com/posts";
    private final String baseURIComments = "https://jsonplaceholder.typicode.com/comments";


    @Test(description = "Задание 1", priority = 1, groups = {"api"})
    public void getTable() {
        logger.info("TEST: " + Thread.currentThread().getStackTrace()[1].getMethodName());

        String responsePosts = HttpUtil.getRequest(baseURIPosts);
        String responseComments = HttpUtil.getRequest(baseURIComments);

        List<Post> posts = Mapper.jsonToListOfObject(responsePosts, Post.class);
        List<Comment> comments = Mapper.jsonToListOfObject(responseComments, Comment.class);


        Map<Integer, List<String>> map = comments.stream()
                .collect(Collectors.groupingBy(Comment::getPostId,
                        Collectors.mapping(Comment::getBody, Collectors.toList())));

        List<String> listOfComments = map.values()
                .stream()
                .map(elem -> elem.toString().replaceAll("\\[|]", ""))
                .collect(Collectors.toList());

        AsciiTable asciiTable = new AsciiTable();
        asciiTable.addRule();
        asciiTable.addRow("Пост", "Список комментариев к посту");
        asciiTable.addRule();

        for (int i = 0; i < posts.size(); i++) {
            asciiTable.addRow(posts.get(i).getBody(), listOfComments.get(i));
            asciiTable.addRule();
        }
        asciiTable.setTextAlignment(TextAlignment.CENTER);
        String render = asciiTable.render();
        System.out.println(render);
    }

    @Test(description = "Проверка результата по запросу выдачи постов", priority = 2, groups = {"api"})
    public void getPosts() {
        logger.info("TEST: " + Thread.currentThread().getStackTrace()[1].getMethodName());

        String responsePosts = HttpUtil.getRequest(baseURIPosts);
        List<Post> posts = Mapper.jsonToListOfObject(responsePosts, Post.class);

        List<Integer> postIds = posts.stream().map(Post::getId).collect(Collectors.toList());

        SoftAssert softAssert = new SoftAssert();
        softAssert.assertTrue(Ordering.natural().isOrdered(postIds), "ids в ответе не отсортированы по возрастанию");
        posts.forEach(post ->
                softAssert.assertTrue(
                        post.getUserId() != null &&
                                post.getId() != null &&
                                !post.getTitle().isEmpty() &&
                                !post.getBody().isEmpty(),
                        String.format("post c id %s не соответствует требованиям", post.getId())));
        softAssert.assertAll();
        logger.info("Проверки пройдены успешно");
    }

    @Test(description = "Проверка результата по запросу выдачи комментариев", priority = 3, groups = {"api"})
    public void getComments() {
        logger.info("TEST: " + Thread.currentThread().getStackTrace()[1].getMethodName());

        String responseComments = HttpUtil.getRequest(baseURIComments);
        List<Comment> comments = Mapper.jsonToListOfObject(responseComments, Comment.class);

        List<Integer> commentIds = comments.stream().map(Comment::getId).collect(Collectors.toList());

        SoftAssert softAssert = new SoftAssert();
        softAssert.assertTrue(Ordering.natural().isOrdered(commentIds), "ids в ответе не отсортированы по возрастанию");
        comments.forEach(comment ->
                softAssert.assertTrue(
                        comment.getPostId() != null &&
                                comment.getId() != null &&
                                !comment.getName().isEmpty() &&
                                !comment.getEmail().isEmpty() &&
                                !comment.getBody().isEmpty(),
                        String.format("комментарий c id %s не соответствует требованиям", comment.getId())));
        softAssert.assertAll();

        logger.info("Проверки пройдены успешно");
    }
}
