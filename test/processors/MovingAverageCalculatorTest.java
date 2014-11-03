package processors;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import models.Dwelling;
import models.Saving;

import org.junit.Test;

public class MovingAverageCalculatorTest {

	@Test
	public void test() {

		final List<Saving> savings = new ArrayList();
		final Dwelling dwelling = new Dwelling();
		dwelling.surface = 100;
		savings.add(new Saving(2014, 28, dwelling, 0, 0, 0.1, 0.5, 0, 20, 14, 0, 3000, 1500, 0, 0.9, 0.8));
		savings.add(new Saving(2014, 27, dwelling, 0, 0, 0.5, 0.4, 0, 14, 12, 0, 1500, 2000, 0, 0.8, 0.7));
		savings.add(new Saving(2014, 26, dwelling, 0, 0, 0.4, 0.5, 0, 12, 10, 0, 2000, 1000, 0, 0.7, 0.6));
		savings.add(new Saving(2014, 25, dwelling, 0, 0, 0.5, 0.4, 0, 10, 17, 0, 1000, 1300, 0, 0.6, 0.5));
		savings.add(new Saving(2014, 24, dwelling, 0, 0, 0.4, 0.4, 0, 17, 17, 0, 1300, 1300, 0, 0.5, 0.5));

		final MovingAverageCalculator calc = new MovingAverageCalculator();

		assertEquals("sem 24", 107, calc.calculate(savings, 24).movingAverageConsumption, 0.01);
		assertEquals("sem 25", 113.5, calc.calculate(savings, 25).movingAverageConsumption, 0.01);
		assertEquals("sem 26", 116.33, calc.calculate(savings, 26).movingAverageConsumption, 0.01);
		assertEquals("sem 27", 123.25, calc.calculate(savings, 27).movingAverageConsumption, 0.01);
		assertEquals("sem 28", 126.5, calc.calculate(savings, 28).movingAverageConsumption, 0.01);
		assertEquals("sem 28 ref", 123.25, calc.calculate(savings, 28).movingAverageRef, 0.01);
		assertEquals("sem 28 saving", -0.02, calc.calculate(savings, 28).saving, 0.01);
	}
}
