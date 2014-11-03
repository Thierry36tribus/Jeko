package visual;

import java.util.List;

import models.Saving;
import models.VisualSavingElement;

public interface VisualElementsCreator {

	List<VisualSavingElement> create(Saving saving);
}
