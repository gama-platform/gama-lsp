/*******************************************************************************************************
 *
 * msi.gaml.descriptions.ValidationContext.java, in plugin msi.gama.core, is part of the source code of the GAMA
 * modeling and simulation platform (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gaml.descriptions;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;

import msi.gama.common.interfaces.IDocManager;
import msi.gama.common.interfaces.IGamlDescription;
import msi.gama.common.preferences.GamaPreferences;
import msi.gama.util.Collector;
import msi.gaml.compilation.GamlCompilationError;

public class ValidationContext extends Collector.AsList<GamlCompilationError> implements IDocManager {

	final static int MAX_SIZE = 1000;
	public static final ValidationContext NULL = new ValidationContext(null, false, IDocManager.NULL);
	final URI resourceURI;
	final Collector.AsList<GamlCompilationError> importedErrors = Collector.getList();
	private boolean noWarning, noInfo, hasSyntaxErrors, noExperiment;
	private final IDocManager docDelegate;

	public ValidationContext(final URI uri, final boolean syntax, final IDocManager delegate) {
		this.resourceURI = uri;
		hasSyntaxErrors = syntax;
		docDelegate = delegate == null ? IDocManager.NULL : delegate;
	}

	@Override
	public boolean add(final GamlCompilationError error) {
		if (error.isWarning()) {
			if (!GamaPreferences.Modeling.WARNINGS_ENABLED.getValue() || noWarning) { return false; }
		} else if (error.isInfo()) {
			if (!GamaPreferences.Modeling.INFO_ENABLED.getValue() || noInfo) { return false; }
		}
		final URI uri = error.getURI();
		final boolean sameResource = uri.equals(resourceURI);
		if (sameResource) {
			return super.add(error);
		} else if (error.isError()) {
			importedErrors.add(error);
			return true;
		}
		return false;
	}

	public static Predicate<GamlCompilationError> IS_INFO = input -> input.isInfo();
	public static Predicate<GamlCompilationError> IS_WARNING = input -> input.isWarning();
	public static Predicate<GamlCompilationError> IS_ERROR = input -> input.isError();

	public boolean hasInternalSyntaxErrors() {
		return hasSyntaxErrors;
	}

	public void hasInternalSyntaxErrors(final boolean errors) {
		hasSyntaxErrors = errors;
	}

	public boolean hasErrors() {
		return hasSyntaxErrors || hasInternalErrors() || hasImportedErrors();
	}

	public boolean hasInternalErrors() {
		return false;
	}

	public boolean hasImportedErrors() {
		return !importedErrors.isEmpty();
	}

	public List<GamlCompilationError> getInternalErrors() {
		return null;
	}

	public Collection<GamlCompilationError> getImportedErrors() {
		return importedErrors.items();
	}

	public Iterable<GamlCompilationError> getWarnings() {
		return null;
	}

	public Iterable<GamlCompilationError> getInfos() {
		return null;
	}

	@Override
	public void clear() {
		super.clear();
		Collector.release(importedErrors);
		hasSyntaxErrors = false;
	}

	/**
	 * Method iterator()
	 *
	 * @see java.lang.Iterable#iterator()
	 */
	@Override
	public Iterator<GamlCompilationError> iterator() {
		return null;
	}

	public Map<String, URI> getImportedErrorsAsStrings() {
		return null;
	}

	public void setNoWarning() {
		noWarning = true;

	}

	public void setNoInfo() {
		noInfo = true;
	}

	public void resetInfoAndWarning() {
		noInfo = false;
		noWarning = false;

	}

	@Override
	public void document(final IDescription description) {
		docDelegate.document(description);
	}

	@Override
	public IGamlDescription getGamlDocumentation(final EObject o) {
		return docDelegate.getGamlDocumentation(o);
	}

	@Override
	public IGamlDescription getGamlDocumentation(final IGamlDescription o) {
		return docDelegate.getGamlDocumentation(o);
	}

	@Override
	public void setGamlDocumentation(final EObject object, final IGamlDescription description, final boolean replace,
			final boolean force) {
		docDelegate.setGamlDocumentation(object, description, replace, force);
	}

	@Override
	public void addCleanupTask(final ModelDescription model) {
		docDelegate.addCleanupTask(model);
	}

	public boolean hasErrorOn(final EObject... objects) {
		return true;
	}

	public void setNoExperiment() {
		noExperiment = true;
	}

	public boolean getNoExperiment() {
		return noExperiment;
	}

}
