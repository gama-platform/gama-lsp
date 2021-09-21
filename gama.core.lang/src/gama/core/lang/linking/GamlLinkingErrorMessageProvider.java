/*********************************************************************************************
 *
 * 'GamlLinkingErrorMessageProvider.java, in plugin msi.gama.lang.gaml, is part of the source code of the
 * GAMA modeling and simulation platform.
 * (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package gama.core.lang.linking;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.CrossReference;
import org.eclipse.xtext.diagnostics.Diagnostic;
import org.eclipse.xtext.diagnostics.DiagnosticMessage;
import org.eclipse.xtext.diagnostics.Severity;
import org.eclipse.xtext.linking.impl.IllegalNodeException;
import org.eclipse.xtext.linking.impl.LinkingDiagnosticMessageProvider;

import gama.core.lang.gaml.ActionRef;
import gama.core.lang.gaml.EquationRef;
import gama.core.lang.gaml.SkillRef;
import gama.core.lang.gaml.TypeRef;
import gama.core.lang.gaml.VariableRef;
import gama.core.lang.gaml.util.GamlSwitch;

public class GamlLinkingErrorMessageProvider extends LinkingDiagnosticMessageProvider {

	@Override
	public DiagnosticMessage getUnresolvedProxyMessage(final ILinkingDiagnosticContext context) {
		// final EClass referenceType =
		// context.getReference().getEReferenceType();
		final EObject referee = context.getContext();
		final String referenceName = new GamlSwitch<String>() {

			@Override
			public String caseVariableRef(final VariableRef v) {
				return "variable or species";
			}

			@Override
			public String caseTypeRef(final TypeRef v) {
				return "type";
			}

			@Override
			public String caseActionRef(final ActionRef v) {
				return "primitive or action";
			}

			@Override
			public String caseEquationRef(final EquationRef v) {
				return "equation";
			}

			@Override
			public String caseSkillRef(final SkillRef v) {
				return "skill";
			}

			@Override
			public String defaultCase(final EObject object) {
				return "element";
			}

		}.doSwitch(referee);
		String linkText = "";
		try {
			linkText = context.getLinkText();
		} catch (final IllegalNodeException e) {
			linkText = e.getNode().getText();
		}
		final String msg = "The " + referenceName + " '" + linkText
				+ "' cannot be resolved (either it is not defined or not accessible)";
		return new DiagnosticMessage(msg, Severity.ERROR, Diagnostic.LINKING_DIAGNOSTIC);
	}

	@Override
	public DiagnosticMessage getIllegalNodeMessage(final ILinkingDiagnosticContext context,
			final IllegalNodeException ex) {
		final String message = ex.getMessage();
		return new DiagnosticMessage(message, Severity.ERROR, Diagnostic.LINKING_DIAGNOSTIC);
	}

	@Override
	public DiagnosticMessage getIllegalCrossReferenceMessage(final ILinkingDiagnosticContext context,
			final CrossReference reference) {
		final String message = "Cannot find reference " + reference;
		return new DiagnosticMessage(message, Severity.ERROR, Diagnostic.LINKING_DIAGNOSTIC);
	}

	@Override
	public DiagnosticMessage getViolatedBoundsConstraintMessage(final ILinkingDiagnosticContext context,
			final int size) {
		final String message = "Too many matches for reference to '" + context.getLinkText() + "'. " + "Feature "
				+ context.getReference().getName() + " can only hold " + context.getReference().getUpperBound()
				+ " reference" + (context.getReference().getUpperBound() != 1 ? "s" : "") + " but found " + size
				+ " candidate" + (size != 1 ? "s" : "");
		return new DiagnosticMessage(message, Severity.ERROR, Diagnostic.LINKING_DIAGNOSTIC);
	}

	@Override
	public DiagnosticMessage getViolatedUniqueConstraintMessage(final ILinkingDiagnosticContext context) {
		final String message = "Cannot refer to '" + context.getLinkText() + "' more than once.";
		return new DiagnosticMessage(message, Severity.ERROR, Diagnostic.LINKING_DIAGNOSTIC);
	}

}
