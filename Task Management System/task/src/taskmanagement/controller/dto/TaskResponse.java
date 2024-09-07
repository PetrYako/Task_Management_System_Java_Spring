package taskmanagement.controller.dto;

public class TaskResponse {
    private String id;
    private String title;
    private String description;
    private String status;
    private String author;

    public TaskResponse(String id, String title, String description, String status, String author) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.status = status;
        this.author = author;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getStatus() {
        return status;
    }

    public String getAuthor() {
        return author;
    }
}
