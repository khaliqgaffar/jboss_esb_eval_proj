package com.emirates;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import org.jboss.soa.esb.ConfigurationException;
import org.jboss.soa.esb.actions.AbstractActionPipelineProcessor;
import org.jboss.soa.esb.actions.ActionProcessingException;
import org.jboss.soa.esb.actions.ActionUtils;
import org.jboss.soa.esb.actions.converters.xstream.conf.ClassAliasConf;
import org.jboss.soa.esb.actions.converters.xstream.conf.ConverterConf;
import org.jboss.soa.esb.actions.converters.xstream.conf.FieldAliasConf;
import org.jboss.soa.esb.actions.converters.xstream.conf.ImplicitCollectionConf;
import org.jboss.soa.esb.actions.converters.xstream.conf.XStreamConfigurator;
import org.jboss.soa.esb.helpers.ConfigTree;
import org.jboss.soa.esb.listeners.message.MessageDeliverException;
import org.jboss.soa.esb.message.Message;
import org.jboss.soa.esb.message.MessagePayloadProxy;
import org.jboss.soa.esb.message.body.content.BytesBody;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.QNameMap;
import com.thoughtworks.xstream.io.xml.StaxDriver;

/**
 * Object to XML processor.
 * <p/>
 * Uses the <a href="http://xstream.codehaus.org/">XStream</a> processor to generate an XML message String from the supplied object.
 * <p/>
 * Sample Action Configuration:
 * <pre>{@code
 * <action name="Customer-To-XML" processor="ObjectToXStream">
 *     <property name="class-alias" value="Customer" /> 
 *     <property name="exclude-package" value="false" /> 
 *     <property name="namespace-uri" value="namespace goes here" />
 *     <property name="aliases"> 
 * 		<alias name="aliasName" class="className" />
 * 		<alias name="aliasName" class="className" />
 * 		...
 *     </property>
 *     <property name="namespaces"> 
 * 		<namespace namespace-uri="http://www.xyz.com" local-part="xyz" /> 
 * 		<namespace namespace-uri="http://www.xyz.com/x" local-part="x" />
 *     </property>
 *     <property name="fieldAliases">
 *      <field-alias alias="aliasName" class="className" fieldName="fieldName"/> 
 *      <field-alias alias="aliasName" definedIn="className" fieldName="fieldName"/>
 *      ...
 *     </property>
 *     <property name="attributeAliases">
 * 		<attribute-alias name="aliasName" class="className"/> 
 * 		<attribute-alias name="aliasName" class="className"/> 
 * 		...
 *     </property>
 *     <property name="implicit-collections">
 *      <implicit-collection class="className" fieldName="fieldName" fieldType="java.lang.String" />
 *      <implicit-collection class="className" fieldName="fieldName" fieldType="java.lang.Integer"/>
 *      ...
 *     </property>
 *     <property name="converters"> 
 *      <converter class="className" />
 *      <converter class="className" />
 * 		...
 *     </property>
 * </action>
 * }</pre>
 * <p/>
 *  * <p/>
 * <lu>
 * <li><i>class-alias</i> Optional. Class alias for the 'incoming-type'.</li>
 * <li><i>exclude-package</i> Optional, defaults to true. Determines whether package name should be removed from the incoming type.</li>
 * <li><i>aliases</i> Optional. Specifies extra class aliases.</li>
 * <li><i>namespace-uri</i> Optional. List of namespaces that will be registerd with XStream.</li>
 * <li><i>fieldAliases</i> Optional. Specifies field aliases.</li>
 * <li><i>implicit-collections</i> Optional. Specifies implicit collections which are when you have an xml element that is a place holder for a collection of other elements.
 *                                 In this case you are telling XStream to not include the holder element but instead place its element into the the 'fieldName' in the target class.
 *                                 'className' is the collection type.
 *                                 'fieldType' is the type the elements in the collection.</li>
 * <li><i>converters</i> Optional. Specifies converters that will be registered with XStream.</li>
 * </lu>
 * The XML root element is either set from the "class-alias" property or the classes full name.  In the later case, the class package is
 * excluded unless "exclude-package" is set to "false"/"no". 
 * 
 * @author <a href="mailto:tom.fennelly@jboss.com">tom.fennelly@jboss.com</a>
 * @author <a href="mailto:daniel.bevenius@gmail.com">daniel.bevenius@gmail.com</a>
 * @since Version 4.0
 */
public class ObjectToXStream extends AbstractActionPipelineProcessor {
	
	public static final String ATTR_CLASS_ALIAS = "class-alias";
	public static final String ATTR_EXCLUDE_PACKAGE = "exclude-package";
	public static final String ATTR_NAMESPACE_URI = "namespace-uri";
	public static final String ATTR_NAMESPACE_LOCALPART = "local-part";
	public static final String ATTR_XSTREAM_MODE = "xstream-mode";
	public static final String ATTR_DEFAULT_NAMESPACE = "defaultNameSpace";
	public static final String ATTR_DEFAULT_PREFIX = "defaultPrefix";
	
