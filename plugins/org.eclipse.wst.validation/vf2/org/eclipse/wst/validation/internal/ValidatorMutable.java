package org.eclipse.wst.validation.internal;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.Platform;
import org.eclipse.wst.validation.IMutableValidator;
import org.eclipse.wst.validation.MessageSeveritySetting;
import org.eclipse.wst.validation.Validator;
import org.eclipse.wst.validation.Validator.V1;
import org.eclipse.wst.validation.Validator.V2;
import org.eclipse.wst.validation.internal.model.FilterGroup;

/**
 * The mutable fields that a user can change through the preference or property page.
 * @author karasiuk
 *
 */
public final class ValidatorMutable implements IAdaptable, IMutableValidator {
	
	private boolean _build;
	private boolean	_manual;
	private String _delegatingId;
	
	private int _changeCountGroups;
	private int _changeCountMessages;
	
	private final String	_name;
	private final String 	_sourceId;
	private final boolean _isV1;
	private final boolean _isV2;
	private final ValidatorMetaData _vmd;
	private final String _id;
	private final int	_version;

	private FilterGroup[] _groups;
	private final Map<String, MessageSeveritySetting> _messageSettings;
	private final String _validatorClassname;
	
	private final boolean 	_origBuild;
	private final boolean 	_origManual;
	private final String	_origDelegating;

	public ValidatorMutable(Validator validator) {
		_name = validator.getName();
		_sourceId = validator.getSourceId();
		_manual = validator.isManualValidation();
		_build = validator.isBuildValidation();
		V2 v2 = validator.asV2Validator();
		_isV2 = v2 != null;
		_delegatingId = validator.getDelegatingId();
		
		V1 v1 = validator.asV1Validator();
		_isV1 = v1 != null;
		_vmd = _isV1 ? v1.getVmd() : null;
		_id = validator.getId();
		
		_origBuild = _build;
		_origDelegating = _delegatingId;
		_origManual = _manual;
		_version = validator.getVersion();
		_validatorClassname = validator.getValidatorClassname();
		_messageSettings = new HashMap<String, MessageSeveritySetting>(10); 
		for (Map.Entry<String, MessageSeveritySetting> me : validator.getMessageSettings().entrySet()){
			_messageSettings.put(me.getKey(), me.getValue().copy());
		}
		
		if (v2 != null){
			FilterGroup[] groups = v2.getGroups();
			_groups = new FilterGroup[groups.length];
			System.arraycopy(groups, 0, _groups, 0, groups.length);
		}
	}

	public ValidatorMutable(ValidatorMutable val) {
		_build = val._build;
		_delegatingId = val._delegatingId;
		FilterGroup[] groups = val.getGroups();
		_groups = new FilterGroup[groups.length];
		System.arraycopy(groups, 0, _groups, 0, groups.length);
		
		_id = val._id;
		_isV1 = val._isV1;
		_isV2 = val._isV2;
		_manual = val._manual;
		_messageSettings = new HashMap<String, MessageSeveritySetting>(10); 
		for (Map.Entry<String, MessageSeveritySetting> me : val.getMessageSettings().entrySet()){
			_messageSettings.put(me.getKey(), me.getValue().copy());
		}

		_name = val._name;
		_origBuild = val._origBuild;
		_origDelegating = val._origDelegating;
		_origManual = val._origManual;
		_sourceId = val._sourceId;
		_validatorClassname = val._validatorClassname;
		_version = val._version;
		_vmd = val._vmd;
	}

	public void setBuildValidation(boolean build) {
		_build = build;		
	}

	public void setManualValidation(boolean manual) {
		_manual = manual;
	}

	public String getName() {
		return _name;
	}

	public boolean isManualValidation() {
		return _manual;
	}

	public boolean isBuildValidation() {
		return _build;
	}

	public boolean isV2Validator() {
		return _isV2;
	}

	public String getDelegatingId() {
		return _delegatingId;
	}

	/**
	 * The caller of this method must not change the ValidatorMetaData.
	 */
	public ValidatorMetaData getVmd() {
		return _vmd;
	}

	public boolean isV1Validator() {
		return _isV1;
	}

	public String getId() {
		return _id;
	}

	/**
	 * Answer true if any of your settings have changed.
	 */
	public boolean isChanged() {
		if (hasGlobalChanges())return true;
		if (_changeCountGroups > 0 || _changeCountMessages > 0)return true;
		return false;
	}

	public boolean hasGlobalChanges() {
		if (_origBuild != _build)return true;
		if (_origManual != _manual)return true;
		if (!Misc.same(_origDelegating, _delegatingId))return true;
		return false;
	}

	public int getVersion() {
		return _version;
	}

	public void replaceFilterGroup(FilterGroup existing, FilterGroup merged) {
		int i = find(existing);
		if (i == -1)add(merged);  // this should never happen
		else {
			_groups[i] = merged;
			bumpChangeCountGroups();
		}
	}
	
	public void remove(FilterGroup group) {
		int i = find(group);
		if (i == -1)return;
		
		FilterGroup[] groups = new FilterGroup[_groups.length-1];
		if (i > 0)System.arraycopy(_groups, 0, groups, 0, i);
		if (i < groups.length)System.arraycopy(_groups, i+1, groups, i, groups.length-i);
		_groups = groups;
		bumpChangeCountGroups();
	}
	
	private int find(FilterGroup group) {
		for (int i=0; i<_groups.length; i++)if (group == _groups[i])return i;
		return -1;
	}

	public void add(FilterGroup fg) {
		assert fg != null;
		FilterGroup[] groups = new FilterGroup[_groups.length+1];
		System.arraycopy(_groups, 0, groups, 0, _groups.length);
		groups[_groups.length] = fg;
		_groups = groups;
		bumpChangeCountGroups();
	}
	
	private void bumpChangeCountGroups(){
		_changeCountGroups++;
	}

	public int getChangeCountGroups() {
		return _changeCountGroups;
	}
	
	public FilterGroup[] getGroups(){
		return _groups;
	}

	public void setDelegatingId(String id) {
		_delegatingId = id;	
	}

	public Map<String, MessageSeveritySetting> getMessageSettings() {
		return _messageSettings;
	}

	public int getChangeCountMessages(){
		return _changeCountMessages;
	}
	
	public void bumpChangeCountMessages(){
		_changeCountMessages++;
	}

	public String getValidatorClassname() {
		return _validatorClassname;
	}

	@SuppressWarnings("unchecked")
	public Object getAdapter(Class adapter) {
		return Platform.getAdapterManager().getAdapter(this, adapter);
	}

	public boolean sameConfig(Validator validator) {
		if (validator == null)return false;
		return hashCodeForConfig() == validator.hashCodeForConfig();
	}

	private int hashCodeForConfig() {
		int h = 0;
		if (_build)h += 101;
		if (_delegatingId != null)h += _delegatingId.hashCode();
		if (_manual)h += 201;
		if (_messageSettings != null){
			for (MessageSeveritySetting ms : _messageSettings.values())h += ms.hashCode();
		}
		if (_sourceId != null)h += _sourceId.hashCode();
		h += _version;
		if (_id != null)h += _id.hashCode();
		for (FilterGroup fg : _groups)h += fg.hashCodeForConfig();
		return h;
	}

	@Override
	public String toString() {
		return _name;
	}


}
