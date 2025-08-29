package socialmedia;

public interface SocialMediaActions {
	
	void post(String content);

    void deletePost(int index);

    void comment(int postIndex, String comment);

    void follow(String user, String platform);

    void unfollow(String user, String platform);

}
