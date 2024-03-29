package tv.dyndns.kishibe.qmaclone.client.game.panel;

import tv.dyndns.kishibe.qmaclone.client.game.AnswerView;
import tv.dyndns.kishibe.qmaclone.client.game.AnswerViewImpl;
import tv.dyndns.kishibe.qmaclone.client.game.SessionData;
import tv.dyndns.kishibe.qmaclone.client.game.input.InputWidget;
import tv.dyndns.kishibe.qmaclone.client.game.input.InputWidgetGroup;
import tv.dyndns.kishibe.qmaclone.client.game.sentence.WidgetProblemSentence;
import tv.dyndns.kishibe.qmaclone.client.game.sentence.WidgetProblemSentenceNormal;
import tv.dyndns.kishibe.qmaclone.client.packet.PacketProblem;

public class QuestionPanelGroup extends QuestionPanel {
	public QuestionPanelGroup(PacketProblem problem, SessionData sessionData) {
		super(problem, sessionData);
	}

	@Override
	protected WidgetProblemSentence createWidgetProblemSentence() {
		return new WidgetProblemSentenceNormal(problem);
	}

	@Override
	protected AnswerView createAnswerView() {
		return new AnswerViewImpl(1024);
	}

	@Override
	protected InputWidget createWidgetInput() {
		return new InputWidgetGroup(problem, answerView, this, getSessionData());
	}
}
