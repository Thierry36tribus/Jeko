package models;

import javax.persistence.Entity;

import play.db.jpa.Model;

/** Utilisé pour les questions du JekoQuizz */
@Entity
public class EcoTip extends Model {

	public String question;
	public String goodAnswer;
	public String answer2;
	public String answer3;
	public String answer4;

	public EcoTip(final String question, final String goodAnswer, final String answer2, final String answer3,
			final String answer4) {
		super();
		this.question = question;
		this.goodAnswer = goodAnswer;
		this.answer2 = answer2;
		this.answer3 = answer3;
		this.answer4 = answer4;
	}

	@Override
	public String toString() {
		return "EcoTip [question=" + question + ", goodAnswer=" + goodAnswer + ", answer2=" + answer2 + ", answer3="
				+ answer3 + ", answer4=" + answer4 + "]";
	}

}
