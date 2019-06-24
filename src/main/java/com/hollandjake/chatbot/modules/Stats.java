package com.hollandjake.chatbot.modules;

import com.hollandjake.chatbot.Chatbot;
import com.hollandjake.chatbot.utils.CONSTANTS;
import com.hollandjake.chatbot.utils.CommandModule;
import com.hollandjake.messengerBotAPI.message.Message;
import com.hollandjake.messengerBotAPI.message.MessageComponent;
import com.hollandjake.messengerBotAPI.message.Text;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.concurrent.TimeUnit;


public class Stats implements CommandModule {
	private final String STATS_REGEX = CONSTANTS.ACTIONIFY("stats");
	private final String UPTIME_REGEX = CONSTANTS.ACTIONIFY("uptime");
	private final String PUPTIME_REGEX = CONSTANTS.ACTIONIFY("puptime");
	private final Chatbot chatbot;

	public Stats(Chatbot chatbot) {
		this.chatbot = chatbot;
	}

	@Override
	public boolean process(Message message) {
		for (MessageComponent component : message.getComponents()) {
			if (component instanceof Text) {
				String text = ((Text) component).getText();
				if (text.matches(STATS_REGEX)) {
					chatbot.sendMessage(getStats());
					return true;
				} else if (text.matches(UPTIME_REGEX) || text.matches(PUPTIME_REGEX)) {
					chatbot.sendMessage(getUptime());
					return true;
				}
			}
		}
		return false;
	}

	public String getMinifiedStats() {
		String var10000 = this.chatbot.getVersion();
		return "Version: " + var10000 + "\nJava version: " + System.getProperty("java.version") + "\nOperating System: " + System.getProperty("os.name");
	}

	private String getStats() {
		String minifiedStats = this.getMinifiedStats();
		return minifiedStats + "\n\n" + this.getUptime() + "\nUnique messages read this session: " + this.chatbot.getNumMessages();
	}

	private String getUptime() {
		LocalDateTime startupTime = this.chatbot.getStartup();
		LocalDateTime now = LocalDateTime.now();
		long diff = now.toEpochSecond(ZoneOffset.UTC) - startupTime.toEpochSecond(ZoneOffset.UTC);
		long diffSeconds = TimeUnit.SECONDS.convert(diff, TimeUnit.SECONDS) % 60;
		long diffMinutes = TimeUnit.MINUTES.convert(diff, TimeUnit.SECONDS) % 60;
		long diffHours = TimeUnit.HOURS.convert(diff, TimeUnit.SECONDS) % 24;
		long diffDays = TimeUnit.DAYS.convert(diff, TimeUnit.SECONDS);
		String started = CONSTANTS.DATE_TIME_FORMATTER.format(startupTime);
		return "I've been running since " + started +
				"\n[" + (diffDays > 0 ? diffDays + " day" + (diffDays != 1 ? "s" : "") + " " : "") +
				(diffHours > 0 ? diffHours + " hour" + (diffHours != 1 ? "s" : "") + " " : "") +
				(diffMinutes > 0 ? diffMinutes + " minute" + (diffMinutes != 1 ? "s" : "") + " " : "") +
				diffSeconds + " second" + (diffSeconds != 1 ? "s" : "") + "]";
	}
}
