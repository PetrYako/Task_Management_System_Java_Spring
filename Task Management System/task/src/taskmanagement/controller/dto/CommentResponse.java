package taskmanagement.controller.dto;

public class CommentResponse {
    private String id;
    private String task_id;
    private String text;
    private String author;

    public CommentResponse(String id, String task_id, String text, String author) {
        this.id = id;
        this.task_id = task_id;
        this.text = text;
        this.author = author;
    }

    public String getId() {
        return id;
    }

    public String getTask_id() {
        return task_id;
    }

    public String getText() {
        return text;
    }

    public String getAuthor() {
        return author;
    }
}