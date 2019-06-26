package com.hollandjake.chatbot.modules;

import com.hollandjake.chatbot.Chatbot;
import com.hollandjake.chatbot.utils.DatabaseCommandModule;
import com.hollandjake.messengerBotAPI.message.Message;
import com.hollandjake.messengerBotAPI.message.MessageComponent;
import com.hollandjake.messengerBotAPI.message.Text;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static com.hollandjake.chatbot.utils.CONSTANTS.GET_RANDOM;

public abstract class RedditModule extends DatabaseCommandModule {

	//region Constants
	private final List<String> REGEXES;
	//endregion

	//region Database statements
	private PreparedStatement GET_SUBREDDITS_STMT;
	private PreparedStatement GET_RESPONSE_STMT;
	//endregion

	public RedditModule(Chatbot chatbot, List<String> regexes) {
		super(chatbot);
		REGEXES = regexes;
	}

	@Override
	public void prepareStatements(Connection connection) throws SQLException {
		updateModuleId(connection);

		GET_SUBREDDITS_STMT = connection.prepareStatement("" +
				"SELECT" +
				"   link " +
				"FROM subreddit S " +
				"WHERE module_id = " + MOD_ID);

		GET_RESPONSE_STMT = connection.prepareStatement("" +
				"SELECT" +
				"   text " +
				"FROM text T " +
				"JOIN response_text rt on T.text_id = rt.text_id " +
				"WHERE module_id = " + MOD_ID +
				" ORDER BY RAND() " +
				"LIMIT 1");
	}

	@Override
	public boolean process(Message message) {
		for (MessageComponent component : message.getComponents()) {
			if (component instanceof Text) {
				String text = ((Text) component).getText();
				for (String regex : REGEXES) {
					if (text.matches(regex)) {
						String response = getResponse();
						String image = getImage();
						chatbot.sendMessageWithImage(response, image);
						return true;
					}
				}
			}
		}
		return false;
	}

	protected String getImage() {
		List<String> subreddits = getSubreddits();
		while (subreddits != null && subreddits.size() > 0) {
			String image = Reddit.getSubredditPicture(GET_RANDOM(subreddits));
			if (image != null) {
				return image;
			}
		}
		return null;
	}

	protected String getResponse() {
		try {
			chatbot.checkDbConnection();
			ResultSet resultSet = GET_RESPONSE_STMT.executeQuery();
			if (resultSet.next()) {
				return resultSet.getString("text");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	public List<String> getSubreddits() {
		List<String> subreddits = new ArrayList<>();
		try {
			chatbot.checkDbConnection();
			ResultSet resultSet = GET_SUBREDDITS_STMT.executeQuery();
			while (resultSet.next()) {
				subreddits.add(resultSet.getString("link"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return subreddits;
	}
}