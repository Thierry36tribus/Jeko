package jobs;

import models.Weather;
import play.jobs.Job;
import play.jobs.On;

/**
 * Toutes les heures à l'heure pile cf http://www.cronmaker.com/
 * */
@On("0 0 0/1 1/1 * ? *")
public class UpdateWeatherJob extends Job {

	@Override
	public void doJob() {
		Weather.update();
	}
}
