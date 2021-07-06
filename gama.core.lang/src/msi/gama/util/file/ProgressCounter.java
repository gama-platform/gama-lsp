package msi.gama.util.file;


import msi.gama.common.interfaces.IStatusDisplayer;
import msi.gama.runtime.GAMA;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;

class ProgressCounter {

	final IScope scope;
	final String name;
	float progress;

	ProgressCounter(final IScope scope, final String name) {
		this.scope = scope;
		this.name = name;
	}

	IStatusDisplayer getDisplayer() {
		return scope.getGui().getStatus(scope);
	}

	public void complete() {
		getDisplayer().setSubStatusCompletion(1d);
	}

	public void dispose() {
		getDisplayer().endSubStatus(name.toString());
	}

	public void exceptionOccurred(final Throwable arg0) {
		GAMA.reportAndThrowIfNeeded(scope, GamaRuntimeException.create(arg0, scope), true);
	}

	public float getProgress() {
		return progress;
	}

	public Object getTask() {
		return null;
	}

	public boolean isCanceled() {
		return scope.interrupted();
	}

	public void progress(final float p) {
		progress = p;
		getDisplayer().setSubStatusCompletion(progress);
	}

	public void setCanceled(final boolean cancel) {
		getDisplayer().endSubStatus(name.toString());
	}

	public void setTask(final Object n) {}

	public void started() {
		getDisplayer().beginSubStatus(name.toString());
	}

	public void warningOccurred(final String source, final String location, final String warning) {
		GAMA.reportAndThrowIfNeeded(scope, GamaRuntimeException.warning(warning, scope), false);
	}

}