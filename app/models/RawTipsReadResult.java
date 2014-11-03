package models;

import java.util.ArrayList;
import java.util.List;

public class RawTipsReadResult {

	public List<EcoTip> tips;

	public RawTipsReadResult() {
		super();
		this.tips = new ArrayList();
	}

	@Override
	public String toString() {
		return "RawTipsReadResult [tips=" + tips + "]";
	}

}
