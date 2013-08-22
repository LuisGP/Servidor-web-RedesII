<xsd:schema xmlns:xsd="http://www.w3.org/2001/XMLSchema">
	<xsd:element name="USUARIOS_P2P" type="TipoUSUARIOS"/>
	<xsd:element name="USUARIO">
		<xsd:complexType>
			<xsd:sequence>
				<xsd:element name="NICK" type="xsd:string"/>
				<xsd:element name="NOMBRE_COMPLETO" type="xsd:string"/>
				<xsd:element name="PUERTO" type="xsd:string"/>
				<xsd:element name="DIRECTORIO_COMPARTIDO" type="xsd:string"/>
				<xsd:element name="CORREO_ELECTRONICO" type="xsd:string"/>
				<xsd:element name="CLAVE" type="xsd:string"/>
			</xsd:sequence>
		</xsd:complexType>
	</xsd:element>

	<xsd:complexType name="TipoUSUARIOS">
		<xsd:sequence>
			<xsd:element ref="USUARIO" minOccurs="1" maxOccurs="unbounded"/>
		</xsd:sequence>
	</xsd:complexType>
</xsd:schema>
