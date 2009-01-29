
package org.rifidi.edge.epcglobal.ale.api.lr.ws;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;
import org.rifidi.edge.epcglobal.ale.api.lr.data.LRSpec;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the org.rifidi.edge.epcglobal.ale.api.lr.ws package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _GetVendorVersion_QNAME = new QName("urn:epcglobal:alelr:wsdl:1", "GetVendorVersion");
    private final static QName _ValidationException_QNAME = new QName("urn:epcglobal:alelr:wsdl:1", "ValidationException");
    private final static QName _AddReaders_QNAME = new QName("urn:epcglobal:alelr:wsdl:1", "AddReaders");
    private final static QName _GetVendorVersionResult_QNAME = new QName("urn:epcglobal:alelr:wsdl:1", "GetVendorVersionResult");
    private final static QName _SetProperties_QNAME = new QName("urn:epcglobal:alelr:wsdl:1", "SetProperties");
    private final static QName _DuplicateNameException_QNAME = new QName("urn:epcglobal:alelr:wsdl:1", "DuplicateNameException");
    private final static QName _ImplementationException_QNAME = new QName("urn:epcglobal:alelr:wsdl:1", "ImplementationException");
    private final static QName _ReaderLoopException_QNAME = new QName("urn:epcglobal:alelr:wsdl:1", "ReaderLoopException");
    private final static QName _InUseException_QNAME = new QName("urn:epcglobal:alelr:wsdl:1", "InUseException");
    private final static QName _GetLogicalReaderNames_QNAME = new QName("urn:epcglobal:alelr:wsdl:1", "GetLogicalReaderNames");
    private final static QName _GetStandardVersionResult_QNAME = new QName("urn:epcglobal:alelr:wsdl:1", "GetStandardVersionResult");
    private final static QName _GetStandardVersion_QNAME = new QName("urn:epcglobal:alelr:wsdl:1", "GetStandardVersion");
    private final static QName _ImmutableReaderException_QNAME = new QName("urn:epcglobal:alelr:wsdl:1", "ImmutableReaderException");
    private final static QName _ALEException_QNAME = new QName("urn:epcglobal:alelr:wsdl:1", "ALEException");
    private final static QName _GetLRSpecResult_QNAME = new QName("urn:epcglobal:alelr:wsdl:1", "GetLRSpecResult");
    private final static QName _NonCompositeReaderException_QNAME = new QName("urn:epcglobal:alelr:wsdl:1", "NonCompositeReaderException");
    private final static QName _GetLRSpec_QNAME = new QName("urn:epcglobal:alelr:wsdl:1", "GetLRSpec");
    private final static QName _Update_QNAME = new QName("urn:epcglobal:alelr:wsdl:1", "Update");
    private final static QName _Undefine_QNAME = new QName("urn:epcglobal:alelr:wsdl:1", "Undefine");
    private final static QName _NoSuchNameException_QNAME = new QName("urn:epcglobal:alelr:wsdl:1", "NoSuchNameException");
    private final static QName _SecurityException_QNAME = new QName("urn:epcglobal:alelr:wsdl:1", "SecurityException");
    private final static QName _SetReaders_QNAME = new QName("urn:epcglobal:alelr:wsdl:1", "SetReaders");
    private final static QName _GetLogicalReaderNamesResult_QNAME = new QName("urn:epcglobal:alelr:wsdl:1", "GetLogicalReaderNamesResult");
    private final static QName _GetPropertyValue_QNAME = new QName("urn:epcglobal:alelr:wsdl:1", "GetPropertyValue");
    private final static QName _RemoveReaders_QNAME = new QName("urn:epcglobal:alelr:wsdl:1", "RemoveReaders");
    private final static QName _Define_QNAME = new QName("urn:epcglobal:alelr:wsdl:1", "Define");
    private final static QName _GetPropertyValueResult_QNAME = new QName("urn:epcglobal:alelr:wsdl:1", "GetPropertyValueResult");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: org.rifidi.edge.epcglobal.ale.api.lr.ws
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link UndefineResult }
     * 
     */
    public UndefineResult createUndefineResult() {
        return new UndefineResult();
    }

    /**
     * Create an instance of {@link Update }
     * 
     */
    public Update createUpdate() {
        return new Update();
    }

    /**
     * Create an instance of {@link SetReadersResult }
     * 
     */
    public SetReadersResult createSetReadersResult() {
        return new SetReadersResult();
    }

    /**
     * Create an instance of {@link InUseException }
     * 
     */
    public InUseException createInUseException() {
        return new InUseException();
    }

    /**
     * Create an instance of {@link SecurityException }
     * 
     */
    public SecurityException createSecurityException() {
        return new SecurityException();
    }

    /**
     * Create an instance of {@link Define }
     * 
     */
    public Define createDefine() {
        return new Define();
    }

    /**
     * Create an instance of {@link AddReadersResult }
     * 
     */
    public AddReadersResult createAddReadersResult() {
        return new AddReadersResult();
    }

    /**
     * Create an instance of {@link DuplicateNameException }
     * 
     */
    public DuplicateNameException createDuplicateNameException() {
        return new DuplicateNameException();
    }

    /**
     * Create an instance of {@link SetReaders.Readers }
     * 
     */
    public SetReaders.Readers createSetReadersReaders() {
        return new SetReaders.Readers();
    }

    /**
     * Create an instance of {@link RemoveReadersResult }
     * 
     */
    public RemoveReadersResult createRemoveReadersResult() {
        return new RemoveReadersResult();
    }

    /**
     * Create an instance of {@link NonCompositeReaderException }
     * 
     */
    public NonCompositeReaderException createNonCompositeReaderException() {
        return new NonCompositeReaderException();
    }

    /**
     * Create an instance of {@link AddReaders.Readers }
     * 
     */
    public AddReaders.Readers createAddReadersReaders() {
        return new AddReaders.Readers();
    }

    /**
     * Create an instance of {@link SetProperties }
     * 
     */
    public SetProperties createSetProperties() {
        return new SetProperties();
    }

    /**
     * Create an instance of {@link ReaderLoopException }
     * 
     */
    public ReaderLoopException createReaderLoopException() {
        return new ReaderLoopException();
    }

    /**
     * Create an instance of {@link AddReaders }
     * 
     */
    public AddReaders createAddReaders() {
        return new AddReaders();
    }

    /**
     * Create an instance of {@link ValidationException }
     * 
     */
    public ValidationException createValidationException() {
        return new ValidationException();
    }

    /**
     * Create an instance of {@link GetLRSpec }
     * 
     */
    public GetLRSpec createGetLRSpec() {
        return new GetLRSpec();
    }

    /**
     * Create an instance of {@link SetReaders }
     * 
     */
    public SetReaders createSetReaders() {
        return new SetReaders();
    }

    /**
     * Create an instance of {@link NoSuchNameException }
     * 
     */
    public NoSuchNameException createNoSuchNameException() {
        return new NoSuchNameException();
    }

    /**
     * Create an instance of {@link DefineResult }
     * 
     */
    public DefineResult createDefineResult() {
        return new DefineResult();
    }

    /**
     * Create an instance of {@link ALEException }
     * 
     */
    public ALEException createALEException() {
        return new ALEException();
    }

    /**
     * Create an instance of {@link RemoveReaders.Readers }
     * 
     */
    public RemoveReaders.Readers createRemoveReadersReaders() {
        return new RemoveReaders.Readers();
    }

    /**
     * Create an instance of {@link RemoveReaders }
     * 
     */
    public RemoveReaders createRemoveReaders() {
        return new RemoveReaders();
    }

    /**
     * Create an instance of {@link SetPropertiesResult }
     * 
     */
    public SetPropertiesResult createSetPropertiesResult() {
        return new SetPropertiesResult();
    }

    /**
     * Create an instance of {@link GetPropertyValue }
     * 
     */
    public GetPropertyValue createGetPropertyValue() {
        return new GetPropertyValue();
    }

    /**
     * Create an instance of {@link ArrayOfString }
     * 
     */
    public ArrayOfString createArrayOfString() {
        return new ArrayOfString();
    }

    /**
     * Create an instance of {@link EmptyParms }
     * 
     */
    public EmptyParms createEmptyParms() {
        return new EmptyParms();
    }

    /**
     * Create an instance of {@link SetProperties.Properties }
     * 
     */
    public SetProperties.Properties createSetPropertiesProperties() {
        return new SetProperties.Properties();
    }

    /**
     * Create an instance of {@link ImmutableReaderException }
     * 
     */
    public ImmutableReaderException createImmutableReaderException() {
        return new ImmutableReaderException();
    }

    /**
     * Create an instance of {@link ImplementationException }
     * 
     */
    public ImplementationException createImplementationException() {
        return new ImplementationException();
    }

    /**
     * Create an instance of {@link UpdateResult }
     * 
     */
    public UpdateResult createUpdateResult() {
        return new UpdateResult();
    }

    /**
     * Create an instance of {@link Undefine }
     * 
     */
    public Undefine createUndefine() {
        return new Undefine();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link EmptyParms }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:epcglobal:alelr:wsdl:1", name = "GetVendorVersion")
    public JAXBElement<EmptyParms> createGetVendorVersion(EmptyParms value) {
        return new JAXBElement<EmptyParms>(_GetVendorVersion_QNAME, EmptyParms.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ValidationException }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:epcglobal:alelr:wsdl:1", name = "ValidationException")
    public JAXBElement<ValidationException> createValidationException(ValidationException value) {
        return new JAXBElement<ValidationException>(_ValidationException_QNAME, ValidationException.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AddReaders }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:epcglobal:alelr:wsdl:1", name = "AddReaders")
    public JAXBElement<AddReaders> createAddReaders(AddReaders value) {
        return new JAXBElement<AddReaders>(_AddReaders_QNAME, AddReaders.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:epcglobal:alelr:wsdl:1", name = "GetVendorVersionResult")
    public JAXBElement<String> createGetVendorVersionResult(String value) {
        return new JAXBElement<String>(_GetVendorVersionResult_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SetProperties }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:epcglobal:alelr:wsdl:1", name = "SetProperties")
    public JAXBElement<SetProperties> createSetProperties(SetProperties value) {
        return new JAXBElement<SetProperties>(_SetProperties_QNAME, SetProperties.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DuplicateNameException }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:epcglobal:alelr:wsdl:1", name = "DuplicateNameException")
    public JAXBElement<DuplicateNameException> createDuplicateNameException(DuplicateNameException value) {
        return new JAXBElement<DuplicateNameException>(_DuplicateNameException_QNAME, DuplicateNameException.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ImplementationException }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:epcglobal:alelr:wsdl:1", name = "ImplementationException")
    public JAXBElement<ImplementationException> createImplementationException(ImplementationException value) {
        return new JAXBElement<ImplementationException>(_ImplementationException_QNAME, ImplementationException.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ReaderLoopException }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:epcglobal:alelr:wsdl:1", name = "ReaderLoopException")
    public JAXBElement<ReaderLoopException> createReaderLoopException(ReaderLoopException value) {
        return new JAXBElement<ReaderLoopException>(_ReaderLoopException_QNAME, ReaderLoopException.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link InUseException }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:epcglobal:alelr:wsdl:1", name = "InUseException")
    public JAXBElement<InUseException> createInUseException(InUseException value) {
        return new JAXBElement<InUseException>(_InUseException_QNAME, InUseException.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link EmptyParms }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:epcglobal:alelr:wsdl:1", name = "GetLogicalReaderNames")
    public JAXBElement<EmptyParms> createGetLogicalReaderNames(EmptyParms value) {
        return new JAXBElement<EmptyParms>(_GetLogicalReaderNames_QNAME, EmptyParms.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:epcglobal:alelr:wsdl:1", name = "GetStandardVersionResult")
    public JAXBElement<String> createGetStandardVersionResult(String value) {
        return new JAXBElement<String>(_GetStandardVersionResult_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link EmptyParms }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:epcglobal:alelr:wsdl:1", name = "GetStandardVersion")
    public JAXBElement<EmptyParms> createGetStandardVersion(EmptyParms value) {
        return new JAXBElement<EmptyParms>(_GetStandardVersion_QNAME, EmptyParms.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ImmutableReaderException }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:epcglobal:alelr:wsdl:1", name = "ImmutableReaderException")
    public JAXBElement<ImmutableReaderException> createImmutableReaderException(ImmutableReaderException value) {
        return new JAXBElement<ImmutableReaderException>(_ImmutableReaderException_QNAME, ImmutableReaderException.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ALEException }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:epcglobal:alelr:wsdl:1", name = "ALEException")
    public JAXBElement<ALEException> createALEException(ALEException value) {
        return new JAXBElement<ALEException>(_ALEException_QNAME, ALEException.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link LRSpec }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:epcglobal:alelr:wsdl:1", name = "GetLRSpecResult")
    public JAXBElement<LRSpec> createGetLRSpecResult(LRSpec value) {
        return new JAXBElement<LRSpec>(_GetLRSpecResult_QNAME, LRSpec.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link NonCompositeReaderException }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:epcglobal:alelr:wsdl:1", name = "NonCompositeReaderException")
    public JAXBElement<NonCompositeReaderException> createNonCompositeReaderException(NonCompositeReaderException value) {
        return new JAXBElement<NonCompositeReaderException>(_NonCompositeReaderException_QNAME, NonCompositeReaderException.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetLRSpec }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:epcglobal:alelr:wsdl:1", name = "GetLRSpec")
    public JAXBElement<GetLRSpec> createGetLRSpec(GetLRSpec value) {
        return new JAXBElement<GetLRSpec>(_GetLRSpec_QNAME, GetLRSpec.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Update }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:epcglobal:alelr:wsdl:1", name = "Update")
    public JAXBElement<Update> createUpdate(Update value) {
        return new JAXBElement<Update>(_Update_QNAME, Update.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Undefine }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:epcglobal:alelr:wsdl:1", name = "Undefine")
    public JAXBElement<Undefine> createUndefine(Undefine value) {
        return new JAXBElement<Undefine>(_Undefine_QNAME, Undefine.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link NoSuchNameException }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:epcglobal:alelr:wsdl:1", name = "NoSuchNameException")
    public JAXBElement<NoSuchNameException> createNoSuchNameException(NoSuchNameException value) {
        return new JAXBElement<NoSuchNameException>(_NoSuchNameException_QNAME, NoSuchNameException.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SecurityException }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:epcglobal:alelr:wsdl:1", name = "SecurityException")
    public JAXBElement<SecurityException> createSecurityException(SecurityException value) {
        return new JAXBElement<SecurityException>(_SecurityException_QNAME, SecurityException.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SetReaders }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:epcglobal:alelr:wsdl:1", name = "SetReaders")
    public JAXBElement<SetReaders> createSetReaders(SetReaders value) {
        return new JAXBElement<SetReaders>(_SetReaders_QNAME, SetReaders.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ArrayOfString }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:epcglobal:alelr:wsdl:1", name = "GetLogicalReaderNamesResult")
    public JAXBElement<ArrayOfString> createGetLogicalReaderNamesResult(ArrayOfString value) {
        return new JAXBElement<ArrayOfString>(_GetLogicalReaderNamesResult_QNAME, ArrayOfString.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetPropertyValue }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:epcglobal:alelr:wsdl:1", name = "GetPropertyValue")
    public JAXBElement<GetPropertyValue> createGetPropertyValue(GetPropertyValue value) {
        return new JAXBElement<GetPropertyValue>(_GetPropertyValue_QNAME, GetPropertyValue.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link RemoveReaders }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:epcglobal:alelr:wsdl:1", name = "RemoveReaders")
    public JAXBElement<RemoveReaders> createRemoveReaders(RemoveReaders value) {
        return new JAXBElement<RemoveReaders>(_RemoveReaders_QNAME, RemoveReaders.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Define }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:epcglobal:alelr:wsdl:1", name = "Define")
    public JAXBElement<Define> createDefine(Define value) {
        return new JAXBElement<Define>(_Define_QNAME, Define.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:epcglobal:alelr:wsdl:1", name = "GetPropertyValueResult")
    public JAXBElement<String> createGetPropertyValueResult(String value) {
        return new JAXBElement<String>(_GetPropertyValueResult_QNAME, String.class, null, value);
    }

}
