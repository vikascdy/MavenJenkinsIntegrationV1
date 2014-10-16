// JCRLocale.JS
// Provide map for JCR field label and values
// ----------------------------------------------------------------------------

Ext.define('Util.JCRLocale', {}); // Placeholder, ignore this.

window.JCRLocale = {
		
		fileInfoLabels : null,
		fileInfoValues : null,
		
		initializeLabelMap : function(){
			
			this.fileInfoLabels=new Ext.util.HashMap();
			this.fileInfoLabels.add('jcr:created', 'Created');
			this.fileInfoLabels.add('jcr:createdBy', 'Created by');
			this.fileInfoLabels.add('jcr:lastModified','Last modified');
			this.fileInfoLabels.add('jcr:lastModifiedBy', 'Last modified by');
			this.fileInfoLabels.add('jcr:primaryType', 'Primary type');
			this.fileInfoLabels.add('jcr:mixinTypes', 'Mixin types');
			this.fileInfoLabels.add('jcr:predecessors', 'Predecessors');
			this.fileInfoLabels.add('jcr:baseVersion','Base version');
			this.fileInfoLabels.add('jcr:isCheckedOut', 'Is checked out');
			this.fileInfoLabels.add('jcr:data', 'Data');
			this.fileInfoLabels.add('jcr:versionHistory', 'Version history');
			this.fileInfoLabels.add('jcr:uuid', 'UUID');
			this.fileInfoLabels.add('jcr:baseVersion', 'Base version');
		},
		
		initializeValueMap : function(){
			
			this.fileInfoValues=new Ext.util.HashMap();
			this.fileInfoValues.add('nt:folder', 'Folder');
			this.fileInfoValues.add('nt:resource', 'Resource');
		}

};
