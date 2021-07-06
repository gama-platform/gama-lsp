/*******************************************************************************************************
 *
 * msi.gama.util.file.GamlFileInfo.java, in plugin msi.gama.core, is part of the source code of the GAMA modeling and
 * simulation platform (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.util.file;

import static java.util.Arrays.asList;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class GamlFileInfo extends GamaFileMetaData {

	public static String BATCH_PREFIX = "***";
	public static String ERRORS = "errors detected";

	private final Collection<String> experiments;
	private final Collection<String> imports;
	private final Collection<String> uses;
	private final Collection<String> tags;
	public final boolean invalid;

	public GamlFileInfo(final long stamp, final Collection<String> imports, final Collection<String> uses,
			final Collection<String> exps, final Collection<String> tags) {
		super(stamp);
		invalid = stamp == Long.MAX_VALUE;
		this.imports = imports;
		this.uses = uses;
		this.experiments = exps;
		this.tags = tags;
	}

	public Collection<String> getImports() {
		return imports == null ? Collections.EMPTY_LIST : imports;
	}

	public Collection<String> getUses() {
		return uses == null ? Collections.EMPTY_LIST : uses;
	}

	public Collection<String> getTags() {
		return tags == null ? Collections.EMPTY_LIST : tags;
	}

	public Collection<String> getExperiments() {
		return experiments == null ? Collections.EMPTY_LIST : experiments;
	}

	public GamlFileInfo(final String propertyString) {
		super(propertyString);
		int size = 0;
		this.imports = null;
		this.uses = null;
		this.experiments = null;
		this.tags = null;
		this.invalid = true;
	}

	/**
	 * Method getSuffix()
	 *
	 * @see msi.gama.util.file.GamaFileMetaInformation#getSuffix()
	 */
	@Override
	public String getSuffix() {
		if (invalid)
			return ERRORS;
		final int expCount = experiments == null ? 0 : experiments.size();
		if (expCount > 0) { return "" + (expCount == 1 ? "1 experiment" : expCount + " experiments"); }

		return "no experiment";
	}

	@Override
	public void appendSuffix(final StringBuilder sb) {
		if (invalid) {
			sb.append(ERRORS);
			return;
		}
		final int expCount = experiments == null ? 0 : experiments.size();
		if (expCount > 0) {
			sb.append(expCount).append(" experiment");
			if (expCount > 1)
				sb.append("s");
		} else
			sb.append("no experiment");
	}

	@Override
	public String toPropertyString() {
		final StringBuilder sb = new StringBuilder();
		sb.append(super.toPropertyString()).append(DELIMITER);
		sb.append(imports == null ? "" : (imports + SUB_DELIMITER)).append(DELIMITER);
		sb.append(uses == null ? "" : (uses + SUB_DELIMITER)).append(DELIMITER);
		sb.append(experiments == null ? "" : (experiments + SUB_DELIMITER)).append(DELIMITER);
		sb.append(tags == null ? "" : (tags + SUB_DELIMITER)).append(DELIMITER);
		sb.append(invalid ? "TRUE" : "FALSE").append(DELIMITER);
		return sb.toString();

	}

	@Override
	public String getDocumentation() {
		return "GAML model file with " + getSuffix();
	}

}