    private String classAlias;
    private boolean excludePackage;
    private MessagePayloadProxy payloadProxy;
    private Map<String,String> namespaces;
    private String mode;
    private List<ClassAliasConf> classAliases;
	private List<FieldAliasConf> fieldAliases;
	private List<ImplicitCollectionConf> implicitCollections;
	private List<ConverterConf> converters;
	private String defaultPrefix;
	private String defaultNameSpace;
	
    
    
    /**
     * Public constructor.
     * @param configTree Action Properties.
     * @throws ConfigurationException Action not properly configured.
     */
    public ObjectToXStream(ConfigTree configTree) {
        payloadProxy = new MessagePayloadProxy(configTree,
                                               new String[] {BytesBody.BYTES_LOCATION, ActionUtils.POST_ACTION_DATA},
                                               new String[] {ActionUtils.POST_ACTION_DATA});
        classAlias = configTree.getAttribute(ATTR_CLASS_ALIAS);
        excludePackage = configTree.getAttribute(ATTR_EXCLUDE_PACKAGE, "true").equals("true");
        namespaces = getNamespaces(configTree);
        mode = configTree.getAttribute(ATTR_XSTREAM_MODE, "XPATH_RELATIVE_REFERENCES");
        XStreamConfigurator xstreamConfig = new XStreamConfigurator(configTree);
        classAliases = xstreamConfig.getClassAliases();
    	fieldAliases = xstreamConfig.getFieldAliases();
    	implicitCollections = xstreamConfig.getImplicitCollections();
    	converters = xstreamConfig.getConverters();
    	defaultNameSpace = configTree.getAttribute(ATTR_DEFAULT_NAMESPACE);
    	defaultPrefix = configTree.getAttribute(ATTR_DEFAULT_PREFIX);
    }

    /* (non-Javadoc)
     * @see org.jboss.soa.esb.actions.ActionProcessor#process(java.lang.Object)
     */
    public Message process(Message message) throws ActionProcessingException {

        Object object;
        try {
            object = payloadProxy.getPayload(message);
        } catch (MessageDeliverException e) {
            throw new ActionProcessingException(e);
        }

        XStream xstream = createXStreamInstance();
        
        if(classAlias == null) {
            if(excludePackage) {
                xstream.alias(object.getClass().getSimpleName(), object.getClass());
            } else {
                xstream.alias(object.getClass().getName(), object.getClass());
            }
        } else {
            xstream.alias(classAlias, object.getClass());
        }
        XStreamConfigurator.addClassAliases(classAliases, xstream);
        XStreamConfigurator.addFieldAliases(fieldAliases, xstream);
        XStreamConfigurator.addImplicitCollections(implicitCollections, xstream);
        XStreamConfigurator.addConverters(converters, xstream);
        
        try {
            String xml = xstream.toXML(object);
            payloadProxy.setPayload(message, xml);
        } catch (MessageDeliverException e) {
            throw new ActionProcessingException(e);
        }

        return message;
    }
    
    /**
     * Creates an XStream instance. If namespace mappings have been defined the XStream
     * instance is created with a StaxDriver configured with those mappings.
     * 
     * @param aliases  Map of aliases.
     * @throws ActionProcessingException 
     */
    protected XStream createXStreamInstance() {
      if (namespaces == null || namespaces.size() == 0) {
        return new XStream();
      }
      else {
        final QNameMap nsm = new QNameMap();
        nsm.setDefaultNamespace(defaultNameSpace);
        nsm.setDefaultPrefix(defaultPrefix);
        for (Map.Entry<String,String> me : namespaces.entrySet() ) {
          final String namespaceURI = (String)me.getKey();
          final String localPart = (String)me.getValue();
          nsm.registerMapping(new QName(namespaceURI, localPart), localPart);
        }
        XStream stream = new XStream(new StaxDriver(nsm));
        
        if ("XPATH_RELATIVE_REFERENCES".equals(mode))
        {
            // default
        }
        else
        {
            if ("XPATH_ABSOLUTE_REFERENCES".equals(mode))
                stream.setMode(XStream.XPATH_ABSOLUTE_REFERENCES);
            else
            {
                if ("ID_REFERENCES".equals(mode))
                    stream.setMode(XStream.ID_REFERENCES);
                else
                {
                    if ("NO_REFERENCES".equals(mode))
                        stream.setMode(XStream.NO_REFERENCES);
                }
            }
        }
        
        return stream;
      }
    }
    
    /**
     * Will extract the namespace elements from the passed-in configTree 
     * 
     * @param configTree      the configuration for this class
     * 
     * @return Map<String,String>   either an empty map or a map containing the name
     *                space URI as its key and the corresponding value is the local
     *                XML element name to map it to.  
     */
    protected Map<String,String> getNamespaces(ConfigTree configTree) {
		Map<String,String> namespaces = new HashMap<String,String>();
		      
		ConfigTree[] children = configTree.getChildren("namespace");
		      
		if (children != null) 
		{
			for (ConfigTree namespace : children)
				namespaces.put(namespace.getAttribute(ATTR_NAMESPACE_URI), namespace.getAttribute(ATTR_NAMESPACE_LOCALPART));
		}
		return namespaces;
	}
}
