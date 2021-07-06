/*******************************************************************************************************
 *
 * msi.gama.metamodel.topology.projection.ProjectionFactory.java, in plugin msi.gama.core, is part of the source code of
 * the GAMA modeling and simulation platform (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.metamodel.topology.projection;

import java.util.Map;

import msi.gama.common.geometry.Envelope3D;
import msi.gama.common.preferences.GamaPreferences;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaMapFactory;
import msi.gama.util.file.GamaGisFile;

/**
 * Class ProjectionFactory.
 *
 * @author drogoul
 * @since 17 déc. 2013
 *
 */
public class ProjectionFactory {

	private static final String EPSGPrefix = "EPSG:";
	private static final String defaultTargetCRS = String
			.valueOf(GamaPreferences.External.LIB_TARGET_CRS.getInitialValue(null));
	private static final String defaultSaveCRS = String
			.valueOf(GamaPreferences.External.LIB_OUTPUT_CRS.getInitialValue(null));
	private static Map<String, Object> CRSCache = GamaMapFactory.createUnordered();

	private IProjection world;
	private Object unitConverter = null;
	public Object targetCRS;

	public void setWorldProjectionEnv(final IScope scope, final Envelope3D env) {
		if (world != null) {
			return;
		}
		// ((WorldProjection) world).updateTranslations(env);
	}

	void computeTargetCRS(final IScope scope, final Object crs, final double longitude, final double latitude) {
	}

	public Object getTargetCRS(final IScope scope) {
		return null;
	}

	public Object getSaveCRS(final IScope scope) {
		return null;
	}

	public Object getCRS(final IScope scope, final int code) {
		return null;
	}

	public Object getCRS(final IScope scope, final int code, final boolean longitudeFirst) {
		return null;
	}

	public Object getCRS(final IScope scope, final String code) {
		return null;
	}

	public Object getCRS(final IScope scope, final String code, final boolean longitudeFirst) {
		return null;
	}

	/*
	 * Thai.truongming@gmail.com ---------------begin date: 03-01-2014
	 */
	public IProjection getWorld() {
		return world;
	}

	/*
	 * thai.truongming@gmail.com -----------------end
	 */
	public Object computeDefaultCRS(final IScope scope, final int code, final boolean target) {
		return null;
	}

	public IProjection fromParams(final IScope scope, final Map<String, Object> params, final Envelope3D env) {
		return null;
	}

	public IProjection fromCRS(final IScope scope, final Object crs, final Envelope3D env) {
		return null;
	}

	public IProjection forSavingWith(final IScope scope, final Integer epsgCode) {
		return null;
	}

	public IProjection forSavingWith(final IScope scope, final Integer epsgCode, final boolean lonFirst) {
		return null;
	}

	public IProjection forSavingWith(final IScope scope, final String code) {
		return null;
	}

	public IProjection forSavingWith(final IScope scope, final Object crs) {
		return null;
	}

	public IProjection forSavingWith(final IScope scope, final String code, final boolean lonFirst) {
		return null;
	}

	public Object getDefaultInitialCRS(final IScope scope) {
		if (!GamaPreferences.External.LIB_PROJECTED.getValue()) {
			try {
				return getCRS(scope, GamaPreferences.External.LIB_INITIAL_CRS.getValue());
			} catch (final GamaRuntimeException e) {
				throw GamaRuntimeException.error("The code " + GamaPreferences.External.LIB_INITIAL_CRS.getValue()
						+ " does not correspond to a known EPSG code. Try to change it in Gama > Preferences... > External",
						scope);
			}
		} else {
			return getTargetCRS(scope);
		}
	}

	public void testConsistency(final IScope scope, final Object crs, final Object env) {
		return;
	}

	public Object getUnitConverter() {
		return unitConverter;
	}

}
