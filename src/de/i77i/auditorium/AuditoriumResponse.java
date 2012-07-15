package de.i77i.auditorium;

public class AuditoriumResponse {
	
	private String message;
	private int id, comments;
	private boolean isNewPost;
	
	public AuditoriumResponse(int id, String message, boolean isNewPost, int comments) {
		this.id = id;
		this.message = message;
		this.comments = comments;
		this.isNewPost = isNewPost;
	}

	public String getMessage() {
		return message;
	}

	public int getId() {
		return id;
	}

	public int getComments() {
		return comments;
	}

	public boolean isNewPost() {
		return isNewPost;
	}


}
