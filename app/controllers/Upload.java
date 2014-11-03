package controllers;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import models.ConsumptionFile;
import models.EcoTip;
import models.RawSavingsReadResult;
import models.RawTipsReadResult;
import models.Saving;
import models.TipsFile;

import org.apache.commons.io.FileUtils;

import play.Logger;
import play.mvc.Controller;
import play.mvc.With;
import processors.Notifier;
import processors.RawSavingsXlsReader;
import processors.RawTipsXlsReader;
import processors.SavingsStorer;
import controllers.minifymod.MinifyAndGzipResponse;

@With({ Secure.class, MinifyAndGzipResponse.class })
@Check("admin")
public class Upload extends Controller {

	private static final SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");

	public static void uploadSavings(final File file) {
		final File localFile = getLocalFileFromName(file.getName(), "cons");
		try {
			FileUtils.copyFile(file, localFile);
			final RawSavingsReadResult result = readConsFile(localFile);

			final ConsumptionFile consFile = new ConsumptionFile(new Date(), file.getName(), localFile.getName(),
					result.year, result.week);
			consFile.create();
			renderJSON(Utils.gson().toJson(new Object[] { consFile, result.savings }));
		} catch (final Exception e) {
			Logger.error(e, "unable to upload file %s in local file %s", file.getName(), localFile.getAbsolutePath());
			error(500, "Unable to upload file");
		}
	}

	public static void importSavings(final long consFileId) {
		try {
			final ConsumptionFile consFile = ConsumptionFile.findById(consFileId);
			final RawSavingsReadResult result = readConsFile(getLocalFileFromLocalName(consFile.localName, "cons"));
			final SavingsStorer storer = new SavingsStorer();
			storer.store(result.year, result.week, result.savings);
			consFile.uploadDate = new Date();
			consFile.imported = true;
			consFile.save();
		} catch (final Exception e) {
			Logger.error(e, "unable to import fileId %s ", consFileId);
			error(500, "Unable to import file");
		}
		ok();
	}

	public static void notifyUsers(final int year, final int week) {
		try {
			Notifier.savingsUploaded(year, week);
		} catch (final Exception ex) {
			Logger.error(ex, "Unable to send notifications for 'savingsUploaded'");
		}
		ok();
	}

	public static void consFiles() {
		final List<ConsumptionFile> consFiles = ConsumptionFile.find("order by uploadDate desc").fetch(50);
		renderJSON(Utils.gson().toJson(consFiles));
	}

	private static final RawSavingsReadResult readConsFile(final File localFile) {
		final RawSavingsXlsReader reader = new RawSavingsXlsReader(Saving.FIRST_WEEK);
		final RawSavingsReadResult result = reader.read(localFile);
		Logger.debug("File %s contains data for week %s of %s", localFile.getPath(), result.week, result.year);
		return result;
	}

	public static void tipsFiles() {
		final List<TipsFile> consFiles = TipsFile.find("order by uploadDate desc").fetch(50);
		renderJSON(Utils.gson().toJson(consFiles));
	}

	public static void uploadTips(final File file) {
		final File localFile = getLocalFileFromName(file.getName(), "tips");
		try {
			FileUtils.copyFile(file, localFile);
			final RawTipsReadResult result = readTipsFile(localFile);

			final TipsFile tipsFile = new TipsFile(new Date(), file.getName(), localFile.getName());
			tipsFile.create();
			renderJSON(Utils.gson().toJson(new Object[] { tipsFile, result.tips }));
		} catch (final Exception e) {
			Logger.error(e, "unable to upload file %s in local file %s", file.getName(), localFile.getAbsolutePath());
			error(500, "Unable to upload file");
		}
	}

	public static void importTips(final long tipsFileId) {
		try {
			final TipsFile tipsFile = TipsFile.findById(tipsFileId);
			final RawTipsReadResult result = readTipsFile(getLocalFileFromLocalName(tipsFile.localName, "tips"));
			EcoTip.deleteAll();
			for (final EcoTip tip : result.tips) {
				tip.create();
			}
			tipsFile.uploadDate = new Date();
			tipsFile.imported = true;
			tipsFile.save();
		} catch (final Exception e) {
			Logger.error(e, "unable to import fileId %s ", tipsFileId);
			error(500, "Unable to import file");
		}
		ok();
	}

	private static final RawTipsReadResult readTipsFile(final File localFile) {
		final RawTipsXlsReader reader = new RawTipsXlsReader();
		final RawTipsReadResult result = reader.read(localFile);
		Logger.debug("File %s contains %s tips", localFile.getPath(), result.tips.size());
		return result;
	}

	private static File getLocalFileFromName(final String name, final String folder) {
		return getLocalFileFromLocalName(SDF.format(new Date()) + "_" + name, folder);

	}

	private static File getLocalFileFromLocalName(final String localName, final String folder) {
		return new File("data/" + folder, localName);

	}
}
